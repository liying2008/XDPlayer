package lxy.liying.hdtvneu.service.task;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.MediaStore.Video.Media;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import lxy.liying.hdtvneu.app.App;
import lxy.liying.hdtvneu.domain.VideoFolder;
import lxy.liying.hdtvneu.domain.XDVideo;
import lxy.liying.hdtvneu.service.callback.OnGetVideoFolderListener;
import lxy.liying.hdtvneu.utils.Constants;
import lxy.liying.hdtvneu.utils.FileUtils;

/**
 * =======================================================
 * 作者：liying
 * 日期：2016/8/17 15:09
 * 版本：1.0
 * 描述：获取SD卡中所有视频文件的异步任务类
 * 备注：
 * =======================================================
 */
public class GetAllVideoFolderTask extends AsyncTask<Void, Integer, Void> {
    private OnGetVideoFolderListener listener;
    private static WeakReference<Context> context;
    private VideoFolder videoFolder;
    private static MediaMetadataRetriever mmr;
    private static final int UPDATE_LOCAL_LIST = 0x0000;
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == UPDATE_LOCAL_LIST) {
                listener.onVideoFolderGot(videoFolder);
            }
            return false;
        }
    });

    public GetAllVideoFolderTask(Context context, OnGetVideoFolderListener listener) {
        GetAllVideoFolderTask.context = new WeakReference<>(context);
        this.listener = listener;
    }

    @Override
    protected Void doInBackground(Void... params) {
        App.xdService.removeAllVideoInfo();
        ContentResolver contentResolver = context.get().getContentResolver();
        Cursor cursor = contentResolver.query(Media.EXTERNAL_CONTENT_URI, null,
                null, null, Media.DATA);

        if (cursor != null) {
            cursor.moveToFirst();
            String folderStr = "";
            int fileNum = cursor.getCount();
            List<XDVideo> xdVideos = new ArrayList<>();
            videoFolder = new VideoFolder();
            for (int counter = 0; counter < fileNum; counter++) {
                String data = cursor.getString(cursor.getColumnIndex(Media.DATA));
                String folder = getFolder(data);
                String videoThumbnail = getVideoThumbnail(data);
                long _id = cursor.getLong(cursor.getColumnIndex(Media._ID));
                String title = cursor.getString(cursor.getColumnIndex(Media.TITLE));
                long size = cursor.getLong(cursor.getColumnIndex(Media.SIZE));
                long duration = cursor.getLong(cursor.getColumnIndex(Media.DURATION));
                String displayName = cursor.getString(cursor.getColumnIndex(Media.DISPLAY_NAME));
                String mimeType = cursor.getString(cursor.getColumnIndex(Media.MIME_TYPE));
                long dateAdded = cursor.getLong(cursor.getColumnIndex(Media.DATE_ADDED));
                long dateModified = cursor.getLong(cursor.getColumnIndex(Media.DATE_MODIFIED));
                String artist = cursor.getString(cursor.getColumnIndex(Media.ARTIST));
                String album = cursor.getString(cursor.getColumnIndex(Media.ALBUM));
                String resolution = cursor.getString(cursor.getColumnIndex(Media.RESOLUTION));
                String description = cursor.getString(cursor.getColumnIndex(Media.DESCRIPTION));

                if (folderStr.equals("")) {
                    folderStr = folder;
                }
                if (!folder.equals(folderStr)) {
                    videoFolder.setCount(xdVideos.size());
                    videoFolder.setName(folderStr);
                    videoFolder.setXdVideos(copyXDVideos(xdVideos));
                    handler.sendEmptyMessage(UPDATE_LOCAL_LIST);

                    folderStr = folder;
                    xdVideos.clear();
                }

                XDVideo xdVideo = new XDVideo(_id, folder, videoThumbnail, title, size, duration, data,
                        displayName, mimeType, dateAdded, dateModified, artist, album, resolution, description);
                saveToDB(xdVideo);
                xdVideos.add(xdVideo);
                cursor.moveToNext();
            }
            videoFolder.setCount(xdVideos.size());
            videoFolder.setName(folderStr);
            videoFolder.setXdVideos(xdVideos);
            handler.sendEmptyMessage(UPDATE_LOCAL_LIST);
            cursor.close();
        }
        return null;
    }

    /**
     * 保存到数据库
     * @param xdVideo
     */
    private void saveToDB(XDVideo xdVideo) {
        App.xdService.addXDVideo(xdVideo);
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
     * 得到视频所在文件夹名字
     *
     * @param data
     * @return
     */
    private String getFolder(String data) {
        String[] split = data.split("/");
        return split[split.length - 2];
    }

    /**
     * 得到视频缩略图
     *
     * @param videoPath
     * @return
     */
    public static String getVideoThumbnail(String videoPath) {
        Bitmap bitmap = null;
        // 获取视频的缩略图
        bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, MediaStore.Images.Thumbnails.MICRO_KIND);
        String thumbPath = null;
        if (bitmap != null) {
            //将缩略图保存到文件
            File file = FileUtils.saveBitmapToSDCard(context.get().getFilesDir() + "/" + UUID.randomUUID().toString(), bitmap);
            if (file != null) {
                thumbPath = file.getAbsolutePath();
            }
        }
        return thumbPath;
    }

    /***
     * 获取视频时长 <br/>
     * 部分视频获取到的时长为0，报运行时异常 <br/>
     * todo 待解决
     * @param videoPath
     * @return
     */
    public static long getVideoDuration(String videoPath) {
        if (mmr == null) {
            mmr = new MediaMetadataRetriever();
        }
        try {
            mmr.setDataSource(videoPath);
            String duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            return Long.parseLong(duration);
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
            return 0L;
        }
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        listener.onComplete(null);
        App.getInstance().putSetting(Constants.VIDEO_LIST_CACHE, "true");
    }
}
