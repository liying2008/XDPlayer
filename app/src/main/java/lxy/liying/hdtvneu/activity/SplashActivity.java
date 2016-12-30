package lxy.liying.hdtvneu.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import lxy.liying.hdtvneu.R;
import lxy.liying.hdtvneu.app.App;
import lxy.liying.hdtvneu.utils.Constants;

/**
 * =======================================================
 * 版权：Copyright LiYing 2015-2016. All rights reserved.
 * 作者：liying
 * 日期：2016/5/19 15:32
 * 版本：1.0
 * 描述：启动界面
 * 备注：
 * =======================================================
 */
public class SplashActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                saveVersion();
                loadSettingsInfo();
                jump();
            }
        }, 600);   // 停留时间600ms
    }

    /**
     * 加载一些设置信息
     */
    private void loadSettingsInfo() {
        App.hasVideoListCache = App.getInstance().getSetting(Constants.VIDEO_LIST_CACHE, "false");
    }

    /**
     * 跳转到NEU_PListActivity
     */
    private void jump() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    /** 保存当前版本号 */
    private void saveVersion() {
        int currentVersion = App.getInstance().getVersionCode();
        String lastVersionStr = App.getInstance().getSetting(Constants.VERSION, "0");
        int lastVersion = Integer.parseInt(lastVersionStr);
        if (currentVersion > lastVersion) {
            //如果当前版本大于上次版本，保存当前版本号
            App.getInstance().putSetting(Constants.VERSION, String.valueOf(currentVersion));
        }
    }

    @Override
    public void onBackPressed() {
        //
    }
}
