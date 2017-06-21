package lxy.liying.hdtvneu.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

import lxy.liying.hdtvneu.R;
import lxy.liying.hdtvneu.activity.SearchListActivity;
import lxy.liying.hdtvneu.app.App;

/**
 * =======================================================
 * 作者：liying
 * 日期：2016/8/18 22:35
 * 版本：1.0
 * 描述：视频搜索对话框
 * 备注：
 * =======================================================
 */
public class SearchDialog extends Dialog implements View.OnClickListener {
    private Activity activity;
    private EditText etSearchKeyword;

    public SearchDialog(Activity activity) {
        super(activity);
        this.activity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_search_video);

        etSearchKeyword = (EditText) findViewById(R.id.etSearchKeyword);
        RelativeLayout rlCancel = (RelativeLayout) findViewById(R.id.rlCancel);
        RelativeLayout rlSearch = (RelativeLayout) findViewById(R.id.rlSearch);
        setTitle("本地搜索");
        rlCancel.setOnClickListener(this);
        rlSearch.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.rlCancel) {
            // 取消搜索
            dismiss();
        } else if (id == R.id.rlSearch) {
            // 搜索
            String keyword = etSearchKeyword.getText().toString();
            App.xdVideos = App.xdService.getXDVideos(keyword);
            App.programType = "5";
            Intent intent = new Intent(activity, SearchListActivity.class);
            activity.startActivity(intent);
            activity.overridePendingTransition(R.anim.push_left_in_ac, R.anim.push_left_out_ac);
            dismiss();
        }
    }
}
