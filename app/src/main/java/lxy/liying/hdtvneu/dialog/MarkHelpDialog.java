package lxy.liying.hdtvneu.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import lxy.liying.hdtvneu.R;

/**
 * =======================================================
 * 版权：©Copyright LiYing 2015-2016. All rights reserved.
 * 作者：liying
 * 日期：2016/8/31 18:50
 * 版本：1.0
 * 描述：收藏帮助对话框
 * 备注：
 * =======================================================
 */
public class MarkHelpDialog extends Dialog {

    public MarkHelpDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_mark_help);
        setTitle("如何添加收藏");
    }
}
