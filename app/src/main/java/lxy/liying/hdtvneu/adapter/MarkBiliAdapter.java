package lxy.liying.hdtvneu.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

import lxy.liying.hdtvneu.R;
import lxy.liying.hdtvneu.domain.MarkGroup;
import lxy.liying.hdtvneu.domain.MarkItem;

/**
 * =======================================================
 * 版权：©Copyright LiYing 2015-2016. All rights reserved.
 * 作者：liying
 * 日期：2016/9/11 0:16
 * 版本：1.0
 * 描述：Bilibili视频/AcFun视频收藏列表
 * 备注：
 * =======================================================
 */
public class MarkBiliAdapter extends RecyclerView.Adapter<MarkBiliAdapter.ViewHolder> {
    private Context context;
    private List<MarkItem> markItems = new ArrayList<>(1);

    public MarkBiliAdapter(Context context, List<MarkItem> markItems) {
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
        int location = markItems.size();
        markItems.add(location, item);
        // 刷新
        notifyItemChanged(location);
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
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_mark_bili, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        MarkItem markItem = markItems.get(position);
        GenericDraweeHierarchy hierarchy = holder.ivCover.getHierarchy();
        if (markItem.getGroup() == MarkGroup.BILI) {
            hierarchy.setPlaceholderImage(R.drawable.bili_default_image_tv);
        } else if (markItem.getGroup() == MarkGroup.ACFUN) {
            hierarchy.setPlaceholderImage(R.drawable.acfun_default_image_tv);
        }
        holder.ivCover.setHierarchy(hierarchy);
        holder.ivCover.setImageURI(Uri.parse(markItem.getCoverPath()));
        holder.tvVideoTitle.setText(markItem.getName());
        holder.tvVideoFrom.setText(markItem.getGroup().getName());
    }

    @Override
    public int getItemCount() {
        return markItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private SimpleDraweeView ivCover;
        private TextView tvVideoTitle, tvVideoFrom;
        public ViewHolder(View itemView) {
            super(itemView);
            ivCover = (SimpleDraweeView) itemView.findViewById(R.id.ivCover);
            tvVideoTitle = (TextView) itemView.findViewById(R.id.tvVideoTitle);
            tvVideoFrom = (TextView) itemView.findViewById(R.id.tvVideoFrom);
        }
    }
}
