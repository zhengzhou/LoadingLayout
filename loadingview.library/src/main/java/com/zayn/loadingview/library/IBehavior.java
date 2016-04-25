package com.zayn.loadingview.library;

/**
 * Created by zhou on 16-4-6.
 */
public interface IBehavior {

    /**
     * 在内容上面还是前面还是后面
     * dataView的高度是0.
     * @return
     */
    int getZOrder();

    int getHeight();

    /**
     * 初始时候的位置位移。
     * @return
     */
    int getTotalOffset();

    void onScrolled(int offset);

    boolean onStateChange(int state);

}
