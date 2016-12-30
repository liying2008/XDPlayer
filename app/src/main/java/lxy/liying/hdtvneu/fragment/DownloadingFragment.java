package lxy.liying.hdtvneu.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.flyco.dialog.entity.DialogMenuItem;
import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.listener.OnOperItemClickL;
import com.flyco.dialog.widget.NormalDialog;
import com.flyco.dialog.widget.NormalListDialog;
import com.shizhefei.fragment.LazyFragment;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import cc.duduhuo.applicationtoast.AppToast;
import lxy.liying.hdtvneu.R;
import lxy.liying.hdtvneu.adapter.DownloadingListAdapter;
import lxy.liying.hdtvneu.adapter.WaitDownloadListAdapter;
import lxy.liying.hdtvneu.app.App;
import lxy.liying.hdtvneu.utils.Downloader;
import lxy.liying.hdtvneu.utils.RecyclerItemClickListener;

/**
 * =======================================================
 * 版权：©Copyright LiYing 2015-2016. All rights reserved.
 * 作者：liying
 * 日期：2016/8/14 14:52
 * 版本：1.0
 * 描述：“正在下载”列表
 * 备注：
 * =======================================================
 */
public class DownloadingFragment extends LazyFragment {
    private RecyclerView rvDownloading, rvWaitDownload;
    private TextView tvDownloadingNone, tvWaitDownloadNone;
    private DownloadingListAdapter downloadingAdapter;
    private WaitDownloadListAdapter waitDownloadAdapter;
    private static WeakReference<DownloadingFragment> fragment;
    private ArrayList<DialogMenuItem> mMenuItems = new ArrayList<>();

    @Override
    protected void onCreateViewLazy(Bundle savedInstanceState) {
        initView();
        initData();
        fragment = new WeakReference<>(this);
    }

    /**
     * 得到本Fragment的实例
     * @return
     */
    public static DownloadingFragment getInstance() {
        if (fragment != null) {
            return fragment.get();
        }
        return null;
    }
    /**
     * 初始化视图
     */
    private void initView() {
        setContentView(R.layout.fragment_downloading);
        rvDownloading = (RecyclerView) findViewById(R.id.rvDownloading);
        rvWaitDownload = (RecyclerView) findViewById(R.id.rvWaitDownload);
        tvDownloadingNone = (TextView) findViewById(R.id.tvDownloadingNone);
        tvWaitDownloadNone = (TextView) findViewById(R.id.tvWaitDownloadNone);
        rvDownloading.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvWaitDownload.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    /**
     * 初始化数据
     */
    private void initData() {
        downloadingAdapter = new DownloadingListAdapter(getActivity());
        waitDownloadAdapter = new WaitDownloadListAdapter(getActivity());
        DownloadingListAdapter.hasAttached = false;
        rvDownloading.setAdapter(downloadingAdapter);
        rvWaitDownload.setAdapter(waitDownloadAdapter);

        refreshTextMsg();
        mMenuItems.add(new DialogMenuItem("从队列中移除", R.drawable.ic_menu_delete));

        RecyclerItemClickListener.OnItemClickListener listener = new RecyclerItemClickListener.OnItemClickListener() {

            @Override
            public void onItemClick(View view, int position) {
                Downloader downloader = App.waitDownloaders.get(position);
                if (App.downloaders.size() == 0) {
                    // 移到下载列表中
                    App.downloaders.add(downloader);
                    App.waitDownloaders.remove(downloader);
                    downloadingAdapter.notifyItemChanged(0);
                    waitDownloadAdapter.notifyDataSetChanged();
                    downloader.startTask();
                    AppToast.showToast("开始下载 " + downloader.name);
                    refreshTextMsg();
                } else {
                    // 调整下载任务
                    App.waitDownloaders.remove(position);
                    App.waitDownloaders.add(0, downloader);
                    waitDownloadAdapter.notifyDataSetChanged();
                    AppToast.showToast("下载任务已调整");
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {
                final NormalListDialog dialog = new NormalListDialog(getActivity(), mMenuItems);
                dialog.title("请选择")//
                        .showAnim(App.mBasIn)//
                        .dismissAnim(App.mBasOut)//
                        .show();
                dialog.setOnOperItemClickL(new OnOperItemClickL() {

                    @Override
                    public void onOperItemClick(AdapterView<?> parent, View view, final int position, long id) {
                        if (position == 0) {
                            // 从队列中移除
                            final NormalDialog dialog = App.getNormalDialog(getActivity(),
                                    "确定要移除该下载任务?");

                            dialog.setOnBtnClickL(
                                    new OnBtnClickL() {
                                        @Override
                                        public void onBtnClick() {
                                            // 取消
                                            dialog.dismiss();
                                        }
                                    },
                                    new OnBtnClickL() {
                                        @Override
                                        public void onBtnClick() {
                                            // 确定
                                            App.waitDownloaders.remove(position);
                                            waitDownloadAdapter.notifyDataSetChanged();
                                            AppToast.showToast("已移除");
                                            refreshTextMsg();
                                            dialog.dismiss();
                                        }
                                    });
                        }
                        dialog.dismiss();
                    }
                });
            }
        };
        rvWaitDownload.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), rvWaitDownload, listener));
    }

    /**
     * 刷新列表
     */
    public void refreshList() {
        downloadingAdapter.notifyDataSetChanged();
        waitDownloadAdapter.notifyDataSetChanged();
    }
    /**
     * 刷新文本信息显示，“空”的显示
     */
    public void refreshTextMsg() {
        if (App.downloaders.size() == 0) {
            tvDownloadingNone.setVisibility(View.VISIBLE);
        } else {
            tvDownloadingNone.setVisibility(View.GONE);
        }
        if (App.waitDownloaders.size() == 0) {
            tvWaitDownloadNone.setVisibility(View.VISIBLE);
        } else {
            tvWaitDownloadNone.setVisibility(View.GONE);
        }
    }
}
