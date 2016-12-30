package lxy.liying.hdtvneu.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import cc.duduhuo.applicationtoast.AppToast;
import io.vov.vitamio.MediaPlayer;
import lxy.liying.hdtvneu.utils.Constants;

/**
 * =======================================================
 * 版权：Copyright LiYing 2015-2016. All rights reserved.
 * 作者：liying
 * 日期：2016/5/17 21:31
 * 版本：1.0
 * 描述：流媒体播放Activity
 * 备注：
 * =======================================================
 */
public class M3U8Player extends PlayerActivity implements MediaPlayer.OnPreparedListener{
    private static final String TAG = "M3U8Player";
    // TODO: 2016/5/18 电视节目回看不支持Seek

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        initView();
        initData();
    }

    /**
     * 初始化界面
     */
    private void initView() {
    }

    /**
     * 初始化数据
     */
    private void initData() {
        mVideoView.setOnPreparedListener(this);
        Intent intentData = getIntent();
        String path;
        Uri data = intentData.getData();
        if (data != null) {
            // 从外部调用或从收藏调用
            mVideoView.setVideoURI(data);
        } else {
            path = intentData.getStringExtra(Constants.PROGRAM);
            if ("".equals(path)) {
                AppToast.showToast("视频播放URL不正确", Toast.LENGTH_LONG);
                return;
            }
            mVideoView.setVideoURI(Uri.parse(path));
        }
    }

    @Override
    public void playNewProgram(Uri uri) {
        super.playNewProgram(uri);
        mVideoView.setVideoURI(uri);
    }

    @Override
    public void onPrepared(MediaPlayer mp) {

    }
}
