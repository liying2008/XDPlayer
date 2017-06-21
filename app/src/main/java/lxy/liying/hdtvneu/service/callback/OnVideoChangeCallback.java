package lxy.liying.hdtvneu.service.callback;

import android.net.Uri;

import lxy.liying.hdtvneu.domain.XDVideo;

/**
 * =======================================================
 * 作者：liying
 * 日期：2016/8/21 18:22
 * 版本：1.0
 * 描述：更改播放的视频
 * 备注：
 * =======================================================
 */
public interface OnVideoChangeCallback {
    /**
     * 播放新视频
     * @param xdVideo
     */
    void playNewVideo(XDVideo xdVideo);

    /**
     * 播放新节目
     * @param uri
     */
    void playNewProgram(Uri uri);
}
