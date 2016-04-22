package com.zayn.loadingview.library;

/**
 * Created by zhou on 16-3-24.
 */
public interface OnSwipeLoadListener {

    void onStateChanged(NestedLoadingLayout loadingLayout, int place, int state);

    void onScrolled(NestedLoadingLayout loadingLayout, int place, float positionOffset, int positionOffsetPixels);
}
