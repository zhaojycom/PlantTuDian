package com.zhaojy.planttudian.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

/**
 * @author: zhaojy
 * @data:On 2018/1/23.
 */

public class ObservableScrollView extends ScrollView {
    private OnScollChangedListener onScollChangedListener = null;

    public ObservableScrollView(Context context) {
        super(context);
    }

    public ObservableScrollView(Context context, AttributeSet attrs,
                                int defStyle) {
        super(context, attrs, defStyle);
    }

    public ObservableScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setOnScollChangedListener(OnScollChangedListener onScollChangedListener) {
        this.onScollChangedListener = onScollChangedListener;
    }

    @Override
    protected void onScrollChanged(int x, int y, int oldx, int oldy) {
        super.onScrollChanged(x, y, oldx, oldy);
        if (onScollChangedListener != null) {
            onScollChangedListener.onScrollChanged(this, x, y, oldx, oldy);
        }

        //scrollview的起始点+总高度==crollView的computeVerticalScrollRange
        if (getScrollY() + getHeight() == computeVerticalScrollRange()) {
            if (onScollChangedListener != null) {
                onScollChangedListener.onScrollToFooter(this, x, y, oldx, oldy);
            }
        }

    }

    public interface OnScollChangedListener {
        void onScrollChanged(ObservableScrollView scrollView, int x, int y, int oldx, int oldy);

        void onScrollToFooter(ObservableScrollView scrollView, int x, int y, int oldx, int oldy);
    }

}