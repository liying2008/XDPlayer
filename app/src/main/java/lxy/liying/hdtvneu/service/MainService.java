package lxy.liying.hdtvneu.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;



/**
 * =======================================================
 * 版权：Copyright LiYing 2015-2016. All rights reserved.
 * 作者：liying
 * 日期：2016/5/15 15:54
 * 版本：1.0
 * 描述：
 * 备注：
 * =======================================================
 */
public class MainService extends Service {
    private MainBinder mainBinder;

    public IBinder onBind(Intent intent) {
        return mainBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mainBinder = new MainBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            handlerIntent(intent);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void handlerIntent(Intent intent) {
        //
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mainBinder.onDestroy();
    }
}
