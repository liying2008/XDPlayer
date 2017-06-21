package lxy.liying.hdtvneu.activity;

import android.view.MotionEvent;

/**
 * =======================================================
 * 作者：liying
 * 日期：2016/8/21 13:59
 * 版本：1.0
 * 描述：播放器接口
 * 备注：
 * =======================================================
 */
public interface Player {
    /** 手势结束 */
    void endGesture();
    /** 改变声音或亮度 */
    void changeVolOrBri(MotionEvent e1, MotionEvent e2);
    /** 改变画面尺寸 */
    void changePictureSize();
}
