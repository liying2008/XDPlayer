package lxy.liying.hdtvneu.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flyco.dialog.listener.OnOperItemClickL;
import com.flyco.dialog.widget.ActionSheetDialog;

import cc.duduhuo.applicationtoast.AppToast;
import lxy.liying.hdtvneu.R;
import lxy.liying.hdtvneu.app.App;
import lxy.liying.hdtvneu.utils.Constants;

/**
 * =======================================================
 * 版权：Copyright LiYing 2015-2016. All rights reserved.
 * 作者：liying
 * 日期：2016/5/19 16:31
 * 版本：1.0
 * 描述：关于界面
 * 备注：
 * =======================================================
 */
public class AboutActivity extends BaseActivity {
    /**
     * 作者微博地址
     */
    private static final String WEIBO_URL = "http://weibo.com/neuliying";
    /**
     * 官方网站
     */
    private static final String OFFICIAL_WEBSITE = "http://duduhuo.cc/";
    /**
     * 作者邮箱地址
     */
    private static final String EMAIL = "liruoer2008@yeah.net";
    private String version;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        TextView tvVersion = (TextView) findViewById(R.id.tvVersion);
        /** 显示版本 */
        version = "v" + App.getInstance().getVersionName();
        tvVersion.setText(version);
        LinearLayout llBack = (LinearLayout) findViewById(R.id.llBack);
        llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.push_right_in_ac, R.anim.push_right_out_ac);
            }
        });
    }

    /**
     * 访问作者微博
     *
     * @param view
     */
    public void onWeiboClick(View view) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(WEIBO_URL));
        ComponentName cn = intent.resolveActivity(getPackageManager());
        if (cn != null) {
            startActivity(intent);
        } else {
            AppToast.showToast(R.string.about_source_open_failed);
        }
    }

    /**
     * 应用推荐
     *
     * @param view
     */
    public void onRecommendClick(View view) {
        Intent intent = new Intent(this, RecommendActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.push_left_in_ac, R.anim.push_left_out_ac);
    }

    /**
     * 给作者发送邮件
     *
     * @param view
     */
    public void sendMail(View view) {
        final String[] stringItems = {"复制邮箱地址", "给作者发邮件"};
        final ActionSheetDialog dialog = new ActionSheetDialog(this, stringItems, null);
        dialog.title("选择操作")//
                .titleTextSize_SP(14.5f)//
                .show();

        dialog.setOnOperItemClickL(new OnOperItemClickL() {
            @Override
            public void onOperItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    // 复制邮箱地址
                    ClipboardManager cmbName = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clipDataName = ClipData.newPlainText(null, EMAIL);
                    cmbName.setPrimaryClip(clipDataName);
                    AppToast.showToast("邮箱地址已复制到剪贴板。");
                } else if (position == 1) {
                    // 发送邮件
                    // 获取设备分辨率大小
                    Display display = getWindowManager().getDefaultDisplay();
                    String resolution = "Resolution: " + display.getWidth() + "x" + display.getHeight() + "; ";
                    String msgPreset = resolution + "\nAndroid: " + android.os.Build.VERSION.RELEASE
                            + "; \nPhone: " + android.os.Build.MODEL
                            + "; \nVersion: " + version
                            + "; \n（以上数据由应用自动收集，发送邮件时请保留）。";
                    Intent mailIntent = new Intent(Intent.ACTION_SENDTO);
                    mailIntent.setData(Uri.parse("mailto:" + EMAIL));
                    mailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name) + " - 用户反馈");
                    mailIntent.putExtra(Intent.EXTRA_TEXT, msgPreset);
                    ComponentName componentName = mailIntent.resolveActivity(getPackageManager());
                    if (componentName != null) {
                        startActivity(mailIntent);
                    } else {
                        AppToast.showToast("您没有安装邮件类应用。");
                    }
                }
                dialog.dismiss();
            }
        });
    }

    /**
     * 访问官方下载地址
     *
     * @param view
     */
    public void onOfficialWebsite(View view) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(OFFICIAL_WEBSITE));
        ComponentName cn = intent.resolveActivity(getPackageManager());
        if (cn != null) {
            startActivity(intent);
        } else {
            AppToast.showToast(R.string.about_source_open_failed);
        }
    }

    /**
     * 分享该应用
     *
     * @param view
     */
    public void onShareClick(View view) {
        // 分享应用
        String content = getString(R.string.share_description, getString(R.string.app_name));
        content += "\n\n——发送自 " + getString(R.string.app_name);
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType("text/*");
        shareIntent.putExtra(Intent.EXTRA_TEXT, content);
        ComponentName componentName1 = shareIntent.resolveActivity(getPackageManager());
        if (componentName1 != null) {
            startActivity(Intent.createChooser(shareIntent, getString(R.string.share_to)));
        } else {
            AppToast.showToast("无法分享。");
        }
    }
}
