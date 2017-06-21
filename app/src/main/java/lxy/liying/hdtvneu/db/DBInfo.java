package lxy.liying.hdtvneu.db;

import lxy.liying.hdtvneu.domain.DownloadItem;
import lxy.liying.hdtvneu.domain.MarkItem;
import lxy.liying.hdtvneu.domain.XDVideo;

/**
 * =======================================================
 * 作者：liying
 * 日期：2016/7/24 10:39
 * 版本：1.0
 * 描述：数据库常量信息
 * 备注：
 * =======================================================
 */
class DBInfo {
    /** 数据库名称 */
    static final String DB_NAME = "xd.db";
    /** 数据库版本 */
    static final int DB_VERSION = 1;

    /** 数据库表 */
    static class Table {
        static final String TB_VIDEO_NAME = "xd_video";
        static final String TB_MARK_NAME = "mark";
        static final String TB_DOWNLOAD_NAME = "download";
        /**
         * 创建表xd_video的SQL语句
         */
        static final String TB_VIDEO_CREATE = "CREATE TABLE IF NOT EXISTS " + TB_VIDEO_NAME + " (" +
                XDVideo._ID + " LONG PRIMARY KEY NOT NULL," +
                XDVideo.FOLDER + " TEXT NOT NULL," +
                XDVideo.VIDEO_THUMBNAIL + " TEXT, " +
                XDVideo.TITLE + " TEXT NOT NULL," +
                XDVideo.SIZE + " LONG NOT NULL," +
                XDVideo.DURATION + " LONG NOT NULL," +
                XDVideo.DATA + " TEXT NOT NULL," +
                XDVideo.DISPLAY_NAME + " TEXT NOT NULL," +
                XDVideo.MIME_TYPE + " TEXT," +
                XDVideo.DATE_ADDED + " LONG NOT NULL," +
                XDVideo.DATE_MODIFIED + " LONG NOT NULL," +
                XDVideo.ARTIST + " TEXT NOT NULL," +
                XDVideo.ALBUM + " TEXT NOT NULL," +
                XDVideo.RESOLUTION + " TEXT," +
                XDVideo.DESCRIPTION + " TEXT," +
                XDVideo.POSITION + " LONG" +
                ");";

        /**
         * 创建表mark_video的SQL语句
         */
        static final String TB_MARK_CREATE = "CREATE TABLE IF NOT EXISTS " + TB_MARK_NAME + " (" +
                MarkItem.MARK_ID + " LONG PRIMARY KEY NOT NULL," +
                MarkItem.NAME + " TEXT NOT NULL," +
                MarkItem.COVER_PATH + " TEXT, " +
                MarkItem.GROUP_INDEX + " INTEGER NOT NULL," +
                MarkItem.PATH + " TEXT NOT NULL," +
                MarkItem.FOLDER + " TEXT," +
                MarkItem.POSITION + " LONG" +
                ");";

        /**
         * 创建表download的SQL语句
         */
        static final String TB_DOWNLOAD_CREATE = "CREATE TABLE IF NOT EXISTS " + TB_DOWNLOAD_NAME + " (" +
                DownloadItem.NAME + " TEXT NOT NULL," +
                DownloadItem.FROM + " TEXT," +
                DownloadItem.PATH + " TEXT PRIMARY KEY NOT NULL" +
                ");";

    }
}
