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

/**
 * =======================================================
 * 版权：©Copyright LiYing 2015-2016. All rights reserved.
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
        holder.tvVideoSize.setText(getSizeString(video.getSize()));
        holder.tvVideoDuration.setText(formatTime(video.getDuration()));
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

    /**
     * 格式化视频大小，将字节转换为其他单位
     * @param size
     * @return
     */
    public static CharSequence getSizeString(long size) {
        if (size > (1024 * 1024 * 1024)) {
            // 以G为单位
            double rlt = (double)size / (double)(1024 * 1024 * 1024);
            return String.format(Locale.getDefault(), "%.3f", rlt) + "GB";
        } else if (size > (1024 * 1024)) {
            // 以M为单位
            double rlt = (double)size / (double)(1024 * 1024);
            return String.format(Locale.getDefault(), "%.2f", rlt) + "MB";
        } else if (size > 1024) {
            // 以K为单位
            double rlt = (double)size / (double)(1024);
            return String.format(Locale.getDefault(), "%.2f", rlt) + "KB";
        } else {
            // 以B为单位
            return String.valueOf(size) + "B";
        }

    }

    /**
     * 格式化时间
     *
     * @return
     * 格式：09:08:31
     */
    public static CharSequence formatTime(long msec) {
        long sec = msec / 1000;
        long h = sec / 3600;
        long m = (sec % 3600) / 60;
        long s = sec % 3600 % 60;
        String out = "";
        if (h > 0) {
            if (h < 10) {
                out += "0" + h + ":";
            } else {
                out += h + ":";
            }
        }
        if (m < 10) {
            out += "0" + m + ":";
        } else {
            out += m + ":";
        }

        if (s < 10) {
            out += "0" + s + "";
        } else {
            out += s + "";
        }
        return out;
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
