package com.zayn.loadingview.library;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.os.Build;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.NestedScrollingParentHelper;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ScrollerCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.jiongbull.jlog.JLog;
import com.zayn.loadingview.library.behavior.BehindBehavior;

import java.util.ArrayList;
import java.util.List;

/**
 * implement the nestScrollParent.
 * can swipe to refresh or loadMore.
 * <p/>
 * Created by zhou on 16-3-24.
 */
public class NestedLoadingLayout extends NestAsChildLayout implements NestedScrollingParent, ISwiper {

    public static final String TAG = "LoadingLayout";

    public static final int SCROLL_STATE_IDLE = 0;
    public static final int SCROLL_STATE_DRAGGING = 1;
    public static final int SCROLL_STATE_SETTLING = 2;
    public static final int SCROLL_STATE_WAITING = 3;

    private final float resistance = 1.6f;
    private final int during = 300;

    private NestedScrollingParentHelper parentHelper;
    private int axes = ViewCompat.SCROLL_AXIS_VERTICAL;
    private boolean contentScroll;
    private boolean startEnable, endEnable;
    private int startOffset, endOffset;
    private ScrollerCompat scrollerCompat;
    private int currentScrollOffset;
    private OnSwipeLoadListener swipeLoadListener = new SimpleSwipeLoadListener();
    private List<IBehavior> behaviorList;

    private int scrollState = SCROLL_STATE_IDLE;
    private int scrollDirect = Gravity.NO_GRAVITY;


    public NestedLoadingLayout(Context context) {
        this(context, null);
    }

