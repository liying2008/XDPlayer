package lxy.liying.hdtvneu.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import lxy.liying.hdtvneu.R;
import lxy.liying.hdtvneu.app.App;
import lxy.liying.hdtvneu.utils.Downloader;

/**
 * =======================================================
 * 作者：liying
 * 日期：2016/9/17 20:47
 * 版本：1.0
 * 描述：“正在下载”列表适配器
 * 备注：
 * =======================================================
 */
public class DownloadingListAdapter extends RecyclerView.Adapter<DownloadingListAdapter.ViewHolder> {
    private Context context;
    private List<Downloader> downloaders;
    public static boolean hasAttached = false;
    private ViewHolder viewHolder;
    private boolean pause = false;

    public DownloadingListAdapter(Context context) {
        this.context = context;
        if (App.downloaders != null) {
            downloaders = App.downloaders;
        } else {
            downloaders = new ArrayList<>(1);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewHolder == null) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_downloading, parent, false);
            viewHolder = new ViewHolder(view);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if (!hasAttached) {
            final Downloader downloader = downloaders.get(position);
            holder.tvDownloadingName.setText(downloader.name);
            downloader.setIngWidget(this, WaitDownloadListAdapter.getAdapter(), position,
                    holder.tvProgress, holder.tvSpeed, holder.pbProgress);
            downloader.setEdWidget(DownloadedListAdapter.getEdAdapter());

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 点击下载/暂停
                    // TODO: 2016/9/24 点击下载/暂停未实现
                }
            });
            hasAttached = true;
        }
    }

    @Override
    public int getItemCount() {
        return downloaders.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvDownloadingName, tvProgress, tvSpeed;
        private ProgressBar pbProgress;

        public ViewHolder(View itemView) {
            super(itemView);
            tvDownloadingName = (TextView) itemView.findViewById(R.id.tvDownloadingName);
            tvProgress = (TextView) itemView.findViewById(R.id.tvProgress);
            tvSpeed = (TextView) itemView.findViewById(R.id.tvSpeed);
            pbProgress = (ProgressBar) itemView.findViewById(R.id.pbProgress);
        }
    }
}
