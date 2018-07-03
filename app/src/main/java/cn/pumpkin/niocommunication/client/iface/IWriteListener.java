package cn.pumpkin.niocommunication.client.iface;

public interface IWriteListener<T> {
	void onSuccess(T msg, int code);
}
