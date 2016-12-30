package lxy.liying.hdtvneu.service.task;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import lxy.liying.hdtvneu.service.callback.OnGetBiliPName;
import lxy.liying.hdtvneu.utils.HtmlGetter;

/**
 * =======================================================
 * 版权：©Copyright LiYing 2015-2016. All rights reserved.
 * 作者：liying
 * 日期：2016/8/24 0:31
 * 版本：1.0
 * 描述：查找视频是否有多P
 * 备注：
 * =======================================================
 */
public class BiliPNameGetterTask extends AsyncTask<String, Void, Void> {
    private static final String TAG = "BiliPNameGetterTask";
    private List<String> pNames;
    private OnGetBiliPName callback;
    private String av;

    private static final int GET_P = 0x0000;
    private static final int NO_P = 0x0001;
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == NO_P) {
                callback.noP(av);
            } else if (msg.what == GET_P) {
                callback.getPNames(av, pNames);
            }
            return false;
        }
    });

    public BiliPNameGetterTask(OnGetBiliPName callback) {
        this.callback = callback;
    }

    /**
     * 查找视频分P名称
     *
     * @param params params[0]:av号
     * @return
     */
    @Override
    protected Void doInBackground(String... params) {
        pNames = new ArrayList<>();
        av = params[0];
        String url = "http://www.bilibili.com/video/av" + av;
        String html = HtmlGetter.getHtmlWithUA(url, "UTF-8");
        Document document = Jsoup.parse(html);
        Elements options = document.getElementsByTag("option");
//        Log.i(TAG, "options = " + options);
        if (options.size() == 0) {
            // 没有分P
            handler.sendEmptyMessage(NO_P);
        } else {
            for (Element option : options) {
                pNames.add(option.text());
            }
            handler.sendEmptyMessage(GET_P);
        }
        return null;
    }
}
