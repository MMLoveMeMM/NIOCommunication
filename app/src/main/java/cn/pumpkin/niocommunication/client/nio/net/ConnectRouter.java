package cn.pumpkin.niocommunication.client.nio.net;

import android.text.TextUtils;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import cn.pumpkin.niocommunication.client.constants.Status;
import cn.pumpkin.niocommunication.client.iface.IConnectListener;

/*
 * 这个类专门用于连接路由设备,连接成功后就返回给代理类继续进行其他开发
 * */
public class ConnectRouter {

	private IConnectListener mListener;

	public void registerListener(IConnectListener listener) {
		mListener=listener;
	}

	private String DEF_ROUTER_HOST_IP="127.0.0.1";
	private int DEF_ROUTER_HOST_PORT=9987;

	private TCPConnector mConnector;

	public ConnectRouter(String ip,int port) {

		if(!TextUtils.isEmpty(ip)){
			DEF_ROUTER_HOST_IP=ip;
		}
		if(port>0){
			DEF_ROUTER_HOST_PORT=port;
		}

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

		return false;
	}

	public boolean isConnect() {
		if(mConnector!=null && mConnector.isConnect()) {
			// 如果已经连接上,断开重连,状态机下很难遇到
			return true;
		}
		return false;
	}

}
