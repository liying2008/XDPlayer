package lxy.liying.hdtvneu.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import java.util.List;

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
public class BiliAcFunVideoPlayer extends PlayerActivity implements MediaPlayer.OnPreparedListener, MediaPlayer.OnSeekCompleteListener {
    private static final String TAG = "BiliAcFunVideoPlayer";
    private long lastPosition = 0L;
    private String path;
    /** 当前播放段落 */
    private int currentSection = 0;
    /** 总视频段落 */
    private int totalSection = 0;
    /** 视频下载地址 */
    private List<String> urls;

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
        urls = intentData.getStringArrayListExtra(Constants.VIDEO_URL);

        totalSection = urls.size();
//        if (totalSection == 0) {
//            App.getInstance().showToast("视频播放URL不正确", Toast.LENGTH_LONG);
//            return;
//        }
        mVideoView.setOnPreparedListener(this);
        mVideoView.setOnSeekCompleteListener(this);
        mVideoView.setVideoURI(Uri.parse(urls.get(0)));
        if (!mVideoView.isPlaying()) {
            mVideoView.start();
        }
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
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (currentSection < totalSection - 1) {
            // 继续播放下一段
            currentSection++;
            mVideoView.setVideoURI(Uri.parse(urls.get(currentSection)));
            if (!mVideoView.isPlaying()) {
                mVideoView.start();
            }
        } else {
            super.onCompletion(mp);
        }
    }
}
