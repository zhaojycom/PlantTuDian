package com.zhaojy.planttudian.ui.application;

import android.app.Application;
import android.os.StrictMode;

import com.mob.MobSDK;
import com.tencent.bugly.crashreport.CrashReport;
import com.zhaojy.planttudian.constant.Strings;

/**
 * @author: zhaojy
 * @data:On 2018/9/29.
 */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // android 7.0系统解决拍照的问题
        StrictMode.VmPolicy.Builder builder = null;
        builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();
        //短信验证码接口初始化
        MobSDK.init(this);

        //异常日志
        /*CrashHandler crashHandler = CrashHandler.getsInstance();
        crashHandler.init(this);*/

        CrashReport.initCrashReport(getApplicationContext(), Strings.BUGLY_APP_ID, false);
    }
}
