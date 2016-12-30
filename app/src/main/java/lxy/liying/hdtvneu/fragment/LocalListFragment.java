package lxy.liying.hdtvneu.fragment;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Toast;

import com.flyco.dialog.entity.DialogMenuItem;
import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.listener.OnOperItemClickL;
import com.flyco.dialog.widget.NormalDialog;
import com.flyco.dialog.widget.NormalListDialog;
import com.shizhefei.fragment.LazyFragment;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import cc.duduhuo.applicationtoast.AppToast;
import lxy.liying.hdtvneu.R;
import lxy.liying.hdtvneu.activity.LocalVideoListActivity;
import lxy.liying.hdtvneu.adapter.LocalListAdapter;
import lxy.liying.hdtvneu.app.App;
import lxy.liying.hdtvneu.dialog.SearchDialog;
import lxy.liying.hdtvneu.domain.VideoFolder;
import lxy.liying.hdtvneu.domain.XDVideo;
import lxy.liying.hdtvneu.service.callback.OnGetVideoFolderListener;
import lxy.liying.hdtvneu.service.task.GetAllVideoFolderTask;
import lxy.liying.hdtvneu.service.task.GetCacheVideoListTask;
import lxy.liying.hdtvneu.service.task.RefreshMediaDBTask;
import lxy.liying.hdtvneu.utils.Constants;
import lxy.liying.hdtvneu.utils.DensityUtil;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.RuntimePermissions;

/**
 * =======================================================
 * 版权：©Copyright LiYing 2015-2016. All rights reserved.
 * 作者：liying
 * 日期：2016/8/14 14:42
 * 版本：1.0
 * 描述：本地播放列表
 * 备注：
 * =======================================================
 */
