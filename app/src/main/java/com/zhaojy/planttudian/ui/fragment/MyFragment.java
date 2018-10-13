package com.zhaojy.planttudian.ui.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.signature.StringSignature;
import com.google.gson.Gson;
import com.zhaojy.planttudian.R;
import com.zhaojy.planttudian.adapter.ViewPagerAdapter;
import com.zhaojy.planttudian.bean.Count;
import com.zhaojy.planttudian.bean.User;
import com.zhaojy.planttudian.constant.SiteInfo;
import com.zhaojy.planttudian.constant.Strings;
import com.zhaojy.planttudian.data.Update;
import com.zhaojy.planttudian.data.user.GetCollectCountPresenter;
import com.zhaojy.planttudian.data.user.GetHistoryCountPresenter;
import com.zhaojy.planttudian.data.user.LoginPresenter;
import com.zhaojy.planttudian.data.user.UploadImages;
import com.zhaojy.planttudian.ui.activity.LoginActivity;
import com.zhaojy.planttudian.ui.activity.SetActivity;
import com.zhaojy.planttudian.utils.ScreenUtils;
import com.zhaojy.planttudian.utils.SharePreferUtils;
import com.zhaojy.selectlibrary.control.PhotoSelectBuilder;
import com.zhaojy.selectlibrary.util.DisplayUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: zhaojy
 * @data:On 2018/9/15.
 */

public class MyFragment extends BaseFragment implements View.OnClickListener {
    private final static String TAG = MyFragment.class.getSimpleName();
    private View root;
    private RelativeLayout myBanner;
    private ViewPagerAdapter viewPagerAdapter;
    private ViewPager viewPager;
    private TextView collect;
    private TextView history;
    private View indicator;
    private ImageView avatar;
    private PhotoSelectBuilder builder;
    private ImageView menu;

    /**
     * ViewPager的当前选中页
     */
    private int currentIndex = 0;

    /**
     * 页面总数
     */
    private static final int PAGER_SUM = 2;

    /**
     * 获取收藏总数presenter
     */
    private GetCollectCountPresenter collectCountPresenter;

    /**
     * 获取浏览历史总数presenter
     */
    private GetHistoryCountPresenter historyCountPresenter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        if (root == null) {
            root = inflater.inflate(R.layout.my, container, false);
            init();
        }

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (null != root) {
            ((ViewGroup) root.getParent()).removeView(root);
        }

