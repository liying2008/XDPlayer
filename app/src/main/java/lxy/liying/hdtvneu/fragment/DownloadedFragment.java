package lxy.liying.hdtvneu.fragment;

import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.flyco.dialog.entity.DialogMenuItem;
import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.listener.OnOperItemClickL;
import com.flyco.dialog.widget.NormalDialog;
import com.flyco.dialog.widget.NormalListDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cc.duduhuo.applicationtoast.AppToast;
import lxy.liying.hdtvneu.R;
import lxy.liying.hdtvneu.activity.LocalVideoPlayer;
import lxy.liying.hdtvneu.adapter.DownloadedListAdapter;
import lxy.liying.hdtvneu.app.App;
import lxy.liying.hdtvneu.domain.DownloadItem;
import lxy.liying.hdtvneu.utils.RecyclerItemClickListener;

/**
 * =======================================================
 * 版权：©Copyright LiYing 2015-2016. All rights reserved.
 * 作者：liying
 * 日期：2016/8/14 14:52
 * 版本：1.0
 * 描述：“下载完成”列表
 * 备注：
 * =======================================================
 */
public class DownloadedFragment extends Fragment {
    private RecyclerView rvDownloaded;
    private DownloadedListAdapter adapter;
    private ArrayList<DialogMenuItem> mMenuItems = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_downloaded, container, false);
        rvDownloaded = (RecyclerView) view.findViewById(R.id.rvDownloaded);
        rvDownloaded.setLayoutManager(new LinearLayoutManager(getActivity()));
        initData();
        return view;
    }

    private void initData() {
        List<DownloadItem> downloadItems = App.downloadService.getAllDownloadItems();
        if (downloadItems != null) {
            App.downloadItems = downloadItems;
        }
        adapter = new DownloadedListAdapter(getActivity());
        rvDownloaded.setAdapter(adapter);
        mMenuItems.add(new DialogMenuItem("从列表中移除", R.drawable.ic_menu_delete));
        mMenuItems.add(new DialogMenuItem("从SD上删除", R.drawable.ic_menu_delete_from_disk));
        mMenuItems.add(new DialogMenuItem("分享该视频", R.drawable.ic_menu_share));

        RecyclerItemClickListener.OnItemClickListener listener = new RecyclerItemClickListener.OnItemClickListener() {

            @Override
            public void onItemClick(View view, int position) {
                // 播放下载的视频
                App.programType = "-1"; // 节目类型
                Intent intent = new Intent(getActivity(), LocalVideoPlayer.class);
                intent.setData(Uri.parse(App.downloadItems.get(position).getPath()));
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view, final int location) {
                final NormalListDialog dialog = new NormalListDialog(getActivity(), mMenuItems);
                dialog.title("请选择")//
                        .showAnim(App.mBasIn)//
                        .dismissAnim(App.mBasOut)//
                        .show();
                dialog.setOnOperItemClickL(new OnOperItemClickL() {
                    @Override
                    public void onOperItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (position == 0) {
                            // 从列表中移除
                            final NormalDialog dialog = App.getNormalDialog(getActivity(),
                                    "确定要将该视频从列表中移除?\n(不删除SD上的文件)");

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
                                            App.downloadService.removeDownloadItem(App.downloadItems.get(location).getPath());
                                            App.downloadItems.remove(location);
                                            adapter.notifyDataSetChanged();
                                            AppToast.showToast("已移除");
                                            dialog.dismiss();
                                        }
                                    });
                        } else if (position == 1) {
                            // 从SD上删除
                            final NormalDialog dialog = App.getNormalDialog(getActivity(),
                                    "确定要将该视频从SD卡中删除?\n(同时从列表中移除)");

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
                                            App.downloadService.removeDownloadItem(App.downloadItems.get(location).getPath());
                                            File f = new File(App.downloadItems.get(location).getPath());
                                            f.delete();
                                            App.downloadItems.remove(location);
                                            adapter.notifyDataSetChanged();
                                            AppToast.showToast("已删除");
                                            dialog.dismiss();
                                        }
                                    });
                        } else if (position == 2) {
                            // 分享视频
                            Intent shareIntent = new Intent();
                            shareIntent.setAction(Intent.ACTION_SEND);
                            shareIntent.setType("video/*");
                            File f = new File(App.downloadItems.get(location).getPath());
                            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(f));
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
        rvDownloaded.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), rvDownloaded, listener));
    }
}
