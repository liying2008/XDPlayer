package lxy.liying.hdtvneu.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import cc.duduhuo.applicationtoast.AppToast;
import io.vov.vitamio.MediaPlayer;
import lxy.liying.hdtvneu.R;
import lxy.liying.hdtvneu.domain.XDVideo;
import lxy.liying.hdtvneu.utils.Constants;

/**
 * =======================================================
 * 版权：Copyright LiYing 2015-2016. All rights reserved.
 * 作者：liying
 * 日期：2016/5/17 21:31
 * 版本：1.0
 * 描述：在线视频播放Activity
 * 备注：
 * =======================================================
 */
public class OnlineVideoPlayer extends PlayerActivity implements MediaPlayer.OnPreparedListener, MediaPlayer.OnSeekCompleteListener {
    private static final String TAG = "OnlineVideoPlayer";
    private long lastPosition = 0L;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
//        initView();
        initData();
    }

    /**
     * 初始化界面
     */
    private void initView() {
        setContentView(R.layout.activity_video_player);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        Intent intentData = getIntent();
        String url = intentData.getStringExtra(Constants.VIDEO_URL);

        if ("".equals(url)) {
            AppToast.showToast("视频播放URL不正确", Toast.LENGTH_LONG);
            return;
        }
        mVideoView.setOnPreparedListener(this);
        mVideoView.setOnSeekCompleteListener(this);
        mVideoView.setVideoURI(Uri.parse(url));
    }

    @Override
    public void playNewVideo(XDVideo xdVideo) {
        super.playNewVideo(xdVideo);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.setPlaybackSpeed(1.0f);
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {
        Log.i(TAG, "onSeekComplete = " + mVideoView.getCurrentPosition());
    }
}
