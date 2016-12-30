package lxy.liying.hdtvneu.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flyco.dialog.entity.DialogMenuItem;
import com.flyco.dialog.listener.OnOperItemClickL;
import com.flyco.dialog.widget.NormalListDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cc.duduhuo.applicationtoast.AppToast;
import lxy.liying.hdtvneu.R;
import lxy.liying.hdtvneu.adapter.AcFunVideoListAdapter;
import lxy.liying.hdtvneu.app.App;
import lxy.liying.hdtvneu.domain.AcFunVideo;
import lxy.liying.hdtvneu.domain.MarkGroup;
import lxy.liying.hdtvneu.domain.MarkItem;
import lxy.liying.hdtvneu.fragment.MarkBiliFragment;
import lxy.liying.hdtvneu.service.task.GetAcFunVideoURITask;
import lxy.liying.hdtvneu.service.callback.OnGetAcFunVideoCallback;
import lxy.liying.hdtvneu.service.callback.OnGetBiliVideoURICallback;
import lxy.liying.hdtvneu.service.task.ParseAcFunVideosTask;
import lxy.liying.hdtvneu.utils.Constants;
import lxy.liying.hdtvneu.utils.Downloader;
import lxy.liying.hdtvneu.utils.FileUtils;
import lxy.liying.hdtvneu.utils.NetworkUtils;

/**
 * =======================================================
 * 版权：©Copyright LiYing 2015-2016. All rights reserved.
 * 作者：liying
 * 日期：2016/9/8 9:00
 * 版本：1.0
 * 描述：AcFun视频搜索界面
 * 备注：
 * =======================================================
 */
public class AcFunMainActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener, OnGetAcFunVideoCallback, AcFunVideoListAdapter.OnItemClickListener, AcFunVideoListAdapter.OnItemLongClickListener, OnGetBiliVideoURICallback {
    private EditText etKeyword;
    private ImageView ivSearch;
    private LinearLayout llBack;
    private RecyclerView rvVideos;
    private AcFunVideo currVideo;
    private AcFunVideoListAdapter adapter;
    private int page = 1;
    /**
     * 是否已经加载了全部视频
     */
    private static boolean isAll = false;

    private List<AcFunVideo> acFunVideos = new ArrayList<>(1);
    private SwipeRefreshLayout mSwipeRefreshWidget;
    private static int lastVisibleItem;
    private LinearLayoutManager layoutManager;

    private ArrayList<DialogMenuItem> mMenuItems = new ArrayList<>();
    /**
     * PLAY_MODE：播放模式
     * DOWNLOAD_MODE：下载模式
     * SHARE_MODE：分享模式
     */
    private static int mode = 0x0000;
    private static final int PLAY_MODE = 0x0000;
    private static final int DOWNLOAD_MODE = 0x0001;
    private static final int SHARE_MODE = 0x0002;

