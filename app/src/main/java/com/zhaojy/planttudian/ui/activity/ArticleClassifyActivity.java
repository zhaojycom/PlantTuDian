package com.zhaojy.planttudian.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhaojy.planttudian.R;
import com.zhaojy.planttudian.adapter.ArticleAdapter;
import com.zhaojy.planttudian.bean.Article;
import com.zhaojy.planttudian.constant.SiteInfo;
import com.zhaojy.planttudian.data.Update;
import com.zhaojy.planttudian.data.plant.GetClassifyArticlesPresenter;
import com.zhaojy.planttudian.helper.StatusBarHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: zhaojy
 * @data:On 2018/10/7.
 */

public class ArticleClassifyActivity extends BaseActivity implements View.OnClickListener {
    private final static String TAG = ArticleClassifyActivity.class.getSimpleName();
    public final static String CLASSIFY_NAME = "classifyName";
    public final static String CLASSIFY_ID = "classifyId";
    private ImageView back;
    private TextView classifyName;
    private String classifyNameStr;
    private RecyclerView articleRecycler;
    private ArticleAdapter articleAdapter;
    private List<Article> articleList;
    /**
     * 获取分类文章presenter
     */
    private GetClassifyArticlesPresenter articlesPresenter;

    private int classifyId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.article_classify);
        //设置状态栏字体颜色为深色
        StatusBarHelper.statusBarLightMode(this, StatusBarHelper.ANDROID_M);
        init();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        articlesPresenter.onStop();
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
        //设置分类名称
        setClassifyName();
        //设置文章列表
        setArticleRecycler();
        //设置分类文章presenter
        setClssifyArticleListPresenter();
    }

    private void findViewById() {
        back = findViewById(R.id.back);
        articleRecycler = findViewById(R.id.articleRecycler);
        classifyName = findViewById(R.id.classifyName);

    }

    /**
     * 获取intent信息
     */
    private void getIntentInfo() {
        Intent intent = getIntent();
        classifyNameStr = intent.getStringExtra(CLASSIFY_NAME);
        classifyId = intent.getIntExtra(CLASSIFY_ID, 0);

    }

    /**
     * 设置分类名称
     */
    private void setClassifyName() {
        classifyName.setText(classifyNameStr);
    }

    /**
     * 设置监听器
     */
    private void setListener() {
        back.setOnClickListener(this);
    }

    /**
     * 设置文章列表
     */
    private void setArticleRecycler() {
        if (articleList == null) {
            articleList = new ArrayList<>();
        }

        articleAdapter = new ArticleAdapter(articleList, this);
        articleAdapter.setOnItemClickListener(new ArticleAdapter.OnItemClickListener() {

            /**
             * 点击事件
             * @param view
             * @param position
             */
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(ArticleClassifyActivity.this, ArticleDetailActivity.class);
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
        // articleRecycler.setHasFixedSize(true);
        articleRecycler.setAdapter(articleAdapter);

        //RecyclerView滚动监听
        articleRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (recyclerView.computeVerticalScrollExtent() + recyclerView.computeVerticalScrollOffset() >= recyclerView.computeVerticalScrollRange()) {
                    //滑动到底部，继续加载
                    articlesPresenter.getArticleList();
                }

            }

        });
    }

    /**
     * 设置分类文章presenter
     */
    private void setClssifyArticleListPresenter() {
        articlesPresenter = new GetClassifyArticlesPresenter(this);
        articlesPresenter.reset(classifyId);
        articlesPresenter.setBaseUrl(SiteInfo.HOST_URL + SiteInfo.PLANT);
        articlesPresenter.attachUpdate(new Update() {
            @Override
            public void onSuccess(Object object) {
                List<Article> articles = (List<Article>) object;
                articleList.addAll(articles);
                int total = articleList.size();
                int loadSum = articles.size();
                int startPos = total - loadSum;
                articleAdapter.notifyItemRangeInserted(startPos, loadSum);
            }

            @Override
            public void onError(String result) {
                Log.e(TAG, result);
            }
        });
        articlesPresenter.onCreate();
        articlesPresenter.getArticleList();
    }

}
