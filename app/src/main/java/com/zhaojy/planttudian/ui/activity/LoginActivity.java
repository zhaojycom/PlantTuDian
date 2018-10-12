package com.zhaojy.planttudian.ui.activity;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.zhaojy.planttudian.R;
import com.zhaojy.planttudian.bean.User;
import com.zhaojy.planttudian.constant.SiteInfo;
import com.zhaojy.planttudian.constant.Strings;
import com.zhaojy.planttudian.data.Update;
import com.zhaojy.planttudian.data.user.LoginPresenter;
import com.zhaojy.planttudian.utils.CheckPhoneUtils;
import com.zhaojy.planttudian.utils.DimUtils;
import com.zhaojy.planttudian.utils.SharePreferUtils;
import com.zhaojy.planttudian.utils.VerCodeUtils;

import java.lang.ref.WeakReference;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

/**
 * @author: zhaojy
 * @data:On 2018/9/19.
 */

public class LoginActivity extends BaseActivity implements View.OnClickListener {
    private final static String TAG = LoginActivity.class.getSimpleName();
    /**
     * 背景模糊度
     */
    private final static int DIM_RADIUS = 8;
    private ConstraintLayout bg;
    private ImageView verCode;
    private EditText phoneInput;
    private EditText verCodeInput;
    private ImageView clear;
    private TextView loginBt;
    /**
     * 生成图形验证码工具类
     */
    private VerCodeUtils vcu = VerCodeUtils.getInstance();
    private EventHandler eventHandler;
    private TextView immediateAccess;
    private TextView msgVerCode;
    private LoginPresenter loginPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置状态栏透明
        super.setStatusBarTranparent();
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.login);
        init();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //使用完EventHandler需注销，否则可能出现内存泄漏
        SMSSDK.unregisterEventHandler(eventHandler);
        if (loginPresenter != null) {
            loginPresenter.onStop();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.verCode:
                generateVerCode();
                break;
            case R.id.clear:
                phoneInput.setText("");
                break;
            case R.id.immediateAccess:
                //获取输入的手机号码
                String phoneNumber = phoneInput.getText().toString();
                boolean isLegal = CheckPhoneUtils.isPhone(this, phoneNumber);
                if (isLegal) {
                    // 请求验证码，其中country表示国家代码，如“86”；phone表示手机号码，如“13800138000”
                    SMSSDK.getVerificationCode(Strings.COUNTRY_CODE, phoneNumber);
                }
                break;
            case R.id.loginBt:
                //获取输入的手机号码
                String phoneNumber2 = phoneInput.getText().toString();
                //图形验证码
                String verCodeStr = verCodeInput.getText().toString();
                //短信验证码
                String msgVerCodeStr = msgVerCode.getText().toString();
                if (!CheckPhoneUtils.isPhone(this, phoneNumber2)) {
                    return;
                } else if (verCodeStr.equals("")) {
                    Toast.makeText(this, Strings.PLEASE_INPUT_VERCODE,
                            Toast.LENGTH_SHORT).show();
                    return;
                } else if (msgVerCodeStr.equals("")) {
                    Toast.makeText(this, Strings.PLEASE_INPUT_MSG_CODE,
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                // 提交验证码，其中的code表示验证码，如“1357”
                SMSSDK.submitVerificationCode(Strings.COUNTRY_CODE,
                        phoneNumber2, msgVerCodeStr);
                break;
            default:
                break;
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (hasFocus) {
            //背景模糊处理
            dimBg();
        }
    }

    private void init() {
        findViewById();
        //设置监听器
        setListener();
        //生成图片验证码
        generateVerCode();
        //设置短信发送
        setMessageSender();
    }

    private void findViewById() {
        bg = findViewById(R.id.bg);
        verCode = findViewById(R.id.verCode);
        phoneInput = findViewById(R.id.phoneInput);
        verCodeInput = findViewById(R.id.verCodeInput);
        clear = findViewById(R.id.clear);
        loginBt = findViewById(R.id.loginBt);
        immediateAccess = findViewById(R.id.immediateAccess);
        msgVerCode = findViewById(R.id.msgVerCode);
    }

    /**
     * 设置短信发送
     */
    private void setMessageSender() {
        // 在尝试读取通信录时以弹窗提示用户（可选功能）
        SMSSDK.setAskPermisionOnReadContact(true);

        eventHandler = new EventHandler() {
            @Override
            public void afterEvent(int event, int result, Object data) {
                // afterEvent会在子线程被调用，因此如果后续有UI相关操作，需要将数据发送到UI线程
                Message msg = new Message();
                msg.arg1 = event;
                msg.arg2 = result;
                msg.obj = data;
                new Handler(Looper.getMainLooper(), new Handler.Callback() {
                    @Override
                    public boolean handleMessage(Message msg) {
                        int event = msg.arg1;
                        int result = msg.arg2;
                        Object data = msg.obj;
                        if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                            if (result == SMSSDK.RESULT_COMPLETE) {
                                //发送成功回调
                                Toast.makeText(LoginActivity.this,
                                        Strings.MSG_SEND_SUCCESS, Toast.LENGTH_SHORT).show();
                            } else {

                                ((Throwable) data).printStackTrace();
                            }
                        } else if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                            if (result == SMSSDK.RESULT_COMPLETE) {
                                //短信验证码提交成功回调
                                //提交登录注册信息
                                submitLoginRegisterInfo();
                            } else {

                                ((Throwable) data).printStackTrace();
                            }
                        }

                        return false;
                    }
                }).sendMessage(msg);
            }
        };
        // 注册一个事件回调，用于处理SMSSDK接口请求的结果
        SMSSDK.registerEventHandler(eventHandler);
    }

    /**
     * 模糊背景处理
     */
    private void dimBg() {
        Glide.with(this)
                .load(R.mipmap.login_bg)
                .asBitmap()
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        WeakReference<Bitmap> overlay = new WeakReference<>
                                (DimUtils.getDimBitmap(resource, DIM_RADIUS));
                        Drawable drawable = new BitmapDrawable(overlay.get());
                        bg.setBackground(drawable);
                    }
                });
    }

    /**
     * 产生验证码
     */
    private void generateVerCode() {
        Bitmap codeBitmap = vcu.createBitmap();
        verCode.setImageBitmap(codeBitmap);
    }

    /**
     * 设置监听事件
     */
    private void setListener() {
        clear.setOnClickListener(this);
        verCode.setOnClickListener(this);
        loginBt.setOnClickListener(this);
        immediateAccess.setOnClickListener(this);
    }

    /**
     * 提交登录注册信息
     */
    private void submitLoginRegisterInfo() {
        String verCodeStr = verCodeInput.getText().toString();
        if (verCodeStr.equals(vcu.getCode())) {
            //如果输入正确的验证码
            //提交注册信息
            //获取输入的手机号码
            final String phoneNumber = phoneInput.getText().toString();
            boolean isLegal = CheckPhoneUtils.isPhone(this, phoneNumber);
            if (isLegal) {
                if (loginPresenter == null) {
                    loginPresenter = new LoginPresenter(this);
                }
                loginPresenter.setBaseUrl(SiteInfo.HOST_URL + SiteInfo.USER);
                loginPresenter.attachUpdate(new Update() {
                    @Override
                    public void onSuccess(Object object) {
                        //登录成功
                        User temp = (User) object;
                        temp.setUserChange(true);
                        User.copy(temp);

                        SharePreferUtils.storeDataByKey(LoginActivity.this,
                                SharePreferUtils.USER_PHONE, phoneNumber);

                        Toast.makeText(LoginActivity.this, Strings.LOGIN_SUCCESS
                                , Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    @Override
                    public void onError(String result) {
                        Log.e(TAG, result);
                        Toast.makeText(LoginActivity.this, Strings.LOGIN_FAILURE
                                , Toast.LENGTH_SHORT).show();
                    }
                });
                loginPresenter.onCreate();
                loginPresenter.login(phoneNumber);
            }

        } else {
            //如果输入错误的验证码
            //产生新的验证码
            generateVerCode();
        }
    }

}
