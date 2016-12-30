package lxy.liying.hdtvneu.db;

import android.content.Context;

/**
 * =======================================================
 * 版权：©Copyright LiYing 2015-2016. All rights reserved.
 * 作者：liying
 * 日期：2016/9/16 13:09
 * 版本：1.0
 * 描述：
 * 备注：
 * =======================================================
 */
class BaseDbService {
    static DatabaseHelper dbHelper = null;
    /**
     * 构造方法
     *
     * @param context
     */
    BaseDbService(Context context) {
        if (dbHelper == null) {
            dbHelper = new DatabaseHelper(context);
        }
    }
}
