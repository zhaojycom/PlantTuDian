package com.zhaojy.planttudian.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zhaojy.planttudian.R;
import com.zhaojy.planttudian.adapter.CollectAdapter;
import com.zhaojy.planttudian.bean.Collect;
import com.zhaojy.planttudian.bean.User;
import com.zhaojy.planttudian.constant.SiteInfo;
import com.zhaojy.planttudian.data.Update;
import com.zhaojy.planttudian.data.user.ReadCollectPresenter;
import com.zhaojy.planttudian.utils.SharePreferUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: zhaojy
 * @data:On 2018/9/16.
 */

public class CollectFragment extends BaseFragment {
    private final static String TAG = CollectFragment.class.getSimpleName();
    private View root;
    private SwipeRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    /**
     * 读取收藏presenter
     */
    private ReadCollectPresenter collectPresenter;
    private List<Collect> collectList;
    private CollectAdapter adapter;
    private MyFragment myFragment;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.collect, container, false);

        init();
        return root;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        collectPresenter.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (SharePreferUtils.getString(getActivity(),
                SharePreferUtils.USER_PHONE) == null) {
            if (collectList != null && adapter != null) {
                collectList.clear();
                adapter.notifyDataSetChanged();
            }
        } else if (User.getInstance().isUserChange()) {
            //如果账号发生改变
            init();
        } else {
            if (collectList != null && adapter != null) {
                collectList.clear();
                adapter.notifyDataSetChanged();
                collectPresenter.reset();
                collectPresenter.readCollect();
            }
        }
    }

    private void init() {
        findViewById();
        //设置RecyclerView
        setRecyclerView();
        setRefreshLayout();
        myFragment = (MyFragment) getParentFragment();
    }

    private void findViewById() {
        refreshLayout = root.findViewById(R.id.refreshLayout);
        recyclerView = root.findViewById(R.id.recyclerView);
    }

    /**
     *
     */
    private void setRefreshLayout() {
        refreshLayout.setColorSchemeResources(R.color.theme);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                collectList.clear();
                collectPresenter.reset();
                collectPresenter.readCollect();

                //重新获取收藏总数
                myFragment.getCollectCount();
            }
        });
    }

    /**
     * 设置RecyclerView
     */
    private void setRecyclerView() {
        if (collectList == null) {
            collectList = new ArrayList<>();
        }

        adapter = new CollectAdapter(collectList, this.getActivity());

        //设置布局管理器
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL, false) {
            @Override
            public boolean canScrollVertically() {
                return true;
            }
        };

        //RecyclerView滚动监听
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (recyclerView.computeVerticalScrollExtent() + recyclerView.computeVerticalScrollOffset() >= recyclerView.computeVerticalScrollRange()) {
                    //滑动到底部，继续加载
                    collectPresenter.readCollect();
                }

            }

        });

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
        //设置读取浏览历史presenter
        setHistoryPresenter();
    }

    /**
     * 设置读取收藏presenter
     */
    private void setHistoryPresenter() {
        collectPresenter = new ReadCollectPresenter(getActivity());
        collectPresenter.setBaseUrl(SiteInfo.HOST_URL + SiteInfo.USER);
        collectPresenter.attachUpdate(new Update() {
            @Override
            public void onSuccess(Object object) {
                List<Collect> collects = (List<Collect>) object;

                collectList.addAll(collects);
                int total = collectList.size();
                int loadSum = collects.size();
                int startPos = total - loadSum;
                adapter.notifyItemRangeInserted(startPos, loadSum);

                //停止刷新
                refreshLayout.setRefreshing(false);
            }

            @Override
            public void onError(String result) {
                //停止刷新
                refreshLayout.setRefreshing(false);
                Log.e(TAG, result);
            }
        });
        collectPresenter.onCreate();
        collectPresenter.readCollect();
        refreshLayout.setRefreshing(true);
    }

    @Override
    protected void lazyLoad() {

    }

}
