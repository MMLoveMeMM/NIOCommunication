package cn.pumpkin.niocommunication.client.nio.net;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import cn.pumpkin.niocommunication.client.constants.Status;
import cn.pumpkin.niocommunication.client.iface.IConnectListener;
import cn.pumpkin.niocommunication.client.nio.TCPConnector;

/*
 * 这个类专门用于连接路由设备,连接成功后就返回给代理类继续进行其他开发
 * */
public class ConnectRouter {

	private IConnectListener mListener;

	public void registerListener(IConnectListener listener) {
		mListener=listener;
	}

	private final static String DEF_ROUTER_HOST_IP="127.0.0.1";
	private final static int DEF_ROUTER_HOST_PORT=8001;

	private TCPConnector mConnector;
	private Timer connTimer = new Timer();

	public ConnectRouter() {

	}

	public TCPConnector getTCPConnector() {
		return mConnector;
	}

	public boolean connectRouter() {

		// 最好获取网络状态,查询自身的IP地址
		try {

			if(mConnector!=null && mConnector.isConnect()) {
				// 如果已经连接上,断开重连,状态机下很难遇到
				mConnector.close();
			}

			mConnector=new TCPConnector();
			mConnector.createLink(DEF_ROUTER_HOST_IP, DEF_ROUTER_HOST_PORT);
			if(mConnector.isConnect()) {
				mListener.onStatusChanged(Status.NIO_CONNECTED);
				return true;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			// 如果主机后启动,启动连接就会异常
			e.printStackTrace();
		}

		if(mListener!=null) {
			mListener.onStatusChanged(Status.NIO_NO_CONNECTION_EXCEPTION);
		}
		// 如果不能够连接,启动定时器,不断开启重连任务,顶层是状态机,不需要在这里维护
		// reconnRouter(Status.NIO_NO_CONNECTION_EXCEPTION);

		return false;
	}

	// 网络出现任何异常,进行重连
	public void reconnRouter(int type) {
		connTimer.schedule(new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if(connectRouter()) {
					// 连接上了就退出
					connTimer.cancel();
					mListener.onStatusChanged(Status.NIO_CONNECTED);
				}else {
					// mListener.connectStatus(-1);
				}
			}

		}, 5*1000);
	}

	public boolean isConnect() {
		if(mConnector!=null && mConnector.isConnect()) {
			// 如果已经连接上,断开重连,状态机下很难遇到
			return true;
		}
		return false;
	}

}
