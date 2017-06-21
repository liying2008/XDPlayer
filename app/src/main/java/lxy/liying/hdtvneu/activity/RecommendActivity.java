package lxy.liying.hdtvneu.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

import cc.duduhuo.applicationtoast.AppToast;
import lxy.liying.hdtvneu.R;
import lxy.liying.hdtvneu.adapter.RecommendListAdapter;

/**
 * =======================================================
 * 作者：liying
 * 日期：2016/9/8 18:33
 * 版本：1.0
 * 描述：应用推荐界面
 * 备注：
 * =======================================================
 */
public class RecommendActivity extends BaseActivity {
    // 图标数组
    private static int[] icons = {R.drawable.circle_512x512, R.drawable.ipgw_512x512, R.drawable.sec_512x512};
    // 软件名称数组
    private static int[] name = {R.string.recommend_name_circle, R.string.recommend_name_ipgw, R.string.recommend_name_sec};
    // 软件描述数组
    private static int[] desc = {R.string.recommend_desc_circle, R.string.recommend_desc_ipgw, R.string.recommend_desc_sec};
    // 软件包名
    public static String[] packageName = {"lxy.liying.circletodo", "com.liying.ipgw", "lxy.liying.secbook"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend);

        LinearLayout llBack = (LinearLayout) findViewById(R.id.llBack);
        llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.push_right_in_ac, R.anim.push_right_out_ac);
            }
        });

        ListView lvRecommend = (ListView) findViewById(R.id.lvRecommend);
        RecommendListAdapter adapter = new RecommendListAdapter(this, icons, name, desc);
        lvRecommend.setAdapter(adapter);
        adapter.setViewDetailsListener(new RecommendListAdapter.ViewDetailsListener() {
            @Override
            public void viewDetails(String packageName) {
                goToDetails(packageName);
            }
        });
    }

    /**
     * 跳到应用商店的下载页面
     *
     * @param packageName 应用的包名
     */
    public void goToDetails(String packageName) {
        Uri uri = Uri.parse("market://details?id=" + packageName);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ComponentName componentName = intent.resolveActivity(getPackageManager());
        if (componentName != null) {
            startActivity(intent);
        } else {
            AppToast.showToast("您没有安装应用市场类软件，无法打开应用下载页面。");
        }
    }
}
