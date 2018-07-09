package cn.pumpkin.niocommunication.client.proxy;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

import cn.pumpkin.niocommunication.aidl.INIOClientServiceInterface;

/**
 * @author: zhibao.Liu
 * @version:
 * @date: 2018/7/9 16:03
 * @des:
 * @see {@link }
 */

public class NIOClientProxy implements ServiceConnection {

    private static final NIOClientProxy instance = new NIOClientProxy();

    private INIOClientServiceInterface service;

    public static NIOClientProxy getInstance() {
        return instance;
    }

    private NIOClientProxy() {
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder srv) {
        service = INIOClientServiceInterface.Stub.asInterface(srv);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        if(service!=null){
            service=null;
        }
    }

    public void startWork(String ip,int port){
        if(service!=null){
            try {
                service.startWork(ip,port);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

}
