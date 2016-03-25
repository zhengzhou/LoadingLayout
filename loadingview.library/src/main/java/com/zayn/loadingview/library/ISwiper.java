package com.zayn.loadingview.library;

import android.support.v4.view.ViewCompat;

/**
 * swipe to refresh or load more.
 * Created by zhou on 16-3-24.
 */
public interface ISwiper {

    /**
     * @param axes Flag indicating the current axes of nested scrolling
     * @see ViewCompat#SCROLL_AXIS_HORIZONTAL
     * @see ViewCompat#SCROLL_AXIS_VERTICAL
     * @see ViewCompat#SCROLL_AXIS_NONE
     *
     * default is ViewCompat#SCROLL_AXIS_VERTICAL
     */
    void setScrollAxes(int axes);

    /**
     * set swipe enable.
     * @param start direct start. always is refresh action. may at top or start position
     * @param end direct end. always is loading more action. may at end or bottom position
     */
    void setSwipeEnable(boolean start, boolean end);

    /**
     * set event listener.
     * @param listener @see OnSwipeLoadListener
     */
    void setOnSwipeListener(OnSwipeLoadListener listener);

    /**
     * stop loading state.
     *
     */
    void stopSwipeLoading();

    /**
     * set the offset when scroll the layout.
     */
    void setSwipeOffset(int start, int end);

    /**
     * is the content scroll with.
     * @param scroll is the content scroll with.
     */
    void setContentScrollEnable(boolean scroll);
}