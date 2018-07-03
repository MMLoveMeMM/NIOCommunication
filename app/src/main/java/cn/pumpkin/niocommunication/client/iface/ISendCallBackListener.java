package cn.pumpkin.niocommunication.client.iface;

public interface ISendCallBackListener<T> {
	void onComplete(T msg, int code);
}
