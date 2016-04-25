package com.zayn.loadingview.library.behavior;

import android.view.View;

import com.zayn.loadingview.library.IBehavior;
import com.zayn.loadingview.library.NestedLoadingLayout;

/**
 * Created by zhou on 16-4-25.
 */
public class UpBehavior implements IBehavior {

    private View target;

    public UpBehavior(View target) {
        this.target = target;
    }

    @Override
    public int getZOrder() {
        return 1;
    }

    @Override
    public int getHeight() {
        return target.getHeight();
    }

    @Override
    public int getTotalOffset() {
        return getHeight();
    }

    @Override
    public void onScrolled(int offset) {
        //do nothing.
    }

    @Override
    public boolean onStateChange(int state) {
        if(state == NestedLoadingLayout.SCROLL_STATE_DRAGGING){
            target.setVisibility(View.VISIBLE);
        } else if(state == NestedLoadingLayout.SCROLL_STATE_IDLE){
            target.setVisibility(View.INVISIBLE);
        }
        return false;
    }
}