package cn.pumpkin.niocommunication.client.iface;
/*
 * TCP��ȡ�߳�:��ȡ�����ݺ�����������
 * */
public interface IReadListener<T> {
	void onReceiver(T message, int code);
}
