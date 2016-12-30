package lxy.liying.hdtvneu.fragment;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.shizhefei.fragment.LazyFragment;

import cc.duduhuo.applicationtoast.AppToast;
import lxy.liying.hdtvneu.R;
import lxy.liying.hdtvneu.activity.AboutActivity;
import lxy.liying.hdtvneu.activity.AcFunMainActivity;
import lxy.liying.hdtvneu.activity.BiliMainActivity;
import lxy.liying.hdtvneu.activity.DownloadManagerActivity;
import lxy.liying.hdtvneu.activity.OnlineVideoActivity;
import lxy.liying.hdtvneu.activity.SettingsActivity;
import lxy.liying.hdtvneu.adapter.MoreFuncListAdapter;
import lxy.liying.hdtvneu.dialog.UpdateDialog;
import lxy.liying.hdtvneu.domain.UpdateMsg;
import lxy.liying.hdtvneu.service.task.CheckUpdateTask;
import lxy.liying.hdtvneu.service.callback.OnCheckUpdateCallback;
import lxy.liying.hdtvneu.utils.NetworkUtils;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.RuntimePermissions;

/**
 * =======================================================
 * 版权：©Copyright LiYing 2015-2016. All rights reserved.
 * 作者：liying
 * 日期：2016/8/14 14:47
 * 版本：1.0
 * 描述：更多功能页面
 * 备注：
 * =======================================================
 */
@RuntimePermissions
public class MoreFragment extends LazyFragment implements MoreFuncListAdapter.OnItemClickListener {
    private int[] icons = {R.drawable.more_online, R.drawable.more_bilibili,
            R.drawable.more_acfun, R.drawable.more_download,
            R.drawable.more_settings, R.drawable.more_update,
            R.drawable.more_about};
    private CharSequence[] titles = {"播放在线视频", "B", "A", "下载管理", "应用设置",
            "检查更新", "关于XDPlayer"};

    @Override
    protected void onCreateViewLazy(Bundle savedInstanceState) {
        super.onCreateViewLazy(savedInstanceState);
        setContentView(R.layout.fragment_more);

        RecyclerView rvMoreFunc = (RecyclerView) findViewById(R.id.rvMoreFunc);
        rvMoreFunc.setLayoutManager(new LinearLayoutManager(getActivity()));

        MoreFuncListAdapter adapter = new MoreFuncListAdapter(getActivity(), icons, titles);
        rvMoreFunc.setAdapter(adapter);
        adapter.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(View view, int position) {
        Intent intent;
        switch (position) {
            case 0:
                // 播放在线视频
                intent = new Intent(getActivity(), OnlineVideoActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.push_left_in_ac, R.anim.push_left_out_ac);
                break;
            case 1:
                // 哔哩哔哩
                intent = new Intent(getActivity(), BiliMainActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.push_left_in_ac, R.anim.push_left_out_ac);
                break;
            case 2:
                // AcFun
                intent = new Intent(getActivity(), AcFunMainActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.push_left_in_ac, R.anim.push_left_out_ac);
                break;
            case 3:
                // 下载管理
                intent = new Intent(getActivity(), DownloadManagerActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.push_left_in_ac, R.anim.push_left_out_ac);
                break;
            case 4:
                // 应用设置
                intent = new Intent(getActivity(), SettingsActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.push_left_in_ac, R.anim.push_left_out_ac);
                break;
            case 5:
                // 检查更新
                AppToast.showToast("正在检查更新……");
                if (!NetworkUtils.isNetworkAvailable(getActivity())) {
                    AppToast.showToast("网络不可用，请检查您的网络设置。");
                    return;
                }
                MoreFragmentPermissionsDispatcher.startCheckUpdateTaskWithCheck(this);
                break;
            case 6:
                // 关于XDPlayer
                intent = new Intent(getActivity(), AboutActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.push_left_in_ac, R.anim.push_left_out_ac);
                break;
            default:
                break;
        }
    }

    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    void startCheckUpdateTask() {
        CheckUpdateTask task = new CheckUpdateTask(new OnCheckUpdateCallback() {
            @Override
            public void onUpdate(UpdateMsg msg) {
                UpdateDialog dialog = new UpdateDialog(getActivity(), msg);
                dialog.show();
            }

            @Override
            public void onNoUpdate() {
                AppToast.showToast("当前版本已是最新。");
            }

            @Override
            public void onNull() {
                AppToast.showToast("检查更新失败。");
            }
        });
        task.execute();
    }

    @OnPermissionDenied(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    void permissionDenied() {
        AppToast.showToast("权限不足，无法检查更新。");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        MoreFragmentPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }
}
