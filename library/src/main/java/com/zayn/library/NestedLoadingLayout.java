package com.zayn.library;

import android.content.Context;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.NestedScrollingParentHelper;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;

/**
 * implement the nestScrollParent.
 * can swipe to refresh or loadMore.
 * <p/>
 * Created by zhou on 16-3-24.
 */
public class NestedLoadingLayout extends LoadingLayout implements NestedScrollingParent, ISwiper {

    public static final int STATE_START_LOADING = 0x10;
    public static final int STATE_END_LOADING = 0x11;

    private int axes = ViewCompat.SCROLL_AXIS_VERTICAL;
    private boolean startEnable, endEnable;
    private NestedScrollingParentHelper parentHelper;

    public NestedLoadingLayout(Context context) {
        this(context, null);
    }

    public NestedLoadingLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NestedLoadingLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        parentHelper = new NestedScrollingParentHelper(this);
        addView(stateViewHolder.loadStartView);
    }

    private boolean isStateSafe() {
        return state == STATE_DEFAULT;
    }

    ///////////////////////////////////////////////////////////////////////////
    // NestedScrollingParent
    ///////////////////////////////////////////////////////////////////////////
    public int getNestedScrollAxes() {
        return axes;
    }

    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        if (!isStateSafe()) return false;
        return false;
    }

    public void onNestedScrollAccepted(View child, View target, int nestedScrollAxes) {
        if (!isStateSafe()) return;
        parentHelper.onNestedScrollAccepted(child, target, nestedScrollAxes);
    }

    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
        if (!isStateSafe()) return;
    }

    public void onNestedScroll(View target, int dxConsumed, int dyConsumed,
                               int dxUnconsumed, int dyUnconsumed) {
        if (!isStateSafe()) return;
    }

    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
        if (!isStateSafe()) return false;
        return false;
    }

    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
        if (!isStateSafe()) return false;
        return false;
    }

    public void onStopNestedScroll(View target) {
        if (!isStateSafe()) return;
    }

    ///////////////////////////////////////////////////////////////////////////
    // ISwiper
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public void setScrollAxes(int axes) {
        this.axes = axes;
    }

    @Override
    public void setSwipeEnable(boolean start, boolean end) {
        this.startEnable = start;
        this.endEnable = end;
    }

    @Override
    public void setOnSwipeListener(OnSwipeLoadListener listener) {

    }

    @Override
    public void stopSwipeLoading() {

    }

    @Override
    public void setSwipeOffset(int start, int end) {

    }
}
