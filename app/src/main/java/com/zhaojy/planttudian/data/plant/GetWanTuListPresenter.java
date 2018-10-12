package com.zhaojy.planttudian.data.plant;

import android.content.Context;
import android.content.Intent;

import com.google.gson.Gson;
import com.zhaojy.planttudian.bean.RequestParams;
import com.zhaojy.planttudian.bean.WanTuBean;
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
 * @data:On 2018/9/25.
 */

public class GetWanTuListPresenter implements Presenter {
    private final String TAG = GetWanTuListPresenter.class.getSimpleName();
    private DataManager manager;
    private CompositeSubscription mCompositeSubscription;
    private Context mContext;
    private Update update;
    private String baseUrl;
    private List<WanTuBean> wanTuBeanList;
    /**
     * 每次读取数据条数
     */
    private final static int LIMIT = 2;
    /**
     * 初次读取数据条数
     */
    private final static int FIRST_LIMIT = 8;
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
    private RequestParams requestParams;

    /**
     * 是否正在加载数据
     */
    private boolean isLoading = false;

    public GetWanTuListPresenter(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public void onCreate() {
        manager = new DataManager(mContext, baseUrl);
        mCompositeSubscription = new CompositeSubscription();

        requestParams = new RequestParams();
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
     * 获取玩图列表信息
     */
    public synchronized void getWanTuList() {
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
        firstLoad = false;
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse(
                Strings.MEDIATYPE_JSON)
                , new Gson().toJson(requestParams));

        mCompositeSubscription.add(manager.getWanTuList(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<WanTuBean>>() {
                    @Override
                    public void onCompleted() {
                        if (wanTuBeanList != null) {
                            update.onSuccess(wanTuBeanList);
                            //增加偏移量
                            offset += wanTuBeanList.size();
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
                    public void onNext(List<WanTuBean> wanTuBeans) {
                        wanTuBeanList = wanTuBeans;
                    }
                })
        );
    }

}
