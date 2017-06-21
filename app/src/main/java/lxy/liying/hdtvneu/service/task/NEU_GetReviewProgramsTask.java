package lxy.liying.hdtvneu.service.task;

import lxy.liying.hdtvneu.domain.AsyncResult;
import lxy.liying.hdtvneu.domain.AsyncSingleTask;
import lxy.liying.hdtvneu.domain.ReviewList;
import lxy.liying.hdtvneu.service.callback.On_NEU_GetReviewProgramsCallback;

/**
 * =======================================================
 * 作者：liying
 * 日期：2016/5/16 23:06
 * 版本：1.0
 * 描述：
 * 备注：
 * =======================================================
 */
public class NEU_GetReviewProgramsTask extends AsyncSingleTask<ReviewList> {
    private String p;
    private On_NEU_GetReviewProgramsCallback onNEUGetReviewProgramsCallback;

    /**
     * 构造器
     * @param p 频道
     * @param onNEUGetReviewProgramsHtmlCallback 回调接口
     */
    public NEU_GetReviewProgramsTask(String p, On_NEU_GetReviewProgramsCallback onNEUGetReviewProgramsHtmlCallback) {
        this.p = p;
        this.onNEUGetReviewProgramsCallback = onNEUGetReviewProgramsHtmlCallback;
    }

    @Override
    protected AsyncResult<ReviewList> doInBackground(AsyncResult<ReviewList> asyncResult) {
        NEU_ReviewProgramsInfo reviewProgramsInfo = new NEU_ReviewProgramsInfoImpl();
        ReviewList reviewList = reviewProgramsInfo.getReviewProgramsInfo(p);
        asyncResult.setData(reviewList);
        return asyncResult;
    }

    @Override
    protected void runOnUIThread(AsyncResult<ReviewList> asyncResult) {
        onNEUGetReviewProgramsCallback.onGetReviewPrograms(asyncResult.getData());
    }
}
