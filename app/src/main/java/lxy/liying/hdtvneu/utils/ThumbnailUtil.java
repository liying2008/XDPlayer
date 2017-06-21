package lxy.liying.hdtvneu.utils;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import io.vov.vitamio.MediaMetadataRetriever;
import io.vov.vitamio.ThumbnailUtils;
import lxy.liying.hdtvneu.R;

/**
 * =======================================================
 * 作者：liying
 * 日期：2016/9/12 14:09
 * 版本：1.0
 * 描述：得到网络视频缩略图
 * 备注：
 * =======================================================
 */
public class ThumbnailUtil {
    private static final String TAG = "ThumbnailUtil";
    /**
     * 创建在线视频的缩略图
     * @param url
     * @param width
     * @param height
     * @return
     */
    public static Bitmap createVideoThumbnail(Context context, String url, int width, int height) {
        Bitmap bitmap = null;
        // FIXME: 2016/12/29 Caused by: java.lang.UnsatisfiedLinkError: io.vov.vitamio.MediaMetadataRetriever
        /**********************************************************
        MediaMetadataRetriever mmr = new MediaMetadataRetriever(context);
        Log.i(TAG, "createVideoThumbnail: mmr = " + mmr);
        try {
            mmr.setDataSource(url);
            bitmap = mmr.getFrameAtTime(-1);
        } catch (Exception ex) {
            // Assume this is a corrupt video file
        } finally {
            try {
                mmr.release();
            } catch (RuntimeException ex) {
                // Ignore failures while cleaning up.
            }
        }
         ************************************************************/
        if (bitmap != null && bitmap.getWidth() > width) {
            bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
                    ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        }
        if (bitmap != null) {
            return bitmap;
        } else {
            return BitmapFactory.decodeResource(context.getResources(), R.drawable.no_tv_thumbnail);
        }
    }
}
