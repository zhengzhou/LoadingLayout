package com.zayn.loadingview.library;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.os.Build;
import android.support.annotation.LayoutRes;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.NestedScrollingParentHelper;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ScrollerCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Xml;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.jiongbull.jlog.JLog;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * implement the nestScrollParent.
 * can swipe to refresh or loadMore.
 * <p/>
 * Created by zhou on 16-3-24.
 */
public class NestedLoadingLayout extends NestAsChildLayout implements NestedScrollingParent, ISwiper {

    public static final String TAG = "LoadingLayout";
    static final String WIDGET_PACKAGE_NAME;

    static {
        final Package pkg = NestedLoadingLayout.class.getPackage();
        WIDGET_PACKAGE_NAME = pkg != null ? pkg.getName() : null;
    }
    static final Class<?>[] CONSTRUCTOR_PARAMS = new Class<?>[] {
            Context.class,
            AttributeSet.class
    };

    static final ThreadLocal<Map<String, Constructor<IBehavior>>> sConstructors =
            new ThreadLocal<>();


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
        //if start view & end View are defined in xml
        for (int i = 0; i < getChildCount(); i++) {
            configLoadingView(getChildAt(i));
        }

        if (startEnable && stateViewHolder.loadStartView == null) {
            inflateLoadingView(stateViewHolder.loadStartLayout, LayoutParams.START);
        }
        if (endEnable && stateViewHolder.loadEndView == null) {
            inflateLoadingView(stateViewHolder.loadStartLayout, LayoutParams.END);
        }

