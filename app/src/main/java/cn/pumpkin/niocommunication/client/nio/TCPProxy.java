package cn.pumpkin.niocommunication.client.nio;

import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Timer;
import java.util.concurrent.BlockingQueue;

import cn.pumpkin.niocommunication.client.constants.Status;
import cn.pumpkin.niocommunication.client.iface.ICompleteListener;
import cn.pumpkin.niocommunication.client.iface.IConnectListener;
import cn.pumpkin.niocommunication.client.iface.IExceptionListener;
import cn.pumpkin.niocommunication.client.iface.IReadListener;
import cn.pumpkin.niocommunication.client.iface.ISendCallBackListener;
import cn.pumpkin.niocommunication.client.iface.IWriteListener;
import cn.pumpkin.niocommunication.client.msg.MsgRequest;
import cn.pumpkin.niocommunication.client.msg.MsgResponse;
import cn.pumpkin.niocommunication.client.nio.net.ConnectRouter;
import cn.pumpkin.niocommunication.client.nio.net.TCPConnector;
import cn.pumpkin.niocommunication.client.timeout.DelayRequest;
import cn.pumpkin.niocommunication.client.timeout.WaitTimeTask;
import cn.pumpkin.niocommunication.client.timer.NetTimerTask;
import cn.pumpkin.niocommunication.client.utils.TimeStamp;

public class TCPProxy implements IConnectListener,IReadListener<MsgResponse>,IWriteListener<MsgRequest>,IExceptionListener<Integer> {

	private final static String TAG=TCPProxy.class.getName();
	private String DEF_ROUTER_HOST_IP="127.0.0.1";
	private int DEF_ROUTER_HOST_PORT=9987;
	private ConnectRouter mConnectRouter;
	/*
	 * 网络监听
	 * */
	private NetTimerTask mNetCheckTimerTask;
	private Timer mNetTimer;
	/*
	 * 网络连接器
	 * */
	private TCPConnector mConnector;
	private ReadTask mReadTask;
	private SendTask mWriteTask;
	private WaitTimeTask mWaitTimeTask;
	// 消息队列
	private BlockingQueue<MsgRequest> mSendQueue;
	private BlockingQueue<MsgResponse> mReadQueue;
	private HashSet<ICompleteListener<String>> mProxyCompleteListeners = new HashSet<ICompleteListener<String>>();

	private HashSet<ISendCallBackListener<MsgResponse>> mSendCallBackListeners = new HashSet<ISendCallBackListener<MsgResponse>>();

	private int mStatus;

	public TCPProxy(String ip,int port) {
		// 初始化状态标记为未连接
		mStatus= Status.NET_STATUS_FAIL;

		if(!TextUtils.isEmpty(ip)){
			DEF_ROUTER_HOST_IP=ip;
		}
		if(port>0){
			DEF_ROUTER_HOST_PORT=port;
		}

		// 初始化消息队列
		init();
	}

	public void init() {
		// 将登入信息传入

		mNetTimer=new Timer();

	}

	public void addCompleteListener(ICompleteListener listener) {
		mProxyCompleteListeners.add(listener);
	}

	public void pushMessage(MsgRequest msg) {
		msg.setInqueuetime(TimeStamp.getSecondTimestampTwo(new Date()));
		if(mStatus>=Status.ROUTER_LOGINED) {
			// TCP连接后开始传输数据
			// 这个地方操作有个点:重连的时候,重连之前的数据都会被丢失
			msg.setCnt(1);
			msg.setTotaltime(3); // 重发次数为3次
			msg.setResendtimedelay(3*1000);// 每三秒重发一次
			mWriteTask.pushQueue(msg);
			// 这个地方改到入发送队列的时候,不然时序可能出错(延时机制还没有建立,就已经收到数据了)
			DelayRequest req=new DelayRequest();
			req.setReq_id(msg.getReq_id());
			req.setInsertTime(msg.getInqueuetime());
			req.setDelayTime(msg.getTimeout());
			req.setListener(msg.getCallbacklistener());
			mWaitTimeTask.pushDelayQueue(req);

		}
	}

