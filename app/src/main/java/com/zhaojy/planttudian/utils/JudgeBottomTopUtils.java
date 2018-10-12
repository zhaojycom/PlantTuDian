package com.zhaojy.planttudian.utils;

import android.view.View;
import android.widget.GridView;

/**
 * 判断滑动控件是否到达顶部或底部
 *
 * @author: zhaojy
 * @data:On 2018/9/25.
 */

public class JudgeBottomTopUtils {

    /**
     * 判断gridView 是否到达顶部
     *
     * @param gridView
     * @return
     */
    public static  boolean gridViewIsTop(GridView gridView) {
        View firstView = null;
        if (gridView.getCount() == 0) {
            return true;
        }
        firstView = gridView.getChildAt(0);
        if (firstView != null) {
            if (gridView.getFirstVisiblePosition() == 0 && firstView.getTop() == gridView.getListPaddingTop()) {
                return true;
            }
        } else {
            return true;
        }

        return false;
    }

    /**
     * 判断gridView 是否到达底部
     *
     * @param gridView
     * @return
     */
    public static  boolean gridViewIsBottom(GridView gridView) {
        int lastPosition = gridView.getLastVisiblePosition();
        int count = gridView.getCount();
        int childCount = gridView.getChildCount();
        View lastVisibaleView = gridView.getChildAt(childCount - 1);
        if (childCount == count) {
            return true;
        }
        if (lastVisibaleView != null) {
            if (lastPosition == count - 1 && lastVisibaleView.getBottom() + gridView.getListPaddingBottom() == gridView.getHeight()) {
                return true;
            }
        }
        return false;
    }

}
