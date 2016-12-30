package lxy.liying.hdtvneu.service.task;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import lxy.liying.hdtvneu.domain.BiliVideo;
import lxy.liying.hdtvneu.service.callback.OnGetBiliVideoCallback;
import lxy.liying.hdtvneu.utils.HtmlGetter;

/**
 * =======================================================
 * 版权：©Copyright LiYing 2015-2016. All rights reserved.
 * 作者：liying
 * 日期：2016/8/22 20:59
 * 版本：1.0
 * 描述：获取并解析Html
 * 备注：
 * =======================================================
 */
public class ParseBiliHtmlTask extends AsyncTask<String, Void, Void> {
    private OnGetBiliVideoCallback callback;
    private List<BiliVideo> biliVideos = new ArrayList<>();
    private boolean isAll = false;
    private int avNum;
    private static final int NO_MORE = 0x0000;
    private static final int ON_FAILURE = 0x0001;

    public ParseBiliHtmlTask(OnGetBiliVideoCallback callback, int avNum) {
        this.callback = callback;
        this.avNum = avNum;
    }

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == NO_MORE) {
                callback.noMore();
            } else if (msg.what == ON_FAILURE) {
                callback.onFailure();
            }
            return false;
        }
    });
    /**
     * 获取json并解析出html,在解析html获取数据
     *
     * @param params params[0]：搜索关键字，params[1]：页码
     * @return
     */
    @Override
    protected Void doInBackground(String... params) {
        if (avNum == -1) {
            // 关键词搜索
            searchKeyword(params[0], params[1]);
        } else {
            // av号搜索
            searchAV(avNum);
        }
        return null;
    }

    /**
     * av号搜索
     * @param avNum
     */
    private void searchAV(int avNum) {
        try {
            String url = "http://search.bilibili.com/all?keyword=" + avNum;
            String html = HtmlGetter.getHtml(url, "UTF-8");
            Document doc = Jsoup.parse(html);

            Element select = doc.select(".video").select(".list").select(".av").get(0);

            Element mainHrefEle = select.getElementsByTag("a").get(0);
            // 得到视频链接
            String mainHref = mainHrefEle.attr("href");
            // 得到视频标题
            String mainTitle = mainHrefEle.attr("title");
            // 得到视频AV号
            String mainAv = getAVNum(mainHref);
            // 得到图片地址
            String mainCoverUrl= select.getElementsByTag("img").get(0).attr("src");
            // 得到播放时间
            String mainTime= select.getElementsByTag("span").get(0).text();

            Elements mainInfo = select.getElementsByClass("so-icon");

            // 得到播放数
            String mainWatchInfo = mainInfo.get(0).text();

            Element mainUpInfo = mainInfo.get(3);
            // 得到UP主姓名
            String mainUp = mainUpInfo.getElementsByTag("a").text();

            BiliVideo biliVideo = new BiliVideo(mainCoverUrl, mainAv, mainTitle, mainUp, mainWatchInfo, mainTime);

            biliVideos.add(biliVideo);
        } catch (Exception e) {
            handler.sendEmptyMessage(ON_FAILURE);
            isAll = true;
            e.printStackTrace();
        }
    }

    private void searchKeyword(String keyword, String page) {
        try {
            keyword = URLEncoder.encode(keyword, "UTF-8");
            String url = "http://search.bilibili.com/ajax_api/video?keyword=" + keyword + "&order=totalrank&page=" + page;
            JSONObject json = getJson(url);
            int code = json.getInt("code");
            if (code == 1) {
                // 没有相关数据
                isAll = true;
                handler.sendEmptyMessage(NO_MORE);
            } else if (code == 0) {
                String html = json.getString("html");
                Document doc = Jsoup.parse(html);
                Elements elements = doc.select(".video").select(".matrix");
                for (Element ele : elements) {
                    Element hrefEle = ele.getElementsByTag("a").get(0);
                    // 得到视频链接
                    String href = hrefEle.attr("href");
                    // 得到视频标题
                    String title = hrefEle.attr("title");
                    // 得到视频AV号
                    String av = getAVNum(href);
                    Element img = ele.getElementsByTag("img").get(0);
                    // 得到图片地址
                    String coverUrl = img.attr("src");
                    // 得到播放时间
                    String time= ele.getElementsByTag("span").get(0).text();

                    Elements info = ele.getElementsByClass("so-icon");

                    // 得到播放数
                    String watchInfo = info.get(0).text();

                    Element upInfo = info.get(3);
                    // 得到UP主姓名
                    String up = upInfo.getElementsByTag("a").text();
                    // 保存
                    biliVideos.add(new BiliVideo(coverUrl, av, title, up, watchInfo, time));
                }
            }
        } catch (Exception e) {
            handler.sendEmptyMessage(ON_FAILURE);
            isAll = true;
            e.printStackTrace();
        }
    }
    /**
     * 从指定url获取json字符串
     *
     * @param url
     * @return
     */
    private JSONObject getJson(String url) throws JSONException {
        String html = HtmlGetter.getHtml(url, "UTF-8");
        return new JSONObject(html);
    }

    /**
     * 得到AV号
     *
     * @param href
     * @return
     */
    private String getAVNum(String href) {
        String av = href.replace("http://www.bilibili.com/video/av", "").replace("/", "");
        return av;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (!isAll) {
            callback.getAllVideo(biliVideos);
        }
    }
}
