package lxy.liying.hdtvneu.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import lxy.liying.hdtvneu.R;
import lxy.liying.hdtvneu.app.App;
import lxy.liying.hdtvneu.utils.Downloader;

/**
 * =======================================================
 * 作者：liying
 * 日期：2016/9/24 18:55
 * 版本：1.0
 * 描述：等待下载列表适配器
 * 备注：
 * =======================================================
 */

public class WaitDownloadListAdapter extends RecyclerView.Adapter<WaitDownloadListAdapter.ViewHolder> {
    private Context context;
    private List<Downloader> waitDownloaders;
    private static WeakReference<WaitDownloadListAdapter> adapter;

    public WaitDownloadListAdapter(Context context) {
        this.context = context;
        if (App.waitDownloaders != null) {
            waitDownloaders = App.waitDownloaders;
        } else {
            waitDownloaders = new ArrayList<>(1);
        }
        adapter = new WeakReference<>(this);
    }

    /** 得到Adapter实例 */
    public static WaitDownloadListAdapter getAdapter() {
        return adapter.get();
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_wait_download, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Downloader downloader = waitDownloaders.get(position);
        holder.tvWaitDownloadName.setText(downloader.name);
    }

    @Override
    public int getItemCount() {
        return waitDownloaders.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvWaitDownloadName;

        public ViewHolder(View itemView) {
            super(itemView);
            tvWaitDownloadName = (TextView) itemView.findViewById(R.id.tvWaitDownloadName);
        }
    }
}