	/*
	 * 启动一个线程循环处理状态机
	 * */
	public void doWork() {
		Log.d(TAG,"client machine ready to work");
		// 开始状态机循环
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				while(true) {
					synchronized(TCPProxy.this) {

						switch(mStatus) {
							case Status.NET_STATUS_FAIL:{
								// 开始定时检查网络状态,通过获取IP地址吧

								mNetCheckTimerTask=new NetTimerTask();
								mNetCheckTimerTask.registerListener(TCPProxy.this);
								mNetTimer.schedule(mNetCheckTimerTask, 100, 3000);
								//切换到等待状态
								mStatus=Status.NET_STATUS_CHECKING;//可以开始连接TCP
							}
							break;
							case Status.NET_STATUS_CHECKING:{
								// 等待网络OK
							}
							break;
							case Status.NET_STATUS_OK:{
								// 取消定时器,开始连接
								if(mNetTimer!=null) {
									mNetTimer.cancel();
								}
								mStatus=Status.NIO_READY_CONNECT;
							}
							break;
							case Status.NIO_NO_CONNECTION_EXCEPTION:{
								// 这个可以安排5s后处理
							}
							case Status.NIO_READY_CONNECT:{
								if(mReadTask!=null) {
									mReadTask.destroy();
								}
								if(mWriteTask!=null) {
									mWriteTask.destroy();
								}
								if(mWaitTimeTask!=null) {
									mWaitTimeTask.destroy();
								}
								mStatus=Status.NIO_CONNECTING;
								// 这个连接建立或者重新建立
								mConnectRouter=new ConnectRouter(DEF_ROUTER_HOST_IP,DEF_ROUTER_HOST_PORT);
								mConnectRouter.registerListener(TCPProxy.this);
								mConnectRouter.connectRouter();

							}
							break;
							case Status.NIO_CONNECTING:{
								// todo nothing,waiting to connect router,reserve status

							}
							break;
							case Status.NIO_CONNECTED:{
								try {
									mStatus=Status.ROUTER_UNLOGIN;//开始去登入
									// 超时线程
									mConnector=mConnectRouter.getTCPConnector();
									mWaitTimeTask=new WaitTimeTask();
									mWaitTimeTask.doWork();
									// 连接上以后开始读写任务
									mWriteTask=new SendTask(mConnector);
									mWriteTask.registerListener(TCPProxy.this,TCPProxy.this);
									mWriteTask.doWork();
									// 读取数据线程
									mReadTask=new ReadTask(mConnector);
									mReadTask.registerListener(TCPProxy.this,TCPProxy.this);
									mReadTask.readWork();

								} catch (IOException e) {
									// TODO Auto-generated catch block
									// 读写过程如果出现异常,直接让TCP重连
									mStatus=Status.NIO_NO_CONNECTION_EXCEPTION;
									e.printStackTrace();
								}

							}
							break;
							case Status.ROUTER_UNLOGIN:{
								// 准备去登入
								mStatus=Status.ROUTER_LOGINING;//登入中去等待
								loginRouter();
							}
							break;
							case Status.ROUTER_LOGINING:{
								// 等待登入中返回的结果
							}
							break;
							case Status.ROUTER_LOGINED:{
								// 登入成功后,继续切换状态机
								// 置于可以数据通信的状态
								mStatus=Status.NET_DATA;
							}
							break;
							case Status.NET_DATA:{
								// 循环发送数据
								// 状态机正处于正常工作中

							}
							break;
							default:
								break;
						}
					}
				}

			}

		}).start();

	}

	/*
	 * 登入
	 * */
	private void loginRouter() {
		Log.d(TAG,"start to login!");
		MsgRequest msg=new MsgRequest();
		msg.setBody("start to login");
		msg.setInqueuetime(System.currentTimeMillis());
		System.out.println("current time : "+System.currentTimeMillis());
		msg.setTimeout(3000);
		msg.setReq_id(1001);
		msg.setPriority(10);
		//msg.setListener(mListener);
		msg.setCallbacklistener(mLoginCBListener);

		mWriteTask.pushQueue(msg);
		// 这个地方改到入发送队列的时候,不然时序可能出错(延时机制还没有建立,就已经收到数据了)
		DelayRequest req=new DelayRequest();
		req.setReq_id(msg.getReq_id());
		req.setInsertTime(msg.getInqueuetime());
		req.setDelayTime(msg.getTimeout());
		req.setListener(msg.getCallbacklistener());
		mWaitTimeTask.pushDelayQueue(req);
	}

	private ISendCallBackListener mLoginCBListener=new ISendCallBackListener() {

		@Override
		public void onComplete(Object msg, int code) {
			// TODO Auto-generated method stub
			Log.d(TAG,"****************login code : "+code);
			if(code==0) {
				mStatus=Status.ROUTER_LOGINED;//登入成功
			}else {
				try {
					Thread.sleep(3000);//间隔3s重新登入
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				mStatus=Status.ROUTER_UNLOGIN;//登入失败,将状态置回去
			}

		}

	};

	@Override
	public void onStatusChanged(int code) {
		// TODO Auto-generated method stub
		synchronized(TCPProxy.this) {
			mStatus=code;
			Log.d(TAG,"+++ status change : "+code);
		}

	}

	@Override
	public void onSuccess(MsgRequest message,int code) {
		// TODO Auto-generated method stub
		if(message!=null && code==0) {
			// message.getListener().onComplete(code);
			// 超时管理:发送成功以后开始加入超时队列,进行超时判断
			// 这个地方改到入发送队列的时候,不然时序可能出错(延时机制还没有建立,就已经收到数据了)
			/*DelayRequest req=new DelayRequest();
			req.setReq_id(message.getReq_id());
			req.setInsertTime(message.getStarttime());
			req.setDelayTime(message.getTimeout());
			req.setListener(message.getCallbacklistener());
			mWaitTimeTask.pushDelayQueue(req);*/

		}else if(code ==-1) {
			if(mWaitTimeTask!=null) {
				// 重发失败就不需要超时了,直接报失败
				mWaitTimeTask.removeDelayQueue(message.getReq_id());
			}
		}
	}

	@Override
	public void onReceiver(MsgResponse message,int code) {
		// TODO Auto-generated method stub
		Log.d(TAG,"onReceiver msg : "+message.getResultMessage());
		if(message!=null) {
			// 入读取数据的队列
			//mReadQueue.offer(message);//未初始化
		}
		if(code==0 && message!=null) {
			// 移除超时操作:
			if(mWaitTimeTask!=null) {
				mWaitTimeTask.removeDelayQueue(message.getReq_id());
			}
			if(mWriteTask!=null) {
				mWriteTask.removeSendedQueue(message.getReq_id());
			}
			// 分发数据给各个业务模块
			Iterator<ICompleteListener<String>> it = mProxyCompleteListeners.iterator();
			ICompleteListener<String> listener;
			while (it.hasNext()) {
				listener = it.next();
				listener.onComplete(message.getResultMessage());//这个地方不直接分发,可能会因为业务层耗时影响网络层,凡是註冊過的都回調
			}

		}else {
			// 网络读取状态可能异常,状态机重新开始
			mStatus=code;
		}

	}

	@Override
	public void onError(Integer code) {
		// TODO Auto-generated method stub
		// 线程中出现异常
		mStatus=code;
	}

}
