package com.zhaojy.planttudian.data.plant;

import android.content.Context;
import android.content.Intent;

import com.google.gson.Gson;
import com.zhaojy.planttudian.bean.Article;
import com.zhaojy.planttudian.bean.KeywordRequestParams;
import com.zhaojy.planttudian.constant.Strings;
import com.zhaojy.planttudian.data.BaseUpdate;
import com.zhaojy.planttudian.data.DataManager;
import com.zhaojy.planttudian.data.Presenter;
import com.zhaojy.planttudian.data.Update;

import java.util.List;

import okhttp3.RequestBody;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * @author: zhaojy
 * @data:On 2018/10/9.
 */

public class SearchArticlePresenter implements Presenter {
    private final String TAG = SearchArticlePresenter.class.getSimpleName();

    private DataManager manager;
    private CompositeSubscription mCompositeSubscription;
    private Context mContext;
    private Update update;
    private String baseUrl;
    private List<Article> articleList;

    /**
     * 每次读取数据条数
     */
    private final static int LIMIT = 2;

    /**
     * 初次读取数据条数
     */
    private int firstLimit = 6;

    /**
     * 读取数据的偏移量
     */
    private int offset = 0;
    /**
     * 是否初次加载
     */
    private boolean firstLoad = true;

    /**
     * 请求参数对象
     */
    private KeywordRequestParams requestParams;

    /**
     * 是否正在加载数据
     */
    private boolean isLoading = false;

    public SearchArticlePresenter(Context mContext) {
        this.mContext = mContext;
    }

    /**
     * 重置信息
     */
    public void reset() {
        offset = 0;
        firstLoad = true;
    }

    public void setFirstLimit(int firstLimit) {
        this.firstLimit = firstLimit;
    }

    @Override
    public void onCreate() {
        manager = new DataManager(mContext, baseUrl);
        mCompositeSubscription = new CompositeSubscription();

        requestParams = new KeywordRequestParams();
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onStop() {
        if (mCompositeSubscription.hasSubscriptions()) {
            mCompositeSubscription.unsubscribe();
        }
    }

    @Override
    public void pause() {

    }

    @Override
    public void attachUpdate(BaseUpdate baseUpdate) {
        update = (Update) baseUpdate;
    }

    @Override
    public void attachIncomingIntent(Intent intent) {
    }

    @Override
    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    /**
     * 搜索文章
     *
     * @param keyword 查询关键词
     */
    public void searchArticle(String keyword) {
        if (isLoading) {
            return;
        }
        isLoading = true;
        if (firstLoad) {
            //初次读取
            requestParams.setLimit(firstLimit);
        } else {
            requestParams.setLimit(LIMIT);
        }
        requestParams.setOffset(offset);
        requestParams.setKeyword(keyword);
        firstLoad = false;
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse(
                Strings.MEDIATYPE_JSON)
                , new Gson().toJson(requestParams));

        mCompositeSubscription.add(manager.searchArticle(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Article>>() {
                    @Override
                    public void onCompleted() {
                        if (articleList != null) {
                            update.onSuccess(articleList);
                            offset += articleList.size();

                        }
                        isLoading = false;
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e != null) {
                            update.onError(e.getMessage());
                        }
                        isLoading = false;
                    }

                    @Override
                    public void onNext(List<Article> articles) {
                        articleList = articles;
                    }
                })
        );
    }

}

