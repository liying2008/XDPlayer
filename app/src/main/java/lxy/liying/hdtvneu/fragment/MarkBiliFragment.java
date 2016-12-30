package lxy.liying.hdtvneu.fragment;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
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
import android.widget.Toast;

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
import lxy.liying.hdtvneu.activity.BiliAcFunVideoPlayer;
import lxy.liying.hdtvneu.activity.WebActivity;
import lxy.liying.hdtvneu.adapter.MarkBiliAdapter;
import lxy.liying.hdtvneu.app.App;
import lxy.liying.hdtvneu.domain.MarkGroup;
import lxy.liying.hdtvneu.domain.MarkItem;
import lxy.liying.hdtvneu.service.task.BiliPNameGetterTask;
import lxy.liying.hdtvneu.service.task.GetAcFunVideoURITask;
import lxy.liying.hdtvneu.service.task.GetBiliVideoURITask;
import lxy.liying.hdtvneu.service.task.GetMarkVideoTask;
import lxy.liying.hdtvneu.service.callback.OnGetBiliPName;
import lxy.liying.hdtvneu.service.callback.OnGetBiliVideoURICallback;
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
 * 描述：Bilibili视频和AcFun视频收藏列表
 * 备注：
 * =======================================================
 */
public class MarkBiliFragment extends LazyFragment implements OnGetMarkVideosCallback {
    private RecyclerView rvMarkBili;
    public TextView tvMarkNone;
    public MarkBiliAdapter markBiliAdapter;
    private ProgressDialog progressDialog;

    private static WeakReference<MarkBiliFragment> markBiliFragment;
    private static List<MarkItem> markBiliItems = new ArrayList<>(1);
    private RecyclerItemClickListener.OnItemClickListener biliListener;

    private ArrayList<DialogMenuItem> mMenuItems = new ArrayList<>();
    /**
     * true：播放模式
     * false：下载模式
     */
    private static boolean playMode = true;
    /**
     * 当前播放或下载的分P
     */
    private static String pName = "";

    @Override
    protected void onCreateViewLazy(Bundle savedInstanceState) {
        super.onCreateViewLazy(savedInstanceState);

        initView();
        initData();
        markBiliFragment = new WeakReference<>(this);
    }

    /**
     * 得到本Fragment的实例
     */
    public static MarkBiliFragment getInstance() {
        if (markBiliFragment != null) {
            return markBiliFragment.get();
        }
        return null;
    }

    private void initView() {
        setContentView(R.layout.fragment_mark_bili);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("正在查找收藏列表...");
        progressDialog.show();
        rvMarkBili = (RecyclerView) findViewById(R.id.rvMarkBili);

        rvMarkBili.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvMarkBili.setItemAnimator(new DefaultItemAnimator());

        tvMarkNone = (TextView) findViewById(R.id.tvMarkNone);
    }

