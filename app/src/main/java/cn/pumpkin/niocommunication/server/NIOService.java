package cn.pumpkin.niocommunication.server;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import org.jetbrains.annotations.Nullable;

import java.io.IOException;

import cn.pumpkin.niocommunication.aidl.INIOServerServiceInterface;

/**
 * @author: zhibao.Liu
 * @version:
 * @date: 2018/7/3 10:16
 * @des:
 * @see {@link }
 */

public class NIOService extends Service {

    private static final String TAG=NIOService.class.getName();
    private static final int  NIO_TCP_PORT= 9987;
    private NIOServer mNioServer;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG,"server bind service OK");
        return stub;
    }

    public INIOServerServiceInterface.Stub stub=new INIOServerServiceInterface.Stub(){
        @Override
        public void startWork(int port) throws RemoteException {
            Log.i(TAG,"server start work port : "+port);
            mNioServer=new NIOServer(port);
            try {
                mNioServer.listen();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };


    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }
}
