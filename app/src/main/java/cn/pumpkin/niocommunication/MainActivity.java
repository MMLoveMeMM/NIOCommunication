package cn.pumpkin.niocommunication;

import android.app.Activity;
import android.os.Bundle;

import cn.pumpkin.niocommunication.client.proxy.NIOClientProxy;
import cn.pumpkin.niocommunication.server.proxy.NIOServerProxy;

public class MainActivity extends Activity {

    private NIOServerProxy mNIOServerProxy;
    private NIOClientProxy mNIOClientProxy;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    public void init(){

        mNIOClientProxy=new NIOClientProxy();
        //mNIOClientProxy.init(getApplicationContext(),null,null);
        //mNIOClientProxy.onBindService();

        mNIOServerProxy=new NIOServerProxy();
        mNIOServerProxy.init(getApplicationContext(),null,null);
        mNIOServerProxy.onBindService();

    }

    @Override
    protected void onResume() {
        super.onResume();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // mNIOClientProxy.startWork("127.0.0.1",9987);
                mNIOServerProxy.startWork(9987);

            }
        }).start();

    }
}
