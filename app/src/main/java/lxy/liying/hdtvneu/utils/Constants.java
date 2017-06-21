package lxy.liying.hdtvneu.utils;

import android.os.Environment;

import java.io.File;

/**
 * =======================================================
 * 作者：liying
 * 日期：2016/5/15 19:00
 * 版本：1.0
 * 描述：常量类
 * 备注：
 * =======================================================
 */
public interface Constants {
    /** Activity传递的Intent数据的key */
    String PROGRAM = "program";
    /** Activity传递的Intent数据的key */
    String XDVIDEO = "xdvideo";
    /** Activity传递的Intent数据的key */
    String FOLDER = "folder";
    /** Activity传递的Intent数据的key */
    String VIDEO_URL = "video_url";
    /** 东北大学IPv6视频直播测试站（人气排序） */
    String NEU_HDTV_URL_ONLINE = "http://hdtv.neu6.edu.cn/?online";
    /** 北京邮电大学IPTV(IPv6网络电视直播) */
    String BY_IPTV_URL_6 = "https://tv.byr.cn/mobile/";

    String PREFS_NAME = "saved_state";
    /** SharedPreferences条目：当前版本 */
    String VERSION = "version";
    /** SharedPreferences条目：上次观看视频 */
    String LAST_VIDEO_ID = "last_video_id";
    /**
     * SharedPreferences条目 <br/>
     * 本地视频列表缓存是否存在
     * true：存在
     * false：不存在
     */
    String VIDEO_LIST_CACHE = "VIDEO_LIST_CACHE";
    /**
     * SharedPreferences条目：IPv6电视默认首页 <br/>
     * 0：东北大学HDTV
     * 1：北邮人IPTV
     */
    String IPV6_HOME = "IPV6_HOME";
    /**
     * SharedPreferences条目：收藏默认首页 <br/>
     * 0：IPv6电视
     * 1：本地视频
     * 2：哔哩哔哩/AcFun
     * 4：其他
     */
    String MARK_HOME = "MARK_HOME";

    String XD_DIR = "XDPlayer";
    String COVER_NAME = "Cover";
    /** 工作目录 */
    String WORK_DIR = Environment.getExternalStorageDirectory() + File.separator + XD_DIR;
    /** Bilibili视频下载目录 */
    String BILI_DOWNLOAD = WORK_DIR + File.separator + "BiliDownload";
    /** Bilibili/AcFun封面保存目录 */
    String COVER = WORK_DIR + File.separator + COVER_NAME;
    /** AcFun视频下载目录 */
    String ACFUN_DOWNLOAD = WORK_DIR + File.separator + "AcFunDownload";
    /** 在线视频下载目录 */
    String ONLINE_DOWNLOAD = WORK_DIR + File.separator + "OnlineDownload";

    /** resultCode:刷新列表 */
    int REFRESH_LIST = 0;

    /** 下拉刷新控件颜色 */
    int[] SWIPE_REFRESH_COLOR_SCHEME = {android.R.color.holo_red_light,
        android.R.color.holo_orange_light,
        android.R.color.holo_green_light,
        android.R.color.holo_blue_bright};
}
