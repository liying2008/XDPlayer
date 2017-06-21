package lxy.liying.hdtvneu.domain;

import java.util.List;

/**
 * =======================================================
 * 作者：liying
 * 日期：2016/9/8 9:44
 * 版本：1.0
 * 描述：AcFun视频实体类
 * 备注：
 * =======================================================
 */
public class AcFunVideo {
    /** 封面图片url */
    private String titleImg;
    /** ac号 */
    private String contentId;
    /** 标题 */
    private String title;
    /** 分P名称 */
    private List<String> pNames;
    /** up主名字 */
    private String username;
    /** 播放数量 */
    private String views;
    /** 视频时长(单位：秒) */
    private int time;

    public AcFunVideo() {
    }

    public AcFunVideo(int time, String titleImg, String contentId, String title, String username, String views) {
        this.time = time;
        this.titleImg = titleImg;
        this.contentId = contentId;
        this.title = title;
        this.username = username;
        this.views = views;
    }

    public String getTitleImg() {
        return titleImg;
    }

    public void setTitleImg(String titleImg) {
        this.titleImg = titleImg;
    }

    public String getContentId() {
        return contentId;
    }

    public void setContentId(String contentId) {
        this.contentId = contentId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getpNames() {
        return pNames;
    }

    public void setpNames(List<String> pNames) {
        this.pNames = pNames;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getViews() {
        return views;
    }

    public void setViews(String views) {
        this.views = views;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

}
