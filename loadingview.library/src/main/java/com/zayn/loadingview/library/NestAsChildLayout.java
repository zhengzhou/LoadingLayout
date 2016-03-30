package com.zayn.loadingview.library;

import android.content.Context;
import android.support.v4.view.NestedScrollingChild;
import android.support.v4.view.NestedScrollingChildHelper;
import android.util.AttributeSet;

/**
 * implement scrolling child.
 *
 * Created by zhou on 16-3-30.
 */
class NestAsChildLayout extends LoadingLayout implements NestedScrollingChild {

    private NestedScrollingChildHelper childHelper;

    public NestAsChildLayout(Context context) {
        super(context);
    }

    public NestAsChildLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NestAsChildLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void init(Context context, AttributeSet attrs, int defStyleAttr) {
        super.init(context, attrs, defStyleAttr);
        childHelper = new NestedScrollingChildHelper(this);
    }

    public void setNestedScrollingEnabled(boolean enabled){
        childHelper.setNestedScrollingEnabled(enabled);
    }

    public boolean isNestedScrollingEnabled(){
        return childHelper.isNestedScrollingEnabled();
    }

    public boolean startNestedScroll(int axes){
        return childHelper.startNestedScroll(axes);
    }

    public void stopNestedScroll(){
        childHelper.stopNestedScroll();
    }

    public boolean hasNestedScrollingParent(){
        return childHelper.hasNestedScrollingParent();
    }

    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed,
                                        int dxUnconsumed, int dyUnconsumed, int[] offsetInWindow){
        return childHelper.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow);
    }

    public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow){
        return childHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow);
    }

    public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed){
        return childHelper.dispatchNestedFling(velocityX, velocityY, consumed);
    }

    public boolean dispatchNestedPreFling(float velocityX, float velocityY){
        return childHelper.dispatchNestedPreFling(velocityX, velocityY);
    }
}
