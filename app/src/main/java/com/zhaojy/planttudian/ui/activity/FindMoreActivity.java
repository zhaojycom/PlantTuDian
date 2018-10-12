package com.zhaojy.planttudian.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhaojy.planttudian.R;
import com.zhaojy.planttudian.adapter.SearchArticleAdapter;
import com.zhaojy.planttudian.adapter.SearchPlantAdapter;
import com.zhaojy.planttudian.bean.Article;
import com.zhaojy.planttudian.bean.Plant;
import com.zhaojy.planttudian.constant.SiteInfo;
import com.zhaojy.planttudian.constant.Strings;
import com.zhaojy.planttudian.data.Update;
import com.zhaojy.planttudian.data.plant.SearchArticlePresenter;
import com.zhaojy.planttudian.data.plant.SearchPlantPresenter;
import com.zhaojy.planttudian.helper.StatusBarHelper;
import com.zhaojy.planttudian.utils.JudgeBottomTopUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: zhaojy
 * @data:On 2018/10/9.
 */

public class FindMoreActivity extends BaseActivity implements View.OnClickListener {
    private final static String TAG = FindMoreActivity.class.getSimpleName();
    public final static String TITLE = "title";
    public final static String KEYWORD = "keyword";
    private ImageView back;
    private TextView title;
    private String titleStr;
    private String keyword;
    private GridView plantGridView;
    private RecyclerView articleRecycler;
    private List<Plant> plantList;
    private SearchPlantAdapter plantAdapter;
    private List<Article> articleList;
    private SearchArticleAdapter articleAdapter;
    /**
     * 植物搜索代理类
     */
    private SearchPlantPresenter plantPresenter;
    /**
     * 文章搜索代理类
     */
    private SearchArticlePresenter articlePresenter;

    /**
     * 滚动监听时间间隔
     */
    private final static int SCROLL_LISTENER_GAP = 2000;
    /**
     * gridView滚动监听handler code
     */
    private final static int GRIDVIEW_HANDLER_CODE = 1;

