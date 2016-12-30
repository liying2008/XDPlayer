package lxy.liying.hdtvneu.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import lxy.liying.hdtvneu.R;
import lxy.liying.hdtvneu.domain.MarkItem;
import lxy.liying.hdtvneu.utils.ThumbnailUtil;

/**
 * =======================================================
 * 版权：©Copyright LiYing 2015-2016. All rights reserved.
 * 作者：liying
 * 日期：2016/9/10 23:56
 * 版本：1.0
 * 描述：IPv6电视收藏列表适配器
 * 备注：
 * =======================================================
 */
public class MarkIPv6Adapter extends RecyclerView.Adapter<MarkIPv6Adapter.ViewHolder> {
    private Context context;
    private List<MarkItem> markItems = new ArrayList<>(1);
    private List<String> paths = new ArrayList<>(200);
    private Bitmap mBitmap; //
    private LruCache<String, BitmapDrawable> mMemoryCache;//

    public MarkIPv6Adapter(Context context, List<MarkItem> markItems) {
        this.context = context;
        if (markItems != null) {
            this.markItems = markItems;
            for (MarkItem markItem : markItems) {
                paths.add(markItem.getPath());
            }
        }
    }

    public void setData(List<MarkItem> items) {
        if (items != null) {
            this.markItems = items;
            for (MarkItem markItem : items) {
                paths.add(markItem.getPath());
            }
        }
        //默认显示的图片
        mBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.default_tv_img);
        //计算内存，并且给Lrucache 设置缓存大小
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheSize = maxMemory / 6;
        mMemoryCache = new LruCache<String, BitmapDrawable>(cacheSize) {
            @Override
            protected int sizeOf(String key, BitmapDrawable value) {
                return value.getBitmap().getByteCount();
            }
        };
    }
    public void addData(MarkItem item) {
        markItems.add(item);
        paths.add(item.getPath());
    }
    public void addData(List<MarkItem> items) {
        markItems.addAll(items);
        for (MarkItem markItem : items) {
            paths.add(markItem.getPath());
        }
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
        paths.remove(item.getPath());
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_mark_ipv6, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        MarkItem markItem = markItems.get(position);
        holder.tvProgramTitle.setText(markItem.getName());
        holder.tvVideoFrom.setText(markItem.getGroup().getName());
        String imageUrl = paths.get(position);
        BitmapDrawable drawable = getBitmapDrawableFromMemoryCache(imageUrl);
        if (drawable != null) {
            holder.ivThumbnail.setBackgroundDrawable(drawable);
        } else if (cancelPotentialTask(imageUrl, holder.ivThumbnail)) {
            //执行下载操作
            DownLoadTask task = new DownLoadTask(holder.ivThumbnail);
            AsyncDrawable asyncDrawable = new AsyncDrawable(context.getResources(), mBitmap, task);
            holder.ivThumbnail.setBackgroundDrawable(asyncDrawable);
            task.execute(imageUrl, String.valueOf(position));
        }
    }

    @Override
    public int getItemCount() {
        return markItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvProgramTitle, tvVideoFrom;
        private ImageView ivThumbnail;
        public ViewHolder(View itemView) {
            super(itemView);
            tvProgramTitle = (TextView) itemView.findViewById(R.id.tvProgramTitle);
            tvVideoFrom = (TextView) itemView.findViewById(R.id.tvVideoFrom);
            ivThumbnail = (ImageView) itemView.findViewById(R.id.ivThumbnail);
        }
    }

    /**
     * 检查复用的ImageView中是否存在其他图片的下载任务，如果存在就取消并且返回ture 否则返回 false
     *
     * @param imageUrl
     * @param imageView
     * @return
     */
    private boolean cancelPotentialTask(String imageUrl, ImageView imageView) {
        DownLoadTask task = getDownLoadTask(imageView);
        if (task != null) {
            String url = task.url;
            if (url == null || !url.equals(imageUrl)) {
                task.cancel(true);
            } else {
                return false;
            }
        }
        return true;
    }


    /**
     * 从缓存中获取已存在的图片
     *
     * @param imageUrl
     * @return
     */
    private BitmapDrawable getBitmapDrawableFromMemoryCache(String imageUrl) {
        if (mMemoryCache != null) {
            return mMemoryCache.get(imageUrl);
        }
        return null;
    }

    /**
     * 添加图片到缓存中
     *
     * @param imageUrl
     * @param drawable
     */
    private void addBitmapDrawableToMemoryCache(String imageUrl, BitmapDrawable drawable) {
        if (getBitmapDrawableFromMemoryCache(imageUrl) == null) {
            mMemoryCache.put(imageUrl, drawable);
        }
    }

    /**
     * 获取当前ImageView 的图片下载任务
     *
     * @param imageView
     * @return
     */
    private DownLoadTask getDownLoadTask(ImageView imageView) {
        if (imageView != null) {
            Drawable drawable = imageView.getDrawable();
            if (drawable instanceof AsyncDrawable) {
                return ((AsyncDrawable) drawable).getDownLoadTaskFromAsyncDrawable();
            }
        }
        return null;
    }

    /**
     * 新建一个类 继承BitmapDrawable
     * 目的： BitmapDrawable 和DownLoadTask建立弱引用关联
     */
    private class AsyncDrawable extends BitmapDrawable {
        private WeakReference<DownLoadTask> downLoadTaskWeakReference;

        public AsyncDrawable(Resources resources, Bitmap bitmap, DownLoadTask downLoadTask) {
            super(resources, bitmap);
            downLoadTaskWeakReference = new WeakReference<DownLoadTask>(downLoadTask);
        }

        private DownLoadTask getDownLoadTaskFromAsyncDrawable() {
            return downLoadTaskWeakReference.get();
        }
    }

    /**
     * 异步加载图片
     * DownLoadTash 和 ImagaeView建立弱引用关联。
     */
    private class DownLoadTask extends AsyncTask<String, Void, BitmapDrawable> {
        String url;
        int position;
        private WeakReference<ImageView> imageViewWeakReference;

        public DownLoadTask(ImageView imageView) {
            imageViewWeakReference = new WeakReference<ImageView>(imageView);
        }

        @Override
        protected BitmapDrawable doInBackground(String... params) {
            url = params[0];
            position = Integer.valueOf(params[1]);
            BitmapDrawable drawable = new BitmapDrawable(
                    ThumbnailUtil.createVideoThumbnail(context, url, 280, 200));
            addBitmapDrawableToMemoryCache(url, drawable);
            return drawable;
        }

        /**
         * 验证ImageView 中的下载任务是否相同 如果相同就返回
         *
         * @return
         */
        private ImageView getAttachedImageView() {
            ImageView imageView = imageViewWeakReference.get();
            if (imageView != null) {
                DownLoadTask task = getDownLoadTask(imageView);
                if (this == task) {
                    return imageView;
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(BitmapDrawable drawable) {
            super.onPostExecute(drawable);
            ImageView imageView = getAttachedImageView();
            if (imageView != null && drawable != null) {
                imageView.setBackgroundDrawable(drawable);
            }
            notifyItemChanged(position);
        }
    }
}
