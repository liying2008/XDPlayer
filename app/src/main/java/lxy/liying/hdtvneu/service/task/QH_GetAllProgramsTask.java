package lxy.liying.hdtvneu.service.task;

import java.util.List;

import lxy.liying.hdtvneu.domain.AsyncResult;
import lxy.liying.hdtvneu.domain.AsyncSingleTask;
import lxy.liying.hdtvneu.domain.Program;
import lxy.liying.hdtvneu.service.callback.On_QH_GetAllProgramsCallback;

/**
 * 异步获取所有正常节目任务
 */
public class QH_GetAllProgramsTask extends AsyncSingleTask<List<Program>> {
    private static final String TAG = "QH_GetAllProgramsTask";
    private On_QH_GetAllProgramsCallback onGetAllProgramsCallback;

    public QH_GetAllProgramsTask(On_QH_GetAllProgramsCallback onGetAllProgramsCallback) {
        this.onGetAllProgramsCallback = onGetAllProgramsCallback;
    }

    @Override
    protected AsyncResult<List<Program>> doInBackground(AsyncResult<List<Program>> asyncResult) {
        List<Program> programs;
        QH_AllProgramsInfo programsInfo = new QH_AllProgramsInfoImpl();
        programs = programsInfo.getAllPrograms();
//        Log.i(TAG, "节目单：" + programs);
        asyncResult.setData(programs);
        return asyncResult;
    }

    @Override
    protected void runOnUIThread(AsyncResult<List<Program>> asyncResult) {
        onGetAllProgramsCallback.onGetAllPrograms(asyncResult.getData());
    }
}
