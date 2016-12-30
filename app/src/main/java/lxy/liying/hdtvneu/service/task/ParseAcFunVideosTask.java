package lxy.liying.hdtvneu.service.task;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import lxy.liying.hdtvneu.domain.AcFunVideo;
import lxy.liying.hdtvneu.service.callback.OnGetAcFunVideoCallback;
import lxy.liying.hdtvneu.utils.HtmlGetter;

/**
 * =======================================================
 * 版权：©Copyright LiYing 2015-2016. All rights reserved.
 * 作者：liying
 * 日期：2016/9/8 10:03
 * 版本：1.0
 * 描述：解析AcFun视频列表
 * 备注：
 * =======================================================
 */
public class ParseAcFunVideosTask extends AsyncTask<String, Void, Void> {
    private OnGetAcFunVideoCallback callback;
    private List<AcFunVideo> acFunVideos = new ArrayList<>();
    private boolean isAll = false;
    private static final int NO_MORE = 0x0000;
    private static final int ON_FAILURE = 0x0001;

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
    public ParseAcFunVideosTask(OnGetAcFunVideoCallback callback) {
        this.callback = callback;
    }

    /**
     * 解析JSON，获得视频信息
     *
     * @param params params[0]：搜索关键字，params[1]：页码
     * @return
     */
    @Override
    protected Void doInBackground(String... params) {

        try {
            String keyword = URLEncoder.encode(params[0], "UTF-8");
            String url = "http://search.acfun.tv/search?cd=1&type=2&q=" + keyword + "&sortType=-1&field=title&sortField=score&pageNo=" + params[1] + "&pageSize=10&aiCount=3&spCount=3&isWeb=1&sys_name=pc";
            JSONObject json = getJson(url);
            if (json.getInt("status") == 200) {
                // 正常获取结果
                JSONArray list = json.getJSONObject("data").getJSONObject("page").getJSONArray("list");
                int length = list.length();
                if (length == 0) {
                    // 没有搜索结果
                    handler.sendEmptyMessage(NO_MORE);
                    isAll = true;
                    return null;
                }

                for (int i = 0; i < length; i++) {
                    JSONObject video = (JSONObject) list.get(i);
                    String contentId = video.getString("contentId");
                    String views = video.getString("views");
                    String titleImg = video.getString("titleImg");
                    String title = video.getString("title");
                    String username = video.getString("username");
                    int time = video.getInt("time");
                    acFunVideos.add(new AcFunVideo(time, titleImg, contentId, title, username, views));
                }

            } else {
                handler.sendEmptyMessage(ON_FAILURE);
                isAll = true;
            }
        } catch (Exception e) {
            handler.sendEmptyMessage(ON_FAILURE);
            isAll = true;
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 从指定url获取json字符串
     *
     * @param url
     * @return
     */
    private JSONObject getJson(String url) throws JSONException {
        String html = HtmlGetter.getHtml(url, "UTF-8");
        // 删掉前面的 system.tv=
        String json = html.substring(10);
        return new JSONObject(json);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (!isAll) {
            callback.getAllVideo(acFunVideos);
        }
    }
}
