package com.zhaojy.planttudian.utils;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.commit451.nativestackblur.NativeStackBlur;

/**
 * 图片模糊处理工具类
 *
 * @author: zhaojy
 * @data:On 2018/9/19.
 */

public class DimUtils {
    /**
     * 模糊处理
     *
     * @param view   处理的ImageView对象
     * @param radius 模糊度
     */
    public static void dimOption(ImageView view, float radius) {
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        //截取区域视图
        Bitmap bitmap = view.getDrawingCache();
        int x = (int) view.getX();
        int y = (int) view.getY();

        Bitmap tempBitmap = null;
        //模糊处理
        if (bitmap != null) {
            int bitmapX = bitmap.getWidth();
            int bitmapY = bitmap.getHeight();
            tempBitmap = Bitmap.createBitmap(bitmap, x, y, bitmapX, bitmapY);
            Bitmap overlay = blur(tempBitmap, radius);
            view.setImageBitmap(overlay);
            tempBitmap.recycle();
        }
        //清除缓存
        view.setDrawingCacheEnabled(false);
    }

    /**
     * @param bkg
     * @param radius
     * @return
     */
    private static Bitmap blur(Bitmap bkg, float radius) {
        int scaleFactor = 1;
        Bitmap overlay = null;

        overlay = Bitmap.createScaledBitmap(bkg, bkg.getWidth() / scaleFactor, bkg.getHeight() / scaleFactor, false);
        //高斯模糊
        overlay = NativeStackBlur.process(overlay, (int) radius);

        return overlay;
    }

    /**
     * 获取模糊处理后的bitmap
     *
     * @param res    源bitmap
     * @param radius 模糊度
     * @return
     */
    public static Bitmap getDimBitmap(Bitmap res, float radius) {
        return blur(res, radius);
    }

}
