package lxy.liying.hdtvneu.service.callback;


import lxy.liying.hdtvneu.domain.ReviewList;

/**
 * =======================================================
 * 作者：liying
 * 日期：2016/5/16 21:54
 * 版本：1.0
 * 描述：获取回看节目列表的回调
 * 备注：
 * =======================================================
 */
public interface On_NEU_GetReviewProgramsCallback {
    void onGetReviewPrograms(ReviewList reviewList);
}
