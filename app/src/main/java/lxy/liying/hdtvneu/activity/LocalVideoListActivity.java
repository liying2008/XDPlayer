package lxy.liying.hdtvneu.activity;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.Video.Media;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flyco.dialog.entity.DialogMenuItem;
import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.listener.OnOperItemClickL;
import com.flyco.dialog.widget.NormalDialog;
import com.flyco.dialog.widget.NormalListDialog;

import java.io.File;
import java.util.ArrayList;

import cc.duduhuo.applicationtoast.AppToast;
import lxy.liying.hdtvneu.R;
import lxy.liying.hdtvneu.adapter.LocalListVideoAdapter;
import lxy.liying.hdtvneu.app.App;
import lxy.liying.hdtvneu.dialog.XDVideoInfoDialog;
import lxy.liying.hdtvneu.domain.MarkGroup;
import lxy.liying.hdtvneu.domain.MarkItem;
import lxy.liying.hdtvneu.domain.XDVideo;
import lxy.liying.hdtvneu.fragment.LocalListFragment;
import lxy.liying.hdtvneu.fragment.MarkLocalFragment;
import lxy.liying.hdtvneu.service.task.GetCacheVideoListTask;
import lxy.liying.hdtvneu.utils.Constants;

/**
 * =======================================================
 * 版权：©Copyright LiYing 2015-2016. All rights reserved.
 * 作者：liying
 * 日期：2016/8/17 21:10
 * 版本：1.0
 * 描述：本地视频列表Activity
 * 备注：
 * =======================================================
 */
public class LocalVideoListActivity extends BaseActivity implements LocalListVideoAdapter.OnItemClickListener, LocalListVideoAdapter.OnItemLongClickListener {
    private LocalListVideoAdapter adapter;

    private ArrayList<DialogMenuItem> mMenuItems = new ArrayList<>();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_video_list);

        TextView tvListInfo = (TextView) findViewById(R.id.tvListInfo);
        RecyclerView rvVideoList = (RecyclerView) findViewById(R.id.rvVideoList);
        LinearLayout llBack = (LinearLayout) findViewById(R.id.llBack);
        llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.push_right_in_ac, R.anim.push_right_out_ac);
            }
        });

        Intent intent = getIntent();
        String folder = intent.getStringExtra(Constants.FOLDER);
        tvListInfo.setText(folder);
        rvVideoList.setLayoutManager(new LinearLayoutManager(this));
        adapter = new LocalListVideoAdapter(this, App.xdVideos);
        rvVideoList.setAdapter(adapter);
        adapter.setOnItemClickListener(this);
        adapter.setOnItemLongClickListener(this);

        mMenuItems.add(new DialogMenuItem("收藏该视频", R.drawable.ic_menu_mark));
        mMenuItems.add(new DialogMenuItem("删除视频文件", R.drawable.ic_menu_delete));
        mMenuItems.add(new DialogMenuItem("查看视频详细信息", R.drawable.ic_menu_info));
        mMenuItems.add(new DialogMenuItem("分享该视频", R.drawable.ic_menu_share));
    }

    @Override
    public void onItemClick(View view, int position) {
        XDVideo video = App.xdVideos.get(position);
        if (!video.equals(App.lastXdVideo)) {
            // 保存当前视频ID
            App.lastXdVideo = video;
            App.getInstance().putSetting(Constants.LAST_VIDEO_ID, String.valueOf(video.get_id()));
            // 更新当前选中列表的颜色
            refreshListColor();
        }
        // 打开播放界面
        Intent intent = new Intent(this, LocalVideoPlayer.class);
        intent.putExtra(Constants.XDVIDEO, video);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Constants.REFRESH_LIST) {
            refreshListColor();
        }
    }

    /**
     * 更新当前选中列表的颜色
     */
    private void refreshListColor() {
        adapter.notifyDataSetChanged();
        LocalListFragment.getInstance().adapter.notifyDataSetChanged();
    }

    /**
     * 长按列表项
     *
     * @param view
     * @param position
     */
    @Override
    public void onItemLongClick(View view, final int position) {
        final XDVideo video = App.xdVideos.get(position);
        final NormalListDialog dialog = new NormalListDialog(this, mMenuItems);
        dialog.title("请选择")//
                .showAnim(App.mBasIn)//
                .dismissAnim(App.mBasOut)//
                .show();
        dialog.setOnOperItemClickL(new OnOperItemClickL() {
            @Override
            public void onOperItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        // 收藏该视频
                        if (App.markService.getMarkByPath(video.getData()) == null) {
                            MarkItem item = new MarkItem(System.currentTimeMillis(), video.getTitle(), video.getVideoThumbnail(),
                                    MarkGroup.LOCAL, video.getData(), video.getFolder(), video.getPosition());
                            App.markService.addMarkVideo(item);
                            AppToast.showToast("视频已收藏。");
                            if (App.localMarkItems != null) {
                                App.localMarkItems.add(item);
                            }
                            if (MarkLocalFragment.getInstance() != null) {
                                // 刷新MarkListFragment
                                MarkLocalFragment.getInstance().markLocalAdapter.addData(item);
                                MarkLocalFragment.getInstance().markLocalAdapter.notifyDataSetChanged();
                                MarkLocalFragment.getInstance().tvMarkNone.setVisibility(View.GONE);
                            }
                        } else {
                            AppToast.showToast("视频已在收藏列表中。");
                        }
                        break;
                    case 1:
                        // 删除视频文件
                        final NormalDialog dialog = App.getNormalDialog(LocalVideoListActivity.this,
                                "确定删除该视频?\n(同时从SD卡上删除)");
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
                                        deleteXDVideo(video);
                                        dialog.dismiss();
                                    }
                                });
                        break;
                    case 2:
                        // 查看视频详细信息
                        XDVideoInfoDialog dialog1 = new XDVideoInfoDialog(LocalVideoListActivity.this, video);
                        dialog1.show();
                        break;
                    case 3:
                        // 分享该视频
                        Intent shareIntent = new Intent();
                        shareIntent.setAction(Intent.ACTION_SEND);
                        shareIntent.setType("video/*");
                        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(video.getData())));
                        ComponentName cn = shareIntent.resolveActivity(getPackageManager());
                        if (cn != null) {
                            startActivity(Intent.createChooser(shareIntent, getString(R.string.share_to)));
                        } else {
                            AppToast.showToast("无法分享。");
                        }
                        break;
                    default:
                        break;
                }
                dialog.dismiss();
            }
        });
    }


    /**
     * 删除XDVideo
     *
     * @param video
     */
    private void deleteXDVideo(XDVideo video) {
        // 删除视频文件
        ContentResolver contentResolver = getContentResolver();
        contentResolver.delete(Media.EXTERNAL_CONTENT_URI, Media._ID + "=" + video.get_id(), null);
        // 删除数据库中该视频文件信息
        App.xdService.removeVideo(video.get_id());
        // 刷新列表
        GetCacheVideoListTask task = new GetCacheVideoListTask(LocalListFragment.getInstance());
        task.execute();
        App.xdVideos.remove(video);
        adapter.setData(App.xdVideos);
        adapter.notifyDataSetChanged();
    }
}
