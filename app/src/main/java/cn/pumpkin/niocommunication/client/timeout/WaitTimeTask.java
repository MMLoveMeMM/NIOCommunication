package cn.pumpkin.niocommunication.client.timeout;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.DelayQueue;

import cn.pumpkin.niocommunication.client.constants.Status;
import cn.pumpkin.niocommunication.client.iface.ISendCallBackListener;

public class WaitTimeTask implements Runnable{

	private DelayQueue<DelayRequest> queue = new DelayQueue<DelayRequest>();

	private Map<Integer, ISendCallBackListener> mCallbackContainer;

	private Thread mWaitTimeThread;

	public WaitTimeTask() {
		mWaitTimeThread=new Thread(this);
		mCallbackContainer = new ConcurrentHashMap<>();
	}

	public void pushDelayQueue(DelayRequest req) {
		if(req!=null) {
			// 加入队列
			//synchronized(this) {
			queue.offer(req);
			if(req.getListener()!=null) {
				// 这个必须要回调
				mCallbackContainer.put(req.getReq_id(), req.getListener());
			}
			//}
		}
	}

	public void removeDelayQueue(int req_id) {
		if(queue!=null && !queue.isEmpty()) {
			if(!mCallbackContainer.isEmpty()) {
				System.out.println("remove 111 req_id : "+req_id);
				if(mCallbackContainer.containsKey(req_id)) {
					//说明数据收发正常
					ISendCallBackListener listener=mCallbackContainer.get(req_id);
					if(listener!=null) {
						listener.onComplete(null, /*req_id*/Status.SEND_TIMEOUT_OK_CODE);
					}
					//移除req_id这条
					mCallbackContainer.remove(req_id);
				}
			}
		}
	}

	public void doWork() {
		mWaitTimeThread.start();
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(true){
			try {
				if(queue!=null && !queue.isEmpty() && !mCallbackContainer.isEmpty()) {
					DelayRequest req = queue.take();// 如果时间没到这个地方会阻塞,这个机制挺牛逼的
					System.out.println("remove 999 req_id : "+req.getReq_id());
					// 回调会去
					if(mCallbackContainer!=null) {
						ISendCallBackListener listener=mCallbackContainer.get(req.getReq_id());
						if(listener!=null) {
							listener.onComplete(req, Status.SEND_TIMEOUT_ERROR_CODE);
						}
						mCallbackContainer.remove(req.getReq_id());
					}
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void destroy() {
		if(mWaitTimeThread!=null && mWaitTimeThread.isAlive()) {
			mWaitTimeThread.stop();
			mWaitTimeThread=null;
		}
	}

}
