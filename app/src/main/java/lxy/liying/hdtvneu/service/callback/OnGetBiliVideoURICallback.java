package lxy.liying.hdtvneu.service.callback;

import java.util.ArrayList;

/**
 * =======================================================
 * 版权：©Copyright LiYing 2015-2016. All rights reserved.
 * 作者：liying
 * 日期：2016/8/23 16:17
 * 版本：1.0
 * 描述：得到视频在线播放的地址的回调接口
 * 备注：
 * =======================================================
 */
public interface OnGetBiliVideoURICallback {
    void getURI(ArrayList<String> urls);
    void onUriFailure();
}
