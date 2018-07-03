package cn.pumpkin.niocommunication.client.timer;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.TimerTask;

import cn.pumpkin.niocommunication.client.constants.Status;
import cn.pumpkin.niocommunication.client.iface.IConnectListener;

/*
 * 定时检查网络情况
 * */
public class NetTimerTask extends TimerTask {

	private IConnectListener mListener;

	public void registerListener(IConnectListener listener) {
		mListener=listener;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		// 获取IP地址
		InetAddress ipaddress;
		try
		{
			ipaddress=InetAddress.getLocalHost();

			System.out.println("本机的IP = " + ipaddress.getHostAddress());
		} catch (UnknownHostException e)
		{
			e.printStackTrace();
		}

		mListener.onStatusChanged(Status.NET_STATUS_OK);
	}

}
