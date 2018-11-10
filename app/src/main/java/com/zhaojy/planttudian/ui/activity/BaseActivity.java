package com.zhaojy.planttudian.ui.activity;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

/**
 * @author: zhaojy
 * @data:On 2018/9/16.
 */

public class BaseActivity extends AppCompatActivity {
    /**
     * 设置状态栏透明
     */
    public void setStatusBarTranparent() {
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
    }

    /**
     * 设置状态栏颜色
     * @param color
     */
    public void setStatusBarThemeColor(int color) {
        Window window = this.getWindow();
        //取消设置透明状态栏,使 ContentView 内容不再覆盖状态栏
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //需要设置这个 flag 才能调用 setStatusBarColor 来设置状态栏颜色
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            //设置状态栏颜色
            window.setStatusBarColor(color);
        }

    }

}
