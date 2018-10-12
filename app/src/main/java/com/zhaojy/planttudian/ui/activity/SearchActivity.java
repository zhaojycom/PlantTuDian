package com.zhaojy.planttudian.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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

import java.util.ArrayList;
import java.util.List;

/**
 * @author: zhaojy
 * @data:On 2018/9/20.
 */

public class SearchActivity extends BaseActivity implements View.OnClickListener {
    private final static String TAG = SearchActivity.class.getSimpleName();
    private ImageView back;
    private LinearLayout plantBox;
    private LinearLayout articleBox;
    private GridView plantGridView;
    private RecyclerView articleRecycler;
    private EditText keyword;
    private List<Plant> plantList;
    private SearchPlantAdapter plantAdapter;
    private List<Article> articleList;
    private SearchArticleAdapter articleAdapter;
    private TextView plantFindMore;
    private TextView articleFindMore;
    private ImageView delete;
    private String input = "";
    /**
     * 植物搜索代理类
     */
    private SearchPlantPresenter plantPresenter;
    /**
     * 文章搜索代理类
     */
    private SearchArticlePresenter articlePresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.search_layout);
        //设置状态栏字体颜色为深色
        StatusBarHelper.statusBarLightMode(this, StatusBarHelper.ANDROID_M);
        init();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

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
            case R.id.delete:
                keyword.setText("");
                break;
            case R.id.plantFindMore:
                //搜索更多植物
                Intent plantIntent = new Intent(this, FindMoreActivity.class);
                plantIntent.putExtra(FindMoreActivity.TITLE, Strings.PLANT);
                plantIntent.putExtra(FindMoreActivity.KEYWORD, input);
                startActivity(plantIntent);
                break;
            case R.id.articleFindMore:
                //搜索更多文章
                Intent articleIntent = new Intent(this, FindMoreActivity.class);
                articleIntent.putExtra(FindMoreActivity.TITLE, Strings.ARTICLE);
                articleIntent.putExtra(FindMoreActivity.KEYWORD, input);
                startActivity(articleIntent);
                break;
            default:
                break;
        }
    }

    private void init() {
        findViewById();
        //设置监听器
        setListener();
        //设置搜索按钮监听
        setSearchListener();
        //设置植物GridView
        setPlantGridView();
        //设置文章搜索RecyclerView
        setArticleRecycler();
    }

    private void findViewById() {
        back = findViewById(R.id.back);
        plantGridView = findViewById(R.id.plantGridView);
        articleRecycler = findViewById(R.id.articleRecycler);
        keyword = findViewById(R.id.keyword);
        plantBox = findViewById(R.id.plantBox);
        articleBox = findViewById(R.id.articleBox);
        plantFindMore = findViewById(R.id.plantFindMore);
        articleFindMore = findViewById(R.id.articleFindMore);
        delete = findViewById(R.id.delete);
    }

    /**
     * 设置植物GridView
     */
    private void setPlantGridView() {
        if (plantList == null) {
            plantList = new ArrayList<>();
        }

        plantGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                    long id) {
                Intent intent = new Intent(SearchActivity.this, PlantActivity.class);
                intent.putExtra(PlantActivity.PLANT_URL,
                        plantList.get(position).getArticleUrl());
                intent.putExtra(PlantActivity.PLANT_NAME,
                        plantList.get(position).getPlantName());
                intent.putExtra(PlantActivity.ID,
                        plantList.get(position).getId());
                startActivity(intent);
            }
        });

    }

    /**
     * 重置植物GridView 适配器
     */
    private void resetPlantGridViewAdapter() {
        plantAdapter = new SearchPlantAdapter(
                this, plantList, plantGridView);
        plantAdapter.setKeyword(input);
        plantGridView.setAdapter(plantAdapter);
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
                Intent intent = new Intent(SearchActivity.this, ArticleDetailActivity.class);
                intent.putExtra(ArticleDetailActivity.TITLE,
                        articleList.get(position).getTitle());
                intent.putExtra(ArticleDetailActivity.ARTICLE_URL,
                        articleList.get(position).getArticleUrl());
                startActivity(intent);
            }

        });

        //设置布局管理器
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false) {
            @Override
            public boolean canScrollVertically() {
                //禁止滚动
                return false;
            }
        };

        articleRecycler.setLayoutManager(linearLayoutManager);
        //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        articleRecycler.setHasFixedSize(true);
        articleRecycler.setAdapter(articleAdapter);
    }

    /**
     * 植物搜索
     */
    private void searchPlant() {
        if (plantPresenter == null) {
            plantPresenter = new SearchPlantPresenter(this);
        }
        plantPresenter.reset();
        plantPresenter.setFirstLimit(3);
        plantPresenter.setBaseUrl(SiteInfo.HOST_URL + SiteInfo.PLANT);
        plantPresenter.attachUpdate(new Update() {
            @Override
            public void onSuccess(Object object) {
                List<Plant> plants = (List<Plant>) object;
                plantList.clear();
                plantList.addAll(plants);
                //重置植物GridView 适配器
                resetPlantGridViewAdapter();

                if (plants.size() == 0) {
                    //搜索结果为空隐藏
                    plantBox.setVisibility(View.GONE);
                } else {
                    plantBox.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onError(String result) {

            }
        });
        plantPresenter.onCreate();
        plantPresenter.searchPlant(input);
    }

    /**
     * 搜索文章
     */
    private void searchArticle() {
        if (articlePresenter == null) {
            articlePresenter = new SearchArticlePresenter(this);
        }
        articlePresenter.reset();
        articlePresenter.setFirstLimit(3);
        articlePresenter.setBaseUrl(SiteInfo.HOST_URL + SiteInfo.PLANT);
        articlePresenter.attachUpdate(new Update() {
            @Override
            public void onSuccess(Object object) {
                List<Article> articles = (List<Article>) object;
                articleList.clear();
                articleList.addAll(articles);
                articleAdapter.setKeyword(input);
                articleAdapter.notifyDataSetChanged();

                if (articles.size() == 0) {
                    //搜索结果为空隐藏
                    articleBox.setVisibility(View.GONE);
                } else {
                    articleBox.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onError(String result) {

            }
        });
        articlePresenter.onCreate();
        articlePresenter.searchArticle(input);
    }

    /**
     * 设置监听器
     */
    private void setListener() {
        back.setOnClickListener(this);
        delete.setOnClickListener(this);
        plantFindMore.setOnClickListener(this);
        articleFindMore.setOnClickListener(this);
    }

    /**
     * 设置搜索监听
     */
    private void setSearchListener() {
        keyword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    input = keyword.getText().toString();
                    if (!TextUtils.isEmpty(input)) {
                        //搜索植物
                        searchPlant();
                        //搜索文章
                        searchArticle();
                    } else {
                        Toast.makeText(SearchActivity.this,
                                Strings.INPUT_CONTENT_NO_EMPTY, Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }
                return false;
            }
        });

        keyword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(keyword.getText().toString())) {
                    delete.setVisibility(View.VISIBLE);
                } else {
                    delete.setVisibility(View.INVISIBLE);
                }
            }
        });

    }

}
