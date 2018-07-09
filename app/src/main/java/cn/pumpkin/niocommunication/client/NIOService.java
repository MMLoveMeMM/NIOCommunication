package cn.pumpkin.niocommunication.client;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

import org.jetbrains.annotations.Nullable;

import java.io.IOException;

import cn.pumpkin.niocommunication.aidl.INIOClientServiceInterface;
import cn.pumpkin.niocommunication.aidl.INIOServerServiceInterface;

/**
 * @author: zhibao.Liu
 * @version:
 * @date: 2018/7/3 11:58
 * @des:
 * @see {@link }
 */

public class NIOService extends Service {

    private static final int NIO_PORT = 9987;
    private static final String NIO_IP="127.0.0.1";//如果是两台设备,这里就输入服务器的IP地址
    private NIOClient mNIOClient;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return stub;
    }

    public INIOClientServiceInterface.Stub stub=new INIOClientServiceInterface.Stub(){

        @Override
        public void startWork(String ip, int port) throws RemoteException {
            if(mNIOClient!=null){
                mNIOClient.init(ip,port);
                mNIOClient.doWork();
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        mNIOClient=new NIOClient();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }
}
