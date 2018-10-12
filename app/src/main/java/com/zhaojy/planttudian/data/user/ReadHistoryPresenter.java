package com.zhaojy.planttudian.data.user;

import android.content.Context;
import android.content.Intent;

import com.google.gson.Gson;
import com.zhaojy.planttudian.bean.History;
import com.zhaojy.planttudian.bean.ReadHistoryCollectionRequestParams;
import com.zhaojy.planttudian.bean.User;
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
 * @data:On 2018/10/11.
 */

public class ReadHistoryPresenter implements Presenter {
    private final String TAG = ReadHistoryPresenter.class.getSimpleName();
    private DataManager manager;
    private CompositeSubscription mCompositeSubscription;
    private Context mContext;
    private Update update;
    private String baseUrl;
    private List<History> historyList;
    /**
     * 每次读取数据条数
     */
    private final static int LIMIT = 4;
    /**
     * 初次读取数据条数
     */
    private final static int FIRST_LIMIT = 12;
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
    private ReadHistoryCollectionRequestParams requestParams;

    /**
     * 是否正在加载数据
     */
    private boolean isLoading = false;

    public ReadHistoryPresenter(Context mContext) {
        this.mContext = mContext;
    }

    public void reset() {
        firstLoad = true;
        offset = 0;
    }

    @Override
    public void onCreate() {
        manager = new DataManager(mContext, baseUrl);
        mCompositeSubscription = new CompositeSubscription();

        requestParams = new ReadHistoryCollectionRequestParams();
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
     * 读取浏览历史
     */
    public synchronized void readHistory() {
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
        requestParams.setUserPhone(User.getInstance().getPhone());
        firstLoad = false;
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse(
                Strings.MEDIATYPE_JSON)
                , new Gson().toJson(requestParams));

        mCompositeSubscription.add(manager.readHistory(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<History>>() {
                    @Override
                    public void onCompleted() {
                        if (historyList != null) {
                            update.onSuccess(historyList);
                            //增加偏移量
                            offset += historyList.size();
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
                    public void onNext(List<History> histories) {
                        historyList = histories;
                    }
                })
        );
    }

}

