package com.zhaojy.planttudian.data.plant;

import android.content.Context;
import android.content.Intent;

import com.google.gson.Gson;
import com.zhaojy.planttudian.bean.Article;
import com.zhaojy.planttudian.bean.ClassifyRequestParams;
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
 * 获取分类文章代理类
 *
 * @author: zhaojy
 * @data:On 2018/10/7.
 */

public class GetClassifyArticlesPresenter implements Presenter {
    private final String TAG = GetClassifyArticlesPresenter.class.getSimpleName();

    private DataManager manager;
    private CompositeSubscription mCompositeSubscription;
    private Context mContext;
    private Update update;
    private String baseUrl;
    private List<Article> articleList;

    /**
     * 所选文章分类id
     */
    private int classifyId = 0;
    /**
     * 每次读取数据条数
     */
    private final static int LIMIT = 2;
    /**
     * 初次读取数据条数
     */
    private final static int FIRST_LIMIT = 6;
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
    private ClassifyRequestParams requestParams;

    /**
     * 是否正在加载数据
     */
    private boolean isLoading = false;

    public GetClassifyArticlesPresenter(Context mContext) {
        this.mContext = mContext;
    }

    /**
     * 重置信息
     */
    public void reset(int classifyId) {
        offset = 0;
        this.classifyId = classifyId;
    }


    @Override
    public void onCreate() {
        manager = new DataManager(mContext, baseUrl);
        mCompositeSubscription = new CompositeSubscription();

        requestParams = new ClassifyRequestParams();
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
     * 根据分类id读取文章
     */
    public void getArticleList() {
        if (isLoading) {
            return;
        }
        isLoading = true;
        if (firstLoad) {
            //初次读取
            requestParams.setLimit(FIRST_LIMIT);
        } else {
            requestParams.setLimit(LIMIT);
        }
        requestParams.setOffset(offset);
        requestParams.setClassifyId(classifyId);
        firstLoad = false;
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse(
                Strings.MEDIATYPE_JSON)
                , new Gson().toJson(requestParams));

        mCompositeSubscription.add(manager.getArticleList(body)
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
