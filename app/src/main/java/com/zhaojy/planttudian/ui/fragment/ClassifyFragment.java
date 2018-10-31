package com.zhaojy.planttudian.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.RelativeLayout;

import com.zhaojy.planttudian.R;
import com.zhaojy.planttudian.adapter.LeftClassifyAdapter;
import com.zhaojy.planttudian.adapter.PlantsAdapter;
import com.zhaojy.planttudian.bean.ClassifyBean;
import com.zhaojy.planttudian.bean.Plant;
import com.zhaojy.planttudian.constant.SiteInfo;
import com.zhaojy.planttudian.data.Update;
import com.zhaojy.planttudian.data.plant.GetClassifyPlantsPresenter;
import com.zhaojy.planttudian.data.plant.GetPlantClassifyPresenter;
import com.zhaojy.planttudian.ui.activity.PlantActivity;
import com.zhaojy.planttudian.ui.activity.SearchActivity;
import com.zhaojy.planttudian.utils.JudgeBottomTopUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: zhaojy
 * @data:On 2018/9/15.
 */

public class ClassifyFragment extends BaseFragment implements View.OnClickListener {
    private final static String TAG = ClassifyFragment.class.getSimpleName();
    private View root;
    private RecyclerView leftClassify;
    private GridView rightContent;
    private LeftClassifyAdapter leftClassifyAdapter;
    private List<ClassifyBean> leftClassifyList;
    private List<Plant> plantList;
    private PlantsAdapter plantsAdapter;
    /**
     * 获取所选分类植物信息集合presenter
     */
    private GetClassifyPlantsPresenter plantsPresenter;
    /**
     * 上次选择的分类下标
     */
    private int preClassifyPos = 0;
    private RelativeLayout searchBar;
    /**
     * 滚动监听时间间隔
     */
    private final static int SCROLL_LISTENER_GAP = 500;
    /**
     * gridView滚动监听handler code
     */
    private final static int GRIDVIEW_HANDLER_CODE = 1;

    /**
     * 当前选择的分类id
     */
    private int curSelectClassifyId = 0;

