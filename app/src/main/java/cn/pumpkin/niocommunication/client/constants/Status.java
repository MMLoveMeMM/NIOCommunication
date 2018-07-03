package cn.pumpkin.niocommunication.client.constants;

/*
 * ����һ��״̬��
 * */
public class Status {
	/*
	 * �豸����״̬
	 * */
	public final static int NET_STATUS_FAIL=-5;
	public final static int NET_STATUS_CHECKING=-4;
	public final static int NET_STATUS_OK=-3;
	/*
	 * ����״̬
	 * */
	public final static int NIO_NO_CONNECTION_EXCEPTION=-2;
	public final static int NIO_READY_CONNECT=-1;
	public final static int NIO_CONNECTING=0;
	public final static int NIO_CONNECTED=1;
	
	/*
	 * ����״̬
	 * */
	public final static int ROUTER_UNLOGIN=2;
	public final static int ROUTER_LOGINING=3;
	public final static int ROUTER_LOGINED=4;
	
	/*
	 * ������д
	 * */
	public final static int NET_DATA=5;
	
	/*
	 * ���ͳ�ʱ
	 * */
	public final static int SEND_TIMEOUT_ERROR_CODE=-8000;
	
	public final static int SEND_TIMEOUT_OK_CODE=0;
	
}
