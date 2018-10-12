package com.zhaojy.planttudian.ui.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;

import com.zhaojy.planttudian.R;
import com.zhaojy.planttudian.adapter.ViewPagerAdapter;
import com.zhaojy.planttudian.bean.User;
import com.zhaojy.planttudian.constant.SiteInfo;
import com.zhaojy.planttudian.constant.Strings;
import com.zhaojy.planttudian.data.Update;
import com.zhaojy.planttudian.data.user.LoginPresenter;
import com.zhaojy.planttudian.helper.BottomNavigationViewHelper;
import com.zhaojy.planttudian.helper.StatusBarHelper;
import com.zhaojy.planttudian.ui.fragment.ClassifyFragment;
import com.zhaojy.planttudian.ui.fragment.MainFragment;
import com.zhaojy.planttudian.ui.fragment.MyFragment;
import com.zhaojy.planttudian.utils.EMUIUtils;
import com.zhaojy.planttudian.utils.FlymeUtils;
import com.zhaojy.planttudian.utils.MIUIUtils;
import com.zhaojy.planttudian.utils.SharePreferUtils;
import com.zhaojy.selectlibrary.util.PermissionUtils;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {
    private final static String TAG = MainActivity.class.getSimpleName();
    private final static int PERMISSION_REQUEST_CODE = 0x100;
    private final static String[] PERSISSIONS = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };
    private BottomNavigationView bottomNavigationView;
    private ViewPagerAdapter viewPagerAdapter;
    private ViewPager viewPager;
    private MenuItem menuItem;

    /**
     * 第一次按下返回键的时间
     */
    private long exitTime = 0;

    /**
     * 退出时间间隔
     */
    private final static int EXIT_TIME_GAP = 2000;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > EXIT_TIME_GAP) {
                Toast.makeText(this, Strings.PRESS_EXIT_PROCEDURE_AGAIN, Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
            }
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            boolean isAllGranted = true;
            //判断是否所有的权限都已经授予了
            for (int grant : grantResults) {
                if (grant != PackageManager.PERMISSION_GRANTED) {
                    isAllGranted = false;
                    break;
                }
            }

            if (isAllGranted) {
                //获得了所有的权限
                init();
            } else {

            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        //设置状态栏字体颜色为深色
        StatusBarHelper.statusBarLightMode(this, StatusBarHelper.ANDROID_M);

        //申请权限
        applyPermission();
    }

    private void init() {
        findViewById();
        setBottomNavigation();
        //登录
        login();

        Log.e(TAG, MIUIUtils.isMIUI() + "  miui");
        Log.e(TAG, FlymeUtils.isFlyme() + "  flyme");
        Log.e(TAG, EMUIUtils.isEMUI() + "  emui");
    }

    private void findViewById() {
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        viewPager = findViewById(R.id.vp);
    }

    /**
     * 设置底部导航栏
     */
    private void setBottomNavigation() {
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (menuItem != null) {
                    menuItem.setChecked(false);
                } else {
                    bottomNavigationView.getMenu().getItem(0).setChecked(false);
                }
                menuItem = bottomNavigationView.getMenu().getItem(position);
                menuItem.setChecked(true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);
        List<Fragment> list = new ArrayList<>();
        list.add(new MainFragment());
        list.add(new ClassifyFragment());
        list.add(new MyFragment());
        viewPagerAdapter.setList(list);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            menuItem = item;
            switch (item.getItemId()) {
                case R.id.mainpage:
                    viewPager.setCurrentItem(0);
                    return true;
                case R.id.classify:
                    viewPager.setCurrentItem(1);
                    return true;
                case R.id.my:
                    viewPager.setCurrentItem(2);
                    return true;
                default:
                    break;
            }
            return false;
        }
    };

    /**
     * 申请权限
     */
    private void applyPermission() {
        //判断权限是否申请
        boolean isAllGranted = false;
        isAllGranted = PermissionUtils.checkPermissionAllGranted(PERSISSIONS, this);
        if (!isAllGranted) {
            ActivityCompat.requestPermissions(
                    this, PERSISSIONS, PERMISSION_REQUEST_CODE
            );
        } else {
            //获得全部权限
            init();
        }
    }

    /**
     * 登录
     */
    private void login() {
        final String phoneNumber = SharePreferUtils.getString(this,
                SharePreferUtils.USER_PHONE);
        if (phoneNumber != null) {
            User user = User.getInstance();
            user.setPhone(phoneNumber);
            LoginPresenter loginPresenter = new LoginPresenter(this);
            loginPresenter.setBaseUrl(SiteInfo.HOST_URL + SiteInfo.USER);
            loginPresenter.attachUpdate(new Update() {
                @Override
                public void onSuccess(Object object) {
                    User temp = (User) object;
                    User.copy(temp);

                    SharePreferUtils.storeDataByKey(MainActivity.this,
                            SharePreferUtils.USER_PHONE, phoneNumber);

                }

                @Override
                public void onError(String result) {

                }
            });
            loginPresenter.onCreate();
            loginPresenter.login(phoneNumber);
        }

    }

}
