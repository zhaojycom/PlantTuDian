package com.zhaojy.planttudian.data.user;

import android.content.Context;
import android.content.Intent;

import com.zhaojy.planttudian.bean.Count;
import com.zhaojy.planttudian.bean.User;
import com.zhaojy.planttudian.constant.Strings;
import com.zhaojy.planttudian.data.BaseUpdate;
import com.zhaojy.planttudian.data.DataManager;
import com.zhaojy.planttudian.data.Presenter;
import com.zhaojy.planttudian.data.Update;

import okhttp3.RequestBody;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * @author: zhaojy
 * @data:On 2018/10/11.
 */

public class GetCollectCountPresenter implements Presenter {
    private final String TAG = GetCollectCountPresenter.class.getSimpleName();

    private DataManager manager;
    private CompositeSubscription mCompositeSubscription;
    private Context mContext;
    private Update update;
    private String baseUrl;
    private Count count;


    public GetCollectCountPresenter(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public void onCreate() {
        manager = new DataManager(mContext, baseUrl);
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
     * 获取用户收藏总数
     */
    public void getCollectSum() {
        if (User.getInstance().getPhone() == null) {
            return;
        }
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse(
                Strings.MEDIATYPE_JSON), User.getInstance().getPhone());

        mCompositeSubscription.add(manager.getCollectSum(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Count>() {
                    @Override
                    public void onCompleted() {
                        if (count != null) {
                            update.onSuccess(count);
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e != null) {
                            update.onError(e.getMessage());
                        }
                    }

                    @Override
                    public void onNext(Count count1) {
                        count = count1;
                    }
                })
        );
    }

}
