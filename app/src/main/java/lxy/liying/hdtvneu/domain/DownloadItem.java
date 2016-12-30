package lxy.liying.hdtvneu.domain;

/**
 * =======================================================
 * 版权：©Copyright LiYing 2015-2016. All rights reserved.
 * 作者：liying
 * 日期：2016/9/17 19:51
 * 版本：1.0
 * 描述：下载项实体类
 * 备注：
 * =======================================================
 */
public class DownloadItem {
    /** 视频名称 */
    private String name;
    /** 视频来源 */
    private String videoFrom;
    /** 本地保存路径 */
    private String path;

    public static final String NAME = "name";
    public static final String FROM = "videoFrom";
    public static final String PATH = "path";
    public DownloadItem() {
    }

    public DownloadItem(String name, String videoFrom, String path) {
        this.name = name;
        this.videoFrom = videoFrom;
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVideoFrom() {
        return videoFrom;
    }

    public void setVideoFrom(String videoFrom) {
        this.videoFrom = videoFrom;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
