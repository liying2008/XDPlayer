package lxy.liying.hdtvneu.utils;

import android.content.Context;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.DownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;

import java.util.Locale;

import cc.duduhuo.applicationtoast.AppToast;
import lxy.liying.hdtvneu.adapter.DownloadedListAdapter;
import lxy.liying.hdtvneu.adapter.DownloadingListAdapter;
import lxy.liying.hdtvneu.adapter.WaitDownloadListAdapter;
import lxy.liying.hdtvneu.app.App;
import lxy.liying.hdtvneu.domain.DownloadItem;
import lxy.liying.hdtvneu.fragment.DownloadingFragment;

/**
 * =======================================================
 * 作者：liying
 * 日期：2016/9/17 20:09
 * 版本：1.0
 * 描述：视频下载器
 * 备注：
 * =======================================================
 */
public class Downloader {
    private Context context;
    /** 下载文件名 */
    public String name;
    /** 视频来源 */
    public String from;
    /** 视频所在本地路径 */
    public String path;
    /** 该条目在RecyclerView中的位置 */
    private int position;
    private DownloadingListAdapter ingAdapter;
    private WaitDownloadListAdapter waitAdapter;
    private DownloadedListAdapter edAdapter;
    private TextView tvProgress;
    private TextView tvSpeed;
    private ProgressBar pbProgress;
    private BaseDownloadTask downloadTask;
    private VideoDownloadListener downloadListener;

    public Downloader(Context context, String name, String from, String path) {
        this.context = context;
        this.name = name;
        this.from = from;
        this.path = path;
    }

    /**
     * 设置控件
     *
     * @param tvProgress
     * @param tvSpeed
     * @param pbProgress
     */
    public void setIngWidget(DownloadingListAdapter ingAdapter, WaitDownloadListAdapter waitAdapter,
                             int position, TextView tvProgress, TextView tvSpeed, ProgressBar pbProgress) {
        this.tvProgress = tvProgress;
        this.tvSpeed = tvSpeed;
        this.pbProgress = pbProgress;
        this.ingAdapter = ingAdapter;
        this.waitAdapter = waitAdapter;
        this.position = position;
    }

    public void setEdWidget(DownloadedListAdapter edAdapter) {
        this.edAdapter = edAdapter;
    }

    /**
     * 更新信息
     */
    private void updateMsg(long progress, int speed) {
        String speedStr;
        String progressStr;
        // 更新提示信息
        if (tvProgress != null && tvSpeed != null && pbProgress != null) {
            progressStr = progress + "%";
            tvProgress.setText(progressStr);
            if (speed > 1000) {
                speedStr = String.format(Locale.getDefault(), "%.2f", (float) speed / 1024) + "MB/s";
                tvSpeed.setText(speedStr);
            } else {
                speedStr = speed + "KB/s";
                tvSpeed.setText(speedStr);
            }
            pbProgress.setProgress((int) progress);
            ingAdapter.notifyItemChanged(position);
        }
    }

    /**
     * 添加下载项
     *
     * @param url
     * @param path
     */
    public void addDownloader(String url, final String path) {
        downloadListener = new VideoDownloadListener();
        downloadTask = FileDownloader.getImpl().create(url).setPath(path)
                .setMinIntervalUpdateSpeed(1000) // 设置下载中刷新下载速度的最小间隔
                .setCallbackProgressMinInterval(500) // 设置每个FileDownloadListener#progress之间回调间隔(ms)
                .setListener(downloadListener);
    }

    /**
     * 启动下载任务
     */
    public void startTask() {
        downloadTask.start();
    }

    /**
     * 暂停任务
     */
    public void pauseTask() {
        // TODO: 2016/9/24 暂停之后无法继续下载
        FileDownloader.getImpl().pause(downloadListener);
    }

    public void resumeTask() {
        // TODO: 2016/9/24 方法无效
        FileDownloader.getImpl().start(downloadListener, true);
    }

    private class VideoDownloadListener extends FileDownloadListener {
        @Override
        protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {

        }

        @Override
        protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
            if (totalBytes == -1) {
                // chunked transfer encoding data
            } else {
                updateMsg((long) soFarBytes * 100 / (long) totalBytes, task.getSpeed());
            }
        }

        @Override
        protected void completed(BaseDownloadTask task) {
            AppToast.showToast(name + " 已下载完成。");
            DownloadingListAdapter.hasAttached = false;
            App.downloaders.remove(Downloader.this);
            // 添加到数据库中
            DownloadItem item = new DownloadItem(name, from, path);
            App.downloadService.addDownloadVideo(item);
            App.downloadItems.add(item);
            // 刷新已下载列表
            if (edAdapter != null) {
                edAdapter.refreshList();
            }
            if (App.waitDownloaders.size() > 0) {
                // 有等待下载的任务
                Downloader downloader = App.waitDownloaders.get(0);
                App.waitDownloaders.remove(downloader);
                App.downloaders.add(downloader);
                downloader.startTask();
            }

            if (ingAdapter != null) {
                ingAdapter.notifyDataSetChanged();
            }
            if (waitAdapter != null) {
                waitAdapter.notifyDataSetChanged();
            }
            DownloadingFragment fragment = DownloadingFragment.getInstance();
            if (fragment != null) {
                fragment.refreshTextMsg();
            }
        }

        @Override
        protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {

        }

        @Override
        protected void error(BaseDownloadTask task, Throwable e) {
            AppToast.showToast(name + " 下载失败。");
            DownloadingListAdapter.hasAttached = false;
            App.downloaders.remove(Downloader.this);

            if (NetworkUtils.isNetworkAvailable(context)) {
                if (App.waitDownloaders.size() > 0) {
                    // 有等待下载的任务
                    Downloader downloader = App.waitDownloaders.get(0);
                    App.waitDownloaders.remove(downloader);
                    App.downloaders.add(downloader);
                    downloader.startTask();
                }
                if (waitAdapter != null) {
                    waitAdapter.notifyDataSetChanged();
                }
            } else {
                AppToast.showToast("网络未连接，请检查网络。");
            }
            if (ingAdapter != null) {
                ingAdapter.notifyDataSetChanged();
            }

            DownloadingFragment fragment = DownloadingFragment.getInstance();
            if (fragment != null) {
                fragment.refreshTextMsg();
            }
        }

        @Override
        protected void warn(BaseDownloadTask task) {

        }
    }
}
