package lxy.liying.hdtvneu.service.callback;

import lxy.liying.hdtvneu.domain.UpdateMsg;

/**
 * =======================================================
 * 版权：©Copyright LiYing 2015-2016. All rights reserved.
 * 作者：liying
 * 日期：2016/9/28 0:23
 * 版本：1.0
 * 描述：检查更新回调接口
 * 备注：
 * =======================================================
 */

public interface OnCheckUpdateCallback {
    /**
     * 有更新
     * @param msg 更新信息
     */
    void onUpdate(UpdateMsg msg);

    /**
     * 无更新
     */
    void onNoUpdate();

    /**
     * 检查更新失败
     */
    void onNull();
}
