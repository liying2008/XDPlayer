package lxy.liying.hdtvneu.service.callback;

import java.util.List;

import lxy.liying.hdtvneu.domain.BiliVideo;

/**
 * =======================================================
 * 版权：©Copyright LiYing 2015-2016. All rights reserved.
 * 作者：liying
 * 日期：2016/8/22 23:14
 * 版本：1.0
 * 描述：获取Bilibili视频的监听回调
 * 备注：
 * =======================================================
 */
public interface OnGetBiliVideoCallback {
    void getAllVideo(List<BiliVideo> biliVideos);
    void onFailure();
    void noMore();
}
