package lxy.liying.hdtvneu.domain;

/**
 * =======================================================
 * 版权：Copyright LiYing 2015-2016. All rights reserved.
 * 作者：liying
 * 日期：2016/5/15 18:46
 * 版本：1.0
 * 描述：节目实体类
 * 备注：
 * =======================================================
 */
public class Program {
    private int id;
    /** 频道 */
    private String p;
    /** 节目名称 */
    private String name;
    /** 节目地址 */
    private String path;

    public Program() {

    }

    public Program(int id, String p, String name, String path) {
        this.id = id;
        this.p = p;
        this.name = name;
        this.path = path;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getP() {
        return p;
    }

    public void setP(String p) {
        this.p = p;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

}
