package lxy.liying.hdtvneu.activity;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Display;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import cc.duduhuo.applicationtoast.AppToast;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.Vitamio;
import io.vov.vitamio.widget.VideoView;
import lxy.liying.hdtvneu.R;
import lxy.liying.hdtvneu.app.App;
import lxy.liying.hdtvneu.domain.XDVideo;
import lxy.liying.hdtvneu.service.callback.OnVideoChangeCallback;
import lxy.liying.hdtvneu.view.BaseMediaController;

/**
 * =======================================================
 * 作者：liying
 * 日期：2016/8/21 14:06
 * 版本：1.0
 * 描述：播放器基类
 * 备注：
 * =======================================================
 */
public class PlayerActivity extends BaseActivity implements Player, MediaPlayer.OnCompletionListener, MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnInfoListener, Runnable, OnVideoChangeCallback {
    private static final String TAG = "PlayerActivity";
    protected VideoView mVideoView;
    private ImageView mOperationBg;
    private ImageView mOperationPercent;
    private AudioManager mAudioManager;
    private LinearLayout mLoadingView;
    private int bufferingUpdatePercent; // 缓冲比例
    private TextView tvBufferUpdatePercent;
    private Toast toast = null;
    private View mVolumeBrightnessLayout;
    /**
     * 播放完成
     */
    protected static boolean playComplete = false;
    /**
     * 最大声音
     */
    private int mMaxVolume;
    /**
     * 当前声音
     */
    private int mVolume = -1;
    /**
     * 当前亮度
     */
    private float mBrightness = -1f;
    /**
     * 当前缩放模式
     */
    private int mLayout = VideoView.VIDEO_LAYOUT_SCALE;
    private GestureDetector mGestureDetector;
    protected BaseMediaController mMediaController;   // 自定义控制器
    private static final int TIME = 0;

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case TIME:
                    mMediaController.setTime(msg.obj.toString());
                    break;
            }
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置无标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 设置全屏
        Window window = getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // 隐藏虚拟按键
        WindowManager.LayoutParams params = window.getAttributes();
        params.systemUiVisibility = View.SYSTEM_UI_FLAG_LOW_PROFILE;
        window.setAttributes(params);
        //强行开启屏幕旋转效果
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
        initView();
        initData();
    }

    /**
     * 初始化界面
     */
    private void initView() {
        setContentView(R.layout.activity_video_player);
        mVideoView = (VideoView) findViewById(R.id.video_view);
        assert mVideoView != null;
        mVideoView.setBufferSize(1024 * 1024);   // 设置缓冲为 1024 * 1024（单位byte）
        mLoadingView = (LinearLayout) findViewById(R.id.video_loading);
        tvBufferUpdatePercent = (TextView) findViewById(R.id.tvBufferUpdatePercent);
        mVolumeBrightnessLayout = findViewById(R.id.operation_volume_brightness);
        mOperationBg = (ImageView) findViewById(R.id.operation_bg);
        mOperationPercent = (ImageView) findViewById(R.id.operation_percent);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        // 项目运行之前必须加载库文件
        if (!Vitamio.isInitialized(getApplicationContext())) {
            AppToast.showToast("Vitamio库无法加载。");
        }
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mMaxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        mMediaController = new BaseMediaController(this, this, this);
        mVideoView.setMediaController(mMediaController);
        mVideoView.requestFocus();
        mVideoView.setOnBufferingUpdateListener(this);
        mVideoView.setOnCompletionListener(this);
        mVideoView.setOnInfoListener(this);
        mGestureDetector = new GestureDetector(this, new MyGestureListener());
        new Thread(this).start();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        AppToast.showToast("播放完毕");
        playComplete = true;
        finish();
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        switch (what) {
            case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                if (mVideoView.isPlaying()) {
                    mVideoView.pause();
                    mLoadingView.setVisibility(View.VISIBLE);
                }
                break;
            case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                mVideoView.start();
                mLoadingView.setVisibility(View.GONE);
                bufferingUpdatePercent = 0;
                mMediaController.hide();
                break;
            case MediaPlayer.MEDIA_INFO_DOWNLOAD_RATE_CHANGED:
                tvBufferUpdatePercent.setText("正在缓冲 " + bufferingUpdatePercent + "%\n\t" + extra + "kb/s");
                break;
        }
        return true;
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        bufferingUpdatePercent = percent;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mGestureDetector.onTouchEvent(event))
            return true;

        // 处理手势结束
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_UP:
                endGesture();
                break;
        }
        return super.onTouchEvent(event);
    }

    /**
     * 手势结束
     */
    public void endGesture() {
        mVolume = -1;
        mBrightness = -1f;

        // 隐藏
        mDismissHandler.removeMessages(0);
        mDismissHandler.sendEmptyMessageDelayed(0, 500);
    }

    @Override
    public void playNewVideo(XDVideo xdVideo) {
        // 交给子类实现
    }

    @Override
    public void playNewProgram(Uri uri) {
        // 交给子类实现
    }

    public class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            long interval = System.currentTimeMillis() - App.lastTapTime;
            App.lastTapTime = System.currentTimeMillis();
            if (interval < 200) {
                // 两次点击时间间隔小于200ms
                changePictureSize();
            }
            return false;
        }

        /**
         * 双击
         */
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            changePictureSize();
            return true;
        }

        /**
         * 滑动
         */
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            mMediaController.hide();    // 隐藏控制器
            changeVolOrBri(e1, e2);
            return super.onScroll(e1, e2, distanceX, distanceY);
        }
    }

    /**
     * 定时隐藏
     */
    private Handler mDismissHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            mVolumeBrightnessLayout.setVisibility(View.GONE);
            return false;
        }
    });

    /**
     * 改变声音或亮度
     *
     * @param e1
     * @param e2
     */
    public void changeVolOrBri(MotionEvent e1, MotionEvent e2) {
        float mOldX = e1.getX(), mOldY = e1.getY();
        int x = (int) e2.getRawX();
        int y = (int) e2.getRawY();
        Display disp = getWindowManager().getDefaultDisplay();
        int windowWidth = disp.getWidth();
        int windowHeight = disp.getHeight();

        if (mOldX > windowWidth * 4.0 / 5)// 右边滑动
            onVolumeSlide((mOldY - y) / windowHeight);
        else if (mOldX < windowWidth / 5.0)// 左边滑动
            onBrightnessSlide((mOldY - y) / windowHeight);
//            else
//                changePregress((x - mOldX) / windowWidth);
    }

    /**
     * 滑动改变声音大小
     *
     * @param percent
     */
    private void onVolumeSlide(float percent) {
        if (mVolume == -1) {
            mVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            if (mVolume < 0)
                mVolume = 0;

            // 显示
            mOperationBg.setImageResource(R.drawable.video_volumn_bg);
            mVolumeBrightnessLayout.setVisibility(View.VISIBLE);
        }

        int index = (int) (percent * mMaxVolume) + mVolume;
        if (index > mMaxVolume)
            index = mMaxVolume;
        else if (index < 0)
            index = 0;

        // 变更声音
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, index, 0);

        // 变更进度条
        ViewGroup.LayoutParams lp = mOperationPercent.getLayoutParams();
        lp.width = findViewById(R.id.operation_full).getLayoutParams().width * index / mMaxVolume;
        mOperationPercent.setLayoutParams(lp);
    }

    /**
     * 滑动改变亮度
     *
     * @param percent
     */
    private void onBrightnessSlide(float percent) {
        if (mBrightness < 0) {
            mBrightness = getWindow().getAttributes().screenBrightness;
            if (mBrightness <= 0.00f)
                mBrightness = 0.50f;
            if (mBrightness < 0.01f)
                mBrightness = 0.01f;

            // 显示
            mOperationBg.setImageResource(R.drawable.video_brightness_bg);
            mVolumeBrightnessLayout.setVisibility(View.VISIBLE);
        }
        WindowManager.LayoutParams lpa = getWindow().getAttributes();
        lpa.screenBrightness = mBrightness + percent;
        if (lpa.screenBrightness > 1.0f)
            lpa.screenBrightness = 1.0f;
        else if (lpa.screenBrightness < 0.01f)
            lpa.screenBrightness = 0.01f;
        getWindow().setAttributes(lpa);

        ViewGroup.LayoutParams lp = mOperationPercent.getLayoutParams();
        lp.width = (int) (findViewById(R.id.operation_full).getLayoutParams().width * lpa.screenBrightness);
        mOperationPercent.setLayoutParams(lp);
    }

    /**
     * 改变画面尺寸
     */
    public void changePictureSize() {
        if (mLayout == VideoView.VIDEO_LAYOUT_ZOOM)
            mLayout = VideoView.VIDEO_LAYOUT_ORIGIN;
        else {
                /*
                public static final int VIDEO_LAYOUT_SCALE
                缩放参数，画面全屏。
                常量值：1

                public static final int VIDEO_LAYOUT_STRETCH
                缩放参数，画面拉伸。
                常量值：2

                1和2效果一样，所以跳过2。
                 */
            mLayout++;
            if (mLayout == 2) mLayout = 3;

        }
        if (mVideoView != null) {
            mVideoView.setVideoLayout(mLayout, 0);
        }

        if (mLayout == 0) {
            toast = AppToast.getToast();
            toast.setText("原始");
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        } else if (mLayout == 1) {
            toast = AppToast.getToast();
            toast.setText("全屏");
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        } else {
            // 2：拉伸（和全屏效果相同，故跳过）
            // 3：裁剪
            toast = AppToast.getToast();
            toast.setText("裁剪");
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }


    long currentPosition = 0;

    @Override
    protected void onPause() {
        super.onPause();
        if (mVideoView != null) {
            mVideoView.pause();
            currentPosition = mVideoView.getCurrentPosition();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mVideoView != null) {
            mVideoView.resume();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mVideoView != null) {
            mVideoView.stopPlayback();
        }
    }

    @Override
    public void run() {
        while (!this.isFinishing()) {
            //时间读取线程
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
            String str = sdf.format(new Date());
            Message msg = mHandler.obtainMessage();
            msg.obj = str;
            msg.what = TIME;
            msg.sendToTarget();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (mVideoView != null)
            mVideoView.setVideoLayout(mLayout, 0);
        super.onConfigurationChanged(newConfig);
    }

    /**
     * 锁定/解锁屏幕方向
     * @param lockScreen
     */
    public void lockScreen(boolean lockScreen) {
        if (lockScreen) {
            Configuration config = getResources().getConfiguration();
            if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
                AppToast.showToast("横屏已锁定");
            } else {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
                AppToast.showToast("竖屏已锁定");
            }
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
            AppToast.showToast("屏幕锁定已解除");
        }
    }
    /***
     * 双击Back键退出
     */
    private long lastBackKeyTime;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                // 按下返回键
                long delay = Math.abs(System.currentTimeMillis() - lastBackKeyTime);
                if (delay > 3000) {
                    // 双击退出程序
                    if (toast != null) {
                        toast.cancel();
                        toast = null;
                    }
                    AppToast.showToast(R.string.toast_key_backplay);
                    lastBackKeyTime = System.currentTimeMillis();
                    return true;
                }
                break;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }
}
