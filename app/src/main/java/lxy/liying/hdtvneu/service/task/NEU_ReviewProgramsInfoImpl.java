package lxy.liying.hdtvneu.service.task;

import lxy.liying.hdtvneu.domain.ReviewList;
import lxy.liying.hdtvneu.regex.NEU_RegexReviewHtml;
import lxy.liying.hdtvneu.utils.CommonUtils;

/**
 * =======================================================
 * 作者：liying
 * 日期：2016/5/16 23:14
 * 版本：1.0
 * 描述：获取回看节目网页
 * 备注：
 * =======================================================
 */
public class NEU_ReviewProgramsInfoImpl implements NEU_ReviewProgramsInfo {
    @Override
    public ReviewList getReviewProgramsInfo(String p) {
        // http://hdtv.neu6.edu.cn/time-select?p=cctv5hd
        String html = CommonUtils.getHtml("http://hdtv.neu6.edu.cn/time-select?p=" + p, "UTF-8");
        NEU_RegexReviewHtml regexReviewHtml = new NEU_RegexReviewHtml();
        ReviewList reviewList = regexReviewHtml.getReviewPrograms(html);
        return reviewList;
    }
}