@RuntimePermissions
public class LocalListFragment extends LazyFragment implements OnGetVideoFolderListener, LocalListAdapter.OnItemClickListener, LocalListAdapter.OnItemLongClickListener {
    public LocalListAdapter adapter;
    private List<VideoFolder> videoFolders;
    private ImageView ivSearch, ivRefresh;
    private static final int ROTATE = 0x0000;
    private static final int STOP_ROTATE = 0x0001;
    private static final int REFRESH_FOLDER = 0x0002;
    private int rotateDegree = 0;
    private static boolean searchFinish = false;
    private static WeakReference<LocalListFragment> mInstance;
    private ArrayList<DialogMenuItem> mMenuItems = new ArrayList<>();

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == ROTATE) {
                ivRefresh.setRotation(rotateDegree);
                rotateDegree += 6;
            } else if (msg.what == STOP_ROTATE) {
                rotateDegree = 0;
                ivRefresh.setBackgroundResource(R.drawable.bar_refresh_selector);
            } else if (msg.what == REFRESH_FOLDER) {
                adapter.deleteData((VideoFolder) msg.getData().getSerializable("folder"));
            }
            return false;
        }
    });

    @Override
    protected void onCreateViewLazy(Bundle savedInstanceState) {
        super.onCreateViewLazy(savedInstanceState);
        setContentView(R.layout.fragment_local_list);

        RecyclerView rvLocalList = (RecyclerView) findViewById(R.id.rvLocalList);
        ivSearch = (ImageView) findViewById(R.id.ivSearch);
        ivRefresh = (ImageView) findViewById(R.id.ivRefresh);
        rvLocalList.setLayoutManager(new LinearLayoutManager(getActivity()));
        videoFolders = new ArrayList<>();
        adapter = new LocalListAdapter(getActivity(), videoFolders);
        rvLocalList.setAdapter(adapter);

        adapter.setOnItemClickListener(this);
        adapter.setOnItemLongClickListener(this);
        ivRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 刷新视频列表数据
                AppToast.showToast("正在刷新媒体数据库，请耐心等待……", Toast.LENGTH_LONG);
                RefreshMediaDBTask task = new RefreshMediaDBTask(getActivity());
                task.execute();

                searchFinish = false;
                new MyThread().start();
            }
        });
        // 长按弹Toast
        ivRefresh.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast toast = AppToast.getToast();
                toast.setGravity(Gravity.TOP, (int) v.getX() - DensityUtil.dip2px(getActivity(), 10),
                        (int) v.getY() + DensityUtil.dip2px(getActivity(), 20));
                toast.setText("刷新视频列表");
                toast.show();
                return true;
            }
        });

        ivSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchDialog dialog = new SearchDialog(LocalListFragment.this.getActivity());
                dialog.show();
            }
        });

        // 长按弹Toast
        ivSearch.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast toast = AppToast.getToast();
                toast.setGravity(Gravity.TOP, (int) v.getX() - DensityUtil.dip2px(getActivity(), 10),
                        (int) v.getY() + DensityUtil.dip2px(getActivity(), 20));
                toast.setText("搜索本地视频");
                toast.show();
                return true;
            }
        });

        if (App.hasVideoListCache.equals("true")) {
            // 先读取缓存
            GetCacheVideoListTask task = new GetCacheVideoListTask(this);
            task.execute();
        } else {
            // 获取权限
            LocalListFragmentPermissionsDispatcher.startGetAllVideoFolderTaskWithCheck(this);
        }
        mMenuItems.add(new DialogMenuItem("删除文件夹", R.drawable.ic_menu_delete));
        mInstance = new WeakReference<>(this);
    }

    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    void startGetAllVideoFolderTask() {
        AppToast.showToast("正在搜索视频文件……");
        new MyThread().start();
        GetAllVideoFolderTask task = new GetAllVideoFolderTask(getActivity(), this);
        task.execute();
    }

    @OnPermissionDenied(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    void permissionDenied() {
        AppToast.showToast("权限不足，无法扫描存储卡中的视频文件。");
    }
    /**
     * 得到该Fragment实例
     *
     * @return 当前Fragment的实例
     */
    public static LocalListFragment getInstance() {
        if (mInstance != null) {
            return mInstance.get();
        }
        return null;
    }

    /**
     * 开始扫描媒体数据库
     */
    public void startScanMediaDB() {
        videoFolders.clear();
        adapter.setData(videoFolders);
        adapter.notifyDataSetChanged();
        // 获取权限
        LocalListFragmentPermissionsDispatcher.startGetAllVideoFolderTaskWithCheck(this);
    }


    /**
     * 刷新按钮旋转
     */
    class MyThread extends Thread {
        @Override
        public void run() {
            while (!searchFinish) {
                handler.sendEmptyMessage(ROTATE);
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onVideoFolderGot(VideoFolder folder) {
        videoFolders.add(new VideoFolder(folder.getName(), folder.getCount(), folder.getXdVideos()));
        adapter.setData(videoFolders);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onComplete(List<VideoFolder> videoFolderList) {
        searchFinish = true;
        handler.sendEmptyMessage(STOP_ROTATE);
        if (videoFolderList != null) {
            videoFolders = videoFolderList;
            adapter.setData(videoFolderList);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        // 打开视频列表页面
        App.xdVideos = videoFolders.get(position).getXdVideos();
        App.programType = "5";
        Intent intent = new Intent(getActivity(), LocalVideoListActivity.class);
        intent.putExtra(Constants.FOLDER, videoFolders.get(position).getName());
        getActivity().startActivity(intent);
        getActivity().overridePendingTransition(R.anim.push_left_in_ac, R.anim.push_left_out_ac);
    }

    @Override
    public void onItemLongClick(View view, final int position) {
        final NormalListDialog dialog = new NormalListDialog(getActivity(), mMenuItems);
        dialog.title("请选择")//
                .showAnim(App.mBasIn)//
                .dismissAnim(App.mBasOut)//
                .show();
        dialog.setOnOperItemClickL(new OnOperItemClickL() {
            @Override
            public void onOperItemClick(AdapterView<?> parent, View view, int location, long id) {
                if (location == 0) {
                    final NormalDialog dialog = App.getNormalDialog(getActivity(),
                            "确定要删除该文件夹下的所有视频？\n(同时从SD中删除)");
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
                                    deleteXDVideos(videoFolders.get(position));
                                    dialog.dismiss();
                                }
                            });
                }
                dialog.dismiss();
            }
        });
    }

    /**
     * 删除XDVideos
     * @param videoFolder
     */
    private void deleteXDVideos(final VideoFolder videoFolder) {
        // 删除视频文件
        final List<XDVideo> videos = videoFolder.getXdVideos();
        AppToast.showToast("删除中……");
        new Thread(new Runnable() {
            @Override
            public void run() {
                ContentResolver contentResolver = getActivity().getContentResolver();
                for (XDVideo video : videos) {
                    contentResolver.delete(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, MediaStore.Video.Media._ID + "=" + video.get_id(), null);
                }
                // 删除数据库表中该文件夹中的视频文件信息
                App.xdService.removeVideos(videoFolder.getName());
                Message message = Message.obtain(handler);
                Bundle bundle = new Bundle();
                bundle.putSerializable("folder", videoFolder);
                message.setData(bundle);
                message.what = REFRESH_FOLDER;
                message.sendToTarget();
            }
        }).start();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        LocalListFragmentPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }
}
