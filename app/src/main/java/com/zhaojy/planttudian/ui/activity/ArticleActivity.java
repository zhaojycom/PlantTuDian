package com.zhaojy.planttudian.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.zhaojy.planttudian.R;
import com.zhaojy.planttudian.adapter.ArticleAdapter;
import com.zhaojy.planttudian.bean.Article;
import com.zhaojy.planttudian.constant.SiteInfo;
import com.zhaojy.planttudian.constant.Strings;
import com.zhaojy.planttudian.data.Update;
import com.zhaojy.planttudian.data.plant.GetClassifyArticlesPresenter;
import com.zhaojy.planttudian.helper.StatusBarHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: zhaojy
 * @data:On 2018/10/7.
 */

public class ArticleActivity extends BaseActivity implements View.OnClickListener {
    private final static String TAG = ArticleActivity.class.getSimpleName();
    private ImageView back;
    private RecyclerView articleRecycler;
    private ArticleAdapter articleAdapter;
    private List<Article> articleList;
    /**
     * 获取分类文章presenter
     */
    private GetClassifyArticlesPresenter articlesPresenter;
    private LinearLayout recommend;
    private LinearLayout zhiwuzhongzhi;
    private LinearLayout quweizhiwu;
    private LinearLayout zhiwuzixun;
    private LinearLayout zhiwuzhishi;

    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.article_layout);
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
            case R.id.recommend:
                intent.putExtra(ArticleClassifyActivity.CLASSIFY_NAME,
                        getResources().getString(R.string.recommendArticle));
                intent.putExtra(ArticleClassifyActivity.CLASSIFY_ID,
                        Strings.RECOMMECND_ARTICLE);
                startActivity(intent);
                break;
            case R.id.zhiwuzhongzhi:
                intent.putExtra(ArticleClassifyActivity.CLASSIFY_NAME,
                        getResources().getString(R.string.zhiwuzhongzhi));
                intent.putExtra(ArticleClassifyActivity.CLASSIFY_ID,
                        Strings.ZHIWUZHONGZHI);
                startActivity(intent);
                break;
            case R.id.quweizhiwu:
                intent.putExtra(ArticleClassifyActivity.CLASSIFY_NAME,
                        getResources().getString(R.string.quweizhiwu));
                intent.putExtra(ArticleClassifyActivity.CLASSIFY_ID,
                        Strings.QUWEIZHIWU);
                startActivity(intent);
                break;
            case R.id.zhiwuzixun:
                intent.putExtra(ArticleClassifyActivity.CLASSIFY_NAME,
                        getResources().getString(R.string.zhiwuzixun));
                intent.putExtra(ArticleClassifyActivity.CLASSIFY_ID,
                        Strings.ZHIWUZIXUN);
                startActivity(intent);
                break;
            case R.id.zhiwuzhishi:
                intent.putExtra(ArticleClassifyActivity.CLASSIFY_NAME,
                        getResources().getString(R.string.zhiwuzhishi));
                intent.putExtra(ArticleClassifyActivity.CLASSIFY_ID,
                        Strings.ZHIWUZHISHI);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    private void init() {
        intent = new Intent(this, ArticleClassifyActivity.class);
        findViewById();
        //设置监听器
        setListener();
        //设置文章列表
        setArticleRecycler();
        //设置分类文章presenter
        setClssifyArticleListPresenter();
    }

    private void findViewById() {
        back = findViewById(R.id.back);
        articleRecycler = findViewById(R.id.articleRecycler);
        recommend = findViewById(R.id.recommend);
        zhiwuzhongzhi = findViewById(R.id.zhiwuzhongzhi);
        quweizhiwu = findViewById(R.id.quweizhiwu);
        zhiwuzixun = findViewById(R.id.zhiwuzixun);
        zhiwuzhishi = findViewById(R.id.zhiwuzhishi);
    }

    /**
     * 设置监听器
     */
    private void setListener() {
        back.setOnClickListener(this);
        recommend.setOnClickListener(this);
        zhiwuzhongzhi.setOnClickListener(this);
        quweizhiwu.setOnClickListener(this);
        zhiwuzixun.setOnClickListener(this);
        zhiwuzhishi.setOnClickListener(this);
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
                Intent intent = new Intent(ArticleActivity.this, ArticleDetailActivity.class);
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
        articlesPresenter.reset(Strings.RECOMMECND_ARTICLE);
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
