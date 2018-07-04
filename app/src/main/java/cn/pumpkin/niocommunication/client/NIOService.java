package cn.pumpkin.niocommunication.client;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import org.jetbrains.annotations.Nullable;

import java.io.IOException;

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
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        try {
            mNIOClient=new NIOClient(NIO_IP,NIO_PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }
}
