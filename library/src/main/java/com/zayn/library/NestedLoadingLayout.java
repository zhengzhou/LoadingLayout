package com.zayn.library;

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

    static final int STATE_START_LOADING = 0x10;
    static final int STATE_END_LOADING = 0x11;

    public static final int SCROLL_STATE_IDLE = 0;
    public static final int SCROLL_STATE_DRAGGING = 1;
    public static final int SCROLL_STATE_SETTLING = 2;


    private NestedScrollingParentHelper parentHelper;
    private OnSwipeLoadListener swipeLoadListener;
    private int axes = ViewCompat.SCROLL_AXIS_VERTICAL;
    private boolean contentScroll = true;
    private boolean startEnable, endEnable;
    private int startOffset, endOffset;
    private ScrollerCompat scrollerCompat;

    private int scrollState = SCROLL_STATE_IDLE;
    private int scrollDirect = Gravity.NO_GRAVITY;
    private int currentOffset;


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
        a.recycle();
        parentHelper = new NestedScrollingParentHelper(this);
        scrollerCompat = ScrollerCompat.create(context);
    }

    @Override
    public void computeScroll() {
        if(scrollerCompat.computeScrollOffset()){
            scrollTo(scrollerCompat.getCurrX(), scrollerCompat.getCurrY());
            postInvalidate();
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
    public void stopLoading() {
        super.stopLoading();
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
            startViewParam.topMargin = -startOffset;
            endViewParam.topMargin = endOffset;
            addView(stateViewHolder.loadStartView, startViewParam);
        }
        if (endEnable) {
            addView(stateViewHolder.loadEndView, endViewParam);
        }
    }

    @Override
    protected void removeAllStateViewInLayout() {
        super.removeAllStateViewInLayout();
        removeViewInLayout(stateViewHolder.loadStartView);
        removeViewInLayout(stateViewHolder.loadEndView);
    }

    private boolean isStateSafe() {
        return state == STATE_DEFAULT && (startEnable || endEnable);
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
            }
            Log.d(TAG, "dy:"+ dy);
            //反方向的滑动
            if (scrollDirect == Gravity.START && dy > 0) {
                if(Math.abs(getScrollY()) < startOffset){
                    scrollBy(0, dy);
                    consumed[1] = dy;
                }
            } else if (scrollDirect == Gravity.END && dy < 0) {
                if(Math.abs(getScrollY()) < endOffset) {
                    scrollBy(0, dy);
                    consumed[1] = dy;
                }
            }
        }


    }

    public void onNestedScroll(View target, int dxConsumed, int dyConsumed,
                               int dxUnconsumed, int dyUnconsumed) {
        if (!isStateSafe()) return;
        /*if(state == STATE_START_LOADING||state == STATE_END_LOADING){
            return;
        }*/
        if (isVerticalScroll() && dyUnconsumed != 0) {
            if (scrollDirect == Gravity.START && startEnable) {

            }
            if (scrollDirect == Gravity.END && endEnable) {
            }

            if (contentScroll) {
                if (scrollDirect == Gravity.START && startEnable) {
                    if(Math.abs(getScrollY()) < startOffset) {
                        scrollBy(0, dyUnconsumed/2);

                    }else{
                        //state = STATE_START_LOADING;
                        if(scrollState == SCROLL_STATE_DRAGGING){
                            scrollState = SCROLL_STATE_IDLE;
                            state = STATE_START_LOADING;
                        }
                    }
                }

                if (scrollDirect == Gravity.END && endEnable) {
                    if (Math.abs(getScrollY()) < endOffset) {
                        scrollBy(0, dyUnconsumed/2);
                    } else if (scrollState == SCROLL_STATE_DRAGGING) {
                        scrollState = SCROLL_STATE_IDLE;
                        state = STATE_END_LOADING;
                    }
                }
            }
        } else if (!isVerticalScroll() && dxUnconsumed != 0) {

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
        scrollState = SCROLL_STATE_IDLE;
        scrollDirect = Gravity.NO_GRAVITY;
        if(state != STATE_START_LOADING && state != STATE_END_LOADING){
            //roll back.
            final int scrollY = getScrollY();
            Log.d(TAG, "ScrollY: " + scrollY);
            post(new Runnable() {
                @Override
                public void run() {
                    scrollerCompat.startScroll(0, scrollY , 0, -scrollY);
                    postInvalidate();
                }
            });

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
//        if (!isStateSafe()) return;
        if (contentScroll) {
            // TODO: 16-3-24 offset content to origin place
            scrollTo(0, 0);
        }
    }

    @Override
    public void setSwipeOffset(int start, int end) {
        this.startOffset = start;
        this.endOffset = end;
    }

    @Override
    public void setContentScrollEnable(boolean scroll) {
        this.contentScroll = scroll;
    }
}
