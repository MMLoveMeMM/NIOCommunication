package cn.pumpkin.niocommunication.client.timeout;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

import cn.pumpkin.niocommunication.client.iface.ISendCallBackListener;

public class DelayRequest implements Delayed{

	private int req_id;
	private long insertTime;//插入时间
	private long delayTime;//超时时间
	private ISendCallBackListener listener;

	public ISendCallBackListener getListener() {
		return listener;
	}

	public void setListener(ISendCallBackListener listener) {
		this.listener = listener;
	}

	public int getReq_id() {
		return req_id;
	}

	public void setReq_id(int req_id) {
		this.req_id = req_id;
	}

	public long getInsertTime() {
		return insertTime;
	}

	public void setInsertTime(long insertTime) {
		this.insertTime = insertTime;
	}

	public long getDelayTime() {
		return delayTime;
	}

	public void setDelayTime(long delayTime) {
		this.delayTime = delayTime;
	}

	@Override
	public int compareTo(Delayed o) {
		// TODO Auto-generated method stub
		DelayRequest that = (DelayRequest) o;
		if (this.insertTime + this.delayTime > that.insertTime + that.delayTime) {
			return 1;
		} else if (this.insertTime + this.delayTime == that.insertTime + that.delayTime) {
			return 0;
		} else {
			return -1;
		}
	}

	@Override
	public long getDelay(TimeUnit unit) {
		// TODO Auto-generated method stub
		// System.out.println("timeout : "+(this.insertTime + delayTime - System.currentTimeMillis()));
		return 15000;//this.insertTime + delayTime - System.currentTimeMillis();
	}

}
