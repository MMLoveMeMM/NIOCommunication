package cn.pumpkin.niocommunication.client;

import android.util.Log;

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

    private final static String TAG=NIOClient.class.getName();
    private TCPConnector mConnector;

    private TCPProxy mTCPProxy;

    private ICompleteListener mListener=new ICompleteListener() {

        @Override
        public void onComplete(Object code) {
            // TODO Auto-generated method stub
            Log.d(TAG,"------------------");
        }

    };

    private ISendCallBackListener mCBListener=new ISendCallBackListener() {

        @Override
        public void onComplete(Object msg, int code) {
            // TODO Auto-generated method stub
            Log.d(TAG,"***************** code : "+code);
        }

    };
    private ISendCallBackListener mCBListener0=new ISendCallBackListener() {

        @Override
        public void onComplete(Object msg, int code) {
            // TODO Auto-generated method stub
            Log.d(TAG,"*****************000 code : "+code);
        }

    };

    public void init(String ip,int port){
        Log.d(TAG,"客服端开始初始化");
        mTCPProxy=new TCPProxy(ip,port);
        mTCPProxy.addCompleteListener(this);
        Log.d(TAG,"客服端初始化完成");
    }

    public void doWork(){
        Log.d(TAG,"客服端开始运行");
        mTCPProxy.doWork();
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
