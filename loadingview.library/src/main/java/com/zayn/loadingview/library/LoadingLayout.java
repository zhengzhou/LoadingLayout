package com.zayn.loadingview.library;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

/**
 * layout with loading state.
 *
 * only have one direct child.
 *
 * Created by zhou on 16-3-24.
 */
public class LoadingLayout extends FrameLayout implements ILoader{

    public static final int STATE_DEFAULT = 0;
    public static final int STATE_LOADING = 1;
    public static final int STATE_EMPTY = 2;
    public static final int STATE_ERROR = 3;

    private int state = STATE_DEFAULT;
    protected StateViewHolder stateViewHolder;
    protected View dataView;

    public LoadingLayout(Context context) {
        this(context, null);
    }

    public LoadingLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadingLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setStateViewHolder(new StateViewHolder(context, attrs, defStyleAttr));
    }

    public void setStateViewHolder(StateViewHolder viewHolder) {
        this.stateViewHolder = viewHolder;
    }

    public StateViewHolder getStateViewHolder() {
        return stateViewHolder;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if(getChildCount() != 1){
            throw new IllegalStateException("can only have one direct child");
        }
        dataView = getChildAt(0);
    }

    @Override
    public void startLoading() {
        state = STATE_LOADING;
        removeAllStateViewInLayout();
        addView(stateViewHolder.loadingView);
    }

    @Override
    public void stopLoading() {
        state = STATE_DEFAULT;
        removeAllStateViewInLayout();
        addView(dataView);
    }

    @Override
    public void error() {
        state = STATE_ERROR;
        removeAllStateViewInLayout();
        addView(stateViewHolder.errorView);
    }

    @Override
    public void empty() {
        state = STATE_EMPTY;
        removeAllStateViewInLayout();
        addView(stateViewHolder.emptyView);
    }

    protected void removeAllStateViewInLayout(){
        removeViewInLayout(dataView);
        removeViewInLayout(stateViewHolder.loadingView);
        removeViewInLayout(stateViewHolder.errorView);
        removeViewInLayout(stateViewHolder.emptyView);
    }

    public int getState() {
        return state;
    }
}
