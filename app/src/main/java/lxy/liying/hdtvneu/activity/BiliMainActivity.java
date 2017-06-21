package lxy.liying.hdtvneu.activity;

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

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.target.Target;
import com.flyco.dialog.entity.DialogMenuItem;
import com.flyco.dialog.listener.OnOperItemClickL;
import com.flyco.dialog.widget.NormalListDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import cc.duduhuo.applicationtoast.AppToast;
import lxy.liying.hdtvneu.R;
import lxy.liying.hdtvneu.adapter.BiliVideoListAdapter;
import lxy.liying.hdtvneu.app.App;
import lxy.liying.hdtvneu.domain.BiliVideo;
import lxy.liying.hdtvneu.domain.MarkGroup;
import lxy.liying.hdtvneu.domain.MarkItem;
import lxy.liying.hdtvneu.fragment.MarkBiliFragment;
import lxy.liying.hdtvneu.service.callback.OnGetBiliVideoCallback;
import lxy.liying.hdtvneu.service.task.ParseBiliHtmlTask;
import lxy.liying.hdtvneu.service.task.SaveCoverTask;
import lxy.liying.hdtvneu.utils.CommonUtils;
import lxy.liying.hdtvneu.utils.Constants;
import lxy.liying.hdtvneu.utils.FileUtils;
import lxy.liying.hdtvneu.utils.NetworkUtils;

/**
 * =======================================================
 * 作者：liying
 * 日期：2016/8/31 9:00
 * 版本：1.0
 * 描述：哔哩哔哩视频搜索界面
 * 备注：
 * =======================================================
 */
public class BiliMainActivity extends BaseActivity implements BiliVideoListAdapter.OnItemClickListener, OnGetBiliVideoCallback, SwipeRefreshLayout.OnRefreshListener, BiliVideoListAdapter.OnItemLongClickListener {
    private EditText etKeyword;
    private ImageView ivSearch;
    private LinearLayout llBack;
    private RecyclerView rvVideos;
    private BiliVideo currVideo;
    private BiliVideoListAdapter adapter;
    private int page = 1;
    /** 是否已经加载了全部视频 */
    private static boolean isAll = false;
    /** 用于标记用户输入的是否是av号 */
    private int avNum = -1;
    private List<BiliVideo> biliVideos = new ArrayList<>(1);
    private SwipeRefreshLayout mSwipeRefreshWidget;
    private static int lastVisibleItem;
    private LinearLayoutManager layoutManager;

    private ArrayList<DialogMenuItem> mMenuItems = new ArrayList<>();
    /** 查找关键词 */
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
                        ParseBiliHtmlTask task = new ParseBiliHtmlTask(BiliMainActivity.this, avNum);
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
        mMenuItems.add(new DialogMenuItem("保存封面", R.drawable.ic_menu_download));
        mMenuItems.add(new DialogMenuItem("分享该视频", R.drawable.ic_menu_share));
        mMenuItems.add(new DialogMenuItem("复制AV号", R.drawable.ic_menu_copy_link));

