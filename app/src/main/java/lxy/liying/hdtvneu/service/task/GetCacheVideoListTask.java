package lxy.liying.hdtvneu.service.task;

import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;

import lxy.liying.hdtvneu.app.App;
import lxy.liying.hdtvneu.domain.VideoFolder;
import lxy.liying.hdtvneu.domain.XDVideo;
import lxy.liying.hdtvneu.service.callback.OnGetVideoFolderListener;

/**
 * =======================================================
 * 作者：liying
 * 日期：2016/8/17 15:09
 * 版本：1.0
 * 描述：获取上次保存所有视频文件信息的异步任务类
 * 备注：
 * =======================================================
 */
public class GetCacheVideoListTask extends AsyncTask<Void, Integer, Void> {
    private OnGetVideoFolderListener listener;
    private List<VideoFolder> videoFolderList;
    public GetCacheVideoListTask(OnGetVideoFolderListener listener) {
        this.listener = listener;
    }

    @Override
    protected Void doInBackground(Void... params) {
        List<XDVideo> allXDVideos = App.xdService.getAllXDVideos();
        List<XDVideo> groupXDVideos = new ArrayList<>();
        String folderStr = "";
        VideoFolder videoFolder = new VideoFolder();
        videoFolderList = new ArrayList<>();
        if (allXDVideos != null && allXDVideos.size() > 0) {
            for (XDVideo video : allXDVideos) {
                String folder = video.getFolder();
                if (folderStr.equals("")) {
                    folderStr = folder;
                }
                if (!folder.equals(folderStr)) {
                    videoFolder.setCount(groupXDVideos.size());
                    videoFolder.setName(folderStr);
                    videoFolder.setXdVideos(copyXDVideos(groupXDVideos));
                    folderStr = folder;
                    groupXDVideos.clear();
                    videoFolderList.add(copyVideoFolder(videoFolder));
                }
                groupXDVideos.add(video);
            }
        }
        videoFolder.setCount(groupXDVideos.size());
        videoFolder.setName(folderStr);
        videoFolder.setXdVideos(groupXDVideos);
        videoFolderList.add(videoFolder);
        return null;
    }

    /**
     * 克隆一份XDVideos
     *
     * @param xdVideos
     * @return
     */
    private List<XDVideo> copyXDVideos(List<XDVideo> xdVideos) {
        List<XDVideo> lists = new ArrayList<>(xdVideos.size());
        for (XDVideo xd : xdVideos) {
            lists.add(xd);
        }
        return lists;
    }
    /**
     * 克隆一份VideoFolder
     *
     * @param videoFolder
     * @return
     */
    private VideoFolder copyVideoFolder(VideoFolder videoFolder) {
        VideoFolder folder = new VideoFolder(videoFolder.getName(), videoFolder.getCount(), videoFolder.getXdVideos());
        return folder;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        listener.onComplete(videoFolderList);
    }
}
