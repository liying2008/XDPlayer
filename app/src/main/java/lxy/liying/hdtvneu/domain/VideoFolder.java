package lxy.liying.hdtvneu.domain;

import java.io.Serializable;
import java.util.List;

/**
 * =======================================================
 * 作者：liying
 * 日期：2016/8/17 14:46
 * 版本：1.0
 * 描述：视频文件夹实体类
 * 备注：
 * =======================================================
 */
public class VideoFolder implements Serializable {
    /** 文件夹名字 */
    private String name;
    /** 文件夹中视频的数量 */
    private int count;
    /** 文件夹中的所有视频信息 */
    private List<XDVideo> xdVideos;

    public VideoFolder() {
    }

    public VideoFolder(String name, int count, List<XDVideo> xdVideos) {
        this.name = name;
        this.count = count;
        this.xdVideos = xdVideos;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<XDVideo> getXdVideos() {
        return xdVideos;
    }

    public void setXdVideos(List<XDVideo> xdVideos) {
        this.xdVideos = xdVideos;
    }
}
