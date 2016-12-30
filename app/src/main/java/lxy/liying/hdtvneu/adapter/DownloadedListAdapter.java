package lxy.liying.hdtvneu.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.List;

import lxy.liying.hdtvneu.R;
import lxy.liying.hdtvneu.app.App;
import lxy.liying.hdtvneu.domain.DownloadItem;

/**
 * =======================================================
 * 版权：©Copyright LiYing 2015-2016. All rights reserved.
 * 作者：liying
 * 日期：2016/9/17 20:53
 * 版本：1.0
 * 描述：“下载完成”列表适配器
 * 备注：
 * =======================================================
 */
public class DownloadedListAdapter extends RecyclerView.Adapter<DownloadedListAdapter.ViewHolder> {
    private Context context;
    private List<DownloadItem> items;
    private static WeakReference<DownloadedListAdapter> edAdapter;

    public DownloadedListAdapter(Context context) {
        this.context = context;
        items = App.downloadItems;
        edAdapter = new WeakReference<>(this);
    }

    /**
     * 得到本类适配器对象
     * @return
     */
    public static DownloadedListAdapter getEdAdapter() {
        return edAdapter.get();
    }

    /** 刷新列表 */
    public void refreshList() {
        items = App.downloadItems;
        notifyDataSetChanged();
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_downloaded, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        DownloadItem item = items.get(position);
        holder.tvDownloadedName.setText(item.getName());
        holder.tvFrom.setText(item.getVideoFrom());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvDownloadedName, tvFrom;

        public ViewHolder(View itemView) {
            super(itemView);
            tvDownloadedName = (TextView) itemView.findViewById(R.id.tvDownloadedName);
            tvFrom = (TextView) itemView.findViewById(R.id.tvFrom);
        }
    }
}
