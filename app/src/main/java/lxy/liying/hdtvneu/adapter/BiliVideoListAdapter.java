package lxy.liying.hdtvneu.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import lxy.liying.hdtvneu.R;
import lxy.liying.hdtvneu.domain.BiliVideo;

/**
 * =======================================================
 * 版权：©Copyright LiYing 2015-2016. All rights reserved.
 * 作者：liying
 * 日期：2016/8/22 20:46
 * 版本：1.0
 * 描述：视频列表适配器
 * 备注：
 * =======================================================
 */
public class BiliVideoListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<BiliVideo> biliVideos;
    private OnItemClickListener mOnItemClickListener;
    private OnItemLongClickListener mOnItemLongClickListener;
    private String footerInfo = "";
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_FOOTER = 1;

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(View view, int position);
    }

    /**
     * 设置监听
     */
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    /**
     * 设置监听
     */
    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.mOnItemLongClickListener = onItemLongClickListener;
    }

    public BiliVideoListAdapter(Context context, List<BiliVideo> biliVideos) {
        this.context = context;
        this.biliVideos = biliVideos;
    }

    public void setData(List<BiliVideo> biliVideos) {
        this.biliVideos = biliVideos;
    }

    /**
     * 设置footerView信息
     * @param info
     */
    public void setFooterInfo(String info) {
        this.footerInfo = info;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_bili_video_list, parent, false);
            return new ItemViewHolder(view);
        }

        // type == TYPE_FOOTER 返回footerView
        else if (viewType == TYPE_FOOTER) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_footer_view, parent, false);
            return new FooterViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {
            BiliVideo biliVideo = biliVideos.get(position);
            ((ItemViewHolder) holder).tvTitle.setText(biliVideo.getTitle());
            ((ItemViewHolder) holder).tvTime.setText(biliVideo.getTime());
            ((ItemViewHolder) holder).tvPlay.setText(biliVideo.getPlay());
            ((ItemViewHolder) holder).tvUp.setText(biliVideo.getUp());
            ((ItemViewHolder) holder).ivCover.setImageURI(Uri.parse(biliVideo.getCoverUrl()));

            // 如果设置了回调，则设置点击事件
            if (mOnItemClickListener != null) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int pos = holder.getLayoutPosition();
                        mOnItemClickListener.onItemClick(holder.itemView, pos);
                    }
                });
            }

            if (mOnItemLongClickListener != null) {
                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        int pos = holder.getLayoutPosition();
                        mOnItemLongClickListener.onItemLongClick(holder.itemView, pos);
                        return true;
                    }
                });
            }
        } else if (holder instanceof FooterViewHolder) {
            ((FooterViewHolder) holder).tvFooter.setText(footerInfo);
        }
    }

    @Override
    public int getItemCount() {
        return biliVideos.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        // 最后一个item设置为footerView
        if (position + 1 == getItemCount()) {
            return TYPE_FOOTER;
        } else {
            return TYPE_ITEM;
        }
    }

    private static class FooterViewHolder extends RecyclerView.ViewHolder {
        private TextView tvFooter;
        public FooterViewHolder(View view) {
            super(view);
            tvFooter = (TextView) view.findViewById(R.id.tvFooter);
        }
    }

    private static class ItemViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTitle, tvUp, tvPlay, tvTime;
        private SimpleDraweeView ivCover;

        public ItemViewHolder(View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            tvUp = (TextView) itemView.findViewById(R.id.tvUp);
            tvPlay = (TextView) itemView.findViewById(R.id.tvPlay);
            tvTime = (TextView) itemView.findViewById(R.id.tvTime);
            ivCover = (SimpleDraweeView) itemView.findViewById(R.id.ivCover);
        }
    }
}