        adapter = new BiliVideoListAdapter(this, biliVideos);
        rvVideos.setAdapter(adapter);
        adapter.setOnItemClickListener(this);
        adapter.setOnItemLongClickListener(this);
    }

    /**
     * 开始搜索
     */
    private void startSearch() {
        keyword = etKeyword.getText().toString();
        if (TextUtils.isEmpty(keyword)) {
            AppToast.showToast("搜索内容为空");
            return;
        }
        refreshSearch();
        mSwipeRefreshWidget.setRefreshing(true);
    }

    /**
     * 刷新搜索
     */
    private void refreshSearch() {
        if (!NetworkUtils.isNetworkAvailable(BiliMainActivity.this)) {
            AppToast.showToast("网络不可用，请检查您的网络设置。");
            return;
        }
        int avNum = isAvNum(keyword);
        // 启动一个异步任务去获取html并解析
        isAll = false;
        page = 1;
        biliVideos.clear();
        ParseBiliHtmlTask task = new ParseBiliHtmlTask(BiliMainActivity.this, avNum);
        task.execute(keyword, String.valueOf(page));
    }

    /**
     * 初始化视图
     */
    private void initView() {
        setContentView(R.layout.activity_bili_main);

        etKeyword = (EditText) findViewById(R.id.etKeyword);
        ivSearch = (ImageView) findViewById(R.id.ivSearch);
        rvVideos = (RecyclerView) findViewById(R.id.rvVideos);
        layoutManager = new LinearLayoutManager(this);
        rvVideos.setLayoutManager(layoutManager);
        rvVideos.setItemAnimator(new DefaultItemAnimator());
        llBack = (LinearLayout) findViewById(R.id.llBack);

        mSwipeRefreshWidget = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
        mSwipeRefreshWidget.setColorSchemeResources(Constants.SWIPE_REFRESH_COLOR_SCHEME);
    }

    /**
     * 判断用户输入的是不是av号，如果是，返回该av号
     *
     * @param str 用户输入的字符串
     * @return -1：不是av号
     */
    public static int isAvNum(String str) {
        boolean av = (str.startsWith("av") || str.startsWith("AV"));
        String substring;
        if (av) {
            substring = str.substring(2);
        } else {
            substring = str;
        }
        for (int i = 0; i < substring.length(); i++) {
            if (!Character.isDigit(substring.charAt(i))) {
                return -1;
            }
        }
        return Integer.parseInt(substring);
    }

    @Override
    public void onItemClick(View view, int position) {
        if (!NetworkUtils.isNetworkAvailable(this)) {
            AppToast.showToast("网络不可用，请检查您的网络设置。");
            return;
        }
        currVideo = biliVideos.get(position);
        // 网页播放
        String path = "http://m.bilibili.com/video/av" + currVideo.getAv() + ".html";
        Intent intent = new Intent(BiliMainActivity.this, WebActivity.class);
        intent.putExtra("web", path);
        startActivity(intent);
    }

    @Override
    public void getAllVideo(List<BiliVideo> biliVideos) {
        mSwipeRefreshWidget.setRefreshing(false);
        App.isRefreshing = false;
        this.biliVideos.addAll(biliVideos);
        adapter.setData(this.biliVideos);
        adapter.setFooterInfo("上拉加载更多");
        adapter.notifyDataSetChanged();
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
     *
     * @param view
     * @param position
     */
    @Override
    public void onItemLongClick(View view, int position) {
        final BiliVideo biliVideo = biliVideos.get(position);
        currVideo = biliVideo;
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
                    if (App.markService.getMarkByPath(biliVideo.getAv()) == null) {
                        MarkItem item = new MarkItem(System.currentTimeMillis(), biliVideo.getTitle(),
                            biliVideo.getCoverUrl(), MarkGroup.BILI, biliVideo.getAv(), null, 0L);
                        App.markService.addMarkVideo(item);
                        AppToast.showToast("视频已收藏。");
                        if (App.biliMarkItems != null) {
                            App.biliMarkItems.add(item);
                        }
                        MarkBiliFragment instance = MarkBiliFragment.getInstance();
                        if (instance != null) {
                            // 刷新MarkListFragment
                            instance.markBiliAdapter.addData(item);
                            instance.tvMarkNone.setVisibility(View.GONE);
                        }
                    } else {
                        AppToast.showToast("视频已在收藏列表中。");
                    }
                } else if (position == 1) {
                    // 保存封面
                    SaveCoverTask task = new SaveCoverTask(BiliMainActivity.this);
                    task.execute(biliVideo.getCoverUrl());
                } else if (position == 2) {
                    // 分享视频
                    String path = "http://m.bilibili.com/video/av" + biliVideo.getAv() + ".html";
                    CommonUtils.shareText(BiliMainActivity.this, path);
                } else if (position == 3) {
                    // 复制AV号
                    CommonUtils.copyText(BiliMainActivity.this, biliVideo.getAv());
                    AppToast.showToast(biliVideo.getAv() + " 已复制", Toast.LENGTH_LONG);
                }
                dialog.dismiss();
            }
        });
    }
}
