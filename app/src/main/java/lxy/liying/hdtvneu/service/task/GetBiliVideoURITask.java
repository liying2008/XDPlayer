package lxy.liying.hdtvneu.service.task;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

import lxy.liying.hdtvneu.service.callback.OnGetBiliVideoURICallback;
import lxy.liying.hdtvneu.utils.HtmlGetter;

/**
 * =======================================================
 * 版权：©Copyright LiYing 2015-2016. All rights reserved.
 * 作者：liying
 * 日期：2016/8/23 16:15
 * 版本：1.0
 * 描述：获取视频在线播放地址
 * 备注：
 * =======================================================
 */
public class GetBiliVideoURITask extends AsyncTask<String, Void, Void> {
    private static final String TAG = "GetBiliVideoURITask";
    private OnGetBiliVideoURICallback callback;
    private static final int ON_FAILURE = 0x0001;
    private ArrayList<String> downloadLinks = new ArrayList<>();

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == ON_FAILURE) {
                callback.onUriFailure();
            }
            return false;
        }
    });

    public GetBiliVideoURITask(OnGetBiliVideoURICallback callback) {
        this.callback = callback;
    }

    /**
     * 获取视频在线播放的地址
     *
     * @param params params[0]：AV号， params[1]分p号，没有分p传0
     * @return null
     */
    @Override
    protected Void doInBackground(String... params) {
        String url;
        if (params[1].equals("0")) {
            // 利用硕鼠解析视频的下载地址
            url = "http://www.flvcd.com/parse.php?format=&kw=http%3A%2F%2Fwww.bilibili.com%2Fvideo%2Fav" + params[0] + "%2F";
        } else {
            // 利用硕鼠解析视频的下载地址
            url = "http://www.flvcd.com/parse.php?kw=http%3A%2F%2Fwww.bilibili.com%2Fvideo%2Fav" + params[0] + "%2Findex_" + params[1] + ".html&flag=one&format=super";
        }
        try {
            String html = HtmlGetter.getHtml(url, "GBK");
            Document doc = Jsoup.parse(html);
            // 查找带有target和onclick属性的a标签，得到其href属性值
            Elements links = doc.select("a[target][onclick]");
            for (Element link : links) {
                downloadLinks.add(link.attr("href"));
            }

//            Log.i(TAG, "下载地址：" + downloadLinks);
        } catch (Exception e) {
            handler.sendEmptyMessage(ON_FAILURE);
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
//        Log.i(TAG, "下载地址：" + downloadLinks);
        callback.getURI(downloadLinks);
    }
}
