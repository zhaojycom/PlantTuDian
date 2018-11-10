package com.zhaojy.planttudian.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.github.chrisbanes.photoview.PhotoView;
import com.zhaojy.planttudian.R;
import com.zhaojy.planttudian.constant.Strings;
import com.zhaojy.planttudian.utils.BitmapUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: zhaojy
 * @data:On 2018/10/7.
 */

public class WanTuDetailActivity extends BaseActivity {
    private final static String TAG = WanTuDetailActivity.class.getSimpleName();
    public final static String IMG_URL = "imgUrl";
    public final static String TITLE = "title";

    private PhotoView img;
    private String imgUrl;
    private ImageView download;
    private TextView title;
    private String titleStr;
    /**
     * 保存加载图片的bitmap
     */
    private Bitmap bitmap = null;
    private List<Palette.Swatch> swatchList;
    private LinearLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.wantu_detail);
        //设置状态栏字体颜色为深色
        //StatusBarHelper.statusBarLightMode(this, StatusBarHelper.ANDROID_M);
        init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bitmap = null;
    }

    private void init() {
        findViewById();
        //获取intent信息
        getIntentInfo();
        //设置图片
        setImg();
        //设置标题
        setTitle();
        //设置监听器
        setListener();
    }

    private void findViewById() {
        img = findViewById(R.id.img);
        download = findViewById(R.id.download);
        title = findViewById(R.id.title);
        container = findViewById(R.id.container);
    }

    /**
     * 获取intent信息
     */
    private void getIntentInfo() {
        Intent intent = getIntent();
        imgUrl = intent.getStringExtra(IMG_URL);
        titleStr = intent.getStringExtra(TITLE);
    }

    /**
     * 设置图片
     */
    private void setImg() {
        Glide.with(this)
                .load(imgUrl)
                .asBitmap()
                .placeholder(R.mipmap.icon)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        bitmap = resource;
                        img.setImageBitmap(resource);
                        //设置最大最小缩放比例
                        img.setMinimumScale(0.5f);
                        img.setMaximumScale(3.0f);

                        //获取调色板
                        getPalette();

                    }
                });

    }

    /**
     * 获取调色板
     */
    private void getPalette() {
        // Asynchronous
        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(Palette palette) {

                if (palette != null) {
                    //有活力的，暗色
                    Palette.Swatch vibrantDark = palette.getDarkVibrantSwatch();
                    //有活力的
                    Palette.Swatch vibrant = palette.getVibrantSwatch();
                    //有活力的，亮色
                    Palette.Swatch vibrantLight = palette.getLightVibrantSwatch();
                    //柔和的，暗色
                    Palette.Swatch mutedDark = palette.getDarkMutedSwatch();
                    //柔和的
                    Palette.Swatch muted = palette.getMutedSwatch();
                    //柔和的,亮色
                    Palette.Swatch mutedLight = palette.getLightMutedSwatch();

                    swatchList = new ArrayList<>();
                    swatchList.add(vibrantDark);
                    swatchList.add(vibrant);
                    swatchList.add(vibrantLight);
                    swatchList.add(mutedDark);
                    swatchList.add(muted);
                    swatchList.add(mutedLight);

                    for (Palette.Swatch swatch : swatchList) {
                        //设置状态栏颜色
                        if (swatch != null) {
                            WanTuDetailActivity.super.setStatusBarThemeColor(swatch.getRgb());
                            break;
                        }
                    }

                }
            }
        });
    }

    /**
     * 设置标题
     */
    private void setTitle() {
        title.setText(titleStr);
    }

    /**
     * 设置监听器
     */
    private void setListener() {
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bitmap != null) {
                    String fileName = imgUrl.hashCode() + ".jpg";
                    File file = new File(Strings.SD_ROOT + Strings.SD_WANTU);

                    if (!file.exists()) {
                        file.mkdirs();
                    }
                    String filePath = file.getAbsolutePath() + "/" + fileName;
                    BitmapUtils.saveBitmapToSD(bitmap, filePath);

                    // 最后通知图库更新
                    WanTuDetailActivity.this.sendBroadcast(
                            new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                                    Uri.fromFile(new File(filePath))));

                    Toast.makeText(WanTuDetailActivity.this,
                            Strings.SAVE_SUCCESS, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
