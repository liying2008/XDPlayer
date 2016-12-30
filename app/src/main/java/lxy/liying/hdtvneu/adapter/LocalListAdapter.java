package lxy.liying.hdtvneu.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import lxy.liying.hdtvneu.R;
import lxy.liying.hdtvneu.app.App;
import lxy.liying.hdtvneu.domain.VideoFolder;
import lxy.liying.hdtvneu.utils.Constants;

/**
 * =======================================================
 * 版权：©Copyright LiYing 2015-2016. All rights reserved.
 * 作者：liying
 * 日期：2016/8/17 14:25
 * 版本：1.0
 * 描述：本地视频列表适配器
 * 备注：
 * =======================================================
 */
public class LocalListAdapter extends RecyclerView.Adapter<LocalListAdapter.ViewHolder> {
    private List<VideoFolder> folders;
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

    public LocalListAdapter(Context context, List<VideoFolder> folders) {
        this.context = context;
        this.folders = folders;
        if (App.lastXdVideo == null) {
            long lastVideoId = Long.parseLong(App.getInstance().getSetting(Constants.LAST_VIDEO_ID, "-1"));
            App.lastXdVideo = App.xdService.getXDVideo(lastVideoId);
        }
    }

    public void setData(List<VideoFolder> folders) {
        this.folders = folders;
    }

    /**
     * 删除一个文件夹
     * @param videoFolder
     */
    public void deleteData(VideoFolder videoFolder) {
        this.folders.remove(videoFolder);
        notifyDataSetChanged();
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_local_list, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if (folders != null && getItemCount() > 0) {
            String name = folders.get(position).getName();
            holder.tvFolderName.setText(name);
            holder.tvFolderName.setTextColor(context.getResources().getColor(R.color.title_color));
            String videoNum = folders.get(position).getCount() + "个视频";
            holder.tvVideoNum.setText(videoNum);
            if (App.lastXdVideo != null && App.lastXdVideo.getFolder().equals(name)) {
                // 设置文件夹颜色
                holder.tvFolderName.setTextColor(context.getResources().getColor(R.color.list_select_name_color));
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
    }

    @Override
    public int getItemCount() {
        if (folders == null) {
            return 0;
        }
        return folders.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvFolderName, tvVideoNum;

        public ViewHolder(View itemView) {
            super(itemView);
            tvFolderName = (TextView) itemView.findViewById(R.id.tvFolderName);
            tvVideoNum = (TextView) itemView.findViewById(R.id.tvVideoNum);
        }
    }
}