    public NestedLoadingLayout(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.loadingLayoutStyle);
    }

    public NestedLoadingLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void init(Context context, AttributeSet attrs, int defStyleAttr) {
        super.init(context, attrs, defStyleAttr);
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
        setNestedScrollingEnabled(true);
        behaviorList = new ArrayList<>(2);
    }

    @Override
    public void computeScroll() {
        if (scrollerCompat.computeScrollOffset()) {

            if (isVerticalScroll()) {
                if (contentScroll) {
                    JLog.d("currentScrollOffset: " + currentScrollOffset + ".  scrollerCompat.CurrY: " + scrollerCompat.getCurrY());
                    moveViewTo(0, - scrollerCompat.getCurrY() / resistance);

                }
                dispatchScrolled(scrollerCompat.getCurrY() - currentScrollOffset);
                currentScrollOffset = scrollerCompat.getCurrY();

                postInvalidate();
            }
        } else if (scrollState == SCROLL_STATE_SETTLING) {
            finishScroll();
        }
    }

    private void finishScroll() {
        //reset state when scroll end.
        scrollState = SCROLL_STATE_IDLE;
        currentScrollOffset = 0;
        dispatchScrollStateChanged(scrollDirect, scrollState);
        scrollDirect = Gravity.NO_GRAVITY;
        moveViewTo(0, 0);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        View startView = null, endView = null;
        LayoutParams startViewParam = null, endViewParam = null;
        //if start view & end View are defined in xml
        for (int i = 0; i < getChildCount(); i++) {
            View v = getChildAt(i);
            LayoutParams layoutParams = (LayoutParams) v.getLayoutParams();
            if (layoutParams.viewType == LayoutParams.BODY) {
                dataView = v;
            } else if (layoutParams.viewType == LayoutParams.START) {
                startView = v;
                startViewParam = layoutParams;
            } else if (layoutParams.viewType == LayoutParams.END) {
                endView = v;
                endViewParam = layoutParams;
            }
        }

        if (startEnable) {
            if (startViewParam == null) {
                startViewParam = generateDefaultLayoutParams();
                startViewParam.viewType = LayoutParams.START;
            }

            startViewParam.gravity = isVerticalScroll() ? Gravity.TOP : Gravity.START;

            if (startView != null) {
                stateViewHolder.loadStartView = startView;
            } else if (startEnable) {
                addView(stateViewHolder.loadStartView, startViewParam);
            }
            startViewParam.behavior = new BehindBehavior(stateViewHolder.loadStartView);
        }

        if (endEnable) {
            if (endViewParam == null) {
                endViewParam = generateDefaultLayoutParams();
                endViewParam.viewType = LayoutParams.END;
            }

            endViewParam.gravity = isVerticalScroll() ? Gravity.BOTTOM : Gravity.END;
            if (endView != null) {
                stateViewHolder.loadEndView = endView;
            } else if (endEnable) {
                addView(stateViewHolder.loadEndView, endViewParam);
            }
            endViewParam.behavior = new BehindBehavior(stateViewHolder.loadEndView);
        }

        setChildrenDrawingOrderEnabled(true);
    }

    @Override
    protected int getChildDrawingOrder(int childCount, int i) {
        View child = getChildAt(i);
        if(child == dataView){
            return childCount / 2;
        }else if(getChildAt(i) == stateViewHolder.loadStartView ||
                    child == stateViewHolder.loadEndView){
            return childCount / 2 + getZOrder(child);

        }
        return super.getChildDrawingOrder(childCount, i);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if(startEnable && stateViewHolder.getLoadStartView() != null) {
            measureChild(stateViewHolder.getLoadStartView(), widthMeasureSpec, heightMeasureSpec);
        }
        if(endEnable && stateViewHolder.getLoadEndView() != null) {
            measureChild(stateViewHolder.getLoadEndView(), widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        final int parentLeft = getPaddingLeft();
        final int parentRight = right - left - getPaddingRight();
        final int parentTop = getPaddingTop();
        final int parentBottom = bottom - top - getPaddingBottom();

        LayoutParams params = (LayoutParams) dataView.getLayoutParams();
        int childLeft = parentLeft + params.leftMargin;
        int childTop = parentTop + params.topMargin;

        View startView = stateViewHolder.getLoadStartView();
        View endView = stateViewHolder.getLoadEndView();

        if (getZOrder(startView) != 0) {
            layoutChildView(startView, parentLeft, parentTop, parentRight, parentBottom);
            layoutChildView(endView, parentLeft, parentTop, parentRight, parentBottom);
        }
        dataView.layout(childLeft, childTop, childLeft + dataView.getMeasuredWidth(), childTop + dataView.getMeasuredHeight());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    private int getZOrder(View view) {
        if(view == null){
            return 0;
        }
        LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
        if(layoutParams != null && layoutParams.behavior != null){
            return layoutParams.behavior.getZOrder();
        }else {
            return 0;
        }
    }

    private void layoutChildView(View view, int left, int top, int right, int bottom) {
        LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
        IBehavior startBehavior = layoutParams.behavior;

        if (layoutParams.viewType == LayoutParams.START && startEnable) {
            if (isVerticalScroll()) {
                top = top - startBehavior.getTotalOffset() + layoutParams.topMargin;
                view.layout(left, top, right, top + view.getMeasuredHeight());
            } else {
                left += startBehavior.getTotalOffset() + layoutParams.leftMargin;
                view.layout(left, top, left + view.getMeasuredWidth(), bottom);
            }
        }else if (layoutParams.viewType == LayoutParams.END && endEnable) {
            if (isVerticalScroll()) {
                top = bottom  - view.getMeasuredHeight() + startBehavior.getTotalOffset() - layoutParams.bottomMargin;
                view.layout(left, top, right, top + view.getMeasuredHeight());
            } else {
                left = right - view.getMeasuredWidth() + startBehavior.getTotalOffset() - layoutParams.rightMargin;
                view.layout(left, top, left + view.getMeasuredWidth(), bottom);
            }
        }
    }

    private IBehavior ensureBehavior(View view){
        IBehavior behavior;

        LoadingBehavior annotation = view.getClass().getAnnotation(LoadingBehavior.class);
        if(annotation != null){

        }
        behavior = new BehindBehavior(view);

        LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
        layoutParams.behavior = behavior;

        return behavior;
    }

    private boolean isVerticalScroll() {
        return axes == ViewCompat.SCROLL_AXIS_VERTICAL;
    }

    public int getStartOffset() {
        return startOffset;
    }

    public int getEndOffset() {
        return endOffset;
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
    private int[] mTempOffsetInWindow;
    boolean mConsumedByParent = false;

    public int getNestedScrollAxes() {
        return axes;
    }

    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        return isStateSafe() && (nestedScrollAxes & axes) != 0;
    }

    public void onNestedScrollAccepted(View child, View target, int nestedScrollAxes) {
        startNestedScroll(axes & nestedScrollAxes);
        if (!isStateSafe()) return;
        scrollerCompat.abortAnimation();
        parentHelper.onNestedScrollAccepted(child, target, nestedScrollAxes);
    }

    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
        scrollerCompat.abortAnimation();

        if (isNestedScrollingEnabled()) {
            dispatchNestedPreScroll(dx, dy, consumed, null);
        }

        if (mConsumedByParent) {
            return;
        }
        if (!isStateSafe()) return;
        if (isVerticalScroll()) {

            //反方向的滑动
            if ((scrollDirect == Gravity.START && dy > 0 && currentScrollOffset < 0)
                    || (scrollDirect == Gravity.END && dy < 0 && currentScrollOffset > 0)) {
                if (contentScroll) {
                    moveViewBy(0, -dy / resistance);
                    consumed[1] += (dy);
                }
                currentScrollOffset += dy;
                dispatchScrolled(dy);

            }
        } else {
            // TODO: 16-3-25 process horizontal scroll...
        }


    }

    public void onNestedScroll(View target, int dxConsumed, int dyConsumed,
                               int dxUnconsumed, int dyUnconsumed) {
        if (isNestedScrollingEnabled()) {
            if (mTempOffsetInWindow == null) {
                mTempOffsetInWindow = new int[2];
            }
            JLog.d("mConsumedByParent:" + mConsumedByParent + ", dyUnconsumed:" + dyUnconsumed + ", offsetInWindow:" + mTempOffsetInWindow[1]);
            dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, mTempOffsetInWindow);
            JLog.d("dyConsumed:" + dyConsumed + ", dyUnconsumed:" + dyUnconsumed + ", offsetInWindow:" + mTempOffsetInWindow[1]);

            if (mConsumedByParent) {
                return;
            }
            mConsumedByParent = mTempOffsetInWindow[0] != 0 || mTempOffsetInWindow[1] != 0;
        }

        if (!isStateSafe()) return;

        if (scrollState == SCROLL_STATE_IDLE) {
            scrollState = SCROLL_STATE_DRAGGING;
            int distance = isVerticalScroll() ? dyUnconsumed : dxUnconsumed;
            if (distance == 0) {
                return;
            }
            scrollDirect = distance < 0 ? Gravity.START : Gravity.END;
            dispatchScrollStateChanged(scrollDirect, scrollState);
        }
        if (scrollDirect == 0) {
            int distance = isVerticalScroll() ? dyUnconsumed : dxUnconsumed;
            scrollDirect = distance < 0 ? Gravity.START : Gravity.END;
        }

        if (isVerticalScroll() && dyUnconsumed != 0) {
            int maxOffset = scrollDirect == Gravity.START ? startOffset : endOffset;
            if ((scrollDirect == Gravity.START && startEnable) || (scrollDirect == Gravity.END && endEnable)) {
                if (Math.abs(currentScrollOffset) < maxOffset) {
                    if (contentScroll) {
                        moveViewBy(0, -dyUnconsumed / resistance);
                    }
                    currentScrollOffset += dyUnconsumed;
                    dispatchScrolled(dyUnconsumed);
                } else {
                    if (scrollState == SCROLL_STATE_DRAGGING) {
                        scrollState = SCROLL_STATE_WAITING;
                    }
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
        JLog.d("...");
        stopNestedScroll();
        if (!isStateSafe()) return;


        if (mConsumedByParent) {
            //reset state.
            scrollState = SCROLL_STATE_IDLE;
            mConsumedByParent = false;
            currentScrollOffset = 0;
            return;
        }
        //above android 5.0. it called onStopNestedScroll first.
        if (scrollState == SCROLL_STATE_DRAGGING) {

            // TODO: 16-3-25 process horizontal scroll...
            //roll back.
            if (currentScrollOffset != 0) {
                scrollState = SCROLL_STATE_SETTLING;
                dispatchScrollStateChanged(scrollDirect, scrollState);
                scrollerCompat.startScroll(0, currentScrollOffset, 0, -currentScrollOffset, during);
                postInvalidate();
            } else {
                finishScroll();
            }
        } else {
            dispatchScrollStateChanged(scrollDirect, scrollState);
        }
    }

    void dispatchScrollStateChanged(int scrollDirect, int state) {
        swipeLoadListener.onStateChanged(this, scrollDirect, state);
        for(IBehavior behavior: behaviorList){
            if(behavior != null)//todo assert not null
            behavior.onStateChange(state);
        }
    }

    void dispatchScrolled(int offsetPixels) {
        float percent = currentScrollOffset / ((scrollDirect == Gravity.START) ? startOffset : endOffset);
        swipeLoadListener.onScrolled(this, scrollDirect, percent, offsetPixels);

        for(IBehavior behavior: behaviorList){
            if(behavior != null)//todo assert not null
                behavior.onScrolled(offsetPixels);
        }
    }

    void moveViewBy(float offsetX, float offsetY){
        if(offsetX != 0){
            float translate = dataView.getTranslationX() + offsetX;
            dataView.setTranslationX(translate);
        }

        if (offsetY != 0) {
            float translate = dataView.getTranslationY() + offsetY;
            dataView.setTranslationY(translate);
        }
    }


    void moveViewTo(float x, float y){
        dataView.setTranslationX(x);
        dataView.setTranslationY(y);
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
        dispatchScrollStateChanged(scrollDirect, scrollState);
        // TODO: 16-3-25 process horizontal scroll...
        // 16-3-24 offset content to origin place
        JLog.d("currentScrollOffset: " + currentScrollOffset);
        scrollerCompat.startScroll(0, currentScrollOffset, 0, -currentScrollOffset, during);
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

    ///////////////////////////////////////////////////////////////////////////
    // on Touch event
    ///////////////////////////////////////////////////////////////////////////

    private boolean mIsBeingDragged;
    private int mActivePointerId;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (scrollState == SCROLL_STATE_WAITING) {
            return true;
        }

        final int action = MotionEventCompat.getActionMasked(ev);

        switch (action) {
            case MotionEvent.ACTION_DOWN:
//                scrollTargetOffset(0, 0);
                mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
                mIsBeingDragged = false;

                float initialDownY = MotionEventCompat.getY(ev, mActivePointerId);
                if (initialDownY == -1) {
                    return false;
                }

                break;

            case MotionEvent.ACTION_MOVE:
                break;

            case MotionEvent.ACTION_POINTER_UP:
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                break;
        }

        return mIsBeingDragged;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    ///////////////////////////////////////////////////////////////////////////
    // layout Params
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        if (p instanceof LayoutParams) {
            return new LayoutParams((LayoutParams) p);
        } else if (p instanceof MarginLayoutParams) {
            return new LayoutParams((MarginLayoutParams) p);
        }
        return new LayoutParams(p);
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.CENTER);
    }

    public static class LayoutParams extends FrameLayout.LayoutParams {

        public static final int START = 1;
        public static final int END = 2;
        public static final int BODY = 3;

        int viewType = BODY;
        IBehavior behavior;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            final TypedArray a = c.obtainStyledAttributes(attrs, R.styleable.LoadingLayout_LayoutParams);
            viewType = a.getInt(R.styleable.LoadingLayout_LayoutParams_ll_viewType, BODY);
            a.recycle();
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(int width, int height, int gravity) {
            super(width, height);
            this.gravity = gravity;
        }

        @TargetApi(Build.VERSION_CODES.KITKAT)
        public LayoutParams(LayoutParams p) {
            super(p);
        }

        public LayoutParams(MarginLayoutParams p) {
            super(p);
        }

        public LayoutParams(ViewGroup.LayoutParams p) {
            super(p);
        }

    }

    public static class SimpleSwipeLoadListener implements OnSwipeLoadListener {

        @Override
        public void onStateChanged(NestedLoadingLayout loadingLayout, int place, int state) {
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
