package lxy.liying.hdtvneu.service.task;

import lxy.liying.hdtvneu.domain.ReviewList;

/**
 * =======================================================
 * 作者：liying
 * 日期：2016/5/16 23:11
 * 版本：1.0
 * 描述：NEU HDTV 获取回看节目列表
 * 备注：
 * =======================================================
 */
public interface NEU_ReviewProgramsInfo {
    /**
     * 获取回看节目列表
     * @param p 频道
     * @return 回看节目列表
     */
    ReviewList getReviewProgramsInfo(String p);
}
