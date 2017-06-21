package lxy.liying.hdtvneu.utils;

import android.util.Log;

/**
 * =======================================================
 * 作者：liying
 * 日期：2016/9/12 14:04
 * 版本：1.0
 * 描述：
 * 备注：
 * =======================================================
 */
public class WebUrlUtil {
    private static final String TAG = "WebUrlUtil";
    /**
     * 从url中提取host
     * @param url 视频网址
     * @return
     */
    public static String getHost(String url) {
        //String url="http://www.baidu.com:8080/index.jsp";
        //String url2="ftp://baidu.com/pub/index.jsp";
        String webUrl = "";
        try {
            int firSplit = url.indexOf("//");
            int webSplit = url.indexOf("/", firSplit + 2);
//            Log.i(TAG, "firSplit: " + firSplit + "webSplit: " + webSplit);
            if (firSplit != -1 && webSplit != -1) {
                webUrl = url.substring(firSplit + 2, webSplit);
            } else {
                webUrl = "未知";
            }
        } catch (StringIndexOutOfBoundsException e) {
            webUrl = "未知";
            e.printStackTrace();
        }
        return webUrl;
    }
}
