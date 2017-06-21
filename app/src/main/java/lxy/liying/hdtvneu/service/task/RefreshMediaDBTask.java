package lxy.liying.hdtvneu.service.task;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.Locale;

import lxy.liying.hdtvneu.fragment.LocalListFragment;

/**
 * =======================================================
 * 作者：liying
 * 日期：2016/9/9 14:42
 * 版本：1.0
 * 描述：刷新媒体数据库任务类
 * 备注：
 * =======================================================
 */
public class RefreshMediaDBTask extends AsyncTask<Void, Void, Void> {
    private static final String TAG = "RefreshMediaDBTask";
    private WeakReference<LocalListFragment> fragment;

    public RefreshMediaDBTask(WeakReference<LocalListFragment> fragment) {
        this.fragment = fragment;
    }

    @Override
    protected Void doInBackground(Void... params) {
        getAllFiles(Environment.getExternalStorageDirectory());
        return null;
    }

    // 遍历接收一个文件路径，然后把文件子目录中的所有文件遍历并输出来
    private void getAllFiles(File root) {
        File files[] = root.listFiles();
        if (files != null) {
            for (File f : files) {
                // 忽略.开头的文件（隐藏文件）
                if (f.isHidden()) {
                    continue;
                }
                if (f.isDirectory()) {
                    getAllFiles(f);
                } else {
                    String mime = getMimeType(f);
                    if (mime.startsWith("video")) {
                        scanVideos(f);
                    }
                }
            }
        }
    }

    private void scanVideos(File file) {
        Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        scanIntent.setData(Uri.fromFile(file));
        if (fragment.get() != null) {
            fragment.get().getActivity().sendBroadcast(scanIntent);
        }
    }

    private static String getSuffix(File file) {
        if (file == null || !file.exists() || file.isDirectory()) {
            return null;
        }
        String fileName = file.getName();
        if (fileName.equals("") || fileName.endsWith(".")) {
            return null;
        }
        int index = fileName.lastIndexOf(".");
        if (index != -1) {
            return fileName.substring(index + 1).toLowerCase(Locale.US);
        } else {
            return null;
        }
    }

    private static String getMimeType(File file) {
        String suffix = getSuffix(file);
        if (suffix == null) {
            return "file/*";
        }
        String type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(suffix);
        if (!TextUtils.isEmpty(type)) {
            return type;
        }
        return "file/*";
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (fragment.get() != null) {
            fragment.get().startScanMediaDB();
        }
    }
}
