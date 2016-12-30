package lxy.liying.hdtvneu.fragment;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
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
import lxy.liying.hdtvneu.activity.LocalVideoPlayer;
import lxy.liying.hdtvneu.adapter.MarkLocalAdapter;
import lxy.liying.hdtvneu.app.App;
import lxy.liying.hdtvneu.domain.MarkGroup;
import lxy.liying.hdtvneu.domain.MarkItem;
import lxy.liying.hdtvneu.service.task.GetMarkVideoTask;
import lxy.liying.hdtvneu.service.callback.OnGetMarkVideosCallback;
import lxy.liying.hdtvneu.utils.Constants;
import lxy.liying.hdtvneu.utils.RecyclerItemClickListener;

/**
 * =======================================================
 * 版权：©Copyright LiYing 2015-2016. All rights reserved.
 * 作者：liying
 * 日期：2016/8/14 14:45
 * 版本：1.0
 * 描述：本地视频收藏列表
 * 备注：
 * =======================================================
 */
public class MarkLocalFragment extends LazyFragment implements OnGetMarkVideosCallback, SwipeRefreshLayout.OnRefreshListener {
    private RecyclerView rvMarkLocal;
    public TextView tvMarkNone;
    public MarkLocalAdapter markLocalAdapter;
    private ProgressDialog progressDialog;
    private SwipeRefreshLayout swipeRefresh;

    private static WeakReference<MarkLocalFragment> markLocalFragment;
    private static List<MarkItem> markLocalItems = new ArrayList<>(1);
    private RecyclerItemClickListener.OnItemClickListener localListener;

    private ArrayList<DialogMenuItem> mMenuItems = new ArrayList<>();

    @Override
    protected void onCreateViewLazy(Bundle savedInstanceState) {
        super.onCreateViewLazy(savedInstanceState);

        initView();
        initData();
        markLocalFragment = new WeakReference<>(this);
    }

    /**
     * 得到本Fragment的实例
     */
    public static MarkLocalFragment getInstance() {
        if (markLocalFragment != null) {
            return markLocalFragment.get();
        }
        return null;
    }

    private void initView() {
        setContentView(R.layout.fragment_mark_local);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("正在查找收藏列表...");
        progressDialog.show();
        rvMarkLocal = (RecyclerView) findViewById(R.id.rvMarkLocal);
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
        swipeRefresh.setColorSchemeResources(android.R.color.holo_red_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_green_light,
                android.R.color.holo_blue_bright
        );

        rvMarkLocal.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvMarkLocal.setItemAnimator(new DefaultItemAnimator());

        tvMarkNone = (TextView) findViewById(R.id.tvMarkNone);
    }

    private void initData() {
        markLocalItems.clear();
        markLocalAdapter = new MarkLocalAdapter(getActivity(), markLocalItems);
        swipeRefresh.setOnRefreshListener(this);
        rvMarkLocal.setAdapter(markLocalAdapter);

        setRecyclerListener();
        rvMarkLocal.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), rvMarkLocal, localListener));

        mMenuItems.add(new DialogMenuItem("从收藏中移除", R.drawable.ic_menu_delete));
        mMenuItems.add(new DialogMenuItem("修改视频名称", R.drawable.ic_menu_modify));
        mMenuItems.add(new DialogMenuItem("分享该视频", R.drawable.ic_menu_share));

        if (App.localMarkItems == null) {
            GetMarkVideoTask task = new GetMarkVideoTask(this);
            task.execute();
        } else {
            onGetLocalMark(App.localMarkItems);
        }
    }

    /**
     * 给RecyclerView设置点击事件监听
     */
    private void setRecyclerListener() {
        this.localListener = new RecyclerItemClickListener.OnItemClickListener() {

            @Override
            public void onItemClick(View view, int position) {
                MarkItem item = markLocalItems.get(position);
                App.programType = "-1";
                Intent intent = new Intent(getActivity(), LocalVideoPlayer.class);
                intent.setData(Uri.parse(item.getPath()));
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view, int position) {
                final MarkItem item = markLocalItems.get(position);
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
                            AppToast.showToast("已移除。");
                        } else if (position == 1) {
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
                                                item.getCoverPath(), MarkGroup.ONLINE, item.getPath(), item.getFolder(), 0L);
                                        App.markService.updateMarkItemName(item.getMarkId(), title);
                                        AppToast.showToast("视频标题已修改。");
                                        // 刷新App中的onlineMarkItems
                                        int location = App.localMarkItems.indexOf(item);
                                        App.localMarkItems.remove(location);
                                        App.localMarkItems.add(location, newItem);
                                        // 刷新MarkListFragment
                                        MarkLocalFragment.getInstance().markLocalAdapter.updateData(item, newItem);
                                    }
                                    dialog.dismiss();
                                }
                            });
                        } else if (position == 2) {
                            // 分享视频
                            Intent shareIntent = new Intent();
                            shareIntent.setAction(Intent.ACTION_SEND);
                            shareIntent.setType("video/*");
                            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(item.getPath())));
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
     * @param item    MarkItem
     */
    private void removeMark(MarkItem item) {
        App.markService.removeMark(item.getMarkId());
        markLocalAdapter.delData(item);
        markLocalAdapter.notifyDataSetChanged();
        markLocalItems.remove(item);
        if (markLocalItems.size() == 0) {
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
        progressDialog.dismiss();
        if (localMarkItems != null && localMarkItems.size() > 0) {
            tvMarkNone.setVisibility(View.GONE);
            markLocalItems.addAll(localMarkItems);
            markLocalAdapter.setData(markLocalItems);
            markLocalAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onGetOnlineMark(List<MarkItem> onlineMarkItems) {
    }

    @Override
    public void onNull() {
        // 收藏为空
        progressDialog.dismiss();
    }

    @Override
    public void onRefresh() {
        System.out.println("onRefresh");
        boolean cleaned = false;
        int invalidCount = 0;
        // 清除失效的收藏（指源视频已不存在的）
        if (markLocalItems != null && markLocalItems.size() > 0) {
            List<MarkItem> tempList = new ArrayList<>(markLocalItems.size());
            for (MarkItem item : markLocalItems) {
                tempList.add(item);
            }

            for (MarkItem item : tempList) {
                if (new File(item.getPath()).exists()) {
                    // 视频文件存在
                } else {
                    // 视频文件不存在
                    removeMark(item);
                    cleaned = true;
                    invalidCount++;
                }
            }
        }
        if (cleaned) {
            AppToast.showToast("已清除" + invalidCount + "个失效的收藏。");
        } else {
            AppToast.showToast("没有失效的收藏。");
        }
        swipeRefresh.setRefreshing(false);
    }
}
