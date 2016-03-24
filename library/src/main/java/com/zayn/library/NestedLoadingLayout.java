package com.zayn.library;

import android.content.Context;
import android.support.v4.view.NestedScrollingParent;
import android.util.AttributeSet;
import android.view.View;

/**
 * implement the nestScrollParent.
 * can swipe to refresh or loadMore.
 * Created by zhou on 16-3-24.
 */
public class NestedLoadingLayout extends LoadingLayout implements NestedScrollingParent {

    public static final int STATE_START_LOADING = 0x10;
    public static final int STATE_END_LOADING  = 0x11;

    public NestedLoadingLayout(Context context) {
        super(context);
    }

    public NestedLoadingLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NestedLoadingLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        return false;
    }

    public void onNestedScrollAccepted(View child, View target, int nestedScrollAxes){}

    public void onStopNestedScroll(View target){}

    public void onNestedScroll(View target, int dxConsumed, int dyConsumed,
                               int dxUnconsumed, int dyUnconsumed){

    }

    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed){}

    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed){
        return false;
    }

    public boolean onNestedPreFling(View target, float velocityX, float velocityY){
        return false;
    }

    public int getNestedScrollAxes(){
        return SCROLL_AXIS_HORIZONTAL;
    }



}
