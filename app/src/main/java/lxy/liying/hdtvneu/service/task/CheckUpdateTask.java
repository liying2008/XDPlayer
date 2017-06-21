package lxy.liying.hdtvneu.service.task;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import org.json.JSONObject;

import lxy.liying.hdtvneu.app.App;
import lxy.liying.hdtvneu.domain.UpdateMsg;
import lxy.liying.hdtvneu.service.callback.OnCheckUpdateCallback;
import lxy.liying.hdtvneu.utils.CommonUtils;

/**
 * =======================================================
 * 作者：liying
 * 日期：2016/9/28 0:21
 * 版本：1.0
 * 描述：检查更新任务类
 * 备注：
 * =======================================================
 */

public class CheckUpdateTask extends AsyncTask<Void, Void, Void> {
    private static final String PATH = "http://duduhuo.cc/ddh/check_update.php?appname=xdplayer";
    private static final String ENCODING = "UTF-8";
    private static final int NO_UPDATE = 0x0000;
    private static final int UPDATE = 0x0001;
    private static final int NULL = 0x0002;
    private OnCheckUpdateCallback callback;
    private UpdateMsg updateMsg;
    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == NO_UPDATE) {
                callback.onNoUpdate();
            } else if (msg.what == UPDATE) {
                callback.onUpdate(updateMsg);
            } else if (msg.what == NULL) {
                callback.onNull();
            }
            return false;
        }
    });
    public CheckUpdateTask(OnCheckUpdateCallback callback) {
        this.callback = callback;
    }
    @Override
    protected Void doInBackground(Void... params) {
        String html = CommonUtils.getHtml(PATH, ENCODING);
        try {
            if ("null".equals(html) || "".equals(html)) {
                mHandler.sendEmptyMessage(NULL);
            } else {
                JSONObject json = new JSONObject(html);
                // 获取服务器上的版本号
                int serverVersion = json.getInt("version");
                // 获取本地版本号
                int localVersion = App.getInstance().getVersionCode();
                // 比较版本号
                if (serverVersion > localVersion) {
                    // 有新版本
                    updateMsg = new UpdateMsg();
                    int code = json.getInt("code");
                    String name = json.getString("name");
                    String versionName = json.getString("versionName");
                    String size = json.getString("size");
                    String updateDate = json.getString("updateDate");
                    String platform = json.getString("platform");
                    String updateLog = json.getString("updateLog");
                    String channel = json.getString("channel");
                    String downloadUrl = json.getString("downloadUrl");
                    updateMsg.setCode(code);
                    updateMsg.setName(name);
                    updateMsg.setVersion(serverVersion);
                    updateMsg.setVersionName(versionName);
                    updateMsg.setSize(size);
                    updateMsg.setUpdateDate(updateDate);
                    updateMsg.setPlatform(platform);
                    updateMsg.setUpdateLog(updateLog);
                    updateMsg.setChannel(channel);
                    updateMsg.setDownloadUrl(downloadUrl);
                    mHandler.sendEmptyMessage(UPDATE);
                } else {
                    // 本地已是最新版本
                    mHandler.sendEmptyMessage(NO_UPDATE);
                }
            }
        } catch (Exception e) {
            mHandler.sendEmptyMessage(NULL);
            e.printStackTrace();
        }
        return null;
    }
}
