package com.zhaojy.planttudian.ui.activity;

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

}
