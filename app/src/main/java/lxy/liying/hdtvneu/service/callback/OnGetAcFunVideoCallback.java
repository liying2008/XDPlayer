package lxy.liying.hdtvneu.service.callback;

import java.util.List;

import lxy.liying.hdtvneu.domain.AcFunVideo;

/**
 * =======================================================
 * 版权：©Copyright LiYing 2015-2016. All rights reserved.
 * 作者：liying
 * 日期：2016/9/8 10:07
 * 版本：1.0
 * 描述：获取AcFun视频的监听回调
 * 备注：
 * =======================================================
 */
public interface OnGetAcFunVideoCallback {
    void getAllVideo(List<AcFunVideo> acFunVideos);
    void onFailure();
    void noMore();
}
