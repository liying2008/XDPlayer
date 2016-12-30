package lxy.liying.hdtvneu.service.task;

import java.util.List;

import lxy.liying.hdtvneu.domain.AsyncResult;
import lxy.liying.hdtvneu.domain.AsyncSingleTask;
import lxy.liying.hdtvneu.domain.Program;
import lxy.liying.hdtvneu.service.callback.On_NEU_GetAllProgramsCallback;

/**
 * 异步获取所有正常节目任务
 */
public class NEU_GetAllProgramsTask extends AsyncSingleTask<List<Program>> {
    private static final String TAG = "NEU_GetAllProgramsTask";
    private On_NEU_GetAllProgramsCallback onGetAllProgramsCallback;

    public NEU_GetAllProgramsTask(On_NEU_GetAllProgramsCallback onGetAllProgramsCallback) {
        this.onGetAllProgramsCallback = onGetAllProgramsCallback;
    }

    @Override
    protected AsyncResult<List<Program>> doInBackground(AsyncResult<List<Program>> asyncResult) {
        List<Program> programs;
        NEU_AllProgramsInfo programsInfo = new NEU_AllProgramsInfoImpl();
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
