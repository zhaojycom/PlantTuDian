package com.zhaojy.planttudian.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.Random;

/**
 * 图形验证码生成工具类
 *
 * @author: zhaojy
 * @data:On 2018/9/19.
 */

public class VerCodeUtils {
    private static final char[] CHARS = {
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
            'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
    };

    private static VerCodeUtils mCodeUtils;
    private int mPaddingLeft, mPaddingTop;
    private StringBuilder mBuilder = new StringBuilder();
    private Random mRandom = new Random();

    //Default Settings
//    private static final int DEFAULT_CODE_LENGTH = 6;//验证码的长度  这里是6位
    /*验证码的长度  这里是4位*/
    private static final int DEFAULT_CODE_LENGTH = 4;
    //字体大小
    private static final int DEFAULT_FONT_SIZE = 60;
    //多少条干扰线
    private static final int DEFAULT_LINE_NUMBER = 6;
    //左边距
    private static final int BASE_PADDING_LEFT = 40;
    //左边距范围值
    private static final int RANGE_PADDING_LEFT = 30;
    //上边距
    private static final int BASE_PADDING_TOP = 70;
    //上边距范围值
    private static final int RANGE_PADDING_TOP = 15;
    //默认宽度.图片的总宽
    private static final int DEFAULT_WIDTH = 240;
    //默认高度.图片的总高
    private static final int DEFAULT_HEIGHT = 120;
    //默认背景颜色值
    private static final int DEFAULT_COLOR = 0x5fe6b3;

    private String code;

    public static VerCodeUtils getInstance() {
        if (mCodeUtils == null) {
            mCodeUtils = new VerCodeUtils();
        }
        return mCodeUtils;
    }

    /**
     * 生成验证码图片  返回类型为bitmap 直接用imageview.setbitmap()即可
     *
     * @return
     */
    public Bitmap createBitmap() {
        //每次生成验证码图片时初始化
        mPaddingLeft = 0;
        mPaddingTop = 0;

        Bitmap bitmap = Bitmap.createBitmap(DEFAULT_WIDTH, DEFAULT_HEIGHT, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        code = createCode();

        canvas.drawColor(Color.rgb(255, 255, 255));
        Paint paint = new Paint();
        paint.setTextSize(DEFAULT_FONT_SIZE);

        for (int i = 0; i < code.length(); i++) {
            randomTextStyle(paint);
            randomPadding();
            canvas.drawText(code.charAt(i) + "", mPaddingLeft, mPaddingTop, paint);
        }

        //干扰线
        for (int i = 0; i < DEFAULT_LINE_NUMBER; i++) {
            drawLine(canvas, paint);
        }
        //保存
        canvas.save();
        canvas.restore();

        return bitmap;
    }

    /**
     * 得到图片中的验证码字符串
     *
     * @return
     */
    public String getCode() {
        return code;
    }

    //生成验证码
    public String createCode() {
        //使用之前首先清空内容
        mBuilder.delete(0, mBuilder.length());

        for (int i = 0; i < DEFAULT_CODE_LENGTH; i++) {
            mBuilder.append(CHARS[mRandom.nextInt(CHARS.length)]);
        }

        return mBuilder.toString();
    }

    /**
     * 生成干扰线
     */
    private void drawLine(Canvas canvas, Paint paint) {
        int color = randomColor();
        int startX = mRandom.nextInt(DEFAULT_WIDTH);
        int startY = mRandom.nextInt(DEFAULT_HEIGHT);
        int stopX = mRandom.nextInt(DEFAULT_WIDTH);
        int stopY = mRandom.nextInt(DEFAULT_HEIGHT);
        paint.setStrokeWidth(1);
        paint.setColor(color);
        canvas.drawLine(startX, startY, stopX, stopY, paint);
    }

    /**
     * 随机颜色
     */
    private int randomColor() {
        //使用之前首先清空内容
        mBuilder.delete(0, mBuilder.length());

        String haxString;
        for (int i = 0; i < 3; i++) {
            haxString = Integer.toHexString(mRandom.nextInt(0xFF));
            if (haxString.length() == 1) {
                haxString = "0" + haxString;
            }

            mBuilder.append(haxString);
        }

        return Color.parseColor("#5fe6b3");
        //return Color.parseColor("#" + mBuilder.toString());
    }

    /**
     * 随机文本样式
     */
    private void randomTextStyle(Paint paint) {
        int color = randomColor();
        paint.setColor(color);
        //true为粗体，false为非粗体
        paint.setFakeBoldText(mRandom.nextBoolean());
        float skewX = mRandom.nextInt(11) / 10;
        skewX = mRandom.nextBoolean() ? skewX : -skewX;
        //float类型参数，负数表示右斜，整数左斜
        paint.setTextSkewX(skewX);
//        paint.setUnderlineText(true); //true为下划线，false为非下划线
//        paint.setStrikeThruText(true); //true为删除线，false为非删除线
    }

    /**
     * 随机间距
     */
    private void randomPadding() {
        mPaddingLeft += BASE_PADDING_LEFT + mRandom.nextInt(RANGE_PADDING_LEFT);
        mPaddingTop = BASE_PADDING_TOP + mRandom.nextInt(RANGE_PADDING_TOP);
    }
}
