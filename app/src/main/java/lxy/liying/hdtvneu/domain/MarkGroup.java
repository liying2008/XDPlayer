package lxy.liying.hdtvneu.domain;

/**
 * =======================================================
 * 版权：©Copyright LiYing 2015-2016. All rights reserved.
 * 作者：liying
 * 日期：2016/8/24 13:49
 * 版本：1.0
 * 描述：收藏的视频分组
 * 备注：
 * =======================================================
 */
public enum MarkGroup implements GroupI {
    NEU_TV(0, "东北大学HDTV"),
    BY_TV(1, "北邮人IPTV"),
    QH_TV(2, "清华大学IPTV"),
    LOCAL(3, "本地视频"),
    ONLINE(4, "在线视频"),
    BILI(5, "B"),
    ACFUN(6, "A");


    private int index;
    private String name;

    // 构造方法
    MarkGroup(int index, String name) {
        this.index = index;
        this.name = name;
    }

    //接口方法
    @Override
    public String getName() {
        return this.name;
    }

    //接口方法
    @Override
    public int getIndex() {
        return this.index;
    }

    public static MarkGroup getMarkGroup(int index) {
        switch (index) {
            case 0:
                return NEU_TV;
            case 1:
                return BY_TV;
            case 2:
                return QH_TV;
            case 3:
                return LOCAL;
            case 4:
                return ONLINE;
            case 5:
                return BILI;
            case 6:
                return ACFUN;
            default:
                return null;
        }
    }
}
