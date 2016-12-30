package lxy.liying.hdtvneu.db;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import lxy.liying.hdtvneu.domain.XDVideo;

/**
 * =======================================================
 * 版权：©Copyright LiYing 2015-2016. All rights reserved.
 * 作者：liying
 * 日期：2016/7/24 13:07
 * 版本：1.0
 * 描述：数据库操作层——操作表 xd_video
 * 备注：
 * =======================================================
 */
public class XDVideoService extends BaseDbService {
    private DatabaseHelper dbHelper;

    /**
     * 构造方法
     *
     * @param context
     */
    public XDVideoService(Context context) {
        super(context);
        dbHelper = super.dbHelper;
    }

    /**
     * 将视频信息添加到数据库中 <br/>
     *
     * @param video
     */
    public void addXDVideo(XDVideo video) {
        long position = getPosition(video.get_id());
        SQLiteDatabase db = this.dbHelper.getWritableDatabase();
        String sql = "INSERT INTO " + DBInfo.Table.TB_VIDEO_NAME + " VALUES (" +
                video.get_id() + ",'" +
                video.getFolder() + "','" +
                video.getVideoThumbnail() + "','" +
                video.getTitle() + "'," +
                video.getSize() + "," +
                video.getDuration() + ",'" +
                video.getData() + "','" +
                video.getDisplayName() + "','" +
                video.getMimeType() + "'," +
                video.getDateAdded() + "," +
                video.getDateModified() + ",'" +
                video.getArtist() + "','" +
                video.getAlbum() + "','" +
                video.getResolution() + "','" +
                video.getDescription() + "'," +
                position +
                ");";
        db.execSQL(sql);
        db.close();
    }

    /**
     * 获取缓存的视频信息 <br/>
     *
     * @return
     */
    public List<XDVideo> getAllXDVideos() {
        SQLiteDatabase db = this.dbHelper.getReadableDatabase();
        List<XDVideo> lists = null;
        Cursor cursor = db.query(DBInfo.Table.TB_VIDEO_NAME, null, null, null, null, null, XDVideo.FOLDER);
        if (cursor.getCount() > 0) {
            lists = new ArrayList<>(cursor.getCount());
            XDVideo xdVideo = null;
            while (cursor.moveToNext()) {
                long _id = cursor.getLong(cursor.getColumnIndex(XDVideo._ID));
                String folder = cursor.getString(cursor.getColumnIndex(XDVideo.FOLDER));
                String videoThumbnail = cursor.getString(cursor.getColumnIndex(XDVideo.VIDEO_THUMBNAIL));
                String title = cursor.getString(cursor.getColumnIndex(XDVideo.TITLE));
                long size = cursor.getLong(cursor.getColumnIndex(XDVideo.SIZE));
                long duration = cursor.getLong(cursor.getColumnIndex(XDVideo.DURATION));
                String data = cursor.getString(cursor.getColumnIndex(XDVideo.DATA));
                String displayName = cursor.getString(cursor.getColumnIndex(XDVideo.DISPLAY_NAME));
                String mimeType = cursor.getString(cursor.getColumnIndex(XDVideo.MIME_TYPE));
                long dateAdded = cursor.getLong(cursor.getColumnIndex(XDVideo.DATE_ADDED));
                long dateModified = cursor.getLong(cursor.getColumnIndex(XDVideo.DATE_MODIFIED));
                String artist = cursor.getString(cursor.getColumnIndex(XDVideo.ARTIST));
                String album = cursor.getString(cursor.getColumnIndex(XDVideo.ALBUM));
                String resolution = cursor.getString(cursor.getColumnIndex(XDVideo.RESOLUTION));
                String description = cursor.getString(cursor.getColumnIndex(XDVideo.DESCRIPTION));
                long position = cursor.getLong(cursor.getColumnIndex(XDVideo.POSITION));

                xdVideo = new XDVideo(_id, folder, videoThumbnail, title, size, duration, data, displayName,
                        mimeType, dateAdded, dateModified, artist, album, resolution, description, position);
                lists.add(xdVideo);
            }
            cursor.close();
        }
        db.close();
        return lists;
    }

    /**
     * 删除所有视频信息
     */
    public void removeAllVideoInfo() {
        SQLiteDatabase db = this.dbHelper.getWritableDatabase();
        String sql = "DELETE FROM " + DBInfo.Table.TB_VIDEO_NAME;
        db.execSQL(sql);
        db.close();
    }

