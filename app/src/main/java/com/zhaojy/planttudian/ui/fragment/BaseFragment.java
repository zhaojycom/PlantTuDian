package com.zhaojy.planttudian.ui.fragment;

import android.support.v4.app.Fragment;

/**
 * @author: zhaojy
 * @data:On 2018/9/16.
 */

public abstract class BaseFragment extends Fragment {
    /**
     * Fragment当前状态是否可见
     */
    protected boolean isVisible;

    /**
     * 是否已加载
     */
    protected boolean loaded;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()) {
            isVisible = true;
            onVisible();
        } else {
            isVisible = false;
            onInvisible();
        }
    }

    /**
     * 可见
     */
    protected void onVisible() {
        if (!loaded) {
            lazyLoad();
        }
        loaded = true;
    }

    /**
     * 不可见
     */
    protected void onInvisible() {
    }

    /**
     * 延迟加载
     * 子类必须重写此方法
     */
    protected abstract void lazyLoad();
}