    @SuppressLint("HandlerLeak")
    private Handler gridViewHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GRIDVIEW_HANDLER_CODE:
                    boolean isBottom = JudgeBottomTopUtils.gridViewIsBottom(rightContent);
                    if (isBottom) {
                        //滑动到底部加载数据
                        plantsPresenter.getClassifyPlantList();
                    }
                    gridViewHandler.sendEmptyMessageDelayed(GRIDVIEW_HANDLER_CODE
                            , SCROLL_LISTENER_GAP);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        //清除Handler消息队列
        gridViewHandler.removeCallbacksAndMessages(null);
        plantsPresenter.onStop();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.searchBar:
                Intent searchIntent = new Intent(getActivity(), SearchActivity.class);
                startActivity(searchIntent);
                break;
            default:
                break;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.classify, container, false);
        //init();
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    private void init() {
        findViewById();
        //设置左侧分类栏
        setLeftClassify();
        //设置监听器
        setListener();
        //获取植物分类列表
        getLeftClassifyList();
    }

    private void findViewById() {
        leftClassify = root.findViewById(R.id.leftClassify);
        rightContent = root.findViewById(R.id.rightContent);
        searchBar = root.findViewById(R.id.searchBar);
    }

    /**
     * 设置左侧分类栏列表
     */
    private void setLeftClassify() {
        if (leftClassifyList == null) {
            leftClassifyList = new ArrayList<>();
        }

        leftClassifyAdapter = new LeftClassifyAdapter(leftClassifyList, this.getActivity());
        leftClassifyAdapter.setOnItemClickListener(new LeftClassifyAdapter.OnItemClickListener() {

            /**
             * 点击事件
             * @param view
             * @param position
             */
            @Override
            public void onItemClick(View view, int position) {
                View preItemView = leftClassify.getLayoutManager()
                        .findViewByPosition(preClassifyPos);
                View preBorder = preItemView.findViewById(R.id.leftBorder);
                preBorder.setVisibility(View.GONE);
                preItemView.setBackgroundResource(R.color.themeBk);

                View curItemView = leftClassify.getLayoutManager()
                        .findViewByPosition(position);
                View curBorder = curItemView.findViewById(R.id.leftBorder);
                curBorder.setVisibility(View.VISIBLE);
                curItemView.setBackgroundResource(R.color.white);

                preClassifyPos = position;

                //加载右侧数据
                if (curSelectClassifyId != leftClassifyList.get(position).getId()) {
                    //移除所有
                    plantList.clear();

                    curSelectClassifyId = leftClassifyList.get(position).getId();
                    setRightContent();
                    plantsPresenter.getClassifyPlantList();
                }
            }

        });

        //设置布局管理器
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL, false) {
            @Override
            public boolean canScrollVertically() {
                return true;
            }
        };

        leftClassify.setLayoutManager(linearLayoutManager);
        //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        leftClassify.setHasFixedSize(true);
        leftClassify.setAdapter(leftClassifyAdapter);
    }

    /**
     * 设置右侧内容
     */
    private void setRightContent() {
        if (plantList == null) {
            plantList = new ArrayList<>();
        }

        plantsAdapter = new PlantsAdapter(getActivity(), plantList, rightContent);

        rightContent.setAdapter(plantsAdapter);

        rightContent.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                    long id) {
                Intent intent = new Intent(getActivity(), PlantActivity.class);
                intent.putExtra(PlantActivity.PLANT_URL,
                        plantList.get(position).getArticleUrl());
                intent.putExtra(PlantActivity.PLANT_NAME,
                        plantList.get(position).getPlantName());
                intent.putExtra(PlantActivity.ID,
                        plantList.get(position).getId());
                startActivity(intent);
            }
        });

        //GridView滚动监听
        gridViewHandler.sendEmptyMessage(GRIDVIEW_HANDLER_CODE);
        //设置分类植物presenter
        setClssifyPlantListPresenter();
    }

    /**
     * 设置监听器
     */
    private void setListener() {
        searchBar.setOnClickListener(this);
    }

    /**
     * 获取左侧植物分类信息集合
     */
    private void getLeftClassifyList() {
        GetPlantClassifyPresenter gpcp = new GetPlantClassifyPresenter(getActivity());
        gpcp.setBaseUrl(SiteInfo.HOST_URL + SiteInfo.PLANT);
        gpcp.attachUpdate(new Update() {
            @Override
            public void onSuccess(Object object) {
                List<ClassifyBean> cbList = (List<ClassifyBean>) object;
                leftClassifyList.addAll(cbList);
                leftClassifyAdapter.notifyDataSetChanged();

                if (cbList.size() > 0) {
                    curSelectClassifyId = cbList.get(0).getId();
                    //设置右侧内容
                    setRightContent();
                }

            }

            @Override
            public void onError(String result) {
                Log.e(TAG, result);
            }
        });
        gpcp.onCreate();
        gpcp.getPlantClassifyList();
    }

    /**
     * 设置分类植物presenter
     */
    private void setClssifyPlantListPresenter() {
        plantsPresenter = new GetClassifyPlantsPresenter(getActivity());
        plantsPresenter.reset(curSelectClassifyId);
        plantsPresenter.setBaseUrl(SiteInfo.HOST_URL + SiteInfo.PLANT);
        plantsPresenter.attachUpdate(new Update() {
            @Override
            public void onSuccess(Object object) {
                List<Plant> plants = (List<Plant>) object;
                plantList.addAll(plants);
                plantsAdapter.notifyDataSetChanged();

            }

            @Override
            public void onError(String result) {
                Log.e(TAG, result);
            }
        });
        plantsPresenter.onCreate();
        plantsPresenter.getClassifyPlantList();
    }

    @Override
    protected void lazyLoad() {
        init();
    }
}