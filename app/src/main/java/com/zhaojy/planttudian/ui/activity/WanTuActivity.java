package com.zhaojy.planttudian.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.GridView;

import com.zhaojy.planttudian.R;
import com.zhaojy.planttudian.adapter.WanTuGridViewAdapter;
import com.zhaojy.planttudian.bean.WanTuBean;
import com.zhaojy.planttudian.constant.SiteInfo;
import com.zhaojy.planttudian.data.Update;
import com.zhaojy.planttudian.data.plant.GetWanTuListPresenter;
import com.zhaojy.planttudian.helper.StatusBarHelper;
import com.zhaojy.planttudian.utils.JudgeBottomTopUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: zhaojy
 * @data:On 2018/9/24.
 */

public class WanTuActivity extends BaseActivity {
    private final static String TAG = WanTuActivity.class.getSimpleName();
    private GridView gridView;
    private List<WanTuBean> wanTuBeanList;
    private WanTuGridViewAdapter gridViewAdapter;
    /**
     * 滚动监听时间间隔
     */
    private final static int SCROLL_LISTENER_GAP = 100;
    /**
     * gridView滚动监听handler code
     */
    private final static int GRIDVIEW_HANDLER_CODE = 1;
    /**
     * 获取玩图列表信息代理类
     */
    private GetWanTuListPresenter wanTuListPresenter;

    @SuppressLint("HandlerLeak")
    private Handler gridViewHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GRIDVIEW_HANDLER_CODE:
                    boolean isBottom = JudgeBottomTopUtils.gridViewIsBottom(gridView);
                    if (isBottom) {
                        //滑动到底部加载数据
                        wanTuListPresenter.getWanTuList();
                    }
                    gridViewHandler.sendEmptyMessageDelayed(GRIDVIEW_HANDLER_CODE, SCROLL_LISTENER_GAP);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.wantu_layout);
        //设置状态栏字体颜色为深色
        StatusBarHelper.statusBarLightMode(this, StatusBarHelper.ANDROID_M);
        init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //清除Handler消息队列
        gridViewHandler.removeCallbacksAndMessages(null);
        wanTuListPresenter.onStop();
    }

    private void init() {
        findViewById();
        //设置Gridview
        setGridView();
    }

    private void findViewById() {
        gridView = findViewById(R.id.gridview);
    }

    /**
     * 设置Gridview
     */
    private void setGridView() {
        if (wanTuBeanList == null) {
            wanTuBeanList = new ArrayList<>();
        }

        gridViewAdapter = new WanTuGridViewAdapter(this, wanTuBeanList, gridView);

        gridView.setAdapter(gridViewAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                    long id) {
                Intent intent = new Intent(WanTuActivity.this
                        , WanTuDetailActivity.class);
                intent.putExtra(WanTuDetailActivity.IMG_URL,
                        wanTuBeanList.get(position).getbImgUrl());
                intent.putExtra(WanTuDetailActivity.TITLE,
                        wanTuBeanList.get(position).getTitle());
                startActivity(intent);
            }
        });

        //GridView滚动监听
        gridViewHandler.sendEmptyMessage(GRIDVIEW_HANDLER_CODE);
        //设置玩图列表信息代理类
        setWanTuListPresenter();
    }

    /**
     * 设置玩图列表信息代理类
     */
    private void setWanTuListPresenter() {
        wanTuListPresenter = new GetWanTuListPresenter(this);
        wanTuListPresenter.setBaseUrl(SiteInfo.HOST_URL + SiteInfo.PLANT);
        wanTuListPresenter.attachUpdate(new Update() {
            @Override
            public void onSuccess(Object object) {
                List<WanTuBean> wanTuBeans = (List<WanTuBean>) object;
                wanTuBeanList.addAll(wanTuBeans);
                gridViewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String result) {
                Log.e(TAG, result);
            }
        });
        wanTuListPresenter.onCreate();
        wanTuListPresenter.getWanTuList();
    }

}
