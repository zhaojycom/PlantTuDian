package com.zhaojy.planttudian.data;

import android.content.Context;

import com.zhaojy.planttudian.bean.Article;
import com.zhaojy.planttudian.bean.ClassifyBean;
import com.zhaojy.planttudian.bean.Collect;
import com.zhaojy.planttudian.bean.Count;
import com.zhaojy.planttudian.bean.History;
import com.zhaojy.planttudian.bean.Plant;
import com.zhaojy.planttudian.bean.User;
import com.zhaojy.planttudian.bean.WanTuBean;

import java.util.List;

import okhttp3.RequestBody;
import rx.Observable;

/**
 * @author: zhaojy
 * @data:On 2018/9/23.
 */

public class DataManager {
    private RetrofitService mRetrofitService;

    /**
     * @param context 上下文
     * @param baseUrl 请求基地址
     */
    public DataManager(Context context, String baseUrl) {
        RetrofitHelper retrofitHelper = new RetrofitHelper(context, baseUrl);
        this.mRetrofitService = retrofitHelper.getServer();
    }

    /**
     * 获取植物分类集合数据
     */
    public Observable<List<ClassifyBean>> getPlantClassifyList() {
        return mRetrofitService.getPlantClassifyList();
    }

    /**
     * 获取轮播图
     */
    public Observable<List<String>> getBannerImgList() {
        return mRetrofitService.getBannerImgList();
    }

    /**
     * 获取玩图列表信息
     *
     * @param requestBody 请求体
     * @return
     */
    public Observable<List<WanTuBean>> getWanTuList(RequestBody requestBody) {
        return mRetrofitService.getWanTuList(requestBody);
    }

    /**
     * 获取所选分类植物信息集合
     *
     * @param requestBody 请求体
     * @return
     */
    public Observable<List<Plant>> getClassifyPlantList(RequestBody requestBody) {
        return mRetrofitService.getClassifyPlantList(requestBody);
    }

    /**
     * 登录
     *
     * @param requestBody 请求体
     * @return
     */
    public Observable<User> login(RequestBody requestBody) {
        return mRetrofitService.login(requestBody);
    }

    /**
     * 根据分类id读取文章
     *
     * @param requestBody 请求体
     * @return
     */
    public Observable<List<Article>> getArticleList(RequestBody requestBody) {
        return mRetrofitService.getArticleList(requestBody);
    }

    /**
     * 搜索植物
     *
     * @param requestBody 请求体
     * @return
     */
    public Observable<List<Plant>> searchPlant(RequestBody requestBody) {
        return mRetrofitService.searchPlant(requestBody);
    }

    /**
     * 搜索文章
     *
     * @param requestBody 请求体
     * @return
     */
    public Observable<List<Article>> searchArticle(RequestBody requestBody) {
        return mRetrofitService.searchArticle(requestBody);
    }

    /**
     * 提交浏览记录
     *
     * @param requestBody 请求体
     * @return
     */
    public Observable<String> recordHistory(RequestBody requestBody) {
        return mRetrofitService.recordHistory(requestBody);
    }

    /**
     * 读取浏览记录
     *
     * @param requestBody 请求体
     * @return
     */
    public Observable<List<History>> readHistory(RequestBody requestBody) {
        return mRetrofitService.readHistory(requestBody);
    }

    /**
     * 根据id获取文章信息
     *
     * @param requestBody 请求体
     * @return
     */
    public Observable<Article> getArticleById(RequestBody requestBody) {
        return mRetrofitService.getArticleById(requestBody);
    }

    /**
     * 根据id获取植物信息
     *
     * @param requestBody 请求体
     * @return
     */
    public Observable<Plant> getPlantById(RequestBody requestBody) {
        return mRetrofitService.getPlantById(requestBody);
    }

    /**
     * 获取收藏信息
     *
     * @param requestBody 请求体
     * @return
     */
    public Observable<Collect> getCollectInfo(RequestBody requestBody) {
        return mRetrofitService.getCollectInfo(requestBody);
    }

    /**
     * 收藏
     *
     * @param requestBody 请求体
     * @return
     */
    public Observable<Collect> collect(RequestBody requestBody) {
        return mRetrofitService.collect(requestBody);
    }

    /**
     * 取消收藏
     *
     * @param requestBody 请求体
     * @return
     */
    public Observable<Collect> cancelCollect(RequestBody requestBody) {
        return mRetrofitService.cancelCollect(requestBody);
    }

    /**
     * 读取收藏
     *
     * @param requestBody 请求体
     * @return
     */
    public Observable<List<Collect>> readCollect(RequestBody requestBody) {
        return mRetrofitService.readCollect(requestBody);
    }

    /**
     * 获取用户收藏总数
     *
     * @param requestBody 请求体
     * @return
     */
    public Observable<Count> getCollectSum(RequestBody requestBody) {
        return mRetrofitService.getCollectSum(requestBody);
    }

    /**
     * 获取用户浏览历史总数
     *
     * @param requestBody 请求体
     * @return
     */
    public Observable<Count> getHistorySum(RequestBody requestBody) {
        return mRetrofitService.getHistorySum(requestBody);
    }

}
