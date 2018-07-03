package cn.pumpkin.niocommunication.client.iface;
/*
 * ר�������쳣�ص���������
 * ����״̬��,�ָ�TCP����
 * */
public interface IExceptionListener<T> {
	void onError(T code);
}
