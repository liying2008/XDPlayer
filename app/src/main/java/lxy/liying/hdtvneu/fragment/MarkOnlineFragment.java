package lxy.liying.hdtvneu.fragment;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flyco.dialog.entity.DialogMenuItem;
import com.flyco.dialog.listener.OnOperItemClickL;
import com.flyco.dialog.widget.NormalListDialog;
import com.shizhefei.fragment.LazyFragment;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import cc.duduhuo.applicationtoast.AppToast;
import lxy.liying.hdtvneu.R;
import lxy.liying.hdtvneu.activity.OnlineVideoPlayer;
import lxy.liying.hdtvneu.adapter.MarkOnlineAdapter;
import lxy.liying.hdtvneu.app.App;
import lxy.liying.hdtvneu.domain.MarkGroup;
import lxy.liying.hdtvneu.domain.MarkItem;
import lxy.liying.hdtvneu.service.task.GetMarkVideoTask;
import lxy.liying.hdtvneu.service.callback.OnGetMarkVideosCallback;
import lxy.liying.hdtvneu.utils.Constants;
import lxy.liying.hdtvneu.utils.Downloader;
import lxy.liying.hdtvneu.utils.FileUtils;
import lxy.liying.hdtvneu.utils.NetworkUtils;
import lxy.liying.hdtvneu.utils.RecyclerItemClickListener;

/**
 * =======================================================
 * 版权：©Copyright LiYing 2015-2016. All rights reserved.
 * 作者：liying
 * 日期：2016/8/14 14:45
 * 版本：1.0
 * 描述：在线视频收藏列表
 * 备注：
 * =======================================================
 */
public class MarkOnlineFragment extends LazyFragment implements OnGetMarkVideosCallback {
    private RecyclerView rvMarkOnline;
    public TextView tvMarkNone;
    public MarkOnlineAdapter markOnlineAdapter;
    private ProgressDialog progressDialog;

    private static WeakReference<MarkOnlineFragment> markOnlineFragment;
    private static List<MarkItem> markOnlineItems = new ArrayList<>(1);
    private RecyclerItemClickListener.OnItemClickListener onlineListener;

    private ArrayList<DialogMenuItem> mMenuItems = new ArrayList<>();

    @Override
    protected void onCreateViewLazy(Bundle savedInstanceState) {
        super.onCreateViewLazy(savedInstanceState);

        initView();
        initData();
        markOnlineFragment = new WeakReference<>(this);
    }

    /**
     * 得到本Fragment的实例
     */
    public static MarkOnlineFragment getInstance() {
        if (markOnlineFragment != null) {
            return markOnlineFragment.get();
        }
        return null;
    }

    private void initView() {
        setContentView(R.layout.fragment_mark_online);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("正在查找收藏列表...");
        progressDialog.show();
        rvMarkOnline = (RecyclerView) findViewById(R.id.rvMarkOnline);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvMarkOnline.setLayoutManager(layoutManager);
        rvMarkOnline.setItemAnimator(new DefaultItemAnimator());

        tvMarkNone = (TextView) findViewById(R.id.tvMarkNone);
    }

