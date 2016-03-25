package com.zayn.loadingview.library;

import android.support.annotation.LayoutRes;
import android.view.View;

/**
 * set the layout relative view.
 * Created by zhou on 16-3-24.
 */
public interface IViewSetter {

    void setLoadingView(View view);

    void setErrorView(View view);

    void setEmptyView(View view);

    void setLoadStartView(View view);

    void setLoadEndView(View view);

    void setLoadingView(@LayoutRes int layoutRes);

    void setErrorView(@LayoutRes int layoutRes);

    void setEmptyView(@LayoutRes int layoutRes);

    void setLoadStartView(@LayoutRes int layoutRes);

    void setLoadEndView(@LayoutRes int layoutRes);

}