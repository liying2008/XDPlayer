package lxy.liying.hdtvneu.utils;

import android.graphics.Bitmap;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * =======================================================
 * 版权：©Copyright LiYing 2015-2016. All rights reserved.
 * 作者：liying
 * 日期：2016/8/18 13:46
 * 版本：1.0
 * 描述：文件工具类
 * 备注：
 * =======================================================
 */
public class FileUtils {
    /**
     * 保存Bitmap到SDCard
     *
     * @param bitName 图片名(不包含路径)，不含扩展名
     * @param mBitmap
     */
    public static File saveBitmapToSDCard(String bitName, Bitmap mBitmap) {
        File f = new File(bitName + ".jpg");

        try {
            f.createNewFile();
        } catch (IOException e) {
            Log.e("FileUtils", "在保存图片时出错");
        }
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
        try {
            fOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return f;
    }

    /**
     * 得到文件后缀（扩展名 .xxx格式）
     * @param fileName 文件名
     * @return 文件后缀
     */
    public static String getNormalFileSuffix(String fileName) {
        String suffix;
        int lastParam = fileName.lastIndexOf("?");
        if (lastParam != -1) {
            fileName = fileName.substring(0, lastParam);
        }
        int lastSuf = fileName.lastIndexOf(".");
        if (lastSuf != -1) {
            suffix = fileName.substring(lastSuf, fileName.length());
        } else {
            suffix = ".unknown";
        }
        return suffix;
    }

    /**
     * 得到网址后缀（扩展名 .xxx格式）
     * @param url 文件名
     * @return 文件后缀
     */
    public static String getUrlSuffix(String url) {
        String suffix;
        int lastParam = url.lastIndexOf("?");
        if (lastParam != -1) {
            url = url.substring(0, lastParam);
        }

        int lastSuf = url.lastIndexOf(".");
        if (lastSuf != -1) {
            suffix = url.substring(lastSuf, url.length());
        } else {
            suffix = ".unknown";
        }
        if (suffix.contains("mp4")) {
            suffix = ".mp4";
        } else if (suffix.contains("flv")) {
            suffix = ".flv";
        } else if (suffix.contains("mkv")) {
            suffix = ".mkv";
        } else if (suffix.contains("3gp")) {
            suffix = ".3gp";
        } else if (suffix.contains("mp3")) {
            suffix = ".mp3";
        }
        return suffix;
    }
}