    private void initData() {
        markOnlineItems.clear();

        markOnlineAdapter = new MarkOnlineAdapter(getActivity(), markOnlineItems);
        rvMarkOnline.setAdapter(markOnlineAdapter);

        setRecyclerListener();
        rvMarkOnline.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), rvMarkOnline, onlineListener));

        mMenuItems.add(new DialogMenuItem("从收藏中移除", R.drawable.ic_menu_delete));
        mMenuItems.add(new DialogMenuItem("下载视频", R.drawable.ic_menu_download));
        mMenuItems.add(new DialogMenuItem("修改视频名称", R.drawable.ic_menu_modify));
        mMenuItems.add(new DialogMenuItem("分享该视频", R.drawable.ic_menu_share));

        if (App.onlineMarkItems == null) {
            GetMarkVideoTask task = new GetMarkVideoTask(this);
            task.execute();
        } else {
            onGetOnlineMark(App.onlineMarkItems);
        }
    }

    /**
     * 给RecyclerView设置点击事件监听
     */
    private void setRecyclerListener() {
        this.onlineListener = new RecyclerItemClickListener.OnItemClickListener() {

            @Override
            public void onItemClick(View view, int position) {
                MarkItem item = markOnlineItems.get(position);
                if (!NetworkUtils.isNetworkAvailable(MarkOnlineFragment.this.getActivity())) {
                    AppToast.showToast("网络不可用，请检查您的网络设置。");
                    return;
                }
                App.programType = "-1"; // 节目类型
                Intent intent = new Intent(MarkOnlineFragment.this.getActivity(), OnlineVideoPlayer.class);
                intent.putExtra(Constants.VIDEO_URL, item.getPath());
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view, int position) {
                final MarkItem item = markOnlineItems.get(position);
                final NormalListDialog dialog = new NormalListDialog(getActivity(), mMenuItems);
                dialog.title("请选择")//
                        .showAnim(App.mBasIn)//
                        .dismissAnim(App.mBasOut)//
                        .show();
                dialog.setOnOperItemClickL(new OnOperItemClickL() {
                    @Override
                    public void onOperItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (position == 0) {
                            // 从收藏中移除
                            removeMark(item);
                        } else if (position == 1) {
                            // 下载视频
                            String fileName = item.getName() + FileUtils.getUrlSuffix(item.getPath());
                            fileName = fileName.replace("/", "&").replace("\\", "&");
                            String path = Constants.ONLINE_DOWNLOAD + File.separator + fileName;
                            Downloader downloader = new Downloader(getActivity(), fileName, item.getFolder(), path);
                            downloader.addDownloader(item.getPath(), path);
                            if (App.downloaders.size() == 0) {
                                downloader.startTask();
                                AppToast.showToast("开始下载 " + fileName);
                                App.downloaders.add(downloader);
                            } else {
                                AppToast.showToast("已添加到下载队列中 " + fileName);
                                App.waitDownloaders.add(downloader);
                            }
                        } else if (position == 2) {
                            // 修改视频名称
                            AlertDialog.Builder builder = App.getAlertDialogBuilder(getActivity());
                            builder.setTitle("修改视频标题");
                            View inflater = getActivity().getLayoutInflater().inflate(R.layout.dialog_mark_title, null);
                            builder.setView(inflater);
                            final AlertDialog dialog = builder.create();
                            dialog.show();
                            final EditText etVideoTitle = (EditText) inflater.findViewById(R.id.etVideoTitle);
                            etVideoTitle.setText(item.getName());
                            etVideoTitle.setSelection(item.getName().length());
                            RelativeLayout rlOK = (RelativeLayout) inflater.findViewById(R.id.rlOK);
                            rlOK.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String title = etVideoTitle.getText().toString();
                                    if (title.equals("")) {
                                        AppToast.showToast("视频标题不可为空。");
                                    } else {
                                        MarkItem newItem = new MarkItem(System.currentTimeMillis(), title,
                                                null, MarkGroup.ONLINE, item.getPath(), item.getFolder(), 0L);
                                        App.markService.updateMarkItemName(item.getMarkId(), title);
                                        AppToast.showToast("视频标题已修改。");
                                        // 刷新App中的onlineMarkItems
                                        int location = App.onlineMarkItems.indexOf(item);
                                        App.onlineMarkItems.remove(location);
                                        App.onlineMarkItems.add(location, newItem);
                                        // 刷新MarkListFragment
                                        MarkOnlineFragment.getInstance().markOnlineAdapter.updateDate(item, newItem);
                                    }
                                    dialog.dismiss();
                                }
                            });
                            // FIXME: 2016/9/16 弹出输入法面板失效，待解决
                            App.autoInputPanel(getActivity());   // 弹出输入法面板
                        } else if (position == 3) {
                            // 分享视频
                            Intent shareIntent = new Intent();
                            shareIntent.setAction(Intent.ACTION_SEND);
                            shareIntent.setType("text/*");
                            String path = item.getPath();
                            shareIntent.putExtra(Intent.EXTRA_TEXT, path);
                            ComponentName cn = shareIntent.resolveActivity(getActivity().getPackageManager());
                            if (cn != null) {
                                startActivity(Intent.createChooser(shareIntent, getString(R.string.share_to)));
                            } else {
                                AppToast.showToast("无法分享。");
                            }
                        }
                        dialog.dismiss();
                    }
                });
            }
        };
    }

    /**
     * 删除收藏
     *
     * @param item MarkItem
     */
    private void removeMark(MarkItem item) {
        App.markService.removeMark(item.getMarkId());
        AppToast.showToast("已移除。");
        markOnlineAdapter.delData(item);
        markOnlineAdapter.notifyDataSetChanged();
        markOnlineItems.remove(item);
        if (markOnlineItems.size() == 0) {
            tvMarkNone.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onGetIPv6Mark(List<MarkItem> ipv6MarkItems) {
    }

    @Override
    public void onGetBiliMark(List<MarkItem> biliMarkItems) {
    }

    @Override
    public void onGetLocalMark(List<MarkItem> localMarkItems) {
    }

    @Override
    public void onGetOnlineMark(List<MarkItem> onlineMarkItems) {
        progressDialog.dismiss();
        if (onlineMarkItems != null && onlineMarkItems.size() > 0) {
            tvMarkNone.setVisibility(View.GONE);
            markOnlineItems.addAll(onlineMarkItems);
            markOnlineAdapter.setData(markOnlineItems);
            markOnlineAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onNull() {
        // 收藏为空
        progressDialog.dismiss();
    }
}
