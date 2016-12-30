package lxy.liying.hdtvneu.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import cc.duduhuo.applicationtoast.AppToast;
import io.vov.vitamio.MediaPlayer;
import lxy.liying.hdtvneu.app.App;
import lxy.liying.hdtvneu.domain.XDVideo;
import lxy.liying.hdtvneu.utils.Constants;

/**
 * =======================================================
 * 版权：Copyright LiYing 2015-2016. All rights reserved.
 * 作者：liying
 * 日期：2016/5/17 21:31
 * 版本：1.0
 * 描述：本地视频播放Activity
 * 备注：
 * =======================================================
 */
public class LocalVideoPlayer extends PlayerActivity implements MediaPlayer.OnPreparedListener, MediaPlayer.OnSeekCompleteListener {
    private static final String TAG = "LocalVideoPlayer";
    private XDVideo xdVideo;
    private long lastPosition = 0L;
    private String path;
    /** 是否是外部调用 */
    private static boolean isOuter = false;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        initData();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        Intent intentData = getIntent();
        Uri data = intentData.getData();
        mVideoView.setOnPreparedListener(this);
        mVideoView.setOnSeekCompleteListener(this);
        if (data != null) {
            // 从外部调用XDPlayer
            isOuter = true;
            path = intentData.getDataString();
            mVideoView.setVideoURI(data);
            // FIXME: 2016/9/27 外部调用：content://com.nll.screenrecorder.provider.AttachmentProvider/download/screenrecorder.20160927070549.mp4 类似Uri无法播放
//            Log.i(TAG, "外部调用：" + path);
        } else {
            // 程序内调用
            isOuter = false;
            xdVideo = (XDVideo) intentData.getSerializableExtra(Constants.XDVIDEO);
            path = xdVideo.getData();
            lastPosition = App.xdService.getPosition(xdVideo.get_id());
            mVideoView.setVideoPath(path);
        }
        if ("".equals(path)) {
            AppToast.showToast("视频播放Path不正确", Toast.LENGTH_LONG);
            return;
        }
        // TODO: 2016/8/21 Seek 不能使用
        // FIXME: 2016/8/21
    }

    /**
     * 读取上次播放进度
     */
    private void seek() {
        if (lastPosition == 0L) {
        } else {

//            AlertDialog.Builder builder = new AlertDialog.Builder(this);
//            builder.setMessage("从上次结束的位置开始播放？");
//            final long finalLastPosition = lastPosition;
//            builder.setPositiveButton("继续播放", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//
//                }
//            });
//            builder.setNegativeButton("重新开始", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//
//                }
//            });
//            AlertDialog dialog = builder.create();
//            dialog.setCanceledOnTouchOutside(false);
//            dialog.show();
        }
    }

    @Override
    public void playNewVideo(XDVideo xdVideo) {
        super.playNewVideo(xdVideo);
        if (!isOuter) {
            savePosition();
        }
        this.xdVideo = xdVideo;
        if (!isOuter) {
            // 保存当前播放的视频ID
            App.lastXdVideo = xdVideo;
            App.getInstance().putSetting(Constants.LAST_VIDEO_ID, String.valueOf(xdVideo.get_id()));
            // 通知LocalVideoListActivity刷新列表颜色
            setResult(Constants.REFRESH_LIST);
            mVideoView.setVideoPath(xdVideo.getData());
        }
    }

    @Override
    protected void onPause() {
        // 记录播放进度
        if (!isOuter) {
            savePosition();
        }
        super.onPause();
    }

    /**
     * 保存播放进度
     */
    protected void savePosition() {
        if (playComplete) {
            // 播放完毕，position置0
            App.xdService.updatePosition(xdVideo.get_id(), 0L);
        } else {
            // 播放中断，记录播放位置
            App.xdService.updatePosition(xdVideo.get_id(), mVideoView.getCurrentPosition());
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.setPlaybackSpeed(1.0f);
        mp.seekTo(lastPosition);
        Log.i(TAG, "seek to = " + lastPosition);
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {
        Log.i(TAG, "onSeekComplete = " + mVideoView.getCurrentPosition());
    }
}
