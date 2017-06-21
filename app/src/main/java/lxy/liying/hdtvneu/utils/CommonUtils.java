package lxy.liying.hdtvneu.utils;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Locale;

import cc.duduhuo.applicationtoast.AppToast;
import lxy.liying.hdtvneu.R;
import lxy.liying.hdtvneu.app.App;

/**
 * =======================================================
 * 作者：liying - liruoer2008@yeah.net
 * 日期：2017/3/31 19:07
 * 版本：1.0
 * 描述：通用工具类
 * 备注：
 * =======================================================
 */
public class CommonUtils {
    /**
     * 拷贝文本
     *
     * @param context
     * @param text    拷贝的文本
     */
    public static void copyText(Context context, String text) {
        ClipboardManager cmbName = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipDataName = ClipData.newPlainText(null, text);
        cmbName.setPrimaryClip(clipDataName);
    }

    /**
     * 分享文本
     *
     * @param context
     * @param text    分享的文本
     */
    public static void shareText(Context context, String text) {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType("text/*");
        shareIntent.putExtra(Intent.EXTRA_TEXT, text);
        ComponentName componentName = shareIntent.resolveActivity(context.getPackageManager());
        if (componentName != null) {
            context.startActivity(Intent.createChooser(shareIntent, context.getString(R.string.share_to)));
        } else {
            AppToast.showToast("无法分享。");
        }
    }

    /**
     * 打开浏览器
     *
     * @param context
     * @param url     浏览器加载的网址
     */
    public static void openBrowser(Context context, String url) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        ComponentName componentName = intent.resolveActivity(App.getInstance().getPackageManager());
        if (componentName != null) {
            context.startActivity(intent);
        } else {
            AppToast.showToast("您没有安装浏览器。");
        }
    }

    /**
     * 格式化时间
     *
     * @param sec 秒
     * @return 格式：09:08:31
     */
    public static CharSequence formatTime(int sec) {
        int h = sec / 3600;
        int m = (sec % 3600) / 60;
        int s = sec % 3600 % 60;
        String out = "";
        if (h > 0) {
            if (h < 10) {
                out += "0" + h + ":";
            } else {
                out += h + ":";
            }
        }
        if (m < 10) {
            out += "0" + m + ":";
        } else {
            out += m + ":";
        }

        if (s < 10) {
            out += "0" + s + "";
        } else {
            out += s + "";
        }
        return out;
    }

    /**
     * 格式化视频大小，将字节转换为其他单位
     *
     * @param size 字节
     * @return
     */
    public static CharSequence getSizeString(long size) {
        if (size > (1024 * 1024 * 1024)) {
            // 以G为单位
            double rlt = (double) size / (double) (1024 * 1024 * 1024);
            return String.format(Locale.getDefault(), "%.3f", rlt) + "GB";
        } else if (size > (1024 * 1024)) {
            // 以M为单位
            double rlt = (double) size / (double) (1024 * 1024);
            return String.format(Locale.getDefault(), "%.2f", rlt) + "MB";
        } else if (size > 1024) {
            // 以K为单位
            double rlt = (double) size / (double) (1024);
            return String.format(Locale.getDefault(), "%.2f", rlt) + "KB";
        } else {
            // 以B为单位
            return String.valueOf(size) + "B";
        }
    }

    /**
     * 获取网页HTML
     *
     * @param urlpath 网址
     * @param encoding 字符编码
     * @return 网页html
     */
    public static String getHtml(String urlpath, String encoding) {
        StringBuilder sb = new StringBuilder();
        try {
            //构建URL对象
            URL url = new URL(urlpath);
            //使用openStream得到输入流并由此构造一个BufferedReader对象
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream(), encoding));
            String line;
            //读取html
            while ((line = in.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            return "null";
        }
        return sb.toString();
    }

}
