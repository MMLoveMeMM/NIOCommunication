package cn.pumpkin.niocommunication.server;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import org.jetbrains.annotations.Nullable;

import java.io.IOException;

/**
 * @author: zhibao.Liu
 * @version:
 * @date: 2018/7/3 10:16
 * @des:
 * @see {@link }
 */

public class NIOService extends Service {

    private NIOServer mNioServer;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mNioServer=new NIOServer(9987);
        try {
            mNioServer.listen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }
}
