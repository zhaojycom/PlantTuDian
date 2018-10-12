package com.zhaojy.planttudian.data;

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
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import rx.Observable;

/**
 * @author: zhaojy
 * @data:On 2018/2/26.
 */

public interface RetrofitService {
    /**
     * 获取植物分类集合数据
     *
     * @return
     */
    @Headers({"Content-type:application/json;charset=UTF-8"})
    @POST("getPlantClassify")
    Observable<List<ClassifyBean>> getPlantClassifyList();

    /**
     * 获取轮播图
     *
     * @return
     */
    @Headers({"Content-type:application/json;charset=UTF-8"})
    @POST("getBannerImg")
    Observable<List<String>> getBannerImgList();

    /**
     * 获取轮播图
     *
     * @param requestBody
     * @return
     */
    @Headers({"Content-type:application/json;charset=UTF-8"})
    @POST("getWanTuList")
    Observable<List<WanTuBean>> getWanTuList(@Body RequestBody requestBody);

    /**
     * 获取轮播图
     *
     * @param requestBody
     * @return
     */
    @Headers({"Content-type:application/json;charset=UTF-8"})
    @POST("getClassifyPlantList")
    Observable<List<Plant>> getClassifyPlantList(@Body RequestBody requestBody);

    /**
     * 登录
     *
     * @param requestBody
     * @return
     */
    @Headers({"Content-type:application/json;charset=UTF-8"})
    @POST("login")
    Observable<User> login(@Body RequestBody requestBody);

    /**
     * 根据分类id读取文章
     *
     * @param requestBody
     * @return
     */
    @Headers({"Content-type:application/json;charset=UTF-8"})
    @POST("getArticleList")
    Observable<List<Article>> getArticleList(@Body RequestBody requestBody);

    /**
     * 搜索植物
     *
     * @param requestBody
     * @return
     */
    @Headers({"Content-type:application/json;charset=UTF-8"})
    @POST("searchPlant")
    Observable<List<Plant>> searchPlant(@Body RequestBody requestBody);

    /**
     * 搜索文章
     *
     * @param requestBody
     * @return
     */
    @Headers({"Content-type:application/json;charset=UTF-8"})
    @POST("searchArticle")
    Observable<List<Article>> searchArticle(@Body RequestBody requestBody);

    /**
     * 提交浏览记录
     *
     * @param requestBody
     * @return
     */
    @Headers({"Content-type:application/json;charset=UTF-8"})
    @POST("recordBrowseHistory")
    Observable<String> recordHistory(@Body RequestBody requestBody);

    /**
     * 读取浏览记录
     *
     * @param requestBody
     * @return
     */
    @Headers({"Content-type:application/json;charset=UTF-8"})
    @POST("readBrowseHistory")
    Observable<List<History>> readHistory(@Body RequestBody requestBody);

    /**
     * 根据id获取文章信息
     *
     * @param requestBody
     * @return
     */
    @Headers({"Content-type:application/json;charset=UTF-8"})
    @POST("getArticleById")
    Observable<Article> getArticleById(@Body RequestBody requestBody);

    /**
     * 根据id获取植物信息
     *
     * @param requestBody
     * @return
     */
    @Headers({"Content-type:application/json;charset=UTF-8"})
    @POST("getPlantById")
    Observable<Plant> getPlantById(@Body RequestBody requestBody);

    /**
     * 获取收藏信息
     *
     * @param requestBody
     * @return
     */
    @Headers({"Content-type:application/json;charset=UTF-8"})
    @POST("getCollectInfo")
    Observable<Collect> getCollectInfo(@Body RequestBody requestBody);

    /**
     * 收藏
     *
     * @param requestBody
     * @return
     */
    @Headers({"Content-type:application/json;charset=UTF-8"})
    @POST("collect")
    Observable<Collect> collect(@Body RequestBody requestBody);

    /**
     * 取消收藏
     *
     * @param requestBody
     * @return
     */
    @Headers({"Content-type:application/json;charset=UTF-8"})
    @POST("cancelCollect")
    Observable<Collect> cancelCollect(@Body RequestBody requestBody);

    /**
     * 读取收藏
     *
     * @param requestBody
     * @return
     */
    @Headers({"Content-type:application/json;charset=UTF-8"})
    @POST("readCollection")
    Observable<List<Collect>> readCollect(@Body RequestBody requestBody);

    /**
     * 获取用户收藏总数
     *
     * @param requestBody
     * @return
     */
    @Headers({"Content-type:application/json;charset=UTF-8"})
    @POST("getCollectSum")
    Observable<Count> getCollectSum(@Body RequestBody requestBody);

    /**
     * 获取用户浏览历史总数
     *
     * @param requestBody
     * @return
     */
    @Headers({"Content-type:application/json;charset=UTF-8"})
    @POST("getHistorySum")
    Observable<Count> getHistorySum(@Body RequestBody requestBody);

}
