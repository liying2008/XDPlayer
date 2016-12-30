package lxy.liying.hdtvneu.fragment;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
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

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import cc.duduhuo.applicationtoast.AppToast;
import lxy.liying.hdtvneu.R;
import lxy.liying.hdtvneu.activity.M3U8Player;
import lxy.liying.hdtvneu.adapter.MarkIPv6Adapter;
import lxy.liying.hdtvneu.app.App;
import lxy.liying.hdtvneu.domain.MarkItem;
import lxy.liying.hdtvneu.service.task.GetMarkVideoTask;
import lxy.liying.hdtvneu.service.callback.OnGetMarkVideosCallback;
import lxy.liying.hdtvneu.utils.Constants;
import lxy.liying.hdtvneu.utils.NetworkUtils;
import lxy.liying.hdtvneu.utils.RecyclerItemClickListener;

/**
 * =======================================================
 * 版权：©Copyright LiYing 2015-2016. All rights reserved.
 * 作者：liying
 * 日期：2016/8/14 14:45
 * 版本：1.0
 * 描述：IPv6电视节目收藏列表
 * 备注：
 * =======================================================
 */
public class MarkIPv6Fragment extends LazyFragment implements OnGetMarkVideosCallback {
    private RecyclerView rvMarkIPv6;
    public TextView tvMarkNone;
    public MarkIPv6Adapter markIPv6Adapter;
    private ProgressDialog progressDialog;

    private static WeakReference<MarkIPv6Fragment> markIPv6Fragment;
    private static List<MarkItem> markIPv6Items = new ArrayList<>(1);
    private RecyclerItemClickListener.OnItemClickListener ipv6Listener;

    private ArrayList<DialogMenuItem> mMenuItems = new ArrayList<>();

    @Override
    protected void onCreateViewLazy(Bundle savedInstanceState) {
        super.onCreateViewLazy(savedInstanceState);

        initView();
        initData();
        markIPv6Fragment = new WeakReference<>(this);
    }

    /**
     * 得到本Fragment的实例
     */
    public static MarkIPv6Fragment getInstance() {
        if (markIPv6Fragment != null) {
            return markIPv6Fragment.get();
        }
        return null;
    }

    private void initView() {
        setContentView(R.layout.fragment_mark_ipv6);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("正在查找收藏列表...");
        progressDialog.show();
        rvMarkIPv6 = (RecyclerView) findViewById(R.id.rvMarkIPv6);

        rvMarkIPv6.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvMarkIPv6.setItemAnimator(new DefaultItemAnimator());

        tvMarkNone = (TextView) findViewById(R.id.tvMarkNone);
    }

    private void initData() {
        markIPv6Items.clear();

        markIPv6Adapter = new MarkIPv6Adapter(getActivity(), markIPv6Items);
        rvMarkIPv6.setAdapter(markIPv6Adapter);

        setRecyclerListener();
        rvMarkIPv6.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), rvMarkIPv6, ipv6Listener));

        mMenuItems.add(new DialogMenuItem("从收藏中移除", R.drawable.ic_menu_delete));
        mMenuItems.add(new DialogMenuItem("修改视频名称", R.drawable.ic_menu_modify));
        mMenuItems.add(new DialogMenuItem("分享该视频", R.drawable.ic_menu_share));

        if (App.ipv6MarkItems == null) {
            GetMarkVideoTask task = new GetMarkVideoTask(this);
            task.execute();
        } else {
            onGetIPv6Mark(App.ipv6MarkItems);
        }
    }

    /**
     * 给RecyclerView设置点击事件监听
     */
    private void setRecyclerListener() {
        this.ipv6Listener = new RecyclerItemClickListener.OnItemClickListener() {

            @Override
            public void onItemClick(View view, int position) {
                if (!NetworkUtils.isNetworkAvailable(MarkIPv6Fragment.this.getActivity())) {
                    AppToast.showToast("网络不可用，请检查您的网络设置。");
                    return;
                }
                MarkItem item = markIPv6Items.get(position);
                App.programType = "-1";
                Intent intent = new Intent(getActivity(), M3U8Player.class);
                intent.setData(Uri.parse(item.getPath()));
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view, int position) {
                final MarkItem item = markIPv6Items.get(position);
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
                                                null, item.getGroup(), item.getPath(), item.getFolder(), 0L);
                                        App.markService.updateMarkItemName(item.getMarkId(), title);
                                        AppToast.showToast("视频标题已修改。");
                                        // 刷新App中的ipv6MarkItems
                                        int location = App.ipv6MarkItems.indexOf(item);
                                        App.ipv6MarkItems.remove(location);
                                        App.ipv6MarkItems.add(location, newItem);
                                        // 刷新MarkListFragment
                                        MarkIPv6Fragment.getInstance().markIPv6Adapter.updateData(item, newItem);
                                    }
                                    dialog.dismiss();
                                }
                            });
                        } else if (position == 2) {
                            // 分享视频
                            Intent shareIntent = new Intent();
                            shareIntent.setAction(Intent.ACTION_SEND);
                            shareIntent.setType("text/*");
                            shareIntent.putExtra(Intent.EXTRA_TEXT, item.getPath());
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
        AppToast.showToast("已移除。");
        markIPv6Adapter.delData(item);
        markIPv6Adapter.notifyDataSetChanged();
        markIPv6Items.remove(item);
        if (markIPv6Items.size() == 0) {
            tvMarkNone.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onGetIPv6Mark(List<MarkItem> ipv6MarkItems) {
        progressDialog.dismiss();
        if (ipv6MarkItems != null && ipv6MarkItems.size() > 0) {
            tvMarkNone.setVisibility(View.GONE);
            markIPv6Items.addAll(ipv6MarkItems);
            markIPv6Adapter.setData(markIPv6Items);
            markIPv6Adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onGetBiliMark(List<MarkItem> biliMarkItems) {

    }

    @Override
    public void onGetLocalMark(List<MarkItem> localMarkItems) {

    }

    @Override
    public void onGetOnlineMark(List<MarkItem> onlineMarkItems) {

    }

    @Override
    public void onNull() {
        // 收藏为空
        progressDialog.dismiss();
    }
}