        if (collectCountPresenter != null) {
            collectCountPresenter.onStop();
        }
        if (historyCountPresenter != null) {
            historyCountPresenter.onStop();
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.collect:
                viewPager.setCurrentItem(0);
                break;
            case R.id.history:
                viewPager.setCurrentItem(1);
                break;
            case R.id.menu:
                Intent setIntent = new Intent(getActivity(), SetActivity.class);
                startActivity(setIntent);
                break;
            case R.id.avatar:
                if (SharePreferUtils.getString(getActivity(),
                        SharePreferUtils.USER_PHONE) != null) {
                    //选择头像
                    selectAvatar();
                } else {
                    //头像点击
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.myBanner:
                if (SharePreferUtils.getString(getActivity(),
                        SharePreferUtils.USER_PHONE) != null) {
                    //选择背景
                    selectBanner();
                } else {
                    //背景点击
                    Toast.makeText(getActivity(), Strings.PLEASE_LOGIN, Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (SharePreferUtils.getString(getActivity(),
                SharePreferUtils.USER_PHONE) == null) {
            User.reset();
            init();
            avatar.setImageResource(R.mipmap.icon);
            myBanner.setBackgroundResource(R.mipmap.banner);
            collect.setText(Strings.COLLECT + "\n0");
            history.setText(Strings.HISTORY + "\n0");
        } else if (User.getInstance().isUserChange()) {
            //如果账号发生改变
            init();
        } else {
            getHistoryCount();
            getCollectCount();
        }
    }

    private void init() {
        findViewById();
        //初始化指示条
        initIndicator();
        setViewPager();
        setListener();
        //登录
        login();
    }

    private void findViewById() {
        myBanner = root.findViewById(R.id.myBanner);
        viewPager = root.findViewById(R.id.viewPager);
        indicator = root.findViewById(R.id.indicator);
        collect = root.findViewById(R.id.collect);
        history = root.findViewById(R.id.history);
        avatar = root.findViewById(R.id.avatar);
        menu = root.findViewById(R.id.menu);
    }

    /**
     * 设置ViewPager
     */
    private void setViewPager() {
        viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);
        List<Fragment> list = new ArrayList<>();
        list.add(new CollectFragment());
        list.add(new HistoryFragment());
        viewPagerAdapter.setList(list);

         /*滑动监听*/
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int pos) {
                selectedOption(pos);
            }

            @Override
            public void onPageScrolled(int pos, float offsetPercentage, int offsetPixel) {
                setIndicatorPos(pos, offsetPercentage);
            }

            @Override
            public void onPageScrollStateChanged(int pos) {

            }
        });
    }

    /**
     * 初始化指示条
     */
    private void initIndicator() {
        //计算每个滑动项所占的宽度
        int w = ScreenUtils.getScreenWidth(getActivity()) / PAGER_SUM;
        LinearLayout.LayoutParams ll = (LinearLayout.LayoutParams) indicator.getLayoutParams();
        ll.width = w / 2;
        ll.setMargins(w / 4, 0, 0, 0);
        indicator.setLayoutParams(ll);
    }

    /**
     * 设置指示条位置
     *
     * @param position
     * @param offset
     */
    private void setIndicatorPos(int position, float offset) {
        int screenWidth = ScreenUtils.getScreenWidth(getActivity());
        int w = screenWidth / PAGER_SUM;

        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) indicator.getLayoutParams();

        if (currentIndex == 0 && position == 0) {
            lp.leftMargin = (int) (offset * w) + currentIndex * w + w / 4;
        } else if (currentIndex == 1 && position == 0) {
            lp.leftMargin = (int) (-(1 - offset) * w) + currentIndex * w + w / 4;
        } else if (currentIndex == 1 && position == 1) {
            lp.leftMargin = (int) (offset * w) + currentIndex * w + w / 4;
        }

        indicator.setLayoutParams(lp);
    }

    /**
     * 选中处理
     *
     * @param pos 索引下标
     */
    private void selectedOption(int pos) {
        switch (pos) {
            case 0:
                selected(collect);
                unSelected(history);
                break;
            case 1:
                unSelected(collect);
                selected(history);
                break;
            default:
                break;
        }
        currentIndex = pos;
    }

    /**
     * 被选中
     *
     * @param tv
     */
    private void selected(TextView tv) {
        tv.setTextColor(getResources().getColor(R.color.theme));
    }

    /**
     * 未被选中
     *
     * @param tv
     */
    private void unSelected(TextView tv) {
        tv.setTextColor(getResources().getColor(R.color.mainpage_tools_font));
    }

    /**
     * 设置监听
     */
    private void setListener() {
        collect.setOnClickListener(this);
        history.setOnClickListener(this);
        avatar.setOnClickListener(this);
        myBanner.setOnClickListener(this);
        menu.setOnClickListener(this);
    }

    /**
     * 选择头像
     */
    private void selectAvatar() {
        if (builder == null) {
            builder = new PhotoSelectBuilder(getActivity());
        }
        builder
                //设置是否可多选
                .setMultiple(false)
                .setCropable(true)
                //裁剪宽度
                .setCropWidth(Strings.AVATAR_WH)
                //裁剪高度
                .setCropHeight(Strings.AVATAR_WH)
                //是否显示照相功能，仅支持单选模式
                .setShowCamera(true)
                //每行显示照片数量
                .setColumnSum(3)
                //照片横向间距
                .setHorizontalSpacing(DisplayUtil.dip2px(getActivity(), 2))
                //照片纵向间距
                .setVerticalSpacing(DisplayUtil.dip2px(getActivity(), 2))
                //占位底图
                .setPlaceholder(R.mipmap.icon)
                //框架title
                .setTitle(Strings.SELECT_AVATAR)
                //选择成功回调接口
                .setSelectedPhotoPath(new PhotoSelectBuilder.ISelectedPhotoPath() {
                    @Override
                    public void selectedResult(List<String> pathList) {
                        String url = pathList.get(0);

                        UploadImages.setUploadListener(new UploadImages.UploadListener() {
                            @Override
                            public void onFailure(String errorInfo) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getActivity(), Strings.AVATAR_UPLOAD_FAILURE
                                                , Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                            @Override
                            public void onResponse(final String body) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (body.equals("null")) {
                                            Toast.makeText(getActivity(), Strings.AVATAR_UPLOAD_FAILURE
                                                    , Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(getActivity(), Strings.AVATAR_UPLOAD_SUCCESS
                                                    , Toast.LENGTH_SHORT).show();

                                            User temp = new Gson().fromJson(body, User.class);
                                            User.copy(temp);
                                        }

                                    }
                                });
                            }
                        });
                        UploadImages.uploadAvatar(new File(url)
                                .getAbsolutePath().replace("/file:", ""));

                        Glide.with(getActivity())
                                .load(url)
                                .asBitmap()
                                .signature(new StringSignature(
                                        String.valueOf(System.currentTimeMillis())))
                                .placeholder(R.mipmap.icon)
                                .into(new SimpleTarget<Bitmap>() {
                                    @Override
                                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                        avatar.setImageBitmap(resource);
                                    }
                                });
                    }
                });

        //设置照片裁剪存放Uri
        File fileCropUri = new File(Environment
                .getExternalStorageDirectory().getPath() + "/crop_photo.jpg");
        builder.setCropUri(Uri.fromFile(fileCropUri));

        //设置拍照存放Uri
        File takePhotoFile = new File(Environment
                .getExternalStorageDirectory().getPath() + "/take_photo.jpg");
        builder.setPhotoUri(Uri.fromFile(takePhotoFile));

        //创建
        builder.create();
    }

    /**
     * 选择背景
     */
    private void selectBanner() {
        if (builder == null) {
            builder = new PhotoSelectBuilder(getActivity());
        }
        builder
                //设置是否可多选
                .setMultiple(false)
                .setCropable(true)
                //裁剪宽度
                .setCropWidth(Strings.BANNER_WIDTH)
                //裁剪高度
                .setCropHeight(Strings.BANNER_HEIGHT)
                //是否显示照相功能，仅支持单选模式
                .setShowCamera(true)
                //每行显示照片数量
                .setColumnSum(3)
                //照片横向间距
                .setHorizontalSpacing(DisplayUtil.dip2px(getActivity(), 2))
                //照片纵向间距
                .setVerticalSpacing(DisplayUtil.dip2px(getActivity(), 2))
                //占位底图
                .setPlaceholder(R.mipmap.icon)
                //框架title
                .setTitle(Strings.SELECT_BANNER)
                //选择成功回调接口
                .setSelectedPhotoPath(new PhotoSelectBuilder.ISelectedPhotoPath() {
                    @Override
                    public void selectedResult(List<String> pathList) {
                        String url = pathList.get(0);
                        UploadImages.setUploadListener(new UploadImages.UploadListener() {
                            @Override
                            public void onFailure(String errorInfo) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getActivity(), Strings.BANNER_UPLOAD_FAILURE
                                                , Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                            @Override
                            public void onResponse(final String body) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (body.equals("null")) {
                                            Toast.makeText(getActivity(), Strings.BANNER_UPLOAD_FAILURE
                                                    , Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(getActivity(), Strings.BANNER_UPLOAD_SUCCESS
                                                    , Toast.LENGTH_SHORT).show();

                                            User temp = new Gson().fromJson(body, User.class);
                                            User.copy(temp);
                                        }

                                    }
                                });
                            }
                        });
                        UploadImages.uploadBanner(new File(url)
                                .getAbsolutePath().replace("/file:", ""));

                        Glide.with(getActivity())
                                .load(url)
                                .asBitmap()
                                .signature(new StringSignature(
                                        String.valueOf(System.currentTimeMillis())))
                                .placeholder(R.mipmap.banner)
                                .into(new SimpleTarget<Bitmap>() {
                                    @Override
                                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                        Drawable drawable = new BitmapDrawable(resource);
                                        myBanner.setBackground(drawable);
                                    }
                                });
                    }
                });

        //设置照片裁剪存放Uri
        File fileCropUri = new File(Environment
                .getExternalStorageDirectory().getPath() + "/crop_photo.jpg");
        builder.setCropUri(Uri.fromFile(fileCropUri));

        //设置拍照存放Uri
        File takePhotoFile = new File(Environment
                .getExternalStorageDirectory().getPath() + "/take_photo.jpg");
        builder.setPhotoUri(Uri.fromFile(takePhotoFile));

        //创建
        builder.create();
    }

    /**
     * 登录
     */
    private void login() {
        if (User.getInstance().getPhone() != null) {
            //设置头像和背景
            setAvatarAndBanner();
            //获取用户收藏总数
            getCollectCount();
            //获取用户浏览历史总数
            getHistoryCount();

            User.getInstance().setUserChange(false);
            return;
        }
        final String phoneNumber = SharePreferUtils.getString(getActivity(),
                SharePreferUtils.USER_PHONE);
        if (phoneNumber != null) {
            User user = User.getInstance();
            user.setPhone(phoneNumber);
            LoginPresenter loginPresenter = new LoginPresenter(getActivity());
            loginPresenter.setBaseUrl(SiteInfo.HOST_URL + SiteInfo.USER);
            loginPresenter.attachUpdate(new Update() {
                @Override
                public void onSuccess(Object object) {
                    User temp = (User) object;
                    User.copy(temp);

                    SharePreferUtils.storeDataByKey(getActivity(),
                            SharePreferUtils.USER_PHONE, phoneNumber);

                    //设置头像和背景
                    setAvatarAndBanner();
                    //获取用户收藏总数
                    getCollectCount();
                    //获取用户浏览历史总数
                    getHistoryCount();
                    User.getInstance().setUserChange(false);
                }

                @Override
                public void onError(String result) {

                }
            });
            loginPresenter.onCreate();
            loginPresenter.login(phoneNumber);
        }

    }

    /**
     * 设置头像和背景
     */
    private void setAvatarAndBanner() {
        User user = User.getInstance();
        //设置头像
        Glide.with(this)
                .load(SiteInfo.HOST_URL + user.getAvatar())
                .asBitmap()
                .signature(new StringSignature(
                        String.valueOf(System.currentTimeMillis())))
                .placeholder(R.mipmap.icon)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        avatar.setImageBitmap(resource);
                    }
                });

        //设置背景
        Glide.with(this)
                .load(SiteInfo.HOST_URL + user.getBanner())
                .asBitmap()
                .signature(new StringSignature(
                        String.valueOf(System.currentTimeMillis())))
                .placeholder(R.mipmap.banner)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        Drawable drawable = new BitmapDrawable(resource);
                        myBanner.setBackground(drawable);
                    }
                });
    }

    /**
     * 获取用户收藏总数
     */
    protected void getCollectCount() {
        collectCountPresenter = new GetCollectCountPresenter(getActivity());
        collectCountPresenter.setBaseUrl(SiteInfo.HOST_URL + SiteInfo.USER);
        collectCountPresenter.attachUpdate(new Update() {
            @Override
            public void onSuccess(Object object) {
                Count count = (Count) object;
                if (count.getCount() > 99) {
                    collect.setText(Strings.COLLECT + "\n" + "99+");
                } else {
                    collect.setText(Strings.COLLECT + "\n" + count.getCount());
                }

            }

            @Override
            public void onError(String result) {

            }
        });
        collectCountPresenter.onCreate();
        collectCountPresenter.getCollectSum();
    }

    /**
     * 获取用户浏览历史总数
     */
    protected void getHistoryCount() {
        historyCountPresenter = new GetHistoryCountPresenter(getActivity());
        historyCountPresenter.setBaseUrl(SiteInfo.HOST_URL + SiteInfo.USER);
        historyCountPresenter.attachUpdate(new Update() {
            @Override
            public void onSuccess(Object object) {
                Count count = (Count) object;
                if (count.getCount() > 99) {
                    history.setText(Strings.HISTORY + "\n" + "99+");
                } else {
                    history.setText(Strings.HISTORY + "\n" + count.getCount());
                }
            }

            @Override
            public void onError(String result) {

            }
        });
        historyCountPresenter.onCreate();
        historyCountPresenter.getHistorySum();
    }

    @Override
    protected void lazyLoad() {

    }

}