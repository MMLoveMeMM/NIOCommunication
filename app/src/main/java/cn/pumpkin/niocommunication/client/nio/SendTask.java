package cn.pumpkin.niocommunication.client.nio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;

import cn.pumpkin.niocommunication.client.constants.Status;
import cn.pumpkin.niocommunication.client.iface.IExceptionListener;
import cn.pumpkin.niocommunication.client.iface.IWriteListener;
import cn.pumpkin.niocommunication.client.msg.MsgRequest;

public class SendTask implements Runnable{

	private Selector selector;
	private SelectionKey mSelectKey;
	private ByteBuffer mainBuffer;//主缓冲区，用于直接全部读取所有可用内容，包括截断包
	private BlockingQueue<MsgRequest> mSendQueue; // 發送的消息先入隊列,这个在发送这边不做任何修改,防止不安全
	private Map<Integer, MsgRequest> mSendedList; // 将已经发送过的指令放到这个队列,如果读线程获取对应的req_id,就移除
	private TCPConnector mTCPConnector;

	// private HashSet<IWriteListener<Integer>> mProxyWriteListeners = new HashSet<IWriteListener<Integer>>();

	private IWriteListener mListener;
	private IExceptionListener mEListener;

	private Thread mWriteTaskThread;

	public SendTask(TCPConnector connector){
		mTCPConnector=connector;
		mSendQueue=new PriorityBlockingQueue();
		mSendedList = new ConcurrentHashMap<>();
		mWriteTaskThread=new Thread(this);
	}

	public void registerListener(IWriteListener listener0,IExceptionListener listener1) {
		mListener=listener0;
		mEListener=listener1;
	}

	public void pushQueue(MsgRequest msg) {
		// 保證讀寫安全
		synchronized(this) {
			mSendQueue.offer(msg);
		}
	}

	public void removeSendedQueue(int req_id) {
		if(mSendedList!=null && mSendedList.isEmpty()) {
			if(mSendedList.containsKey(req_id)) {
				mSendedList.remove(req_id);
			}
		}
	}

	public void addSendedQueue(MsgRequest msg) {
		if(mSendedList!=null) {
			mSendedList.put(msg.getReq_id(), msg);
		}
	}

	// 检查重发队里中哪些没有被移除,而又到了重发时间点
	public void resendCmd2Queue() {

		for(int req_id : mSendedList.keySet()) {

			MsgRequest msg=mSendedList.get(req_id);
			long sendtime=msg.getSendtime();
			// 当前的时间减去上一次发送的时间
			if(System.currentTimeMillis()-sendtime>msg.getResendtimedelay()) {
				// 添加到发送队列中
				if(msg.getCnt()<msg.getTotaltime()) {
					// 重发次数没有达到最大上线
					msg.setCnt(msg.getCnt()+1);
					pushQueue(msg);
				}else {
					// 报发送失败
					if(mListener!=null) {
						mListener.onSuccess(msg, -1);
					}
				}

				// 并移除重发队列
				removeSendedQueue(req_id);
			}

		}

	}

	public void doWork() {
		// 啟動一個消費線程處理消息發送
		mWriteTaskThread.start();

	}

	public void send() {

		if(mSendQueue!=null && !mSendQueue.isEmpty()) {
			MsgRequest message = null;
			try {
				synchronized(this) {
					if(!mSendQueue.isEmpty()) {
						message=mSendQueue.take();
						message.setSendtime(System.currentTimeMillis());
						ByteBuffer msg=ByteBuffer.wrap(message.getBody().getBytes());
						mTCPConnector.sendData(msg);
						if(mListener!=null) {
							mListener.onSuccess(message, 0);
						}
					}
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				if(mEListener!=null) {
					// 更新状态机
					mEListener.onError(Status.NIO_NO_CONNECTION_EXCEPTION);
				}
				e.printStackTrace();
			}
			// 加入到重发队列,如果读线程收到对应的req_id就移除
			if(message!=null) {
				addSendedQueue(message);
			}
			// 检查重发队里中哪些没有被移除,而又到了重发时间点


		}

	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(true) {
			send();
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void destroy() {
		if(mWriteTaskThread!=null && mWriteTaskThread.isAlive()) {
			mSendQueue.clear();
			mWriteTaskThread.stop();
			mWriteTaskThread=null;
		}
	}

}
