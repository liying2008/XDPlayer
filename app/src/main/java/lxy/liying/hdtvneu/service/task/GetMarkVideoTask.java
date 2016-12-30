package lxy.liying.hdtvneu.service.task;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import java.util.ArrayList;
import java.util.List;

import lxy.liying.hdtvneu.app.App;
import lxy.liying.hdtvneu.domain.MarkGroup;
import lxy.liying.hdtvneu.domain.MarkItem;
import lxy.liying.hdtvneu.service.callback.OnGetMarkVideosCallback;

/**
 * =======================================================
 * 版权：©Copyright LiYing 2015-2016. All rights reserved.
 * 作者：liying
 * 日期：2016/9/11 12:47
 * 版本：1.0
 * 描述：从数据库读取收藏视频
 * 备注：
 * =======================================================
 */
public class GetMarkVideoTask extends AsyncTask<Void, Void, Void> {
    private OnGetMarkVideosCallback callback;
    private List<MarkItem> ipv6MarkItems, biliMarkItems, localMarkItems, onlineMarkItems;

    private static final int OK = 0x0000;
    private static final int NULL = 0x0002;
    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case OK:
                    callback.onGetBiliMark(biliMarkItems);
                    callback.onGetIPv6Mark(ipv6MarkItems);
                    callback.onGetLocalMark(localMarkItems);
                    callback.onGetOnlineMark(onlineMarkItems);
                    App.ipv6MarkItems = ipv6MarkItems;
                    App.biliMarkItems = biliMarkItems;
                    App.localMarkItems = localMarkItems;
                    App.onlineMarkItems = onlineMarkItems;
                    break;
                case NULL:
                    callback.onNull();
                    App.ipv6MarkItems = ipv6MarkItems;
                    App.biliMarkItems = biliMarkItems;
                    App.localMarkItems = localMarkItems;
                    App.onlineMarkItems = onlineMarkItems;
                    break;
                default:
                    break;
            }
            return false;
        }
    });

    public GetMarkVideoTask(OnGetMarkVideosCallback callback) {
        this.callback = callback;
    }

    @Override
    protected Void doInBackground(Void... params) {
        ipv6MarkItems = new ArrayList<>();
        biliMarkItems = new ArrayList<>();
        localMarkItems = new ArrayList<>();
        onlineMarkItems = new ArrayList<>();
        List<MarkItem> allMarks = App.markService.getAllMarks();
        if (allMarks != null && allMarks.size() > 0) {
            // 收藏分类
            for (int i = 0; i < allMarks.size(); i++) {
                MarkItem markItem = allMarks.get(i);
                if (markItem.getGroup() == MarkGroup.NEU_TV ||
                        markItem.getGroup() == MarkGroup.BY_TV ||
                        markItem.getGroup() == MarkGroup.QH_TV) {
                    ipv6MarkItems.add(markItem);
                } else if (markItem.getGroup() == MarkGroup.LOCAL) {
                    localMarkItems.add(markItem);
                } else if (markItem.getGroup() == MarkGroup.ONLINE) {
                    onlineMarkItems.add(markItem);
                } else if (markItem.getGroup() == MarkGroup.BILI ||
                        markItem.getGroup() == MarkGroup.ACFUN) {
                    biliMarkItems.add(markItem);
                }
            }
            mHandler.sendEmptyMessage(OK);
        } else {
            mHandler.sendEmptyMessage(NULL);
        }
        return null;
    }
}
