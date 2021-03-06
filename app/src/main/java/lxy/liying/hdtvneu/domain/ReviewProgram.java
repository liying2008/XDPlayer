package lxy.liying.hdtvneu.domain;

/**
 * =======================================================
 * 作者：liying
 * 日期：2016/5/16 21:48
 * 版本：1.0
 * 描述：回看节目列表实体类
 * 备注：
 * =======================================================
 */
public class ReviewProgram {
    private String name;
    private String timeStart;
    private String timeEnd;

    /** 空参构造器 */
    public ReviewProgram() {
    }

    /** 构造器 */
    public ReviewProgram(String name, String timeStart, String timeEnd) {
        this.name = name;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
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

    public void setTimeStart(String timeStart) {
        this.timeStart = timeStart;
    }

    public String getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(String timeEnd) {
        this.timeEnd = timeEnd;
    }
}
