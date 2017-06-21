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
import java.util.Locale;

import lxy.liying.hdtvneu.R;
import lxy.liying.hdtvneu.app.App;
import lxy.liying.hdtvneu.domain.XDVideo;
import lxy.liying.hdtvneu.utils.CommonUtils;

/**
 * =======================================================
 * 作者：liying
 * 日期：2016/8/17 14:25
 * 版本：1.0
 * 描述：本地视频列表（第二级列表）适配器
 * 备注：
 * =======================================================
 */
public class LocalListVideoAdapter extends RecyclerView.Adapter<LocalListVideoAdapter.ViewHolder> {
    private List<XDVideo> videos;
    private Context context;
    private OnItemClickListener mOnItemClickListener;
    private OnItemLongClickListener mOnItemLongClickListener;

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

    public LocalListVideoAdapter(Context context, List<XDVideo> videos) {
        this.context = context;
        if (videos == null) {
            this.videos = new ArrayList<>(1);
        } else {
            this.videos = videos;
        }
    }

    public void setData(List<XDVideo> videos) {
        if (videos == null) {
            this.videos = new ArrayList<>(1);
        } else {
            this.videos = videos;
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_video_list, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        XDVideo video = videos.get(position);
        holder.tvVideoTitle.setText(video.getTitle());
        holder.tvVideoTitle.setTextColor(context.getResources().getColor(R.color.title_color));
        holder.tvVideoSize.setText(CommonUtils.getSizeString(video.getSize()));
        holder.tvVideoDuration.setText(CommonUtils.formatTime((int) (video.getDuration() / 1000)));
        holder.ivThumbnail.setBackgroundDrawable(new BitmapDrawable(videos.get(position).getVideoThumbnail()));
        if (App.lastXdVideo != null && App.lastXdVideo.get_id() == video.get_id()) {
            // 设置文件名颜色
            holder.tvVideoTitle.setTextColor(context.getResources().getColor(R.color.list_select_name_color));
        }

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
    }

    @Override
    public int getItemCount() {
        return videos.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvVideoTitle, tvVideoSize, tvVideoDuration;
        private ImageView ivThumbnail;

        public ViewHolder(View itemView) {
            super(itemView);
            tvVideoTitle = (TextView) itemView.findViewById(R.id.tvVideoTitle);
            tvVideoSize = (TextView) itemView.findViewById(R.id.tvVideoSize);
            tvVideoDuration = (TextView) itemView.findViewById(R.id.tvVideoDuration);
            ivThumbnail = (ImageView) itemView.findViewById(R.id.ivThumbnail);
        }
    }
}