    /**
     * 查找关键词
     */
    private String keyword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.push_right_in_ac, R.anim.push_right_out_ac);
            }
        });

        etKeyword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                startSearch();
                return true;
            }
        });
        ivSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startSearch();
            }
        });
        mSwipeRefreshWidget.setOnRefreshListener(this);

        rvVideos.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE
                        && lastVisibleItem + 1 == adapter.getItemCount()) {

                    if (!isAll) {
                        adapter.setFooterInfo("正在加载……");
                        adapter.notifyDataSetChanged();
                        page++;
                        ParseAcFunVideosTask task = new ParseAcFunVideosTask(AcFunMainActivity.this);
                        task.execute(keyword, String.valueOf(page));
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisibleItem = layoutManager.findLastVisibleItemPosition();
            }
        });

        mMenuItems.add(new DialogMenuItem("收藏该视频", R.drawable.ic_menu_mark));
        mMenuItems.add(new DialogMenuItem("下载该视频", R.drawable.ic_menu_download));
        mMenuItems.add(new DialogMenuItem("从网页播放", R.drawable.ic_menu_webplayer));
        mMenuItems.add(new DialogMenuItem("分享该视频", R.drawable.ic_menu_share));

        adapter = new AcFunVideoListAdapter(this, acFunVideos);
        rvVideos.setAdapter(adapter);
        adapter.setOnItemClickListener(this);
        adapter.setOnItemLongClickListener(this);
    }

    /** 开始搜索 */
    private void startSearch() {
        keyword = etKeyword.getText().toString();
        if (TextUtils.isEmpty(keyword)) {
            AppToast.showToast("搜索内容为空");
            return;
        }
        refreshSearch();
        mSwipeRefreshWidget.setRefreshing(true);
    }
    /** 刷新搜索 */
    private void refreshSearch() {
        if (!NetworkUtils.isNetworkAvailable(AcFunMainActivity.this)) {
            AppToast.showToast("网络不可用，请检查您的网络设置。");
            return;
        }

        // 启动一个异步任务去获取并解析json
        isAll = false;
        page = 1;
        acFunVideos.clear();
        ParseAcFunVideosTask task = new ParseAcFunVideosTask(AcFunMainActivity.this);
        task.execute(keyword, String.valueOf(page));
    }

    /**
     * 初始化视图
     */
    private void initView() {
        setContentView(R.layout.activity_acfun_main);

        etKeyword = (EditText) findViewById(R.id.etKeyword);
        ivSearch = (ImageView) findViewById(R.id.ivSearch);
        rvVideos = (RecyclerView) findViewById(R.id.rvVideos);
        layoutManager = new LinearLayoutManager(this);
        rvVideos.setLayoutManager(layoutManager);
        rvVideos.setItemAnimator(new DefaultItemAnimator());
        llBack = (LinearLayout) findViewById(R.id.llBack);

        mSwipeRefreshWidget = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
        mSwipeRefreshWidget.setColorSchemeResources(android.R.color.holo_red_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_green_light,
                android.R.color.holo_blue_bright
        );
    }

    @Override
    public void onItemClick(View view, int position) {
        if (!NetworkUtils.isNetworkAvailable(this)) {
            AppToast.showToast("网络不可用，请检查您的网络设置。");
            return;
        }
        currVideo = acFunVideos.get(position);
        App.programType = "-3"; // 节目类型
        // 获取视频下载地址（在线播放地址）
        mode = PLAY_MODE;
        AppToast.showToast("正在获取视频播放地址……");
        parseUrl();
    }

    private void parseUrl() {
        GetAcFunVideoURITask task = new GetAcFunVideoURITask(this);
        task.execute(currVideo.getContentId());
    }
    @Override
    public void getAllVideo(List<AcFunVideo> acFunVideos) {
        mSwipeRefreshWidget.setRefreshing(false);
        App.isRefreshing = false;
        this.acFunVideos.addAll(acFunVideos);
        adapter.setData(this.acFunVideos);
        adapter.setFooterInfo("上拉加载更多");
        adapter.notifyDataSetChanged();
    }

    @Override
    public void getURI(ArrayList<String> urls) {
        System.out.println(urls);
        if (mode == PLAY_MODE) {
            if (urls.size() == 0) {
                AppToast.showToast("无法获取播放地址。", Toast.LENGTH_LONG);
                return;
            }
            // 播放视频
            Intent intent = new Intent(this, BiliAcFunVideoPlayer.class);
            intent.putStringArrayListExtra(Constants.VIDEO_URL, urls);
            startActivity(intent);
        } else if (mode == DOWNLOAD_MODE){
            if (urls.size() == 0) {
                AppToast.showToast("无法获取下载地址。", Toast.LENGTH_LONG);
                return;
            }
            // 下载视频
            if (urls.size() == 1) {
                String fileName = currVideo.getTitle() + FileUtils.getUrlSuffix(urls.get(0));
                fileName = fileName.replace("/", "&").replace("\\", "&");
                String path = Constants.ACFUN_DOWNLOAD + File.separator + fileName;
                Downloader downloader = new Downloader(AcFunMainActivity.this, fileName, MarkGroup.ACFUN.getName(), path);
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
                    String fileName = currVideo.getTitle() + "_" + i + FileUtils.getUrlSuffix(urls.get(i));
                    fileName = fileName.replace("/", "&").replace("\\", "&");
                    String path = Constants.ACFUN_DOWNLOAD + File.separator + fileName;
                    Downloader downloader = new Downloader(AcFunMainActivity.this, fileName, MarkGroup.ACFUN.getName(), path);
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
        } else if (mode == SHARE_MODE) {
        }
    }

    @Override
    public void onUriFailure() {
        // 获取视频播放地址失败
        if (mode == PLAY_MODE || mode == SHARE_MODE) {
            AppToast.showToast("获取视频播放地址失败。");
        } else if (mode == DOWNLOAD_MODE){
            AppToast.showToast("获取视频下载地址失败。");
        }
    }

    @Override
    public void onFailure() {
        AppToast.showToast("无法获取搜索结果。");
        App.isRefreshing = false;
        mSwipeRefreshWidget.setRefreshing(false);
        adapter.setFooterInfo("没有更多内容。");
        adapter.notifyDataSetChanged();
    }

    @Override
    public void noMore() {
        // 没有更多内容
        mSwipeRefreshWidget.setRefreshing(false);
        App.isRefreshing = false;
        AppToast.showToast("没有更多内容。");
        adapter.setFooterInfo("没有更多内容。");
        adapter.notifyDataSetChanged();
        isAll = true;
    }

    @Override
    public void onRefresh() {
        startSearch();
    }

    /**
     * 长按下载或收藏视频
     * @param view
     * @param position
     */
    @Override
    public void onItemLongClick(View view, int position) {
        final AcFunVideo acFunVideo = acFunVideos.get(position);
        currVideo = acFunVideo;
        final NormalListDialog dialog = new NormalListDialog(this, mMenuItems);
        dialog.title("请选择")//
                .showAnim(App.mBasIn)//
                .dismissAnim(App.mBasOut)//
                .show();
        dialog.setOnOperItemClickL(new OnOperItemClickL() {
            @Override
            public void onOperItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    // 收藏视频
                    if (App.markService.getMarkByPath(acFunVideo.getContentId()) == null) {
                        MarkItem item = new MarkItem(System.currentTimeMillis(), acFunVideo.getTitle(),
                                acFunVideo.getTitleImg(), MarkGroup.ACFUN, acFunVideo.getContentId(), null, 0L);
                        App.markService.addMarkVideo(item);
                        AppToast.showToast("视频已收藏。");
                        if (App.biliMarkItems != null) {
                            App.biliMarkItems.add(item);
                        }
                        if (MarkBiliFragment.getInstance() != null) {
                            // 刷新MarkListFragment
                            MarkBiliFragment.getInstance().markBiliAdapter.addData(item);
                            MarkBiliFragment.getInstance().tvMarkNone.setVisibility(View.GONE);
                        }
                    } else {
                        AppToast.showToast("视频已在收藏列表中。");
                    }
                } else if (position == 1) {
                    // 下载视频
                    // 获取视频下载地址（在线播放地址）
                    AppToast.showToast("正在获取视频下载地址……");
                    mode = DOWNLOAD_MODE;   // 下载模式
                    parseUrl();
                } else if (position == 2) {
                    // 网页播放
                    String path = "http://m.acfun.tv/v/?ac=" + acFunVideo.getContentId().replace("ac", "");
                    Intent intent = new Intent(AcFunMainActivity.this, WebActivity.class);
                    intent.putExtra("web", path);
                    startActivity(intent);
                } else if (position == 3) {
                    // 分享视频
                    Intent shareIntent = new Intent();
                    shareIntent.setAction(Intent.ACTION_SEND);
                    shareIntent.setType("text/*");
                    String path = "http://m.acfun.tv/v/?ac=" + acFunVideo.getContentId().replace("ac", "");
                    shareIntent.putExtra(Intent.EXTRA_TEXT, path);
                    ComponentName cn = shareIntent.resolveActivity(getPackageManager());
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
}
