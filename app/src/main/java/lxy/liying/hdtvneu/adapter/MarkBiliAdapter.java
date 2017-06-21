package lxy.liying.hdtvneu.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import lxy.liying.hdtvneu.R;
import lxy.liying.hdtvneu.domain.MarkGroup;
import lxy.liying.hdtvneu.domain.MarkItem;

/**
 * =======================================================
 * 作者：liying
 * 日期：2016/9/11 0:16
 * 版本：1.0
 * 描述：Bilibili视频/AcFun视频收藏列表
 * 备注：
 * =======================================================
 */
public class MarkBiliAdapter extends RecyclerView.Adapter<MarkBiliAdapter.ViewHolder> {
    private Activity activity;
    private List<MarkItem> markItems = new ArrayList<>(1);

    public MarkBiliAdapter(Activity activity, List<MarkItem> markItems) {
        this.activity = activity;
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
     *
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
        return new ViewHolder(LayoutInflater.from(activity).inflate(R.layout.item_mark_bili, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        MarkItem markItem = markItems.get(position);
        if (markItem.getGroup() == MarkGroup.BILI) {
            Glide.with(activity).load(markItem.getCoverPath()).centerCrop()
                .placeholder(R.drawable.bili_default_image_tv).into(holder.ivCover);
        } else if (markItem.getGroup() == MarkGroup.ACFUN) {
            Glide.with(activity).load(markItem.getCoverPath()).centerCrop()
                .placeholder(R.drawable.acfun_default_image_tv).into(holder.ivCover);
        }
        holder.tvVideoTitle.setText(markItem.getName());
        holder.tvVideoFrom.setText(markItem.getGroup().getName());
    }

    @Override
    public int getItemCount() {
        return markItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivCover;
        private TextView tvVideoTitle, tvVideoFrom;

        public ViewHolder(View itemView) {
            super(itemView);
            ivCover = (ImageView) itemView.findViewById(R.id.ivCover);
            tvVideoTitle = (TextView) itemView.findViewById(R.id.tvVideoTitle);
            tvVideoFrom = (TextView) itemView.findViewById(R.id.tvVideoFrom);
        }
    }
}
