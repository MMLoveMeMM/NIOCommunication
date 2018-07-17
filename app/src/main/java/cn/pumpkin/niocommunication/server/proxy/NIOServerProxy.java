package cn.pumpkin.niocommunication.server.proxy;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.util.Log;

import cn.pumpkin.niocommunication.aidl.INIOServerServiceInterface;

/**
 * @author: zhibao.Liu
 * @version:
 * @date: 2018/7/9 16:03
 * @des:
 * @see {@link }
 */

public class NIOServerProxy implements ServiceConnection {

    private final static String TAG=NIOServerProxy.class.getName();
    private final static String SERVICE_DEFUALT_CLASSNAME = "cn.pumpkin.niocommunication.server.NIOService";

    private INIOServerServiceInterface service;
    private static String gPackageName;
    private static String gClassName;
    private Context gContext;

    public void init(Context context, Looper looper, String packageName) {
        gContext = context.getApplicationContext();
        gPackageName = (packageName == null ? context.getPackageName() : packageName);
        gClassName = SERVICE_DEFUALT_CLASSNAME;
    }


    @Override
    public void onServiceConnected(ComponentName name, IBinder srv) {
        service = INIOServerServiceInterface.Stub.asInterface(srv);
        Log.d(TAG,"server connected !");
        if(service!=null){
            Log.d(TAG,"server service connect OK !");
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        if(service!=null){
            service=null;
        }
    }

    public void onBindService() {

        if (service == null) {
            Intent iSrv = new Intent().setClassName(gPackageName, gClassName);
            if (!gContext.bindService(iSrv, this, Service.BIND_AUTO_CREATE)) {
                Log.e(TAG, "remote service bind failed");
            }
            return;
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
