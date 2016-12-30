package lxy.liying.hdtvneu.utils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;

/**
 * =======================================================
 * 版权：©Copyright LiYing 2015-2016. All rights reserved.
 * 作者：liying
 * 日期：2016/6/26 19:50
 * 版本：1.0
 * 描述：网页源码获取器
 * 备注：
 * =======================================================
 */
public class HtmlGetter {

    private static byte[] readStream(InputStream inputStream) throws Exception {
        byte[] buffer = new byte[1024];
        int len = -1;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        while ((len = inputStream.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, len);
        }

        inputStream.close();
        byteArrayOutputStream.close();
        return byteArrayOutputStream.toByteArray();
    }

    /**
     * 获取网页HTML
     *
     * @param urlpath 网址
     * @param encoding 字符编码
     * @return 网页html
     */
    public static String getHtml(String urlpath, String encoding) {
        StringBuilder sb = new StringBuilder();
        try {
            //构建URL对象
            URL url = new URL(urlpath);
            //使用openStream得到输入流并由此构造一个BufferedReader对象
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream(), encoding));
            String line;
            //读取html
            while ((line = in.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            return "null";
        }
        return sb.toString();
    }

    /**
     * 获取网页源码（已设置UA）
     * @return 成功：网页源代码。 失败："null"
     */
    public static String getHtmlWithUA(String urlpath, String encoding) {
        StringBuilder sb = new StringBuilder();
        InputStream urlStream = null;
        BufferedReader reader = null;
        try {
            URL url = new URL(urlpath);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.101 Safari/537.36");
            conn.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.6,en;q=0.4");
            conn.connect();
            conn.getInputStream();
            String currentLine;

            urlStream = conn.getInputStream();
            reader = new BufferedReader(new InputStreamReader(urlStream, "UTF-8"));
            while ((currentLine = reader.readLine()) != null) {
                sb.append(currentLine);
                sb.append("\n");
            }
        } catch (IOException ex) {
            return "null";
        }
        finally {
            // 释放资源
            try {
                if (null != reader) {
                    reader.close();
                }
                if (null != urlStream) {
                    urlStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}
