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
            mNIOClient=new NIOClient();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }
}
