package com.zayn.library;

/**
 * Created by zhou on 16-3-24.
 */
public interface OnSwipeLoadListener {

    void onPageScrollStateChanged(int state);

    void onScrolled(int position, float positionOffset, int positionOffsetPixels);

    void onRefresh();

    void onLoadMore();
}
