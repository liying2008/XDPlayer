package lxy.liying.hdtvneu.domain;

import java.util.List;

/**
 * =======================================================
 * 版权：Copyright LiYing 2015-2016. All rights reserved.
 * 作者：liying
 * 日期：2016/5/17 8:13
 * 版本：1.0
 * 描述：回看列表实体类
 * 备注：
 * =======================================================
 */
public class ReviewList {

    private List<ReviewDate> groupDates;
    private List<List<ReviewProgram>> childPrograms;

    public List<ReviewDate> getGroupDates() {
        return groupDates;
    }

    public void setGroupDates(List<ReviewDate> groupDates) {
        this.groupDates = groupDates;
    }

    public List<List<ReviewProgram>> getChildPrograms() {
        return childPrograms;
    }

    public void setChildPrograms(List<List<ReviewProgram>> childPrograms) {
        this.childPrograms = childPrograms;
    }

    public ReviewList(List<ReviewDate> groupDates, List<List<ReviewProgram>> childPrograms) {

        this.groupDates = groupDates;
        this.childPrograms = childPrograms;
    }
}