package lxy.liying.hdtvneu.domain;


import java.io.Serializable;

/**
 * =======================================================
 * 版权：©Copyright LiYing 2015-2016. All rights reserved.
 * 作者：liying
 * 日期：2016/8/17 15:39
 * 版本：1.0
 * 描述：视频信息实体类
 * 备注：
 * =======================================================
 */
public class XDVideo implements Serializable{
    /**
     * 视频ID
     */
    private long _id;
    /**
     * 视频所属文件夹
     */
    private String folder;
    /**
     * 视频缩略图路径
     */
    private String videoThumbnail;
    /**
     * 视频标题
     */
    private String title;
    /**
     * 视频大小
     */
    private long size;
    /**
     * 视频时长
     */
    private long duration;
    /**
     * 视频完整路径
     */
    private String data;
    /**
     * 显示文件名
     */
    private String displayName;
    /**
     * 视频类型
     */
    private String mimeType;
    /**
     * 创建时间
     */
    private long dateAdded;
    /**
     * 上次修改时间
     */
    private long dateModified;
    /**
     * 艺术家
     */
    private String artist;
    /**
     * 专辑
     */
    private String album;
    /**
     * 分辨率
     */
    private String resolution;
    /**
     * 描述
     */
    private String description;
    /**
     * 上次观看位置
     */
    private long position = 0L;

    public static final String _ID = "_id";
    /**
     * 默认排序字段
     */
    public static final String FOLDER = "folder";
    public static final String VIDEO_THUMBNAIL = "videoThumbnail";
    public static final String TITLE = "title";
    public static final String SIZE = "size";
    public static final String DURATION = "duration";
    public static final String DATA = "data";
    public static final String DISPLAY_NAME = "displayName";
    public static final String MIME_TYPE = "mimeType";
    public static final String DATE_ADDED = "dateAdded";
    public static final String DATE_MODIFIED = "dateModified";
    public static final String ARTIST = "artist";
    public static final String ALBUM = "album";
    public static final String RESOLUTION = "resolution";
    public static final String DESCRIPTION = "description";
    public static final String POSITION = "position";

    public XDVideo() {

    }

    public XDVideo(long _id, String folder, String videoThumbnail, String title, long size, long duration, String data, String displayName, String mimeType, long dateAdded, long dateModified, String artist, String album, String resolution, String description) {
        this._id = _id;
        this.folder = folder;
        this.videoThumbnail = videoThumbnail;
        this.title = title;
        this.size = size;
        this.duration = duration;
        this.data = data;
        this.displayName = displayName;
        this.mimeType = mimeType;
        this.dateAdded = dateAdded;
        this.dateModified = dateModified;
        this.artist = artist;
        this.album = album;
        this.resolution = resolution;
        this.description = description;
    }

    public XDVideo(long _id, String folder, String videoThumbnail, String title, long size, long duration, String data, String displayName, String mimeType, long dateAdded, long dateModified, String artist, String album, String resolution, String description, long position) {
        this._id = _id;
        this.folder = folder;
        this.videoThumbnail = videoThumbnail;
        this.title = title;
        this.size = size;
        this.duration = duration;
        this.data = data;
        this.displayName = displayName;
        this.mimeType = mimeType;
        this.dateAdded = dateAdded;
        this.dateModified = dateModified;
        this.artist = artist;
        this.album = album;
        this.resolution = resolution;
        this.description = description;
        this.position = position;
    }

    public long get_id() {
        return _id;
    }

    public void set_id(long _id) {
        this._id = _id;
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public String getVideoThumbnail() {
        return videoThumbnail;
    }

    public void setVideoThumbnail(String videoThumbnail) {
        this.videoThumbnail = videoThumbnail;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public long getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(long dateAdded) {
        this.dateAdded = dateAdded;
    }

    public long getDateModified() {
        return dateModified;
    }

    public void setDateModified(long dateModified) {
        this.dateModified = dateModified;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getPosition() {
        return position;
    }

    public void setPosition(long position) {
        this.position = position;
    }

    /**
     * 判断两个XDVideo对象是否相等
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (o instanceof XDVideo) {
            XDVideo video = (XDVideo) o;
            if (video.get_id() == this._id) {
                return true;
            }
        }
        return false;
    }
}
