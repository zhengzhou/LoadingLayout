package com.zayn.library;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.LayoutRes;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

/**
 * one loadLayout map one stateViewHolder.
 * Created by zhou on 16-3-24.
 *
 * have default Value.
 */
public class StateViewHolder implements IViewSetter {

    private final LayoutInflater inflater;

    View loadingView;
    View errorView;
    View emptyView;
    View loadStartView;
    View loadEndView;

    public StateViewHolder(Context context, AttributeSet attrs, int defStyleAttr) {
        inflater = LayoutInflater.from(context);
        // get default from theme.
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.LoadingLayout, defStyleAttr, R.style.ll__Default_Style);
        int errorLayout = a.getResourceId(R.styleable.LoadingLayout_ll__ErrorView, R.layout.ll__error);
        int emptyLayout = a.getResourceId(R.styleable.LoadingLayout_ll__EmptyView, R.layout.ll__empty);
        int loadingLayout = a.getResourceId(R.styleable.LoadingLayout_ll__LoadingView, R.layout.ll__loading);
        int loadStartLayout = a.getResourceId(R.styleable.LoadingLayout_ll__LoadStartView, R.layout.ll__loading);
        int loadEndLayout = a.getResourceId(R.styleable.LoadingLayout_ll__LoadEndView, R.layout.ll__loading);
        a.recycle();
        setErrorView(errorLayout);
        setEmptyView(emptyLayout);
        setLoadingView(loadingLayout);
        setLoadStartView(loadStartLayout);
        setLoadEndView(loadEndLayout);
    }

    @Override
    public void setLoadingView(View view) {
        loadingView = view;
    }

    @Override
    public void setErrorView(View view) {
        errorView = view;
    }

    @Override
    public void setEmptyView(View view) {
        emptyView = view;
    }

    @Override
    public void setLoadStartView(View view) {
        loadStartView = view;
    }

    @Override
    public void setLoadEndView(View view) {
        loadEndView = view;
    }

    @Override
    public void setLoadingView(@LayoutRes int layoutRes) {
        loadingView = inflater.inflate(layoutRes, null);
    }

    @Override
    public void setErrorView(@LayoutRes int layoutRes) {
        errorView = inflater.inflate(layoutRes, null);
    }

    @Override
    public void setEmptyView(@LayoutRes int layoutRes) {
        emptyView = inflater.inflate(layoutRes, null);
    }

    @Override
    public void setLoadStartView(@LayoutRes int layoutRes) {
        loadStartView = inflater.inflate(layoutRes, null);
    }

    @Override
    public void setLoadEndView(@LayoutRes int layoutRes) {
        loadEndView = inflater.inflate(layoutRes, null);
    }
}
