package com.zhaojy.planttudian.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.zhaojy.planttudian.R;

import static android.graphics.Bitmap.createScaledBitmap;

/**
 * 自定义圆形图片
 *
 * @author: zhaojy
 * @data:On 2018/9/16.
 */
public class RoundImageView extends android.support.v7.widget.AppCompatImageView {
    /*半径*/
    private float radius = 0;
    /**
     * 类型
     */
    private int type;
    /**
     * 类型-圆角
     */
    private final int TYPE_FTLLET = 0;

    /**
     * 类型-圆形
     */
    private final int TYPE_ROUND = 1;
    private Paint paint;
    private int mWidth;
    private int mHeight;

    /**
     * 获得半径
     */
    public float getRadius() {
        return radius;
    }

    /**
     * 设置半径
     *
     * @param radius
     */
    public void setRadius(float radius) {
        this.radius = radius;
    }

    public RoundImageView(Context context, AttributeSet attrs) {
        //没有这个方法就会出错。没有这句话就会读不到值
        this(context, attrs, 0);
    }

    public RoundImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.RoundImageView, defStyleAttr, 0);
        radius = typedArray.getDimension(R.styleable.RoundImageView_xhradius, 0);
        type = typedArray.getInt(R.styleable.RoundImageView_xhtype, 1);
        typedArray.recycle();
        paint = new Paint();
    }

    /**
     * 绘制图片
     */
    @Override
    protected void onDraw(Canvas canvas) {
        Drawable drawable = getDrawable();
        if (null != drawable) {
            Bitmap bitmap = null;
            if (drawable instanceof BitmapDrawable) {
                bitmap = ((BitmapDrawable) drawable).getBitmap();
            } else {
                bitmap = ((GlideBitmapDrawable) drawable).getBitmap();
            }

            bitmap = getScaleBitmap(bitmap, this.getWidth(), this.getHeight());
            switch (type) {
                case TYPE_FTLLET:
                    //画圆角类型
                    /*不可省略*/
                    paint.reset();
                    canvas.drawBitmap(getFilletBitmap(bitmap, radius), 0, 0, null);
                    break;
                case TYPE_ROUND:
                    //画圆形
                    int min = Math.min(getWidth(), getHeight());
                    // 长度如果不一致，按小的值进行压缩
                    /*不可省略*/
                    paint.reset();
                    bitmap = createScaledBitmap(bitmap, min, min, false);
                    canvas.drawBitmap(getRoundBitmap(bitmap, min), 0, 0, null);
                    break;
                default:
                    break;
            }
        } else {
            super.onDraw(canvas);
        }
    }

    /**
     * 获取圆角矩形图片方法
     *
     * @param radius
     * @param bitmap
     */
    public Bitmap getFilletBitmap(Bitmap bitmap, float radius) {
        Bitmap output = Bitmap.createBitmap(this.getWidth(),
                this.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Rect rect = new Rect(0, 0, this.getWidth(), this.getHeight());
        final RectF rectF = new RectF(rect);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, radius, radius, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    /**
     * 获取圆形imageView的方法
     *
     * @param bitmap
     * @param radius
     * @return
     */
    public Bitmap getRoundBitmap(Bitmap bitmap, float radius) {
        Bitmap output = Bitmap.createBitmap((int) radius,
                (int) radius, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        paint.setAntiAlias(true);
        canvas.drawCircle(radius / 2, radius / 2, radius / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, 0, 0, paint);
        return output;
    }

    /**
     * 缩放bitmap到指定大小
     *
     * @param bitmap
     * @param scalex
     * @param scaley
     * @return
     */
    private static Bitmap getScaleBitmap(Bitmap bitmap, float scalex, float scaley) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Matrix matrix = new Matrix();
        matrix.postScale(scalex / width, scaley / height);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }
}
