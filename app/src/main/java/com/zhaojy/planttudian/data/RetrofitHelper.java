package com.zhaojy.planttudian.data;

import android.content.Context;

import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author: zhaojy
 * @data:On 2018/2/26.
 */

public class RetrofitHelper {
    private Context mCntext;

    private OkHttpClient client = new OkHttpClient();
    private GsonConverterFactory factory = GsonConverterFactory.create(new GsonBuilder().create());
    private RetrofitHelper instance = null;
    private Retrofit mRetrofit = null;

    private String baseUrl;

    public RetrofitHelper(Context mContext, String baseUrl) {
        mCntext = mContext;
        this.baseUrl = baseUrl;
    }

    public void init() {
        resetApp();
    }

    private void resetApp() {
        mRetrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addConverterFactory(factory)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
    }

    public RetrofitService getServer() {
        if (mRetrofit == null) {
            init();
        }
        return mRetrofit.create(RetrofitService.class);
    }

}
