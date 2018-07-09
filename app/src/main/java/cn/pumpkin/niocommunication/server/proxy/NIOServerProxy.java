package cn.pumpkin.niocommunication.server.proxy;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

import cn.pumpkin.niocommunication.aidl.INIOClientServiceInterface;
import cn.pumpkin.niocommunication.aidl.INIOServerServiceInterface;

/**
 * @author: zhibao.Liu
 * @version:
 * @date: 2018/7/9 16:03
 * @des:
 * @see {@link }
 */

public class NIOServerProxy implements ServiceConnection {
    private static final NIOServerProxy instance = new NIOServerProxy();

    private INIOServerServiceInterface service;
    public static NIOServerProxy getInstance() {
        return instance;
    }

    private NIOServerProxy() {
    }


    @Override
    public void onServiceConnected(ComponentName name, IBinder srv) {
        service = INIOServerServiceInterface.Stub.asInterface(srv);
        if(service!=null){
            // instance.registerMediaCallBackFilter();
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        if(service!=null){
            service=null;
        }
    }

    public void startWork(int port){
        if(service!=null){
            try {
                service.startWork(port);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

}
