package lxy.liying.hdtvneu.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * =======================================================
 * 版权：©Copyright LiYing 2015-2016. All rights reserved.
 * 作者：liying
 * 日期：2016/7/24 12:39
 * 版本：1.0
 * 描述：数据库Helper
 * 备注：
 * =======================================================
 */
class DatabaseHelper extends SQLiteOpenHelper {

    DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    DatabaseHelper(Context context) {
        this(context, DBInfo.DB_NAME, null, DBInfo.DB_VERSION);
    }

    /**
     * 数据库第一次创建时候调用
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        //创建数据表
        db.execSQL(DBInfo.Table.TB_VIDEO_CREATE);
        db.execSQL(DBInfo.Table.TB_MARK_CREATE);
        db.execSQL(DBInfo.Table.TB_DOWNLOAD_CREATE);
    }

    /**
     * 数据库文件版本号发生变化时调用
     * @param db 数据库对象
     * @param oldVersion 旧版本号
     * @param newVersion 新版本号
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
