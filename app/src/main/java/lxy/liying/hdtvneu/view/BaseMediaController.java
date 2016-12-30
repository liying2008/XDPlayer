package lxy.liying.hdtvneu.view;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import io.vov.vitamio.widget.MediaController;
import lxy.liying.hdtvneu.R;
import lxy.liying.hdtvneu.activity.PlayerActivity;
import lxy.liying.hdtvneu.adapter.PopupListAdapter;
import lxy.liying.hdtvneu.app.App;
import lxy.liying.hdtvneu.domain.XDVideo;
import lxy.liying.hdtvneu.service.callback.OnVideoChangeCallback;
import lxy.liying.hdtvneu.utils.DensityUtil;

/**
 * =======================================================
 * 版权：Copyright LiYing 2015-2016. All rights reserved.
 * 作者：liying
 * 日期：2016/6/20 20:24
 * 版本：1.0
 * 描述：播放器控制器类
 * 备注：
 * =======================================================
 */
public class BaseMediaController extends MediaController {
    private TextView textViewTime;//时间提示
    private PlayerActivity player;
    private Context context;
    private int controllerWidth = 0; //设置mediaController高度为了使横屏时top显示在屏幕顶端
    private GestureDetector mGestureDetector;
    public PopupWindow popupWindow;
    private OnVideoChangeCallback changeCallback;
    private LinearLayout llScreenLock;
    private ImageView ivLock;
    /** 是否锁定屏幕 */
    private boolean screenLocked = false;

    // 关闭播放界面
    private OnClickListener backListener = new OnClickListener() {
        public void onClick(View v) {
            if (player != null) {
                player.finish();
            }
        }
    };

    // 打开节目列表
    private OnClickListener showListListener = new OnClickListener() {
        public void onClick(View v) {
            openPList();    // 打开节目列表
            popupWindow.showAtLocation(player.getCurrentFocus(), Gravity.END, 0, 0);
            hide(); // 隐藏controller
        }
    };

    /**
     * 构造器
     * @param context
     */
    public BaseMediaController(Context context, PlayerActivity player, OnVideoChangeCallback changeCallback) {
        super(context);
        this.context = context;
        this.player = player;
        this.changeCallback = changeCallback;
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        controllerWidth = wm.getDefaultDisplay().getWidth();
        mGestureDetector = new GestureDetector(context, new MyGestureListener());
    }

    @Override
    protected View makeControllerView() {
        View v = LayoutInflater.from(context).inflate(R.layout.controller_base, this);
        v.setMinimumHeight(controllerWidth);
        // 返回按钮
        ImageButton img_back = (ImageButton) v.findViewById(R.id.mediacontroller_top_back);
        // 打开节目列表
        img_back.setOnClickListener(backListener);  // 设置返回监听
        LinearLayout ll_PList = (LinearLayout) v.findViewById(R.id.mediacontroller_llList);
        ivLock = (ImageView) v.findViewById(R.id.ivLock);
        llScreenLock = (LinearLayout) findViewById(R.id.llScreenLock);
        llScreenLock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 锁定/解锁屏幕
                if (screenLocked) {
                    player.lockScreen(false);
                    ivLock.setBackgroundResource(R.drawable.unlock_screen);
                    screenLocked = false;
                } else {
                    player.lockScreen(true);
                    ivLock.setBackgroundResource(R.drawable.lock_screen);
                    screenLocked = true;
                }
            }
        });

        ll_PList.setOnClickListener(showListListener); // 设置打开界面列表监听
        textViewTime = (TextView) v.findViewById(R.id.mediacontroller_time);
        // 1、从外部调用播放器播放视频时
        // 2、播放在线视频时
        // 3、播放哔哩哔哩视频时
        // 隐藏播放列表按钮
        if (App.programType.equals("-1") || App.programType.equals("-2") || App.programType.equals("-3")) {
            ll_PList.setVisibility(GONE);
        }

        return v;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        return true;
    }

    public void setTime(String time) {
        if (textViewTime != null)
            textViewTime.setText(time);
    }

    // 打开节目列表
    public void openPList() {
        if (null != popupWindow) {
            popupWindow.dismiss();
        } else {
            initPopWindow();
        }
    }

    // 初始化PopupWindow
    private void initPopWindow() {
        View contentView = LayoutInflater.from(context).inflate(R.layout.plist_popupwindow, null, false);
        popupWindow = new PopupWindow(contentView, DensityUtil.dip2px(context, 200), LayoutParams.MATCH_PARENT, true);
        // 设置动画效果
        popupWindow.setAnimationStyle(R.style.PopupAnimation);
        ListView listView = (ListView) contentView.findViewById(R.id.p_plist);
        PopupListAdapter adapter = new PopupListAdapter(context, this);
        listView.setAdapter(adapter);
        // 点击其他地方消失
        contentView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (popupWindow != null && popupWindow.isShowing()) {
                    popupWindow.dismiss();
                    popupWindow = null;
                }
                return false;
            }
        });
        // 使其聚焦
        popupWindow.setFocusable(true);
        // 点击back键使PopupWindow消失，PopupWindow的背景不能为空
        popupWindow.setBackgroundDrawable(new ColorDrawable(0));
    }

    // 隐藏/显示
    public void toggleMediaControlsVisibility() {
        if (isShowing()) {
            hide();
        } else {
            show();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mGestureDetector.onTouchEvent(event))
            return true;

        // 处理手势结束
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_UP:
                player.endGesture();
                break;
        }
        return super.onTouchEvent(event);
    }

    private class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            long interval = System.currentTimeMillis() - App.lastTapTime;
            App.lastTapTime = System.currentTimeMillis();
            if (interval < 200) {
                // 两次点击时间间隔小于200ms
                player.changePictureSize();
            }
            return false;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            // 单击时，控制器隐藏/显示
            toggleMediaControlsVisibility();
            return super.onSingleTapConfirmed(e);
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            player.changePictureSize();
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (popupWindow != null && popupWindow.isShowing()) {
            } else {
                player.changeVolOrBri(e1, e2);
            }
            return super.onScroll(e1, e2, distanceX, distanceY);
        }
    }


    /**
     * 播放网络视频
     *
     * @param uri
     */
    public void rePlay(Uri uri) {
        changeCallback.playNewProgram(uri);
    }

    /**
     * 播放本地视频
     *
     * @param xdVideo
     */
    public void rePlay(XDVideo xdVideo) {
        changeCallback.playNewVideo(xdVideo);
    }
}
