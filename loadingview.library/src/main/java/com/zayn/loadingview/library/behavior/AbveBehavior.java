package com.zayn.loadingview.library.behavior;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.zayn.loadingview.library.IBehavior;
import com.zayn.loadingview.library.NestedLoadingLayout;

/**
 * Created by zhou on 16-4-25.
 */
public class AbveBehavior extends IBehavior<View> {

    private View target;

    public AbveBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public int getZOrder() {
        return 1;
    }

    @Override
    public int getTotalOffset(View view) {
        return 0;
    }

    @Override
    public void onScrolled(View view, int offset) {

    }

    @Override
    public boolean onStateChange(View view, int state) {
        if(state == NestedLoadingLayout.SCROLL_STATE_DRAGGING){
            target.setVisibility(View.VISIBLE);
        } else if(state == NestedLoadingLayout.SCROLL_STATE_IDLE){
            target.setVisibility(View.INVISIBLE);
        }
        return false;
    }

}
