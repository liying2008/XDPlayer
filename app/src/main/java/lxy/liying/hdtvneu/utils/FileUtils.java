package lxy.liying.hdtvneu.utils;

import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * =======================================================
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

    /**
     * copy file
     *
     * @param sourceFilePath 源文件路径
     * @param destFilePath   目标文件路径
     * @return 返回是否成功
     * @throws RuntimeException if an error occurs while operator
     *                          FileOutputStream
     */
    public static boolean copyFile(String sourceFilePath, String destFilePath) {

        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(sourceFilePath);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("FileNotFoundException occurred. ", e);
        }
        return writeFile(destFilePath, inputStream);
    }

    /**
     * write file, the bytes will be written to the begin of the file
     *
     * @param filePath 路径
     * @param stream   输入流
     * @return 返回是否写入成功
     */
    public static boolean writeFile(String filePath, InputStream stream) {
        return writeFile(filePath, stream, false);

    }

    /**
     * write file
     *
     * @param filePath 路径
     * @param stream   the input stream
     * @param append   if <code>true</code>, then bytes will be written to the
     *                 end
     *                 of the file rather than the beginning
     * @return return true
     * FileOutputStream
     */
    public static boolean writeFile(String filePath, InputStream stream, boolean append) {

        return writeFile(filePath != null ? new File(filePath) : null, stream,
            append);
    }

    /**
     * write file
     *
     * @param file   the file to be opened for writing.
     * @param stream the input stream
     * @param append if <code>true</code>, then bytes will be written to the
     *               end
     *               of the file rather than the beginning
     * @return return true
     * @throws RuntimeException if an error occurs while operator
     *                          FileOutputStream
     */
    public static boolean writeFile(File file, InputStream stream, boolean append) {
        OutputStream o = null;
        try {
            makeDirs(file.getAbsolutePath());
            o = new FileOutputStream(file, append);
            byte data[] = new byte[1024];
            int length = -1;
            while ((length = stream.read(data)) != -1) {
                o.write(data, 0, length);
            }
            o.flush();
            return true;
        } catch (FileNotFoundException e) {
            throw new RuntimeException("FileNotFoundException occurred. ", e);
        } catch (IOException e) {
            throw new RuntimeException("IOException occurred. ", e);
        } finally {
            IOUtils.close(o);
            IOUtils.close(stream);
        }
    }

    /**
     * @param filePath 路径
     * @return 是否创建成功
     */
    public static boolean makeDirs(String filePath) {

        String folderName = getFolderName(filePath);
        if (TextUtils.isEmpty(folderName)) {
            return false;
        }

        File folder = new File(folderName);
        return (folder.exists() && folder.isDirectory()) || folder.mkdirs();
    }

    /**
     * get folder name from path
     * <p>
     * <pre>
     *      getFolderName(null)               =   null
     *      getFolderName("")                 =   ""
     *      getFolderName("   ")              =   ""
     *      getFolderName("a.mp3")            =   ""
     *      getFolderName("a.b.rmvb")         =   ""
     *      getFolderName("abc")              =   ""
     *      getFolderName("c:\\")              =   "c:"
     *      getFolderName("c:\\a")             =   "c:"
     *      getFolderName("c:\\a.b")           =   "c:"
     *      getFolderName("c:a.txt\\a")        =   "c:a.txt"
     *      getFolderName("c:a\\b\\c\\d.txt")    =   "c:a\\b\\c"
     *      getFolderName("/home/admin")      =   "/home"
     *      getFolderName("/home/admin/a.txt/b.mp3")  =   "/home/admin/a.txt"
     * </pre>
     *
     * @param filePath 路径
     * @return file name from path, include suffix
     */
    public static String getFolderName(String filePath) {


        if (TextUtils.isEmpty(filePath)) {
            return filePath;
        }

        int filePosi = filePath.lastIndexOf(File.separator);
        return (filePosi == -1) ? "" : filePath.substring(0, filePosi);
    }
}
