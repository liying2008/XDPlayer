package lxy.liying.hdtvneu.service;

import android.os.Binder;

import lxy.liying.hdtvneu.domain.AsyncResult;
import lxy.liying.hdtvneu.domain.AsyncSingleTask;
import lxy.liying.hdtvneu.service.callback.On_BY_GetAllProgramsCallback;
import lxy.liying.hdtvneu.service.callback.On_NEU_GetAllProgramsCallback;
import lxy.liying.hdtvneu.service.callback.On_NEU_GetReviewProgramsCallback;
import lxy.liying.hdtvneu.service.task.BY_GetAllProgramsTask;
import lxy.liying.hdtvneu.service.task.NEU_GetAllProgramsTask;
import lxy.liying.hdtvneu.service.task.NEU_GetReviewProgramsTask;

/**
 * =======================================================
 * 作者：liying
 * 日期：2016/5/15 15:54
 * 版本：1.0
 * 描述：
 * 备注：
 * =======================================================
 */
public class MainBinder extends Binder {

    /***
     * 东北大学IPv6视频直播测试站——获取所有正常节目
     * @param onGetAllProgramsCallback 回调接口
     */
    public void neu_getAllPrograms(On_NEU_GetAllProgramsCallback onGetAllProgramsCallback) {
        NEU_GetAllProgramsTask getAllPasswordTask = new NEU_GetAllProgramsTask(onGetAllProgramsCallback);
        getAllPasswordTask.execute();
    }

    /***
     * 获取回看节目列表
     * @param p 频道
     * @param onGetReviewProgramsCallback 回调接口
     */
    public void neu_getReviewProgramsHtml(String p, On_NEU_GetReviewProgramsCallback onGetReviewProgramsCallback) {
        NEU_GetReviewProgramsTask getReviewProgramsHtmlTask = new NEU_GetReviewProgramsTask(p, onGetReviewProgramsCallback);
        getReviewProgramsHtmlTask.execute();
    }

    /***
     * 北京邮电大学IPTV——获取所有节目
     * @param onGetAllProgramsCallback 回调接口
     */
    public void by_getAllPrograms(On_BY_GetAllProgramsCallback onGetAllProgramsCallback) {
        BY_GetAllProgramsTask getAllPasswordTask = new BY_GetAllProgramsTask(onGetAllProgramsCallback);
        getAllPasswordTask.execute();
    }

    void onDestroy() {
        new AsyncSingleTask<Void>() {
            @Override
            protected AsyncResult<Void> doInBackground(AsyncResult<Void> asyncResult) {
                return asyncResult;
            }

            @Override
            protected void runOnUIThread(AsyncResult<Void> asyncResult) {
            }
        }.execute();
    }
}
