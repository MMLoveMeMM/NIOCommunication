package cn.pumpkin.niocommunication.client;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;

import cn.pumpkin.niocommunication.client.iface.ICompleteListener;
import cn.pumpkin.niocommunication.client.iface.ISendCallBackListener;
import cn.pumpkin.niocommunication.client.msg.MsgRequest;
import cn.pumpkin.niocommunication.client.nio.net.TCPConnector;
import cn.pumpkin.niocommunication.client.nio.TCPProxy;

/**
 * @author: zhibao.Liu
 * @version:
 * @date: 2018/7/3 11:27
 * @des:
 * @see {@link }
 */

public class NIOClient implements ICompleteListener<String> {

    private TCPConnector mConnector;

    private TCPProxy mTCPProxy;

    private ICompleteListener mListener=new ICompleteListener() {

        @Override
        public void onComplete(Object code) {
            // TODO Auto-generated method stub
            System.out.println("------------------");
        }

    };

    private ISendCallBackListener mCBListener=new ISendCallBackListener() {

        @Override
        public void onComplete(Object msg, int code) {
            // TODO Auto-generated method stub
            System.out.println("***************** code : "+code);
        }

    };
    private ISendCallBackListener mCBListener0=new ISendCallBackListener() {

        @Override
        public void onComplete(Object msg, int code) {
            // TODO Auto-generated method stub
            System.out.println("*****************000 code : "+code);
        }

    };

    public NIOClient(String ip,int port) throws IOException {

        mTCPProxy=new TCPProxy(ip,port);
        mTCPProxy.addCompleteListener(this);
        mTCPProxy.doWork();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

		/*MsgRequest msg=new MsgRequest();
		msg.setContent("11hello-world");
		msg.setDuration(3000);
		msg.setTimeout(3000);
		msg.setReq_id(1001);
		msg.setPriority(10);
		msg.setListener(mListener);
		msg.setCallbacklistener(mCBListener);
		mTCPProxy.pushMessage(msg);	*/

		/*MsgRequest msg1=new MsgRequest();
		msg1.setContent("22hello-world");
		msg1.setDuration(5000);
		msg1.setTimeout(5000);
		msg1.setReq_id(1002);
		msg1.setPriority(9);
		msg1.setListener(mListener);
		msg1.setCallbacklistener(mCBListener0);
		mTCPProxy.pushMessage(msg1);

		MsgRequest msg12=new MsgRequest();
		msg12.setContent("33hello-world");
		msg12.setDuration(50000);
		msg12.setTimeout(50000);
		msg12.setReq_id(1003);
		msg12.setPriority(8);
		msg12.setListener(mListener);
		msg12.setCallbacklistener(mCBListener0);
		mTCPProxy.pushMessage(msg12);*/

		/*mSendQueue=new PriorityBlockingQueue();
		try {
			mConnector=new TCPConnector("127.0.0.1",8000);
			mConnector.createLink("127.0.0.1", 8000);
			// mConnector.checkConnect();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		mReadTask=new ReadTask(mConnector,mSendQueue);
		mReadTask.registerListener(this);
		mReadTask.readWork();

		mWriteTask=new WriteTask(mConnector,mSendQueue);


		MsgRequest msg=new MsgRequest();
		msg.setContent("hello-world");
		mWriteTask.pushQueue(msg);
		mWriteTask.sendData();*/
        while(true) {
            try {
                Thread.sleep(5000);
                MsgRequest msg=new MsgRequest();
                msg.setInqueuetime(System.currentTimeMillis());
                msg.setBody("11hello-world\n");
                msg.setTimeout(3000);
                msg.setReq_id(1001);
                msg.setPriority(10);
                msg.setListener(mListener);
                msg.setCallbacklistener(mCBListener);
                mTCPProxy.pushMessage(msg);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onComplete(String code) {
        // TODO Auto-generated method stub
        // System.out.println("main receiver : "+code);
    }
}