    private void initData() {
        markBiliItems.clear();

        markBiliAdapter = new MarkBiliAdapter(getActivity(), markBiliItems);
        rvMarkBili.setAdapter(markBiliAdapter);

        setRecyclerListener();
        rvMarkBili.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), rvMarkBili, biliListener));
        mMenuItems.add(new DialogMenuItem("从收藏中移除", R.drawable.ic_menu_delete));
        mMenuItems.add(new DialogMenuItem("下载视频", R.drawable.ic_menu_download));
        mMenuItems.add(new DialogMenuItem("从网页播放", R.drawable.ic_menu_webplayer));
        mMenuItems.add(new DialogMenuItem("修改视频名称", R.drawable.ic_menu_modify));
        mMenuItems.add(new DialogMenuItem("分享该视频", R.drawable.ic_menu_share));


        if (App.biliMarkItems == null) {
            GetMarkVideoTask task = new GetMarkVideoTask(this);
            task.execute();
        } else {
            onGetBiliMark(App.biliMarkItems);
        }
    }

    /**
     * 给RecyclerView设置点击事件监听
     */
    private void setRecyclerListener() {
        this.biliListener = new RecyclerItemClickListener.OnItemClickListener() {

            @Override
            public void onItemClick(View view, int position) {
                if (!NetworkUtils.isNetworkAvailable(MarkBiliFragment.this.getActivity())) {
                    AppToast.showToast("网络不可用，请检查您的网络设置。");
                    return;
                }
                MarkItem item = markBiliItems.get(position);
                playMode = true;
                App.programType = "-3"; // 节目类型
                if (item.getGroup() == MarkGroup.BILI) {
                    playBiliVideo(item);
                } else if (item.getGroup() == MarkGroup.ACFUN) {
                    playAcFunVideo(item);
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {
                final MarkItem item = markBiliItems.get(position);
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
                            playMode = false;
                            if (item.getGroup() == MarkGroup.BILI) {
                                playBiliVideo(item);
                            } else if (item.getGroup() == MarkGroup.ACFUN) {
                                playAcFunVideo(item);
                            }
                        } else if (position == 2) {
                            // 从网页播放
                            String path = item.getPath();
                            if (item.getGroup() == MarkGroup.BILI) {
                                path = "http://www.bilibili.com/mobile/video/av" + path + ".html";
                            } else if (item.getGroup() == MarkGroup.ACFUN) {
                                path = "http://m.acfun.tv/v/?ac=" + path.replace("ac", "");
                            }
                            Intent intent = new Intent(getActivity(), WebActivity.class);
                            intent.putExtra("web", path);
                            startActivity(intent);
                        } else if (position == 3) {
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
                                                item.getCoverPath(), item.getGroup(), item.getPath(), item.getFolder(), 0L);
                                        App.markService.updateMarkItemName(item.getMarkId(), title);
                                        AppToast.showToast("视频标题已修改。");
                                        // 刷新App中的onlineMarkItems
                                        int location = App.biliMarkItems.indexOf(item);
                                        App.biliMarkItems.remove(location);
                                        App.biliMarkItems.add(location, newItem);
                                        // 刷新MarkListFragment
                                        MarkBiliFragment.getInstance().markBiliAdapter.updateData(item, newItem);
                                    }
                                    dialog.dismiss();
                                }
                            });
                        } else if (position == 4) {
                            // 分享视频
                            Intent shareIntent = new Intent();
                            shareIntent.setAction(Intent.ACTION_SEND);
                            shareIntent.setType("text/*");
                            String path = item.getPath();
                            if (item.getGroup() == MarkGroup.BILI) {
                                path = "http://www.bilibili.com/mobile/video/av" + path + ".html";
                            } else if (item.getGroup() == MarkGroup.ACFUN) {
                                path = "http://m.acfun.tv/v/?ac=" + path.replace("ac", "");
                            }
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
     * 播放AcFun视频
     *
     * @param item
     */
    private void playAcFunVideo(final MarkItem item) {
        // 获取视频下载地址（在线播放地址）
        if (playMode) {
            AppToast.showToast("正在获取视频播放地址……");
        } else {
            AppToast.showToast("正在获取视频下载地址……");
        }
        GetAcFunVideoURITask task = new GetAcFunVideoURITask(new OnGetBiliVideoURICallback() {
            @Override
            public void getURI(ArrayList<String> urls) {
                if (urls.size() == 0) {
                    if (playMode) {
                        AppToast.showToast("无法获取播放地址。", Toast.LENGTH_LONG);
                    } else {
                        AppToast.showToast("无法获取下载地址。", Toast.LENGTH_LONG);
                    }
                    return;
                }
                if (playMode) {
                    // 播放视频
                    Intent intent = new Intent(MarkBiliFragment.this.getActivity(), BiliAcFunVideoPlayer.class);
                    intent.putStringArrayListExtra(Constants.VIDEO_URL, urls);
                    startActivity(intent);
                } else {
                    // 下载视频
                    if (urls.size() == 1) {
                        String fileName = item.getName() + FileUtils.getUrlSuffix(urls.get(0));
                        fileName = fileName.replace("/", "&").replace("\\", "&");
                        String path = Constants.ACFUN_DOWNLOAD + File.separator + fileName;
                        Downloader downloader = new Downloader(getActivity(), fileName, MarkGroup.ACFUN.getName(), path);
                        downloader.addDownloader(urls.get(0), path);
                        if (App.downloaders.size() == 0) {
                            downloader.startTask();
                            AppToast.showToast("开始下载 " + fileName);
                            App.downloaders.add(downloader);
                        } else {
                            AppToast.showToast("已添加到下载队列中 " + fileName);
                            App.waitDownloaders.add(downloader);
                        }
                    } else {
                        for (int i = 0; i < urls.size(); i++) {
                            String fileName = item.getName() + "_" + i + FileUtils.getUrlSuffix(urls.get(i));
                            fileName = fileName.replace("/", "&").replace("\\", "&");
                            String path = Constants.ACFUN_DOWNLOAD + File.separator + fileName;
                            Downloader downloader = new Downloader(getActivity(), fileName, MarkGroup.ACFUN.getName(), path);
                            downloader.addDownloader(urls.get(i), path);
                            if (App.downloaders.size() == 0) {
                                downloader.startTask();
                                AppToast.showToast("开始下载 " + fileName);
                                App.downloaders.add(downloader);
                            } else {
                                AppToast.showToast("已添加到下载队列中 " + fileName);
                                App.waitDownloaders.add(downloader);
                            }
                        }
                    }
                }
            }

            @Override
            public void onUriFailure() {
                // 获取视频播放地址失败
                if (playMode) {
                    AppToast.showToast("获取视频播放地址失败。");
                } else {
                    AppToast.showToast("获取视频下载地址失败。");
                }
            }
        });
        task.execute(item.getPath());
    }

    /**
     * 播放哔哩哔哩视频
     *
     * @param item
     */
    private void playBiliVideo(final MarkItem item) {
        // 查找是否有多P
        AppToast.showToast("检查分P信息……");
        BiliPNameGetterTask task = new BiliPNameGetterTask(new OnGetBiliPName() {
            @Override
            public void getPNames(final String av, final List<String> pNames) {
                final String[] items = new String[pNames.size()];
                for (int i = 0; i < pNames.size(); i++) {
                    items[i] = pNames.get(i);
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(MarkBiliFragment.this.getActivity());
                if (playMode) {
                    builder.setTitle("请选择一P播放");
                } else {
                    builder.setTitle("请选择一P下载");
                }
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, final int which) {
                        if (playMode) {
                            AppToast.showToast("正在获取视频播放地址……");
                        } else {
                            AppToast.showToast("正在获取视频下载地址……");
                        }
                        pName = items[which];
                        final GetBiliVideoURITask task = new GetBiliVideoURITask(new OnGetBiliVideoURICallback() {
                            @Override
                            public void getURI(ArrayList<String> urls) {
                                if (urls.size() == 0) {
                                    if (playMode) {
                                        AppToast.showToast("无法获取播放地址。", Toast.LENGTH_LONG);
                                    } else {
                                        AppToast.showToast("无法获取下载地址。", Toast.LENGTH_LONG);
                                    }
                                    return;
                                }
                                if (playMode) {
                                    // 播放视频
                                    Intent intent = new Intent(getActivity(), BiliAcFunVideoPlayer.class);
                                    intent.putStringArrayListExtra(Constants.VIDEO_URL, urls);
                                    startActivity(intent);
                                } else {
                                    // 下载视频
                                    if (urls.size() == 1) {
                                        String fileName;
                                        fileName = item.getName() + "_" + pName + FileUtils.getUrlSuffix(urls.get(0));
                                        fileName = fileName.replace("/", "&").replace("\\", "&");
                                        String path = Constants.BILI_DOWNLOAD + File.separator + fileName;
                                        Downloader downloader = new Downloader(getActivity(), fileName, MarkGroup.BILI.getName(), path);
                                        downloader.addDownloader(urls.get(0), path);
                                        if (App.downloaders.size() == 0) {
                                            downloader.startTask();
                                            AppToast.showToast("开始下载 " + fileName);
                                            App.downloaders.add(downloader);
                                        } else {
                                            AppToast.showToast("已添加到下载队列中 " + fileName);
                                            App.waitDownloaders.add(downloader);
                                        }
                                    } else {
                                        for (int i = 0; i < urls.size(); i++) {
                                            String fileName;
                                            fileName = item.getName() + "_" + pName + FileUtils.getUrlSuffix(urls.get(0));
                                            fileName = fileName.replace("/", "&").replace("\\", "&");
                                            String path = Constants.BILI_DOWNLOAD + File.separator + fileName;
                                            Downloader downloader = new Downloader(getActivity(), fileName, MarkGroup.BILI.getName(), path);
                                            downloader.addDownloader(urls.get(i), path);
                                            if (App.downloaders.size() == 0) {
                                                downloader.startTask();
                                                AppToast.showToast("开始下载 " + fileName);
                                                App.downloaders.add(downloader);
                                            } else {
                                                AppToast.showToast("已添加到下载队列中 " + fileName);
                                                App.waitDownloaders.add(downloader);
                                            }
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onUriFailure() {
                                // 获取视频播放地址失败
                                if (playMode) {
                                    AppToast.showToast("获取视频播放地址失败。");
                                } else {
                                    AppToast.showToast("获取视频下载地址失败。");
                                }
                            }
                        });
                        task.execute(av, String.valueOf(which + 1));
                    }
                });
                builder.create().show();

            }

            @Override
            public void noP(String av) {
                // 获取视频下载地址（在线播放地址）
                pName = "";
                if (playMode) {
                    AppToast.showToast("正在获取视频播放地址……");
                } else {
                    AppToast.showToast("正在获取视频下载地址……");
                }
                GetBiliVideoURITask task = new GetBiliVideoURITask(new OnGetBiliVideoURICallback() {
                    @Override
                    public void getURI(ArrayList<String> urls) {
                        if (urls.size() == 0) {
                            if (playMode) {
                                AppToast.showToast("无法获取播放地址。", Toast.LENGTH_LONG);
                            } else {
                                AppToast.showToast("无法获取下载地址。", Toast.LENGTH_LONG);
                            }
                            return;
                        }
                        if (playMode) {
                            // 播放视频
                            Intent intent = new Intent(getActivity(), BiliAcFunVideoPlayer.class);
                            intent.putStringArrayListExtra(Constants.VIDEO_URL, urls);
                            startActivity(intent);
                        } else {
                            // 下载视频
                            if (urls.size() == 1) {
                                String fileName = item.getName() + FileUtils.getUrlSuffix(urls.get(0));
                                fileName = fileName.replace("/", "&").replace("\\", "&");
                                String path = Constants.BILI_DOWNLOAD + File.separator + fileName;
                                Downloader downloader = new Downloader(getActivity(), fileName,
                                        MarkGroup.BILI.getName(), path);
                                downloader.addDownloader(urls.get(0), path);
                                if (App.downloaders.size() == 0) {
                                    downloader.startTask();
                                    AppToast.showToast("开始下载 " + fileName);
                                    App.downloaders.add(downloader);
                                } else {
                                    AppToast.showToast("已添加到下载队列中 " + fileName);
                                    App.waitDownloaders.add(downloader);
                                }
                            } else {
                                for (int i = 0; i < urls.size(); i++) {
                                    String fileName = item.getName() + "_" + i + FileUtils.getUrlSuffix(urls.get(i));
                                    fileName = fileName.replace("/", "&").replace("\\", "&");
                                    String path = Constants.BILI_DOWNLOAD + File.separator + fileName;
                                    Downloader downloader = new Downloader(getActivity(), fileName,
                                            MarkGroup.BILI.getName(), path);
                                    downloader.addDownloader(urls.get(i), path);
                                    if (App.downloaders.size() == 0) {
                                        downloader.startTask();
                                        AppToast.showToast("开始下载 " + fileName);
                                        App.downloaders.add(downloader);
                                    } else {
                                        AppToast.showToast("已添加到下载队列中 " + fileName);
                                        App.waitDownloaders.add(downloader);
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onUriFailure() {
                        // 获取视频播放地址失败
                        if (playMode) {
                            AppToast.showToast("获取视频播放地址失败。");
                        } else {
                            AppToast.showToast("获取视频下载地址失败。");
                        }
                    }
                });
                task.execute(av, "0");
            }
        });
        task.execute(item.getPath());
    }

    /**
     * 删除收藏
     *
     * @param item MarkItem
     */
    private void removeMark(MarkItem item) {
        App.markService.removeMark(item.getMarkId());
        AppToast.showToast("已移除。");
        markBiliAdapter.delData(item);
        markBiliAdapter.notifyDataSetChanged();
        markBiliItems.remove(item);
        if (markBiliItems.size() == 0) {
            tvMarkNone.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onGetIPv6Mark(List<MarkItem> ipv6MarkItems) {
    }

    @Override
    public void onGetBiliMark(List<MarkItem> biliMarkItems) {
        progressDialog.dismiss();
        if (biliMarkItems != null && biliMarkItems.size() > 0) {
            tvMarkNone.setVisibility(View.GONE);
            markBiliItems.addAll(biliMarkItems);
            markBiliAdapter.setData(markBiliItems);
            markBiliAdapter.notifyDataSetChanged();
        }
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