        setChildrenDrawingOrderEnabled(true);
    }

    /**
     * find view's type
     * @param view child view
     */
    private void configLoadingView(View view){

        LayoutParams params = getResolvedLayoutParams(view);
        int viewType = params.viewType;
        if (viewType == LayoutParams.BODY) {
            dataView = view;
        } else if(viewType == LayoutParams.START) {
            params.gravity = isVerticalScroll() ? Gravity.TOP : Gravity.START;
            stateViewHolder.setLoadStartView(view);
        }else if(viewType == LayoutParams.END){
            params.gravity = isVerticalScroll() ? Gravity.BOTTOM : Gravity.END;
            stateViewHolder.setLoadEndView(view);
        }
    }

    /**
     * inflate loading view from layoutRes.
     * @param loadLayout layoutRes
     * @param viewType view Type.
     */
    private void inflateLoadingView(@LayoutRes int loadLayout, int viewType){
        XmlResourceParser layout = getContext().getResources().getLayout(loadLayout);
        AttributeSet attributeSet = Xml.asAttributeSet(layout);
        LayoutParams params = generateLayoutParams(attributeSet);
        params.viewType = viewType; //LayoutParams.START;
        View view = LayoutInflater.from(getContext()).inflate(layout, this, false);
        if(viewType == LayoutParams.START) {
            params.gravity = isVerticalScroll() ? Gravity.TOP : Gravity.START;
            stateViewHolder.setLoadStartView(view);
        }else {
            params.gravity = isVerticalScroll() ? Gravity.BOTTOM : Gravity.END;
            stateViewHolder.setLoadEndView(view);
        }
        view.setLayoutParams(params);
        addView(view, getResolvedLayoutParams(view));
    }

    @Override
    protected int getChildDrawingOrder(int childCount, int i) {
        View child = getChildAt(i);
        if(child == dataView){
            return childCount / 2;
        }else if(getChildAt(i) == stateViewHolder.loadStartView ||
                    child == stateViewHolder.loadEndView){
            return childCount / 2 + getZIndex(child);

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

        if (ensureViewBehavior(startView)) {
            layoutChildView(startView, parentLeft, parentTop, parentRight, parentBottom);
        }
        if(ensureViewBehavior(endView)) {
            layoutChildView(endView, parentLeft, parentTop, parentRight, parentBottom);
        }
        dataView.layout(childLeft, childTop, childLeft + dataView.getMeasuredWidth(), childTop + dataView.getMeasuredHeight());
    }

    private IBehavior getBehavior(View view){
        if(view == null){
            return null;
        }
        LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
        if(layoutParams != null && layoutParams.behavior != null){
            return layoutParams.behavior;
        }
        return null;
    }

    private boolean ensureViewBehavior(View view) {
        return getBehavior(view) != null;
    }

    private int getZIndex(View view) {
        if (ensureViewBehavior(view)) {
            return ((LayoutParams) view.getLayoutParams()).behavior.getZOrder();
        } else {
            return 0;
        }
    }

    private void layoutChildView(View view, int left, int top, int right, int bottom) {
        LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
        IBehavior behavior = layoutParams.behavior;

        if (layoutParams.viewType == LayoutParams.START && startEnable) {
            if (isVerticalScroll()) {
                top = top - behavior.getTotalOffset(view) + layoutParams.topMargin;
                view.layout(left, top, right, top + view.getMeasuredHeight());
            } else {
                left += behavior.getTotalOffset(view) + layoutParams.leftMargin;
                view.layout(left, top, left + view.getMeasuredWidth(), bottom);
            }
        } else if (layoutParams.viewType == LayoutParams.END && endEnable) {
            if (isVerticalScroll()) {
                top = bottom  - view.getMeasuredHeight() + behavior.getTotalOffset(view) - layoutParams.bottomMargin;
                view.layout(left, top, right, top + view.getMeasuredHeight());
            } else {
                left = right - view.getMeasuredWidth() + behavior.getTotalOffset(view) - layoutParams.rightMargin;
                view.layout(left, top, left + view.getMeasuredWidth(), bottom);
            }
        }
    }

    /**
     * 从自定义View 的注解上读取behavior.
     * @param child
     * @return
     */
    LayoutParams getResolvedLayoutParams(View child) {
        final LayoutParams result = (LayoutParams) child.getLayoutParams();
        if (!result.mBehaviorResolved) {
            Class<?> childClass = child.getClass();
            LoadingBehavior defaultBehavior = null;
            while (childClass != null &&
                    (defaultBehavior = childClass.getAnnotation(LoadingBehavior.class)) == null) {
                childClass = childClass.getSuperclass();
            }
            if (defaultBehavior != null) {
                try {
                    result.setBehavior(defaultBehavior.value().newInstance());
                } catch (Exception e) {
                    Log.e(TAG, "Default behavior class " + defaultBehavior.value().getName() +
                            " could not be instantiated. Did you forget a default constructor?", e);
                }
            }
            result.mBehaviorResolved = true;
        }
        return result;
    }

    static IBehavior parseBehavior(Context context, AttributeSet attrs, String name) {
        if (TextUtils.isEmpty(name)) {
            return null;
        }

        final String fullName;
        if (name.startsWith(".")) {
            // Relative to the app package. Prepend the app package name.
            fullName = context.getPackageName() + name;
        } else if (name.indexOf('.') >= 0) {
            // Fully qualified package name.
            fullName = name;
        } else {
            // Assume stock behavior in this package (if we have one)
            fullName = !TextUtils.isEmpty(WIDGET_PACKAGE_NAME)
                    ? (WIDGET_PACKAGE_NAME + '.' + name)
                    : name;
        }

        try {
            Map<String, Constructor<IBehavior>> constructors = sConstructors.get();
            if (constructors == null) {
                constructors = new HashMap<>();
                sConstructors.set(constructors);
            }
            Constructor<IBehavior> c = constructors.get(fullName);
            if (c == null) {
                final Class<IBehavior> clazz = (Class<IBehavior>) Class.forName(fullName, true,
                        context.getClassLoader());
                c = clazz.getConstructor(CONSTRUCTOR_PARAMS);
                c.setAccessible(true);
                constructors.put(fullName, c);
            }
            return c.newInstance(context, attrs);
        } catch (Exception e) {
            throw new RuntimeException("Could not inflate IBehavior subclass " + fullName, e);
        }
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

        if(startEnable){
            View startView = getStateViewHolder().loadStartView;
            getBehavior(startView).onScrolled(startView, state);
        }
        if(endEnable){
            View endView = getStateViewHolder().loadEndView;
            getBehavior(endView).onScrolled(endView, state);
        }

    }

    void dispatchScrolled(int offsetPixels) {
        float percent = currentScrollOffset / ((scrollDirect == Gravity.START) ? startOffset : endOffset);
        swipeLoadListener.onScrolled(this, scrollDirect, percent, offsetPixels);

        if(startEnable){
            View startView = getStateViewHolder().loadStartView;
            getBehavior(startView).onScrolled(startView, offsetPixels);
        }
        if(endEnable){
            View endView = getStateViewHolder().loadEndView;
            getBehavior(endView).onScrolled(endView, offsetPixels);
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
        private IBehavior behavior;
        private boolean mBehaviorResolved = false;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            final TypedArray a = c.obtainStyledAttributes(attrs, R.styleable.LoadingLayout_LayoutParams);
            viewType = a.getInt(R.styleable.LoadingLayout_LayoutParams_ll_viewType, BODY);

            if(viewType != BODY) {
                mBehaviorResolved = a.hasValue(
                        R.styleable.LoadingLayout_LayoutParams_ll_behavior);
                if (mBehaviorResolved) {
                    behavior = parseBehavior(c, attrs, a.getString(
                            R.styleable.LoadingLayout_LayoutParams_ll_behavior));
                }
            }
            a.recycle();
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

        public void setBehavior(IBehavior behavior) {
            this.behavior = behavior;
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
