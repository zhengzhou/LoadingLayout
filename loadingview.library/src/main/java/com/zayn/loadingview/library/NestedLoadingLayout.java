package com.zayn.loadingview.library;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.NestedScrollingParentHelper;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ScrollerCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;

/**
 * implement the nestScrollParent.
 * can swipe to refresh or loadMore.
 * <p/>
 * Created by zhou on 16-3-24.
 */
public class NestedLoadingLayout extends LoadingLayout implements NestedScrollingParent, ISwiper {

    public static final String TAG = "LoadingLayout";
    public static final boolean Debug = BuildConfig.DEBUG;

//    static final int STATE_START_LOADING = 0x10;
//    static final int STATE_END_LOADING = 0x11;

    public static final int SCROLL_STATE_IDLE = 0;
    public static final int SCROLL_STATE_DRAGGING = 1;
    public static final int SCROLL_STATE_SETTLING = 2;
    public static final int SCROLL_STATE_WAITING = 3;


    private NestedScrollingParentHelper parentHelper;
    private OnSwipeLoadListener swipeLoadListener = new SimpleSwipeLoadListener();
    private int axes = ViewCompat.SCROLL_AXIS_VERTICAL;
    private boolean contentScroll = true;
    private boolean startEnable, endEnable;
    private int startOffset, endOffset;
    private ScrollerCompat scrollerCompat;
    private int currentScrollOffset;

    private int scrollState = SCROLL_STATE_IDLE;
    private int scrollDirect = Gravity.NO_GRAVITY;


    public NestedLoadingLayout(Context context) {
        this(context, null);
    }

