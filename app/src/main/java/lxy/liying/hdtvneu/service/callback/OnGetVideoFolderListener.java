package lxy.liying.hdtvneu.service.callback;

import java.util.List;

import lxy.liying.hdtvneu.domain.VideoFolder;

/**
 * =======================================================
 * 版权：©Copyright LiYing 2015-2016. All rights reserved.
 * 作者：liying
 * 日期：2016/8/17 15:02
 * 版本：1.0
 * 描述：获取视频文件夹监听回调
 * 备注：
 * =======================================================
 */
public interface OnGetVideoFolderListener {
    /** 得到一个VideoFolder */
    void onVideoFolderGot(VideoFolder album);
    /** 视频搜索完毕 */
    void onComplete(List<VideoFolder> videoFolderList);
}
