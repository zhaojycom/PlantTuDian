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
import com.zhaojy.planttudian.adapter.HistoryAdapter;
import com.zhaojy.planttudian.bean.History;
import com.zhaojy.planttudian.bean.User;
import com.zhaojy.planttudian.constant.SiteInfo;
import com.zhaojy.planttudian.data.Update;
import com.zhaojy.planttudian.data.user.ReadHistoryPresenter;
import com.zhaojy.planttudian.utils.SharePreferUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: zhaojy
 * @data:On 2018/9/16.
 */

public class HistoryFragment extends BaseFragment {
    private final static String TAG = HistoryFragment.class.getSimpleName();
    private View root;
    private SwipeRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    /**
     * 读取浏览历史presenter
     */
    private ReadHistoryPresenter historyPresenter;
    private List<History> historyList;
    private HistoryAdapter adapter;
    private MyFragment myFragment;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        if (root == null) {
            root = inflater.inflate(R.layout.history, container, false);
            init();
        }

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (null != root) {
            ((ViewGroup) root.getParent()).removeView(root);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (historyPresenter != null) {
            historyPresenter.onStop();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (SharePreferUtils.getString(getActivity(),
                SharePreferUtils.USER_PHONE) == null) {
            if (historyList != null && adapter != null) {
                historyList.clear();
                adapter.notifyDataSetChanged();
            }
        } else if (User.getInstance().isUserChange()) {
            //如果账号发生改变
            init();
        } else {
            if (historyList != null && adapter != null) {
                historyList.clear();
                adapter.notifyDataSetChanged();
                historyPresenter.reset();
                historyPresenter.readHistory();
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
     * 设置下拉刷新
     */
    private void setRefreshLayout() {
        refreshLayout.setColorSchemeResources(R.color.theme);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //重新加载数据
                historyList.clear();
                historyPresenter.reset();
                historyPresenter.readHistory();

                //重新获取历史总数
                myFragment.getHistoryCount();
            }
        });
    }

    /**
     * 设置RecyclerView
     */
    private void setRecyclerView() {
        if (historyList == null) {
            historyList = new ArrayList<>();
        }

        adapter = new HistoryAdapter(historyList, this.getActivity());

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
                    historyPresenter.readHistory();
                }

            }

        });

        recyclerView.setLayoutManager(linearLayoutManager);
        //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        // articleRecycler.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        //设置读取浏览历史presenter
        setHistoryPresenter();
    }

    /**
     * 设置读取浏览历史presenter
     */
    private void setHistoryPresenter() {
        historyPresenter = new ReadHistoryPresenter(getActivity());
        historyPresenter.setBaseUrl(SiteInfo.HOST_URL + SiteInfo.USER);
        historyPresenter.attachUpdate(new Update() {
            @Override
            public void onSuccess(Object object) {
                List<History> histories = (List<History>) object;

                historyList.addAll(histories);
                int total = historyList.size();
                int loadSum = histories.size();
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
        historyPresenter.onCreate();
        historyPresenter.readHistory();
        refreshLayout.setRefreshing(true);
    }

    @Override
    protected void lazyLoad() {

    }

}
