package lxy.liying.hdtvneu.domain;

import java.util.List;

/**
 * =======================================================
 * 版权：©Copyright LiYing 2015-2016. All rights reserved.
 * 作者：liying
 * 日期：2016/8/22 21:54
 * 版本：1.0
 * 描述：bilibili视频实体类
 * 备注：
 * =======================================================
 */
public class BiliVideo {
    /** 封面图片url */
    private String coverUrl;
    /** av号 */
    private String av;
    /** 标题 */
    private String title;
    /** 分P名称 */
    private List<String> pNames;
    /** up主名字 */
    private String up;
    /** 播放次数 */
    private String play;
    /** 视频时长 */
    private String time;

    public BiliVideo() {
    }

    public BiliVideo(String coverUrl, String av, String title, String up, String play, String time) {
        this.coverUrl = coverUrl;
        this.av = av;
        this.title = title;
        this.up = up;
        this.play = play;
        this.time = time;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public String getAv() {
        return av;
    }

    public void setAv(String av) {
        this.av = av;
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

    public String getUp() {
        return up;
    }

    public void setUp(String up) {
        this.up = up;
    }

    public String getPlay() {
        return play;
    }

    public void setPlay(String play) {
        this.play = play;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

}
