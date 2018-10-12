package com.zhaojy.planttudian.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.zhaojy.planttudian.R;
import com.zhaojy.planttudian.helper.StatusBarHelper;

/**
 * @author: zhaojy
 * @data:On 2018/10/7.
 */

public class WanTuDetailActivity extends BaseActivity {
    private final static String TAG = WanTuDetailActivity.class.getSimpleName();
    public final static String IMG_URL = "imgUrl";

    private ImageView img;
    private String imgUrl;

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

    }

    private void init() {
        findViewById();
        //获取intent信息
        getIntentInfo();
        //设置图片
        setImg();
    }

    private void findViewById() {
        img = findViewById(R.id.img);
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
                .placeholder(R.mipmap.icon)
                .into(img);
    }

}
