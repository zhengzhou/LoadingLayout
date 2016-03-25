package com.zayn.loadingview.ui;

import android.util.Log;
import android.view.Gravity;
import android.view.View;

import com.jiongbull.jlog.JLog;
import com.zayn.loadingview.library.NestedLoadingLayout;
import com.zayn.loadingview.library.OnSwipeLoadListener;

/**
 * Created by zhou on 16-3-25.
 */
public class LoadingSwipeListener implements OnSwipeLoadListener {

    private PullLoadView loadView;
    private int place;

    private int state;

    /**
     * bind some animation with onScrolled.
     *
     * @param loadingLayout load view.
     * @param place         Gravity.Start or Gravity.End.
     */
    public void bindPullLoadView(NestedLoadingLayout loadingLayout, int place) {
        loadingLayout.setOnSwipeListener(this);
        View loadView = null;
        if (place == Gravity.START) {
            loadView = loadingLayout.getStateViewHolder().getLoadStartView();
        }else {
            loadView = loadingLayout.getStateViewHolder().getLoadEndView();

        }
        if (loadView instanceof PullLoadView) {
            this.loadView = (PullLoadView) loadView;
            if (place == Gravity.START) {
                this.loadView.setMaxOffset(loadingLayout.getStartOffset());
            }else {
                this.loadView.setMaxOffset(loadingLayout.getEndOffset());
            }
            this.place = place;
        } else {
            throw new IllegalArgumentException("container is not a PullLoadView");
        }

    }

    @Override
    public void onPageScrollStateChanged(final NestedLoadingLayout loadingLayout, int place, int state) {
        JLog.d("state change:" + state);
        this.state = state;
        if (NestedLoadingLayout.SCROLL_STATE_WAITING == state) {
            loadView.doLoading();
            loadingLayout.postDelayed(new Runnable() {
                @Override
                public void run() {
                    loadingLayout.stopSwipeLoading();
                }
            }, 300);
        } else if (NestedLoadingLayout.SCROLL_STATE_IDLE == state) {
            loadView.reset();
        }
    }

    @Override
    public void onScrolled(NestedLoadingLayout loadingLayout, int place, float positionOffset, int positionOffsetPixels) {
        if (NestedLoadingLayout.SCROLL_STATE_DRAGGING == state
                || NestedLoadingLayout.SCROLL_STATE_SETTLING == state)
            if (place == this.place) {
                JLog.d("currentScrollOffset: " + positionOffsetPixels);
                loadView.doScroll(-positionOffsetPixels);
            }
    }
}