    public NestedLoadingLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NestedLoadingLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.LoadingLayout, defStyleAttr, R.style.ll__Default_Style);
        axes = a.getInt(R.styleable.LoadingLayout_ll__scroll_axes, 1) == 1 ? ViewCompat.SCROLL_AXIS_VERTICAL : ViewCompat.SCROLL_AXIS_HORIZONTAL;
        startEnable = a.getBoolean(R.styleable.LoadingLayout_ll__start_enable, true);
        endEnable = a.getBoolean(R.styleable.LoadingLayout_ll__end_enable, true);
        startOffset = a.getDimensionPixelOffset(R.styleable.LoadingLayout_ll__start_offset, 0);
        endOffset = a.getDimensionPixelOffset(R.styleable.LoadingLayout_ll__end_offset, 0);
        contentScroll = a.getBoolean(R.styleable.LoadingLayout_ll__content_scroll_enable, true);
        a.recycle();
        parentHelper = new NestedScrollingParentHelper(this);
        scrollerCompat = ScrollerCompat.create(context);
    }

    @Override
    public void computeScroll() {
        if (scrollerCompat.computeScrollOffset()) {
            if (contentScroll) {
                scrollTo(scrollerCompat.getCurrX(), scrollerCompat.getCurrY());
            }
            postInvalidate();
            if (isVerticalScroll()) {
                if (scrollDirect == Gravity.START)
                    swipeLoadListener.onScrolled(this, Gravity.START, currentScrollOffset / startOffset, scrollerCompat.getCurrY());
                else
                    swipeLoadListener.onScrolled(this, Gravity.END, currentScrollOffset / endOffset, scrollerCompat.getCurrY());
            }
        } else if (scrollState == SCROLL_STATE_SETTLING) {
            //reset state when scroll end.
            scrollState = SCROLL_STATE_IDLE;
            currentScrollOffset = 0;
            swipeLoadListener.onPageScrollStateChanged(this, scrollDirect, scrollState);
            scrollDirect = Gravity.NO_GRAVITY;
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (!startEnable && !endEnable)
            return;
        FrameLayout.LayoutParams startViewParam = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        FrameLayout.LayoutParams endViewParam = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        if (isVerticalScroll()) {
            startViewParam.gravity = Gravity.TOP;
            endViewParam.gravity = Gravity.BOTTOM;
        } else {
            startViewParam.gravity = Gravity.START;
            endViewParam.gravity = Gravity.END;
        }
        if (startEnable) {
            if (contentScroll)
                startViewParam.topMargin = -startOffset;
            addView(stateViewHolder.loadStartView, startViewParam);
        }
        if (endEnable) {
            if (contentScroll)
                endViewParam.bottomMargin = -endOffset;
            addView(stateViewHolder.loadEndView, endViewParam);
        }
    }

    private boolean isVerticalScroll() {
        return axes == ViewCompat.SCROLL_AXIS_VERTICAL;
    }

    @Override
    public void startLoading() {
        super.startLoading();
        stopSwipeLoading();
    }

    @Override
    protected void removeAllStateViewInLayout() {
        super.removeAllStateViewInLayout();
        removeViewInLayout(stateViewHolder.loadStartView);
        removeViewInLayout(stateViewHolder.loadEndView);
    }

    private boolean isStateSafe() {
        return getState() == STATE_DEFAULT && (startEnable || endEnable);
    }

    ///////////////////////////////////////////////////////////////////////////
    // NestedScrollingParent
    ///////////////////////////////////////////////////////////////////////////
    public int getNestedScrollAxes() {
        return axes;
    }

    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        return isStateSafe() && (nestedScrollAxes & axes) != 0;
    }

    public void onNestedScrollAccepted(View child, View target, int nestedScrollAxes) {
        if (!isStateSafe()) return;
        parentHelper.onNestedScrollAccepted(child, target, nestedScrollAxes);
    }

    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
        if (!isStateSafe()) return;
        if (isVerticalScroll()) {
            if (scrollState == SCROLL_STATE_IDLE) {
                scrollState = SCROLL_STATE_DRAGGING;
                scrollDirect = dy < 0 ? Gravity.START : Gravity.END;
                swipeLoadListener.onPageScrollStateChanged(this, scrollDirect, scrollState);
            }
            //反方向的滑动
            if (scrollDirect == Gravity.START && dy > 0) {
                if (Math.abs(getScrollY()) < startOffset) {
                    if (contentScroll) {
                        scrollBy(0, dy);
                        consumed[1] = dy;
                    }
                    swipeLoadListener.onScrolled(this, Gravity.START, getScrollY() / startOffset, dy);
                }
            } else if (scrollDirect == Gravity.END && dy < 0) {
                if (Math.abs(getScrollY()) < endOffset) {
                    if (contentScroll) {
                        scrollBy(0, dy);
                        consumed[1] = dy;
                    }
                    swipeLoadListener.onScrolled(this, Gravity.END, getScrollY() / endOffset, dy);
                }
            }
        } else {
            // TODO: 16-3-25 process horizontal scroll...
        }


    }

    public void onNestedScroll(View target, int dxConsumed, int dyConsumed,
                               int dxUnconsumed, int dyUnconsumed) {
        if (!isStateSafe()) return;

        if (isVerticalScroll() && dyUnconsumed != 0) {

            if (scrollDirect == Gravity.START && startEnable) {
                if (Math.abs(getScrollY()) < startOffset) {
                    if (contentScroll) {
                        scrollBy(0, dyUnconsumed / 2);
                    }
                    currentScrollOffset += dyUnconsumed;
                    swipeLoadListener.onScrolled(this, Gravity.START, currentScrollOffset / startOffset, dyUnconsumed);
                } else {
                    //state = STATE_START_LOADING;
                    if (scrollState == SCROLL_STATE_DRAGGING) {
                        scrollState = SCROLL_STATE_WAITING;
                        swipeLoadListener.onPageScrollStateChanged(this, scrollDirect, scrollState);
                    }
                }
            }

            if (scrollDirect == Gravity.END && endEnable) {
                if (Math.abs(getScrollY()) < endOffset) {
                    if (contentScroll) {
                        scrollBy(0, dyUnconsumed / 2);
                    }
                    currentScrollOffset += dyUnconsumed;
                    swipeLoadListener.onScrolled(this, Gravity.END, currentScrollOffset / endOffset, dyUnconsumed);
                } else if (scrollState == SCROLL_STATE_DRAGGING) {
                    scrollState = SCROLL_STATE_WAITING;
                    swipeLoadListener.onPageScrollStateChanged(this, scrollDirect, scrollState);
                }

            }
        } else if (!isVerticalScroll() && dxUnconsumed != 0) {
            // TODO: 16-3-25 process horizontal scroll...
        }
    }

    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
        if (!isStateSafe()) return false;
        return false;
    }

    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
        if (!isStateSafe()) return false;
        return false;
    }

    public void onStopNestedScroll(View target) {
        if (!isStateSafe()) return;
        if (scrollState != SCROLL_STATE_WAITING) {
            scrollState = SCROLL_STATE_SETTLING;
            swipeLoadListener.onPageScrollStateChanged(this, scrollDirect, scrollState);
            // TODO: 16-3-25 process horizontal scroll...
            //roll back.
            //Log.d(TAG, "currentScrollOffset: " + currentScrollOffset);
            scrollerCompat.startScroll(0, -currentScrollOffset, 0, currentScrollOffset);
            postInvalidate();
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // ISwiper
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public void setScrollAxes(int axes) {
        this.axes = axes;
    }

    @Override
    public void setSwipeEnable(boolean start, boolean end) {
        this.startEnable = start;
        this.endEnable = end;
    }

    @Override
    public void setOnSwipeListener(OnSwipeLoadListener listener) {
        this.swipeLoadListener = listener;
    }

    @Override
    public void stopSwipeLoading() {
        scrollState = SCROLL_STATE_SETTLING;
        swipeLoadListener.onPageScrollStateChanged(this, scrollDirect, scrollState);
        // TODO: 16-3-25 process horizontal scroll...
        // 16-3-24 offset content to origin place
        Log.d(TAG, "currentScrollOffset: " + currentScrollOffset);
        scrollerCompat.startScroll(0, -currentScrollOffset, 0, currentScrollOffset);
        postInvalidate();
    }

    @Override
    public void setSwipeOffset(int start, int end) {
        this.startOffset = start;
        this.endOffset = end;
    }

    @Override
    public void setContentScrollEnable(boolean scroll) {
        this.contentScroll = scroll;
        Log.d(TAG, "content enable change");
    }

    public static class SimpleSwipeLoadListener implements OnSwipeLoadListener {

        @Override
        public void onPageScrollStateChanged(NestedLoadingLayout loadingLayout, int place, int state) {
            if (state == SCROLL_STATE_WAITING) {
                if (place == Gravity.START) {
                    onRefresh(loadingLayout);
                } else if (place == Gravity.END) {
                    onLoadMore(loadingLayout);
                }
            }
        }

        @Override
        public void onScrolled(NestedLoadingLayout loadingLayout, int place, float positionOffset, int positionOffsetPixels) {

        }

        public void onRefresh(final NestedLoadingLayout loadingLayout) {

            loadingLayout.postDelayed(new Runnable() {
                @Override
                public void run() {
                    loadingLayout.stopSwipeLoading();
                }
            }, 1000);
        }

        public void onLoadMore(final NestedLoadingLayout loadingLayout) {

            loadingLayout.postDelayed(new Runnable() {
                @Override
                public void run() {
                    loadingLayout.stopSwipeLoading();
                }
            }, 1000);
        }
    }
}
