package lxy.liying.hdtvneu.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import cc.duduhuo.applicationtoast.AppToast;
import lxy.liying.hdtvneu.R;
import lxy.liying.hdtvneu.app.App;
import lxy.liying.hdtvneu.utils.Constants;

/**
 * =======================================================
 * 作者：liying
 * 日期：2016/9/15 14:17
 * 版本：1.0
 * 描述：应用设置界面
 * 备注：
 * =======================================================
 */
public class SettingsActivity extends BaseActivity {
    private Spinner spinnerIPv6Pages, spinnerMarkPages;
    private static int ipv6Home;
    private static int markHome;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);
        LinearLayout llBack = (LinearLayout) findViewById(R.id.llBack);
        llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.push_right_in_ac, R.anim.push_right_out_ac);
            }
        });
        spinnerIPv6Pages = (Spinner) findViewById(R.id.spinnerIPv6Pages);
        spinnerMarkPages = (Spinner) findViewById(R.id.spinnerMarkPages);

        initData();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        int ipv6Home = Integer.parseInt(App.getInstance().getSetting(Constants.IPV6_HOME, "0"));
        final int markHome = Integer.parseInt(App.getInstance().getSetting(Constants.MARK_HOME, "0"));

        SettingsActivity.ipv6Home = ipv6Home;
        spinnerIPv6Pages.setSelection(ipv6Home, false);
        SettingsActivity.markHome = markHome;
        spinnerMarkPages.setSelection(markHome, false);

        spinnerIPv6Pages.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != SettingsActivity.ipv6Home) {
                    App.getInstance().putSetting(Constants.IPV6_HOME, String.valueOf(position));
                    AppToast.showToast("设置已保存。");
                    SettingsActivity.ipv6Home = position;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerMarkPages.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != SettingsActivity.markHome) {
                    App.getInstance().putSetting(Constants.MARK_HOME, String.valueOf(position));
                    AppToast.showToast("设置已保存。");
                    SettingsActivity.markHome = position;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
}
