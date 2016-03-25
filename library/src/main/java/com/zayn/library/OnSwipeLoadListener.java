package com.zayn.library;

/**
 * Created by zhou on 16-3-24.
 */
public interface OnSwipeLoadListener {

    void onPageScrollStateChanged(NestedLoadingLayout loadingLayout, int place, int state);

    void onScrolled(NestedLoadingLayout loadingLayout, int place, float positionOffset, int positionOffsetPixels);
}
