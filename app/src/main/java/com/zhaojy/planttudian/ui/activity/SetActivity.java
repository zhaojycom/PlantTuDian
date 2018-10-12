package com.zhaojy.planttudian.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.zhaojy.planttudian.R;
import com.zhaojy.planttudian.helper.StatusBarHelper;
import com.zhaojy.planttudian.utils.SharePreferUtils;

/**
 * @author: zhaojy
 * @data:On 2018/10/12.
 */

public class SetActivity extends BaseActivity implements View.OnClickListener {
    private final static String TAG = SetActivity.class.getSimpleName();
    private ImageView back;
    private RelativeLayout exitLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.set_layout);
        //设置状态栏字体颜色为深色
        StatusBarHelper.statusBarLightMode(this, StatusBarHelper.ANDROID_M);

        init();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                this.finish();
                break;
            case R.id.exitLogin:
                //退出登录
                exitLogin();
                break;
            default:
                break;
        }
    }

    private void init() {
        findViewById();
        //设置监听器
        setListener();
    }

    private void findViewById() {
        back = findViewById(R.id.back);
        exitLogin = findViewById(R.id.exitLogin);
    }

    /**
     * 退出登录
     */
    private void exitLogin() {
        SharePreferUtils.storeDataByKey(this, SharePreferUtils.USER_PHONE, null);
        this.finish();
    }

    /**
     * 设置监听器
     */
    private void setListener() {
        back.setOnClickListener(this);
        exitLogin.setOnClickListener(this);
    }

}
