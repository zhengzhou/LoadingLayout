package com.zayn.library;

import android.content.Context;
import android.support.v4.view.NestedScrollingParent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

/**
 * Created by zhou on 16-3-24.
 */
public class LoadingLayout extends FrameLayout implements NestedScrollingParent{

    public LoadingLayout(Context context) {
        super(context);
    }

    public LoadingLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LoadingLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        return false;
    }

    public void onNestedScrollAccepted(View child, View target, int nestedScrollAxes){}

    public void onStopNestedScroll(View target){}

    public void onNestedScroll(View target, int dxConsumed, int dyConsumed,
                               int dxUnconsumed, int dyUnconsumed){

    }

    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed){}

    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed){
        return false;
    }

    public boolean onNestedPreFling(View target, float velocityX, float velocityY){
        return false;
    }

    public int getNestedScrollAxes(){

    }



}
