package lxy.liying.hdtvneu.adapter;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import lxy.liying.hdtvneu.R;
import lxy.liying.hdtvneu.domain.MarkItem;

/**
 * =======================================================
 * 版权：©Copyright LiYing 2015-2016. All rights reserved.
 * 作者：liying
 * 日期：2016/9/11 0:11
 * 版本：1.0
 * 描述：本地视频收藏列表适配器
 * 备注：
 * =======================================================
 */
public class MarkLocalAdapter extends RecyclerView.Adapter<MarkLocalAdapter.ViewHolder> {
    private Context context;
    private List<MarkItem> markItems = new ArrayList<>(1);

    public MarkLocalAdapter(Context context, List<MarkItem> markItems) {
        this.context = context;
        if (markItems != null) {
            this.markItems = markItems;
        }
    }

    public void setData(List<MarkItem> items) {
        if (items != null) {
            this.markItems = items;
        }
    }
    public void addData(MarkItem item) {
        markItems.add(item);
    }
    public void addData(List<MarkItem> items) {
        markItems.addAll(items);
    }
    /**
     * 更新数据
     * @param oldItem 旧数据
     * @param newItem 新数据
     */
    public void updateData(MarkItem oldItem, MarkItem newItem) {
        int location = markItems.indexOf(oldItem);
        markItems.remove(location);
        markItems.add(location, newItem);
        notifyItemChanged(location);
    }

    public void delData(MarkItem item) {
        markItems.remove(item);
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_mark_local, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        MarkItem markItem = markItems.get(position);
        holder.ivThumbnail.setBackgroundDrawable(new BitmapDrawable(markItem.getCoverPath()));
        holder.tvVideoTitle.setText(markItem.getName());
        holder.tvVideoFrom.setText(markItem.getFolder());
    }

    @Override
    public int getItemCount() {
        return markItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivThumbnail;
        private TextView tvVideoTitle, tvVideoFrom;
        public ViewHolder(View itemView) {
            super(itemView);
            ivThumbnail = (ImageView) itemView.findViewById(R.id.ivThumbnail);
            tvVideoTitle = (TextView) itemView.findViewById(R.id.tvVideoTitle);
            tvVideoFrom = (TextView) itemView.findViewById(R.id.tvVideoFrom);
        }
    }
}
