package com.zhaojy.planttudian.data.user;

import android.util.Log;

import com.zhaojy.planttudian.bean.User;
import com.zhaojy.planttudian.constant.SiteInfo;
import com.zhaojy.planttudian.constant.Strings;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 上传照片
 *
 * @author: zhaojy
 * @data:On 2018/10/8.
 */

public class UploadImages {
    private final static String TAG = UploadImages.class.getSimpleName();
    private static User user = User.getInstance();

    private static UploadListener uploadListener = null;

    public static void setUploadListener(UploadListener uploadListener) {
        UploadImages.uploadListener = uploadListener;
    }

    /**
     * 上传头像
     *
     * @param path
     */
    public static void uploadAvatar(String path) {
        OkHttpClient mOkHttpClent = new OkHttpClient();
        File file = new File(path);
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(Strings.USER_PHONE, user.getPhone())
                .addFormDataPart("img", "HeadPortrait.jpg",
                        RequestBody.create(MediaType.parse(Strings.MEDIATYPE_IMAGE), file));

        RequestBody requestBody = builder.build();

        String url = SiteInfo.HOST_URL + SiteInfo.USER + SiteInfo.UPLOAD_AVATAR;
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        Call call = mOkHttpClent.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, e.getMessage());
                uploadListener.onFailure(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String body = response.body().string();
                uploadListener.onResponse(body);
            }

        });

    }

    /**
     * 上传背景
     *
     * @param path
     */
    public static void uploadBanner(String path) {
        OkHttpClient mOkHttpClent = new OkHttpClient();
        File file = new File(path);
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(Strings.USER_PHONE, user.getPhone())
                .addFormDataPart("img", "HeadPortrait.jpg",
                        RequestBody.create(MediaType.parse(Strings.MEDIATYPE_IMAGE), file));

        RequestBody requestBody = builder.build();

        String url = SiteInfo.HOST_URL + SiteInfo.USER + SiteInfo.UPLOAD_BANNER;
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        Call call = mOkHttpClent.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, e.getMessage());
                uploadListener.onFailure(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String body = response.body().string();
                uploadListener.onResponse(body);
            }

        });

    }

    /**
     * 上传监听接口
     */
    public interface UploadListener {
        void onFailure(String errorInfo);

        void onResponse(String body);
    }

}
