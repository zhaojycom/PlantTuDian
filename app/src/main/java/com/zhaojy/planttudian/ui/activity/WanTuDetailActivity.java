package com.zhaojy.planttudian.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.zhaojy.planttudian.R;
import com.zhaojy.planttudian.constant.Strings;
import com.zhaojy.planttudian.helper.StatusBarHelper;
import com.zhaojy.planttudian.utils.BitmapUtils;

import java.io.File;

/**
 * @author: zhaojy
 * @data:On 2018/10/7.
 */

public class WanTuDetailActivity extends BaseActivity {
    private final static String TAG = WanTuDetailActivity.class.getSimpleName();
    public final static String IMG_URL = "imgUrl";

    private ImageView img;
    private String imgUrl;
    private ImageView download;
    /**
     * 保存加载图片的bitmap
     */
    private Bitmap bitmap = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.wantu_detail);
        //设置状态栏字体颜色为深色
        StatusBarHelper.statusBarLightMode(this, StatusBarHelper.ANDROID_M);
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
        //设置监听器
        setListener();
    }

    private void findViewById() {
        img = findViewById(R.id.img);
        download = findViewById(R.id.download);
    }

    /**
     * 获取intent信息
     */
    private void getIntentInfo() {
        Intent intent = getIntent();
        imgUrl = intent.getStringExtra(IMG_URL);
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
                    }
                });

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
