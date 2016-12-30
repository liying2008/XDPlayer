package lxy.liying.hdtvneu.service.callback;

import java.util.List;

/**
 * =======================================================
 * 版权：©Copyright LiYing 2015-2016. All rights reserved.
 * 作者：liying
 * 日期：2016/8/24 0:32
 * 版本：1.0
 * 描述：得到Bilibili视频多P的名称
 * 备注：
 * =======================================================
 */
public interface OnGetBiliPName {
    /** 得到多P名称 */
    void getPNames(String av, List<String> pNames);
    /** 没有分P */
    void noP(String av);
}