    @SuppressLint("HandlerLeak")
    private Handler gridViewHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GRIDVIEW_HANDLER_CODE:
                    boolean isBottom = JudgeBottomTopUtils.gridViewIsBottom(plantGridView);
                    if (isBottom) {
                        //滑动到底部加载数据
                        plantPresenter.searchPlant(keyword);
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.findmore_layout);
        //设置状态栏字体颜色为深色
        StatusBarHelper.statusBarLightMode(this, StatusBarHelper.ANDROID_M);
        init();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //清除Handler消息队列
        gridViewHandler.removeCallbacksAndMessages(null);
        if (plantPresenter != null) {
            plantPresenter.onStop();
        }
        if (articlePresenter != null) {
            articlePresenter.onStop();
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                this.finish();
                break;

            default:
                break;
        }
    }

    private void init() {
        findViewById();
        //设置监听器
        setListener();
        //获取intent信息
        getIntentInfo();
        //设置标题
        setTitle();
    }

    private void findViewById() {
        back = findViewById(R.id.back);
        title = findViewById(R.id.title);
        plantGridView = findViewById(R.id.plantGridView);
        articleRecycler = findViewById(R.id.articleRecycler);
    }

    /**
     * 获取intent信息
     */
    private void getIntentInfo() {
        Intent intent = getIntent();
        titleStr = intent.getStringExtra(TITLE);
        keyword = intent.getStringExtra(KEYWORD);
    }

    /**
     * 设置标题
     */
    private void setTitle() {
        title.setText(titleStr);

        switch (titleStr) {
            case Strings.ARTICLE:
                articleRecycler.setVisibility(View.VISIBLE);
                //设置文章搜索RecyclerView
                setArticleRecycler();
                break;
            case Strings.PLANT:
                plantGridView.setVisibility(View.VISIBLE);
                //设置植物GridView
                setPlantGridView();
                break;
            default:
                break;
        }
    }

    /**
     * 设置植物GridView
     */
    private void setPlantGridView() {
        if (plantList == null) {
            plantList = new ArrayList<>();
        }

        plantAdapter = new SearchPlantAdapter(
                this, plantList, plantGridView);
        plantAdapter.setKeyword(keyword);
        plantGridView.setAdapter(plantAdapter);

        plantGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                    long id) {
                Intent intent = new Intent(FindMoreActivity.this, PlantActivity.class);
                intent.putExtra(PlantActivity.PLANT_URL,
                        plantList.get(position).getArticleUrl());
                intent.putExtra(PlantActivity.PLANT_NAME,
                        plantList.get(position).getPlantName());
                intent.putExtra(PlantActivity.ID,
                        plantList.get(position).getId());
                startActivity(intent);
            }
        });

        //设置植物搜索presenter
        setPlantPresenter();
        //设置滚动监听
        gridViewHandler.sendEmptyMessage(GRIDVIEW_HANDLER_CODE);
    }

    /**
     * 设置文章搜索RecyclerView
     */
    private void setArticleRecycler() {
        if (articleList == null) {
            articleList = new ArrayList<>();
        }

        articleAdapter = new SearchArticleAdapter(articleList, this);
        articleAdapter.setOnItemClickListener(new SearchArticleAdapter.OnItemClickListener() {

            /**
             * 点击事件
             * @param view
             * @param position
             */
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(FindMoreActivity.this, ArticleDetailActivity.class);
                intent.putExtra(ArticleDetailActivity.TITLE,
                        articleList.get(position).getTitle());
                intent.putExtra(ArticleDetailActivity.ARTICLE_URL,
                        articleList.get(position).getArticleUrl());
                intent.putExtra(ArticleDetailActivity.ID,
                        articleList.get(position).getId());
                startActivity(intent);
            }

        });

        //设置布局管理器
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false) {
            @Override
            public boolean canScrollVertically() {
                return true;
            }
        };

        articleRecycler.setLayoutManager(linearLayoutManager);
        //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        //articleRecycler.setHasFixedSize(true);
        articleRecycler.setAdapter(articleAdapter);

        //RecyclerView滚动监听
        articleRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (recyclerView.computeVerticalScrollExtent() + recyclerView.computeVerticalScrollOffset() >= recyclerView.computeVerticalScrollRange()) {
                    //滑动到底部，继续加载
                    articlePresenter.searchArticle(keyword);
                }

            }

        });

        //设置搜索文章代理类
        setArticlePresenter();
    }

    /**
     * 设置植物搜索presenter
     */
    private void setPlantPresenter() {
        plantPresenter = new SearchPlantPresenter(this);
        plantPresenter.setBaseUrl(SiteInfo.HOST_URL + SiteInfo.PLANT);
        plantPresenter.attachUpdate(new Update() {
            @Override
            public void onSuccess(Object object) {
                List<Plant> plants = (List<Plant>) object;
                plantList.addAll(plants);
                plantAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String result) {

            }
        });
        plantPresenter.onCreate();
        plantPresenter.searchPlant(keyword);
    }

    /**
     * 设置搜索文章代理类
     */
    private void setArticlePresenter() {
        articlePresenter = new SearchArticlePresenter(this);
        articlePresenter.setBaseUrl(SiteInfo.HOST_URL + SiteInfo.PLANT);
        articlePresenter.attachUpdate(new Update() {
            @Override
            public void onSuccess(Object object) {
                List<Article> articles = (List<Article>) object;
                articleList.addAll(articles);
                articleAdapter.setKeyword(keyword);
                int total = articleList.size();
                int loadSum = articles.size();
                int startPos = total - loadSum;
                articleAdapter.notifyItemRangeInserted(startPos, loadSum);
            }

            @Override
            public void onError(String result) {

            }
        });
        articlePresenter.onCreate();
        articlePresenter.searchArticle(keyword);
    }

    /**
     * 设置监听器
     */
    private void setListener() {
        back.setOnClickListener(this);
    }

}