    /**
     * 获取符合要求的缓存的视频信息 <br/>
     *
     * @param keyword
     * @return
     */
    public List<XDVideo> getXDVideos(String keyword) {
        SQLiteDatabase db = this.dbHelper.getReadableDatabase();
        List<XDVideo> lists = null;
        Cursor cursor = db.query(DBInfo.Table.TB_VIDEO_NAME, null, XDVideo.TITLE + " like '%" + keyword + "%'", null, null, null, XDVideo.FOLDER);
        if (cursor.getCount() > 0) {
            lists = new ArrayList<>(cursor.getCount());
            XDVideo xdVideo = null;
            while (cursor.moveToNext()) {
                long _id = cursor.getLong(cursor.getColumnIndex(XDVideo._ID));
                String folder = cursor.getString(cursor.getColumnIndex(XDVideo.FOLDER));
                String videoThumbnail = cursor.getString(cursor.getColumnIndex(XDVideo.VIDEO_THUMBNAIL));
                String title = cursor.getString(cursor.getColumnIndex(XDVideo.TITLE));
                long size = cursor.getLong(cursor.getColumnIndex(XDVideo.SIZE));
                long duration = cursor.getLong(cursor.getColumnIndex(XDVideo.DURATION));
                String data = cursor.getString(cursor.getColumnIndex(XDVideo.DATA));
                String displayName = cursor.getString(cursor.getColumnIndex(XDVideo.DISPLAY_NAME));
                String mimeType = cursor.getString(cursor.getColumnIndex(XDVideo.MIME_TYPE));
                long dateAdded = cursor.getLong(cursor.getColumnIndex(XDVideo.DATE_ADDED));
                long dateModified = cursor.getLong(cursor.getColumnIndex(XDVideo.DATE_MODIFIED));
                String artist = cursor.getString(cursor.getColumnIndex(XDVideo.ARTIST));
                String album = cursor.getString(cursor.getColumnIndex(XDVideo.ALBUM));
                String resolution = cursor.getString(cursor.getColumnIndex(XDVideo.RESOLUTION));
                String description = cursor.getString(cursor.getColumnIndex(XDVideo.DESCRIPTION));
                long position = cursor.getLong(cursor.getColumnIndex(XDVideo.POSITION));

                xdVideo = new XDVideo(_id, folder, videoThumbnail, title, size, duration, data, displayName,
                        mimeType, dateAdded, dateModified, artist, album, resolution, description, position);
                lists.add(xdVideo);
            }
            cursor.close();
        }
        db.close();
        return lists;
    }


    /**
     * 查询播放位置
     *
     * @param _id
     * @return
     */
    public long getPosition(long _id) {
        SQLiteDatabase db = this.dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DBInfo.Table.TB_VIDEO_NAME, new String[]{XDVideo.POSITION},
                XDVideo._ID + "=" + _id, null, null, null, null);

        long position = 0L;
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            position = cursor.getLong(cursor.getColumnIndex(XDVideo.POSITION));
            cursor.close();
        }
        db.close();
        return position;
    }

    /**
     * 更新播放进度
     *
     * @param _id         本地视频ID
     * @param newPosition 新进度
     */
    public void updatePosition(long _id, long newPosition) {
        SQLiteDatabase db = this.dbHelper.getReadableDatabase();
        String sql = "UPDATE " + DBInfo.Table.TB_VIDEO_NAME + " SET " + XDVideo.POSITION + "=" + newPosition + " WHERE " + XDVideo._ID + "=" + _id;
        db.execSQL(sql);
        db.close();
    }

    /**
     * 获取某一视频信息 <br/>
     *
     * @param _id
     * @return
     */
    public XDVideo getXDVideo(long _id) {
        SQLiteDatabase db = this.dbHelper.getReadableDatabase();
        XDVideo xdVideo = null;
        Cursor cursor = db.query(DBInfo.Table.TB_VIDEO_NAME, null, XDVideo._ID + "=" + _id, null, null, null, XDVideo.FOLDER);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            String folder = cursor.getString(cursor.getColumnIndex(XDVideo.FOLDER));
            String videoThumbnail = cursor.getString(cursor.getColumnIndex(XDVideo.VIDEO_THUMBNAIL));
            String title = cursor.getString(cursor.getColumnIndex(XDVideo.TITLE));
            long size = cursor.getLong(cursor.getColumnIndex(XDVideo.SIZE));
            long duration = cursor.getLong(cursor.getColumnIndex(XDVideo.DURATION));
            String data = cursor.getString(cursor.getColumnIndex(XDVideo.DATA));
            String displayName = cursor.getString(cursor.getColumnIndex(XDVideo.DISPLAY_NAME));
            String mimeType = cursor.getString(cursor.getColumnIndex(XDVideo.MIME_TYPE));
            long dateAdded = cursor.getLong(cursor.getColumnIndex(XDVideo.DATE_ADDED));
            long dateModified = cursor.getLong(cursor.getColumnIndex(XDVideo.DATE_MODIFIED));
            String artist = cursor.getString(cursor.getColumnIndex(XDVideo.ARTIST));
            String album = cursor.getString(cursor.getColumnIndex(XDVideo.ALBUM));
            String resolution = cursor.getString(cursor.getColumnIndex(XDVideo.RESOLUTION));
            String description = cursor.getString(cursor.getColumnIndex(XDVideo.DESCRIPTION));
            long position = cursor.getLong(cursor.getColumnIndex(XDVideo.POSITION));

            xdVideo = new XDVideo(_id, folder, videoThumbnail, title, size, duration, data, displayName,
                    mimeType, dateAdded, dateModified, artist, album, resolution, description, position);

            cursor.close();
        }
        db.close();
        return xdVideo;
    }


    /**
     * 删除指定的视频信息
     *
     * @param _id 视频ID
     */
    public void removeVideo(long _id) {
        SQLiteDatabase db = this.dbHelper.getWritableDatabase();
        String sql = "DELETE FROM " + DBInfo.Table.TB_VIDEO_NAME + " WHERE " + XDVideo._ID + " = " + _id;
        db.execSQL(sql);
        db.close();
    }

    /**
     * 删除指定文件夹里的视频信息
     *
     * @param folder 视频文件夹
     */
    public void removeVideos(String folder) {
        SQLiteDatabase db = this.dbHelper.getWritableDatabase();
        String sql = "DELETE FROM " + DBInfo.Table.TB_VIDEO_NAME + " WHERE " + XDVideo.FOLDER + " = '" + folder + "'";
        db.execSQL(sql);
        db.close();
    }
}
