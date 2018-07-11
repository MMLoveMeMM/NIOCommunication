package cn.pumpkin.niocommunication;

import android.app.Activity;
import android.os.Bundle;

import cn.pumpkin.niocommunication.client.proxy.NIOClientProxy;
import cn.pumpkin.niocommunication.server.proxy.NIOServerProxy;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    public void init(){

        //NIOClientProxy.init(getApplicationContext(),null,null);
        //NIOClientProxy.getInstance().onBindService();

        NIOServerProxy.init(getApplicationContext(),null,null);
        NIOServerProxy.getInstance().onBindService();

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
                // NIOClientProxy.getInstance().startWork("127.0.0.1",9987);
                NIOServerProxy.getInstance().startWork(9987);

            }
        }).start();

    }
}
