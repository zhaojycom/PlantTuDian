package com.zhaojy.planttudian.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.youth.banner.Banner;
import com.youth.banner.Transformer;
import com.youth.banner.loader.ImageLoader;
import com.zhaojy.planttudian.R;
import com.zhaojy.planttudian.adapter.ArticleAdapter;
import com.zhaojy.planttudian.bean.Article;
import com.zhaojy.planttudian.constant.SiteInfo;
import com.zhaojy.planttudian.constant.Strings;
import com.zhaojy.planttudian.data.Update;
import com.zhaojy.planttudian.data.plant.GetClassifyArticlesPresenter;
import com.zhaojy.planttudian.data.system.GetBannerImgPresenter;
import com.zhaojy.planttudian.ui.activity.ArticleActivity;
import com.zhaojy.planttudian.ui.activity.ArticleDetailActivity;
import com.zhaojy.planttudian.ui.activity.SearchActivity;
import com.zhaojy.planttudian.ui.activity.WanTuActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: zhaojy
 * @data:On 2018/9/15.
 */

public class MainFragment extends BaseFragment implements View.OnClickListener {
    private final static String TAG = MainFragment.class.getSimpleName();
    private View root;
    private Banner banner;
    private List<String> images;
    private RecyclerView articleRecycler;
    private RelativeLayout search;
    private RelativeLayout wantu;
    private RelativeLayout article;
    private ArticleAdapter articleAdapter;
    private List<Article> articleList;
    /**
     * 获取分类文章presenter
     */
    private GetClassifyArticlesPresenter articlesPresenter;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search:
                Intent searchIntent = new Intent(getActivity(), SearchActivity.class);
                startActivity(searchIntent);
                break;
            case R.id.wantu:
                Intent wantuIntent = new Intent(getActivity(), WanTuActivity.class);
                startActivity(wantuIntent);
                break;
            case R.id.article:
                Intent articleIntent = new Intent(getActivity(), ArticleActivity.class);
                startActivity(articleIntent);
                break;
            default:
                break;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        if (root == null) {
            root = inflater.inflate(R.layout.mainpage, container, false);
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
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        images.clear();
        articleList.clear();
        articleAdapter.notifyDataSetChanged();
    }

    private void init() {
        findViewById();
        //设置文章列表
        setArticleRecycler();
        //获取轮播图图片地址集合
        getBannerImgList();
        //设置分类文章presenter
        setClssifyArticleListPresenter();
        //设置监听器
        setListener();
    }

    private void findViewById() {

        banner = root.findViewById(R.id.banner);
        articleRecycler = root.findViewById(R.id.articleRecycler);
        search = root.findViewById(R.id.search);
        wantu = root.findViewById(R.id.wantu);
        article = root.findViewById(R.id.article);
    }

    /**
     * 设置轮播图
     */
    private void setBanner() {

        banner.setImages(images).setImageLoader(new ImageLoader() {
            @Override
            public void displayImage(Context context, Object path, ImageView imageView) {
                String url = (String) path;
                Glide.with(context)
                        .load(url)
                        .into(imageView);
            }
        });
        //设置轮播时间
        banner.setDelayTime(6000);
        banner.setBannerAnimation(Transformer.DepthPage);
        banner.start();
    }

    /**
     * 设置文章列表
     */
    @SuppressLint("ClickableViewAccessibility")
    private void setArticleRecycler() {
        if (articleList == null) {
            articleList = new ArrayList<>();
        }

        articleAdapter = new ArticleAdapter(articleList, this.getActivity());
        articleAdapter.setOnItemClickListener(new ArticleAdapter.OnItemClickListener() {

            /**
             * 点击事件
             * @param view
             * @param position
             */
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(getActivity(), ArticleDetailActivity.class);
                intent.putExtra(ArticleDetailActivity.TITLE,
                        articleList.get(position).getTitle());
                intent.putExtra(ArticleDetailActivity.ARTICLE_URL,
                        articleList.get(position).getArticleUrl());
                intent.putExtra(ArticleDetailActivity.ID,
                        articleList.get(position).getId());
                startActivity(intent);
            }

        });

        articleRecycler.setLayoutManager(new LinearLayoutManager(getActivity()
                , LinearLayoutManager.VERTICAL, false));
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
     * 设置监听器
     */
    private void setListener() {
        search.setOnClickListener(this);
        wantu.setOnClickListener(this);
        article.setOnClickListener(this);
    }

    /**
     * 获取轮播图图片地址集合
     */
    private void getBannerImgList() {
        GetBannerImgPresenter gbip = new GetBannerImgPresenter(getActivity());
        gbip.setBaseUrl(SiteInfo.HOST_URL + SiteInfo.SYSTEM);
        gbip.attachUpdate(new Update() {
            @Override
            public void onSuccess(Object object) {
                images = (List<String>) object;
                //设置轮播图
                setBanner();
            }

            @Override
            public void onError(String result) {

            }
        });
        gbip.onCreate();
        gbip.getBannerImgList();
    }

    /**
     * 设置分类文章presenter
     */
    private void setClssifyArticleListPresenter() {
        articlesPresenter = new GetClassifyArticlesPresenter(getActivity());
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

                if (loadSum == 0) {
                    //没有更多了
                    Toast.makeText(getActivity(), Strings.NO_NORE, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String result) {
                Log.e(TAG, result);
            }
        });
        articlesPresenter.onCreate();
        articlesPresenter.getArticleList();
    }

    @Override
    protected void lazyLoad() {

    }
}