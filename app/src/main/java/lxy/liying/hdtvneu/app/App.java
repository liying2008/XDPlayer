package lxy.liying.hdtvneu.app;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Looper;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.flyco.animation.BaseAnimatorSet;
import com.flyco.animation.BounceEnter.BounceTopEnter;
import com.flyco.animation.SlideExit.SlideBottomExit;
import com.flyco.dialog.widget.NormalDialog;
import com.liulishuo.filedownloader.FileDownloader;
import com.shizhefei.fragment.LazyFragment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cc.duduhuo.applicationtoast.AppToast;
import io.vov.vitamio.Vitamio;
import lxy.liying.hdtvneu.db.DownloadService;
import lxy.liying.hdtvneu.db.MarkService;
import lxy.liying.hdtvneu.db.XDVideoService;
import lxy.liying.hdtvneu.domain.DownloadItem;
import lxy.liying.hdtvneu.domain.MarkItem;
import lxy.liying.hdtvneu.domain.Program;
import lxy.liying.hdtvneu.domain.ReviewProgram;
import lxy.liying.hdtvneu.domain.XDVideo;
import lxy.liying.hdtvneu.utils.Constants;
import lxy.liying.hdtvneu.utils.Downloader;

/**
 * =======================================================
 * 版权：©Copyright LiYing 2015-2016. All rights reserved.
 * 作者：liying
 * 日期：2016/8/14 16:11
 * 版本：1.0
 * 描述：
 * 备注：
 * =======================================================
 */
public class App extends Application {
    /**
     * 上次点击屏幕的时间
     */
    public static long lastTapTime = -1L;
    /**
     * 节目类型：<br/>
     * 1：东北大学IPv6视频直播<br/>
     * 2：东北大学IPv6视频回看<br/>
     * 3：北邮IPv6视频直播<br/>
     * 4：清华大学IPTV<br/>
     * 5：本地视频列表<br/>
     * -1：其他<br/>
     * -2：在线视频<br/>
     * -3：哔哩哔哩视频<br/>
     */
    public static String programType = "-1";
    /**
     * 节目列表
     */
    public static List<Program> programsList;
    /**
     * 回看节目列表
     */
    public static List<ReviewProgram> reviewPrograms;
    /**
     * 回看节目的频道
     */
    public static String reviewP;
    /**
     * 当前Application的实例
     */
    private static App mInstance;
    /**
     * 配置文件
     */
    private SharedPreferences sharedPreferences;
    /**
     * 当前Fragment
     */
    public LazyFragment currentFragment;
    public static List<XDVideo> xdVideos;
    /** 是否有视频列表的缓存 */
    public static String hasVideoListCache = "false";
    public static XDVideoService xdService;
    public static MarkService markService;
    public static DownloadService downloadService;
    /** 上一次点击的video */
    public static XDVideo lastXdVideo;
    /** 是否正处于下拉刷新状态 */
    public static boolean isRefreshing = false;
    public static List<MarkItem> ipv6MarkItems, biliMarkItems, localMarkItems, onlineMarkItems;
    public static BaseAnimatorSet mBasIn;
    public static BaseAnimatorSet mBasOut;
    /** 下载器列表 */
    public static List<Downloader> downloaders = new ArrayList<>();
    /** 等待下载的任务列表 */
    public static List<Downloader> waitDownloaders = new ArrayList<>();
    /** 已下载的视频列表 */
    public static List<DownloadItem> downloadItems = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        loadSettings(); // 加载设置
        // 检查工作目录
        checkWorkDir();

        xdService = new XDVideoService(this);
        markService = new MarkService(this);
        downloadService = new DownloadService(this);
        Fresco.initialize(this);    // 初始化Fresco类
        // 初始化下载引擎
        FileDownloader.init(this);
        // 初始化AppToast
        AppToast.init(this);
        // 初始化Dialog基础动画效果
        mBasIn = new BounceTopEnter();
        mBasOut = new SlideBottomExit();
        // 项目运行之前必须加载库文件
        Vitamio.isInitialized(this);
    }

    /**
     * 得到本Application实例
     */
    public static App getInstance() {
        return mInstance;
    }

    /**
     * 检查工作目录和下载目录是否存在，不存在则创建
     */
    private void checkWorkDir() {
        File dir = new File(Constants.BILI_DOWNLOAD);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        dir = new File(Constants.ONLINE_DOWNLOAD);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        dir = new File(Constants.ACFUN_DOWNLOAD);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }
    /**
     * 加载设置，应用启动自动加载
     */
    private void loadSettings() {
        sharedPreferences = getSharedPreferences(Constants.PREFS_NAME, Context.MODE_MULTI_PROCESS);
    }

    /**
     * 获取设置
     *
     * @param key      设置key
     * @param defValue 没有该设置将要返回的默认值
     * @return
     */
    public String getSetting(String key, String defValue) {
        return sharedPreferences.getString(key, defValue);
    }

    /**
     * 保存设置，调用该方法后会产生
     *
     * @param key   设置保存key
     * @param value 需要保存的值
     */
    public void putSetting(String key, String value) {
        sharedPreferences.edit().putString(key, value).apply();
    }

    /**
     * 获取当前版本号
     */
    public String getVersionName() {
        PackageManager packageManager = getPackageManager();
        PackageInfo packInfo;
        String version = "";
        try {
            packInfo = packageManager.getPackageInfo(getPackageName(), 0);
            version = packInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return version;
    }

    /**
     * 获取当前版本号
     *
     * @return
     */
    public int getVersionCode() {
        PackageManager packageManager = getPackageManager();
        PackageInfo packInfo;
        int versionCode = 0;
        try {
            packInfo = packageManager.getPackageInfo(getPackageName(), 0);
            versionCode = packInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    private void checkUIThread() {
        if (!isRunOnUIThread())
            throw new RuntimeException("方法只能在主线程调用！");
    }

    /**
     * 判断当前线程是否是主线程
     *
     * @return true：是
     */
    private boolean isRunOnUIThread() {
        return Looper.getMainLooper().getThread() == Thread.currentThread();
    }

    /**
     * 得到对话框构建器实例
     *
     * @param context You need to use your Activity as the Context for the Dialog not the Application.
     * @return
     */
    public static AlertDialog.Builder getAlertDialogBuilder(Activity context) {
        return new AlertDialog.Builder(context);
    }

    /**
     * Get NormalDialog
     * @param context
     * @param content
     * @return
     */
    public static NormalDialog getNormalDialog(Context context, String content) {
        final NormalDialog dialog = new NormalDialog(context);
        dialog.isTitleShow(false)//
                .bgColor(Color.parseColor("#383838"))//
                .cornerRadius(5)//
                .content(content)//
                .contentGravity(Gravity.CENTER)//
                .contentTextColor(Color.parseColor("#ffffff"))//
                .dividerColor(Color.parseColor("#222222"))//
                .btnTextSize(15.5f, 15.5f)//
                .btnTextColor(Color.parseColor("#ffffff"), Color.parseColor("#ffffff"))//
                .btnPressColor(Color.parseColor("#2B2B2B"))//
                .widthScale(0.85f)//
                .showAnim(App.mBasIn)//
                .dismissAnim(App.mBasOut)//
                .show();
        return dialog;
    }

    /**
     * 弹出输入法面板
     * @param activity
     */
    public static void autoInputPanel(Activity activity) {
        // 解决不自动弹出输入法面板的问题
        Window window = activity.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }
}
