package lxy.liying.hdtvneu.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import lxy.liying.hdtvneu.domain.DownloadItem;

/**
 * =======================================================
 * 作者：liying
 * 日期：2016/9/23 9:40
 * 版本：1.0
 * 描述：数据库操作层——操作表 download
 * 备注：
 * =======================================================
 */

public class DownloadService extends BaseDbService {
    private DatabaseHelper dbHelper;

    /**
     * 构造方法
     *
     * @param context
     */
    public DownloadService(Context context) {
        super(context);
        dbHelper = super.dbHelper;
    }

    /**
     * 将已下载的视频信息添加到数据库中 <br/>
     *
     * @param downloadItem 下载项
     */
    public void addDownloadVideo(DownloadItem downloadItem) {
        SQLiteDatabase db = this.dbHelper.getWritableDatabase();
        // INSERT OR IGNORE INTO：主键重复则不添加
        String sql = "INSERT OR IGNORE INTO " + DBInfo.Table.TB_DOWNLOAD_NAME + " VALUES ('" +
                downloadItem.getName() + "','" +
                downloadItem.getVideoFrom() + "','" +
                downloadItem.getPath() + "'" +
                ");";
        db.execSQL(sql);
        db.close();
    }

    /**
     * 删除指定的下载项
     *
     * @param path 视频路径
     */
    public void removeDownloadItem(String path) {
        SQLiteDatabase db = this.dbHelper.getWritableDatabase();
        String sql = "DELETE FROM " + DBInfo.Table.TB_DOWNLOAD_NAME + " WHERE " +
                DownloadItem.PATH + " = '" + path + "'";
        db.execSQL(sql);
        db.close();
    }

    /**
     * 获取所有下载的视频信息 <br/>
     *
     * @return
     */
    public List<DownloadItem> getAllDownloadItems() {
        SQLiteDatabase db = this.dbHelper.getReadableDatabase();
        List<DownloadItem> lists = null;
        Cursor cursor = db.query(DBInfo.Table.TB_DOWNLOAD_NAME, null, null, null, null, null, null);
        if (cursor.getCount() > 0) {
            lists = new ArrayList<>(cursor.getCount());
            DownloadItem item = null;
            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndex(DownloadItem.NAME));
                String from = cursor.getString(cursor.getColumnIndex(DownloadItem.FROM));
                String path = cursor.getString(cursor.getColumnIndex(DownloadItem.PATH));

                item = new DownloadItem(name, from, path);
                lists.add(item);
            }
            cursor.close();
        }
        db.close();
        return lists;
    }

}
