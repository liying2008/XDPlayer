package lxy.liying.hdtvneu.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import lxy.liying.hdtvneu.domain.MarkGroup;
import lxy.liying.hdtvneu.domain.MarkItem;

/**
 * =======================================================
 * 版权：©Copyright LiYing 2015-2016. All rights reserved.
 * 作者：liying
 * 日期：2016/9/11 1:09
 * 版本：1.0
 * 描述：数据库操作层——操作表 mark
 * 备注：
 * =======================================================
 */
public class MarkService extends BaseDbService {
    private DatabaseHelper dbHelper;

    /**
     * 构造方法
     *
     * @param context
     */
    public MarkService(Context context) {
        super(context);
        dbHelper = super.dbHelper;
    }

    /**
     * 将收藏视频信息添加到数据库中 <br/>
     *
     * @param markItem
     */
    public void addMarkVideo(MarkItem markItem) {
        SQLiteDatabase db = this.dbHelper.getWritableDatabase();
        String sql = "INSERT INTO " + DBInfo.Table.TB_MARK_NAME + " VALUES (" +
                markItem.getMarkId() + ",'" +
                markItem.getName() + "','" +
                markItem.getCoverPath() + "'," +
                markItem.getGroup().getIndex() + ",'" +
                markItem.getPath() + "','" +
                markItem.getFolder() + "'," +
                markItem.getPosition() +
                ");";
        db.execSQL(sql);
        db.close();
    }

    /**
     * 获取收藏的视频信息 <br/>
     *
     * @return
     */
    public List<MarkItem> getAllMarks() {
        SQLiteDatabase db = this.dbHelper.getReadableDatabase();
        List<MarkItem> lists = null;
        Cursor cursor = db.query(DBInfo.Table.TB_MARK_NAME, null, null, null, null, null, MarkItem.GROUP_INDEX);
        if (cursor.getCount() > 0) {
            lists = new ArrayList<>(cursor.getCount());
            MarkItem item = null;
            while (cursor.moveToNext()) {
                long markId = cursor.getLong(cursor.getColumnIndex(MarkItem.MARK_ID));
                String name = cursor.getString(cursor.getColumnIndex(MarkItem.NAME));
                String coverPath = cursor.getString(cursor.getColumnIndex(MarkItem.COVER_PATH));
                int groupIndex = cursor.getInt(cursor.getColumnIndex(MarkItem.GROUP_INDEX));
                String path = cursor.getString(cursor.getColumnIndex(MarkItem.PATH));
                String folder = cursor.getString(cursor.getColumnIndex(MarkItem.FOLDER));
                long position = cursor.getLong(cursor.getColumnIndex(MarkItem.POSITION));

                MarkGroup group = MarkGroup.getMarkGroup(groupIndex);
                item = new MarkItem(markId, name, coverPath, group, path, folder, position);
                lists.add(item);
            }
            cursor.close();
        }
        db.close();
        return lists;
    }

    /**
     * 删除指定的收藏
     *
     * @param markId 收藏ID
     */
    public void removeMark(long markId) {
        SQLiteDatabase db = this.dbHelper.getWritableDatabase();
        String sql = "DELETE FROM " + DBInfo.Table.TB_MARK_NAME + " WHERE " + MarkItem.MARK_ID + " = " + markId;
        db.execSQL(sql);
        db.close();
    }
    /**
     * 删除所有收藏
     */
    public void removeAllMarks() {
        SQLiteDatabase db = this.dbHelper.getWritableDatabase();
        String sql = "DELETE FROM " + DBInfo.Table.TB_MARK_NAME;
        db.execSQL(sql);
        db.close();
    }

    /**
     * 更新收藏视频标题
     *
     * @param markId         收藏视频ID
     * @param newName 新标题
     */
    public void updateMarkItemName(long markId, String newName) {
        SQLiteDatabase db = this.dbHelper.getReadableDatabase();
        String sql = "UPDATE " + DBInfo.Table.TB_MARK_NAME + " SET " + MarkItem.NAME + "='" + newName + "' WHERE " + MarkItem.MARK_ID + "=" + markId;
        db.execSQL(sql);
        db.close();
    }

    /**
     * 获取指定Path的收藏的视频信息 <br/>
     *
     * @return null：没有该视频
     */
    public MarkItem getMarkByPath(String path) {
        SQLiteDatabase db = this.dbHelper.getReadableDatabase();
        List<MarkItem> lists = null;
        Cursor cursor = db.query(DBInfo.Table.TB_MARK_NAME, null, MarkItem.PATH + "=?", new String[]{path}, null, null, MarkItem.GROUP_INDEX);
        MarkItem item = null;
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                long markId = cursor.getLong(cursor.getColumnIndex(MarkItem.MARK_ID));
                String name = cursor.getString(cursor.getColumnIndex(MarkItem.NAME));
                String coverPath = cursor.getString(cursor.getColumnIndex(MarkItem.COVER_PATH));
                int groupIndex = cursor.getInt(cursor.getColumnIndex(MarkItem.GROUP_INDEX));
                String folder = cursor.getString(cursor.getColumnIndex(MarkItem.FOLDER));
                long position = cursor.getLong(cursor.getColumnIndex(MarkItem.POSITION));

                MarkGroup group = MarkGroup.getMarkGroup(groupIndex);
                item = new MarkItem(markId, name, coverPath, group, path, folder, position);
            }
            cursor.close();
        }
        db.close();
        return item;
    }

}
