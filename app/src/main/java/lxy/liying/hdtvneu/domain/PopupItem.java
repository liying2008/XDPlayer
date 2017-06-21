package lxy.liying.hdtvneu.domain;

/**
 * =======================================================
 * 作者：liying
 * 日期：2016/5/16 21:48
 * 版本：1.0
 * 描述：PopupWindow列表内容实体类
 * 备注：
 * =======================================================
 */
public class PopupItem {
    /** 频道 */
    private String p;
    /** 电视节目播放地址 */
    private String path;
    /** 名称 */
    private String name;
    private String timeStart;
    private String timeEnd;
    private XDVideo xdVideo;

    /** 构造器 */
    public PopupItem(String path, String name) {
        this.path = path;
        this.name = name;
    }

    /** 构造器 */
    public PopupItem(String name, XDVideo xdVideo) {
        this.name = name;
        this.xdVideo = xdVideo;
    }

    /** 构造器 */
    public PopupItem(String p, String name, String timeStart, String timeEnd) {
        this.p = p;
        this.name = name;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
    }

    public String getP() {
        return p;
    }

    public void setP(String p) {
        this.p = p;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTimeStart() {
        return timeStart;
    }

    public String getTimeEnd() {
        return timeEnd;
    }

    public XDVideo getXdVideo() {
        return xdVideo;
    }

    public void setXdVideo(XDVideo xdVideo) {
        this.xdVideo = xdVideo;
    }
}
