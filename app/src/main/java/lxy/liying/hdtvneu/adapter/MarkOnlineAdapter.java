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
 * 作者：liying
 * 日期：2016/9/11 0:24
 * 版本：1.0
 * 描述：在线视频收藏列表
 * 备注：
 * =======================================================
 */
public class MarkOnlineAdapter extends RecyclerView.Adapter<MarkOnlineAdapter.ViewHolder> {
    private Context context;
    private List<MarkItem> markItems = new ArrayList<>(1);
    private List<String> paths = new ArrayList<>(200);
    private Bitmap mBitmap; //
    private LruCache<String, BitmapDrawable> mMemoryCache;//

    public MarkOnlineAdapter(Context context, List<MarkItem> markItems) {
        this.context = context;
        if (markItems != null) {
            this.markItems = markItems;
            for (MarkItem markItem : markItems) {
                paths.add(markItem.getPath());
            }
        }
        //默认显示的图片
        mBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.default_online_img);
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
    public void setData(List<MarkItem> items) {
        if (items != null) {
            this.markItems = items;
            for (MarkItem markItem : items) {
                paths.add(markItem.getPath());
            }
        }
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
    public void updateDate(MarkItem oldItem, MarkItem newItem) {
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
    public MarkOnlineAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_mark_online, parent, false);
        ViewHolder holder = new ViewHolder(view);
        holder.tvVideoName = (TextView) view.findViewById(R.id.tvVideoName);
        holder.tvVideoFrom = (TextView) view.findViewById(R.id.tvVideoFrom);
        holder.ivCover = (ImageView) view.findViewById(R.id.ivCover);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final MarkItem markItem = markItems.get(position);
        holder.tvVideoName.setText(markItem.getName());
        holder.tvVideoFrom.setText(markItem.getFolder());
        String imageUrl = paths.get(position);
        BitmapDrawable drawable = getBitmapDrawableFromMemoryCache(imageUrl);
        if (drawable != null) {
            holder.ivCover.setBackgroundDrawable(drawable);
        } else if (cancelPotentialTask(imageUrl, holder.ivCover)) {
            //执行下载操作
            DownLoadTask task = new DownLoadTask(holder.ivCover);
            AsyncDrawable asyncDrawable = new AsyncDrawable(context.getResources(), mBitmap, task);
            holder.ivCover.setBackgroundDrawable(asyncDrawable);
            task.execute(imageUrl, String.valueOf(position));
        }
    }

    @Override
    public int getItemCount() {
        return markItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvVideoName, tvVideoFrom;
        private ImageView ivCover;
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    /**
     * 检查复用的ImageView中是否存在其他图片的下载任务，如果存在就取消并且返回ture 否则返回 false
     * @param imageUrl
     * @param imageView
     * @return
     */
    private boolean cancelPotentialTask(String imageUrl, ImageView imageView) {
        DownLoadTask task = getDownLoadTask(imageView);
        if (task != null) {
            String url = task.url;
            if (url == null || !url .equals(imageUrl)){
                task.cancel(true);
            }else{
                return false;
            }
        }
        return true;
    }


    /**
     * 從缓存中获取已存在的图片
     * @param imageUrl
     * @return
     */
    private BitmapDrawable getBitmapDrawableFromMemoryCache(String imageUrl) {
        return mMemoryCache.get(imageUrl);
    }

    /**
     * 添加图片到缓存中
     * @param imageUrl
     * @param drawable
     */
    private void addBitmapDrawableToMemoryCache(String imageUrl,BitmapDrawable drawable){
        if (getBitmapDrawableFromMemoryCache(imageUrl) == null ){
            mMemoryCache.put(imageUrl, drawable);
        }
    }

    /**
     * 获取当前ImageView 的图片下载任务
     * @param imageView
     * @return
     */
    private DownLoadTask getDownLoadTask(ImageView imageView){
        if (imageView != null){
            Drawable drawable  = imageView.getDrawable();
            if (drawable instanceof AsyncDrawable ){
                return  ((AsyncDrawable) drawable).getDownLoadTaskFromAsyncDrawable();
            }
        }
        return null;
    }

    /**
     * 新建一个类 继承BitmapDrawable
     * 目的： BitmapDrawable 和DownLoadTask建立弱引用关联
     */
    private class AsyncDrawable extends  BitmapDrawable{
        private  WeakReference<DownLoadTask> downLoadTaskWeakReference;

        public AsyncDrawable(Resources resources,Bitmap bitmap,DownLoadTask downLoadTask){
            super(resources,bitmap);
            downLoadTaskWeakReference = new WeakReference<DownLoadTask>(downLoadTask);
        }

        private DownLoadTask getDownLoadTaskFromAsyncDrawable(){
            return downLoadTaskWeakReference.get();
        }
    }

    /**
     * 异步加载图片
     * DownLoadTash 和 ImagaeView建立弱引用关联。
     */
    private class DownLoadTask extends AsyncTask<String ,Void,BitmapDrawable> {
        String url;
        int position;
        private WeakReference<ImageView> imageViewWeakReference;
        public DownLoadTask(ImageView imageView){
            imageViewWeakReference = new WeakReference<ImageView>(imageView);
        }
        @Override
        protected BitmapDrawable doInBackground(String... params) {
            url = params[0];
            position = Integer.parseInt(params[1]);
            BitmapDrawable drawable = new BitmapDrawable(
                    ThumbnailUtil.createVideoThumbnail(context, url, 280, 200));
            addBitmapDrawableToMemoryCache(url,drawable);
            return  drawable;
        }

        /**
         * 验证ImageView 中的下载任务是否相同 如果相同就返回
         * @return
         */
        private ImageView getAttachedImageView() {
            ImageView imageView = imageViewWeakReference.get();
            if (imageView != null){
                DownLoadTask task = getDownLoadTask(imageView);
                if (this == task ){
                    return  imageView;
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(BitmapDrawable drawable) {
            super.onPostExecute(drawable);
            ImageView imageView = getAttachedImageView();
//            System.out.println("3 - " + drawable + "," + imageView);
            if ( imageView != null && drawable != null){
                imageView.setImageDrawable(drawable);
            }
            notifyItemChanged(position);
        }
    }
}
