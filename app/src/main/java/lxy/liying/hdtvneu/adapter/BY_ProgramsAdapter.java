package lxy.liying.hdtvneu.adapter;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flyco.dialog.entity.DialogMenuItem;
import com.flyco.dialog.listener.OnOperItemClickL;
import com.flyco.dialog.widget.NormalListDialog;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import cc.duduhuo.applicationtoast.AppToast;
import lxy.liying.hdtvneu.R;
import lxy.liying.hdtvneu.activity.M3U8Player;
import lxy.liying.hdtvneu.app.App;
import lxy.liying.hdtvneu.domain.MarkGroup;
import lxy.liying.hdtvneu.domain.MarkItem;
import lxy.liying.hdtvneu.domain.Program;
import lxy.liying.hdtvneu.fragment.MarkIPv6Fragment;
import lxy.liying.hdtvneu.utils.Constants;
import lxy.liying.hdtvneu.utils.ThumbnailUtil;

/**
 * =======================================================
 * 版权：Copyright LiYing 2015-2016. All rights reserved.
 * 作者：liying
 * 日期：2016/5/15 15:41
 * 版本：1.0
 * 描述：北邮IPTV——节目列表适配器
 * 备注：
 * =======================================================
 */
public class BY_ProgramsAdapter extends RecyclerView.Adapter<BY_ProgramsAdapter.ViewHolder> {
    /**
     * 节目列表集合
     */
    private List<Program> programsList = new ArrayList<>(150);
    private Context context;
    private String queryString;     // 搜索的字符
    private ForegroundColorSpan foregroundColorSpan;
    private List<String> paths = new ArrayList<>(200);
    private Bitmap mBitmap; //
    private LruCache<String, BitmapDrawable> mMemoryCache;//
    private ArrayList<DialogMenuItem> mMenuItems = new ArrayList<>();
    /** 是否开启预览 */
    private static boolean isPreview = false;

    public BY_ProgramsAdapter(Context context) {
        this.context = context;
        foregroundColorSpan = new ForegroundColorSpan(Color.parseColor("#148370"));
        mMenuItems.add(new DialogMenuItem("添加到收藏", R.drawable.ic_menu_mark));
        mMenuItems.add(new DialogMenuItem("复制播放地址", R.drawable.ic_menu_copy_link));

        String preview = App.getInstance().getSetting(Constants.IPV6_PREVIEW, "1");
        isPreview = preview.equals("1");
    }

    /**
     * 清空缓存
     */
    public void clearCache() {
        if (mMemoryCache != null) {
            if (mMemoryCache.size() > 0) {
                mMemoryCache.evictAll();
            }
//            mMemoryCache = null;
        }
    }

    public void setData(List<Program> programs, String queryString) {
        this.queryString = queryString;
        if (programs != null) {
            programsList = programs;
            paths.clear();
            for (Program program : programs) {
                paths.add(program.getPath());
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

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_by_program, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Program program = programsList.get(position);
        int index = TextUtils.isEmpty(queryString) ? -1 : program.getName().indexOf(queryString);
        if (index == -1) {
            holder.tvProgram.setText(program.getName());
        } else {
            SpannableString ss = new SpannableString(program.getName());
            ss.setSpan(foregroundColorSpan, index, index + queryString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.tvProgram.setText(ss);
        }
        if (isPreview) {
            // 预览
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
        holder.llProgram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 进入播放界面
                Intent intent = new Intent(context, M3U8Player.class);
                intent.putExtra(Constants.PROGRAM, program.getPath());
                context.startActivity(intent);
            }
        });
        // 长按条目
        holder.llProgram.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final NormalListDialog dialog = new NormalListDialog(context, mMenuItems);
                dialog.title("请选择")//
                        .showAnim(App.mBasIn)//
                        .dismissAnim(App.mBasOut)//
                        .show();
                dialog.setOnOperItemClickL(new OnOperItemClickL() {
                    @Override
                    public void onOperItemClick(AdapterView<?> parent, View view, int position, long id) {
                        switch (position) {
                            case 0:
                                // 收藏该节目
                                if (App.markService.getMarkByPath(program.getPath()) == null) {
                                    MarkItem item = new MarkItem(System.currentTimeMillis(), program.getName(), null,
                                            MarkGroup.BY_TV, program.getPath(), null, 0L);
                                    App.markService.addMarkVideo(item);
                                    if (App.ipv6MarkItems != null) {
                                        App.ipv6MarkItems.add(item);
                                    }
                                    AppToast.showToast(program.getName() + " 已收藏。");
                                    if (MarkIPv6Fragment.getInstance() != null) {
                                        // 刷新MarkListFragment
                                        MarkIPv6Fragment.getInstance().markIPv6Adapter.addData(item);
                                        MarkIPv6Fragment.getInstance().markIPv6Adapter.notifyDataSetChanged();
                                        MarkIPv6Fragment.getInstance().tvMarkNone.setVisibility(View.GONE);
                                    }
                                } else {
                                    AppToast.showToast(program.getName() + " 已在收藏列表中。");
                                }
                                break;
                            case 1:
                                // 复制播放地址
                                ClipboardManager cmbName = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                                ClipData clipDataName = ClipData.newPlainText(null, program.getPath());
                                cmbName.setPrimaryClip(clipDataName);
                                AppToast.showToast("播放地址已复制。");
                                break;
                            default:
                                break;
                        }
                        dialog.dismiss();
                    }
                });

                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return programsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView ivThumbnail;
        public TextView tvProgram;
        public LinearLayout llProgram;

        public ViewHolder(View itemView) {
            super(itemView);
            llProgram = (LinearLayout) itemView.findViewById(R.id.llProgram);
            ivThumbnail = (ImageView) itemView.findViewById(R.id.ivThumbnail);
            tvProgram = (TextView) itemView.findViewById(R.id.tvProgram);
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
        if (mMemoryCache == null) {
            return null;
        }
        return mMemoryCache.get(imageUrl);
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
            position = Integer.parseInt(params[1]);
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
