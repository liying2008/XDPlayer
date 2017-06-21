package lxy.liying.hdtvneu.service.task;

import android.app.Activity;
import android.os.AsyncTask;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.target.Target;

import java.io.File;
import java.util.concurrent.ExecutionException;

import cc.duduhuo.applicationtoast.AppToast;
import lxy.liying.hdtvneu.utils.Constants;
import lxy.liying.hdtvneu.utils.FileUtils;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 日期：2017/6/20 22:51
 * 版本：1.0
 * 描述：保存Bilibili和AcFun视频封面的任务类
 * 备注：
 * =======================================================
 */
public class SaveCoverTask extends AsyncTask<String, Void, Boolean> {
    private static final String TAG = "SaveCoverTask";
    private Activity activity;

    public SaveCoverTask(Activity activity) {
        this.activity = activity;
    }

    @Override
    protected Boolean doInBackground(String... params) {
        RequestManager requestManager = Glide.with(activity);
        FutureTarget<File> target = requestManager.load(params[0]).downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);
        String coverPath = Constants.COVER + File.separator + "XD_" + System.currentTimeMillis() + ".jpg";
        try {
            File file = target.get();
            // 拷贝下载的头像文件到SD卡下的应用工作目录
            FileUtils.copyFile(file.getAbsolutePath(), coverPath);
            file.delete();
            return true;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    protected void onPostExecute(Boolean success) {
        super.onPostExecute(success);
        if (success) {
            AppToast.showToast("封面已保存至 " + Constants.COVER_NAME + " 目录下");
        } else {
            AppToast.showToast("封面保存失败");
        }
    }
}
