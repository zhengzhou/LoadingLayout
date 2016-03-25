package com.zayn.loadingview.ui;

import android.util.Log;
import android.view.Gravity;
import android.view.View;

import com.zayn.loadingview.library.NestedLoadingLayout;
import com.zayn.loadingview.library.OnSwipeLoadListener;

/**
 * Created by zhou on 16-3-25.
 */
public class LoadingSwipeListener implements OnSwipeLoadListener{

    private PullLoadView loadView;
    private int place;

    private int state;

    /**
     * bind some animation with onScrolled.
     * @param loadingLayout load view.
     * @param place Gravity.Start or Gravity.End.
     */
    public void bindPullLoadView(NestedLoadingLayout loadingLayout, int place){
        loadingLayout.setOnSwipeListener(this);
        View loadView = null;
        if(place == Gravity.START) {
            loadView = loadingLayout.getStateViewHolder().getLoadStartView();
        }
        if(loadView instanceof PullLoadView){
            this.loadView = (PullLoadView) loadView;
            this.place = place;
        }else {
            throw new IllegalArgumentException("container is not a PullLoadView");
        }
    }

    @Override
    public void onPageScrollStateChanged(NestedLoadingLayout loadingLayout, int place, int state) {
        this.state = state;
        if(NestedLoadingLayout.SCROLL_STATE_WAITING == state){
            loadView.doLoading();
        }else if(NestedLoadingLayout.SCROLL_STATE_IDLE == state){
            loadView.reset();
        }
    }

    @Override
    public void onScrolled(NestedLoadingLayout loadingLayout, int place, float positionOffset, int positionOffsetPixels) {
        if(NestedLoadingLayout.SCROLL_STATE_DRAGGING == state || NestedLoadingLayout.SCROLL_STATE_SETTLING == state)
        if(place == this.place){
            loadView.doScroll(-positionOffsetPixels/2);
        }
    }
}
