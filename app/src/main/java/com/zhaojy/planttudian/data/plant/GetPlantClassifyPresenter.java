package com.zhaojy.planttudian.data.plant;

import android.content.Context;
import android.content.Intent;

import com.zhaojy.planttudian.bean.ClassifyBean;
import com.zhaojy.planttudian.data.BaseUpdate;
import com.zhaojy.planttudian.data.DataManager;
import com.zhaojy.planttudian.data.Presenter;
import com.zhaojy.planttudian.data.Update;

import java.util.List;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * 获取植物分类presenter
 *
 * @author: zhaojy
 * @data:On 2018/9/23.
 */

public class GetPlantClassifyPresenter implements Presenter {
    private final String TAG = GetPlantClassifyPresenter.class.getSimpleName();

    private DataManager manager;
    private CompositeSubscription mCompositeSubscription;
    private Context mContext;
    private Update update;
    private String baseUrl;
    private List<ClassifyBean> cbList;

    public GetPlantClassifyPresenter(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public void onCreate() {
        manager = new DataManager(mContext,baseUrl);
        mCompositeSubscription = new CompositeSubscription();
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
     * 获取植物分类集合数据
     */
    public void getPlantClassifyList() {

        mCompositeSubscription.add(manager.getPlantClassifyList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<ClassifyBean>>() {
                    @Override
                    public void onCompleted() {
                        if (cbList != null) {
                            update.onSuccess(cbList);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e != null) {
                            update.onError(e.getMessage());
                        }
                    }

                    @Override
                    public void onNext(List<ClassifyBean> classifyBeanList) {
                        cbList = classifyBeanList;
                    }
                })
        );
    }

}
