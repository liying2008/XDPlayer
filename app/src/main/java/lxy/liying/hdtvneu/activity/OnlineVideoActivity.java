package lxy.liying.hdtvneu.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.io.File;

import cc.duduhuo.applicationtoast.AppToast;
import lxy.liying.hdtvneu.R;
import lxy.liying.hdtvneu.app.App;
import lxy.liying.hdtvneu.domain.MarkGroup;
import lxy.liying.hdtvneu.domain.MarkItem;
import lxy.liying.hdtvneu.fragment.MarkOnlineFragment;
import lxy.liying.hdtvneu.utils.Constants;
import lxy.liying.hdtvneu.utils.Downloader;
import lxy.liying.hdtvneu.utils.FileUtils;
import lxy.liying.hdtvneu.utils.NetworkUtils;
import lxy.liying.hdtvneu.utils.WebUrlUtil;

/**
 * =======================================================
 * 作者：liying
 * 日期：2016/8/23 15:47
 * 版本：1.0
 * 描述：播放在线视频
 * 备注：
 * =======================================================
 */
public class OnlineVideoActivity extends BaseActivity {
    private EditText etVideoUrl;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_video);
        etVideoUrl = (EditText) findViewById(R.id.etVideoUrl);
        RelativeLayout rlOnlinePlay = (RelativeLayout) findViewById(R.id.rlOnlinePlay);
        RelativeLayout rlOnlineDownload = (RelativeLayout) findViewById(R.id.rlOnlineDownload);
        RelativeLayout rlOnlineMark = (RelativeLayout) findViewById(R.id.rlOnlineMark);
        LinearLayout llBack = (LinearLayout) findViewById(R.id.llBack);
        llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.push_right_in_ac, R.anim.push_right_out_ac);
            }
        });

        rlOnlinePlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = etVideoUrl.getText().toString();
                if (!url.equals("")) {
                    if (!NetworkUtils.isNetworkAvailable(OnlineVideoActivity.this)) {
                        AppToast.showToast("网络不可用，请检查您的网络设置。");
                        return;
                    }
                    App.programType = "-1"; // 节目类型
                    Intent intent = new Intent(OnlineVideoActivity.this, OnlineVideoPlayer.class);
                    intent.putExtra(Constants.VIDEO_URL, url);
                    startActivity(intent);
                } else {
                    AppToast.showToast("URL不能为空。");
                }
            }
        });

        rlOnlineDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String url = etVideoUrl.getText().toString();
                if (!url.equals("")) {
                    // 下载视频
                    AlertDialog.Builder builder = App.getAlertDialogBuilder(OnlineVideoActivity.this);
                    builder.setTitle("视频标题");
                    View view = getLayoutInflater().inflate(R.layout.dialog_mark_title, null);
                    builder.setView(view);
                    final AlertDialog dialog = builder.create();
                    dialog.show();
                    final EditText etVideoTitle = (EditText) view.findViewById(R.id.etVideoTitle);
                    RelativeLayout rlOK = (RelativeLayout) view.findViewById(R.id.rlOK);
                    rlOK.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String title = etVideoTitle.getText().toString();
                            if (title.equals("")) {
                                AppToast.showToast("视频标题不可为空。");
                            } else {
                                String host = WebUrlUtil.getHost(url);
                                MarkItem item = new MarkItem(System.currentTimeMillis(), title,
                                    null, MarkGroup.ONLINE, url, host, 0L);
                                String fileName = item.getName() + FileUtils.getUrlSuffix(item.getPath());
                                fileName = fileName.replace("/", "&").replace("\\", "&");
                                String path = Constants.ONLINE_DOWNLOAD + File.separator + fileName;
                                Downloader downloader = new Downloader(OnlineVideoActivity.this,
                                    fileName, item.getFolder(), path);
                                downloader.addDownloader(item.getPath(), path);
                                if (App.downloaders.size() == 0) {
                                    downloader.startTask();
                                    AppToast.showToast("开始下载 " + fileName);
                                    App.downloaders.add(downloader);
                                } else {
                                    AppToast.showToast("已添加到下载队列中 " + fileName);
                                    App.waitDownloaders.add(downloader);
                                }
                            }
                            dialog.dismiss();
                        }
                    });
                } else {
                    AppToast.showToast("URL不能为空。");
                }
            }
        });

        rlOnlineMark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String url = etVideoUrl.getText().toString();
                if (!url.equals("")) {
                    // 收藏视频
                    if (App.markService.getMarkByPath(url) == null) {
                        AlertDialog.Builder builder = App.getAlertDialogBuilder(OnlineVideoActivity.this);
                        builder.setTitle("视频标题");
                        View view = getLayoutInflater().inflate(R.layout.dialog_mark_title, null);
                        builder.setView(view);
                        final AlertDialog dialog = builder.create();
                        dialog.show();
                        final EditText etVideoTitle = (EditText) view.findViewById(R.id.etVideoTitle);
                        RelativeLayout rlOK = (RelativeLayout) view.findViewById(R.id.rlOK);
                        rlOK.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String title = etVideoTitle.getText().toString();
                                if (title.equals("")) {
                                    AppToast.showToast("视频标题不可为空。");
                                } else {
                                    String host = WebUrlUtil.getHost(url);
                                    MarkItem item = new MarkItem(System.currentTimeMillis(), title,
                                        null, MarkGroup.ONLINE, url, host, 0L);
                                    App.markService.addMarkVideo(item);
                                    AppToast.showToast("视频已收藏。");
                                    if (App.onlineMarkItems != null) {
                                        App.onlineMarkItems.add(item);
                                    }
                                    MarkOnlineFragment fragment = MarkOnlineFragment.getInstance();
                                    if (fragment != null) {
                                        // 刷新MarkListFragment
                                        fragment.markOnlineAdapter.addData(item);
                                        fragment.markOnlineAdapter.notifyDataSetChanged();
                                        fragment.tvMarkNone.setVisibility(View.GONE);
                                    }
                                }
                                dialog.dismiss();
                            }
                        });
                    } else {
                        AppToast.showToast("视频已在收藏列表中。");
                    }
                } else {
                    AppToast.showToast("URL不能为空。");
                }
                App.autoInputPanel(OnlineVideoActivity.this);   // 弹出输入法面板
            }
        });
        App.autoInputPanel(this);   // 弹出输入法面板
    }
}
