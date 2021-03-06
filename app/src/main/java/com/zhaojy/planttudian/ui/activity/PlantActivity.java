package com.zhaojy.planttudian.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.zhaojy.planttudian.R;
import com.zhaojy.planttudian.bean.Collect;
import com.zhaojy.planttudian.bean.CollectionRequestParams;
import com.zhaojy.planttudian.bean.HistoryRequestParams;
import com.zhaojy.planttudian.bean.User;
import com.zhaojy.planttudian.constant.SiteInfo;
import com.zhaojy.planttudian.constant.Strings;
import com.zhaojy.planttudian.data.Update;
import com.zhaojy.planttudian.data.plant.CancelCollectPresenter;
import com.zhaojy.planttudian.data.plant.CollectPresenter;
import com.zhaojy.planttudian.data.plant.GetCollectInfoPresenter;
import com.zhaojy.planttudian.data.plant.RecordHistoryPresenter;
import com.zhaojy.planttudian.helper.StatusBarHelper;


/**
 * @author: zhaojy
 * @data:On 2018/9/28.
 */

public class PlantActivity extends BaseActivity implements View.OnClickListener {
    private final static String TAG = PlantActivity.class.getSimpleName();
    /**
     * 植物网页地址
     */
    public final static String PLANT_URL = "plantUrl";
    public final static String PLANT_NAME = "plantName";
    public final static String ID = "id";

