package lxy.liying.hdtvneu.service.callback;

import java.util.List;

import lxy.liying.hdtvneu.domain.MarkItem;

/**
 * =======================================================
 * 作者：liying
 * 日期：2016/9/11 12:50
 * 版本：1.0
 * 描述：获取收藏视频回调接口
 * 备注：
 * =======================================================
 */
public interface OnGetMarkVideosCallback {
    void onGetIPv6Mark(List<MarkItem> ipv6MarkItems);
    void onGetBiliMark(List<MarkItem> biliMarkItems);
    void onGetLocalMark(List<MarkItem> localMarkItems);
    void onGetOnlineMark(List<MarkItem> onlineMarkItems);
    void onNull();
}
