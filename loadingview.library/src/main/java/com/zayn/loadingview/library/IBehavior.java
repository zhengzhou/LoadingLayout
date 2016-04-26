package com.zayn.loadingview.library;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by zhou on 16-4-6.
 */
public abstract class IBehavior<V extends View> {

    public IBehavior(Context context, AttributeSet attrs) {
    }

    /**
     * 在内容上面还是前面还是后面
     * dataView的高度是0.
     * @return
     */
    public abstract int getZOrder();

    /**
     * 初始时候的位置位移。
     * @return
     */
    public abstract int getTotalOffset(V view);

    public abstract void onScrolled(V view, int offset);

    public abstract boolean onStateChange(V view, int state);

}