    private ImageView back;
    private WebView webView;
    private TextView plantName;
    private ImageView collect;
    private ProgressBar progressBar;
    private String plantUrl;
    private String plantNameStr;
    private int id;
    private Collect collectInfo;
    /**
     * 记录浏览历史presenter
     */
    private RecordHistoryPresenter historyPresenter;
    /**
     * 获取收藏信息胖presenter
     */
    private GetCollectInfoPresenter collectInfoPresenter;
    /**
     * 收藏presenter
     */
    private CollectPresenter collectPresenter;
    /**
     * 取消收藏presenter
     */
    private CancelCollectPresenter cancelCollectPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.plant_layout);
        //设置状态栏字体颜色为深色
        StatusBarHelper.statusBarLightMode(this, StatusBarHelper.ANDROID_M);
        init();

    }

    @Override
    protected void onDestroy() {
        if (webView != null) {
            // 如果先调用destroy()方法，则会命中if (isDestroyed()) return;这一行代码，需要先onDetachedFromWindow()，再
            // destory()
            ViewParent parent = webView.getParent();
            if (parent != null) {
                ((ViewGroup) parent).removeView(webView);
            }

            webView.stopLoading();
            // 退出时调用此方法，移除绑定的服务，否则某些特定系统会报错
            webView.getSettings().setJavaScriptEnabled(false);
            webView.clearHistory();
            webView.clearView();
            webView.removeAllViews();
            webView.destroy();
        }
        super.onDestroy();
        if (historyPresenter != null) {
            historyPresenter.onStop();
        }
        if (collectInfoPresenter != null) {
            collectInfoPresenter.onStop();
        }
        if (collectPresenter != null) {
            collectPresenter.onStop();
        }
        if (cancelCollectPresenter != null) {
            cancelCollectPresenter.onStop();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                this.finish();
                break;
            case R.id.collect:
                if (collectInfo == null) {
                    //收藏
                    collect();
                } else {
                    //取消收藏
                    cancelCollect();
                }
                break;
            default:
                break;
        }
    }

    private void init() {
        findViewById();
        //获取网址
        getIntentInfo();
        //设置监听器
        setListener();
        //设置webview
        setWebView();
        //设置植物名称
        setPlantName();
        //记录历史
        recordHistory();
        //获取收藏信息
        getCollectInfo();
    }

    private void findViewById() {
        back = findViewById(R.id.back);
        webView = findViewById(R.id.webView);
        plantName = findViewById(R.id.plantName);
        collect = findViewById(R.id.collect);
        progressBar = findViewById(R.id.progressBar);
    }

    /**
     * 获取intent 信息
     */
    private void getIntentInfo() {
        Intent intent = getIntent();
        plantUrl = intent.getStringExtra(PLANT_URL);
        plantNameStr = intent.getStringExtra(PLANT_NAME);
        id = intent.getIntExtra(ID, 0);
    }

    /**
     * 记录历史
     */
    private void recordHistory() {
        if (User.getInstance().getPhone() == null) {
            return;
        }
        historyPresenter = new RecordHistoryPresenter(this);
        historyPresenter.setBaseUrl(SiteInfo.HOST_URL + SiteInfo.PLANT);
        historyPresenter.onCreate();
        HistoryRequestParams requestParams = new HistoryRequestParams();
        requestParams.setBrowseSort(Strings.PLANT_SORT);
        requestParams.setUserPhone(User.getInstance().getPhone());
        requestParams.setBrowseId(id);
        historyPresenter.recordHistory(requestParams);
    }

    /**
     * 获取收藏信息
     */
    private void getCollectInfo() {
        if (User.getInstance().getPhone() == null) {
            return;
        }
        collectInfoPresenter = new GetCollectInfoPresenter(this);
        collectInfoPresenter.setBaseUrl(SiteInfo.HOST_URL + SiteInfo.PLANT);
        collectInfoPresenter.attachUpdate(new Update() {
            @Override
            public void onSuccess(Object object) {
                collectInfo = (Collect) object;
                if (collectInfo != null) {
                    //如果该文章已收藏，则显示已收藏图标
                    collect.setImageResource(R.mipmap.collected);
                } else {
                    //如果该文章未收藏，则显示未收藏图标
                    collect.setImageResource(R.mipmap.collect);
                }
            }

            @Override
            public void onError(String result) {
                Log.e(TAG, result);
            }
        });
        collectInfoPresenter.onCreate();
        CollectionRequestParams requestParams = new CollectionRequestParams();
        requestParams.setUserPhone(User.getInstance().getPhone());
        requestParams.setCollectSort(Strings.PLANT_SORT);
        requestParams.setCollectId(id);
        collectInfoPresenter.getCollectInfo(requestParams);
    }

    /**
     * 设置webview
     */
    @SuppressLint("SetJavaScriptEnabled")
    private void setWebView() {
        WebSettings webSettings = webView.getSettings();
        //设置可支持js
        webSettings.setJavaScriptEnabled(true);
        //设置可在大视野范围内上下左右拖动，并且可以任意比例缩放
        webSettings.setUseWideViewPort(true);
        // 设置可以支持缩放
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        //不显示webview缩放按钮
        webSettings.setDisplayZoomControls(false);
        //设置默认加载的可视范围是大视野范围
        webSettings.setLoadWithOverviewMode(true);
        //自适应屏幕
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);

                return false;
            }

        });

        webView.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView view, int progress) {

                if (progress == 100) {
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
        webView.loadUrl(plantUrl);
    }

    /**
     * 设置植物名称
     */
    private void setPlantName() {
        if (plantNameStr.length() > 10) {
            plantName.setText(plantNameStr.substring(0, 10) + "...");
        } else {
            plantName.setText(plantNameStr);
        }

    }

    /**
     * 收藏
     */
    private void collect() {
        collectPresenter = new CollectPresenter(this);
        collectPresenter.setBaseUrl(SiteInfo.HOST_URL + SiteInfo.PLANT);
        collectPresenter.attachUpdate(new Update() {
            @Override
            public void onSuccess(Object object) {
                collectInfo = (Collect) object;
                if (collectInfo == null) {
                    Toast.makeText(PlantActivity.this, Strings.COLLECT_FAILURE,
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(PlantActivity.this, Strings.COLLECT_SUCCESS,
                            Toast.LENGTH_SHORT).show();
                    collect.setImageResource(R.mipmap.collected);
                }

            }

            @Override
            public void onError(String result) {
                Toast.makeText(PlantActivity.this, Strings.COLLECT_FAILURE,
                        Toast.LENGTH_SHORT).show();
                Log.e(TAG, result);
            }
        });
        collectPresenter.onCreate();
        CollectionRequestParams requestParams = new CollectionRequestParams();
        requestParams.setUserPhone(User.getInstance().getPhone());
        requestParams.setCollectSort(Strings.PLANT_SORT);
        requestParams.setCollectId(id);
        collectPresenter.collect(requestParams);
    }

    /**
     * 取消收藏
     */
    private void cancelCollect() {
        cancelCollectPresenter = new CancelCollectPresenter(this);
        cancelCollectPresenter.setBaseUrl(SiteInfo.HOST_URL + SiteInfo.PLANT);
        cancelCollectPresenter.attachUpdate(new Update() {
            @Override
            public void onSuccess(Object object) {
                Toast.makeText(PlantActivity.this, Strings.CANCEL_COLLECT_SUCCESS,
                        Toast.LENGTH_SHORT).show();
                collect.setImageResource(R.mipmap.collect);
                collectInfo = null;
            }

            @Override
            public void onError(String result) {
                Toast.makeText(PlantActivity.this, Strings.CANCEL_COLLECT_FAILURE,
                        Toast.LENGTH_SHORT).show();
                Log.e(TAG, result);
            }
        });
        cancelCollectPresenter.onCreate();
        cancelCollectPresenter.cancelCollect(collectInfo.getId());
    }

    /**
     * 设置监听器
     */
    private void setListener() {
        back.setOnClickListener(this);
        collect.setOnClickListener(this);
    }

}
