package com.zayn.loadingview.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.shapes.Shape;
import android.os.Build;
import android.os.SystemClock;
import android.support.v4.util.Pools;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.AttributeSet;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 一个布局，背景是小星星闪闪。
 * <p/>
 * Created by zhou on 16-1-23.
 */
public class SimpleLoadingView extends FrameLayout {

    NightDrawable starDrawable;
    private int color;

    public SimpleLoadingView(Context context) {
        super(context);
    }

    public static long getFullLength() {
        return (StarProvider.COUNT + 2) * StarProvider.FRAME_OFFSET * StarShape.FRAME_DURATION;
    }

    public SimpleLoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        //color = attrs.getAttributeIntValue(R.styleable.SimpleLoadingView_color, Color.BLACK);
        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ll_loading);
        color = a.getInt(R.styleable.ll_loading_color, Color.BLACK);
        a.recycle();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        startPopStars();
    }

    public void startPopStars() {
        starDrawable = new NightDrawable();
        starDrawable.getPaint().setColor(color);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            setBackground(starDrawable);
        }else {
            setBackgroundDrawable(starDrawable);
        }
        starDrawable.start();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //starDrawable.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
    }


    public static class StarShape extends Shape {

        /**
         * 动画进度相关
         */
        public final static long FRAME_DURATION = 1000 / 60;
        private final static long DURING = 800;
        private int mFrames = (int) (DURING / FRAME_DURATION);
        private int mCurrentFrame = 0;
        private float mProgress = 0f;
        private Interpolator mInterpolator = new FastOutSlowInInterpolator();

        /**
         * 形状相关数据
         */
        private RectF mRect = new RectF();
        private RectF mRealRect = new RectF();
        private float[] mOuterRadii;
        private float mInnerRadii;
        //        private float mOffsetX, mOffsetY;
        private Path mPath;  //最后要绘制的效果
        Matrix mMatrix;

        public boolean nextFrames() {
            boolean inProgress = mCurrentFrame < mFrames;
            if (inProgress) {
                mCurrentFrame++;
                mProgress = mCurrentFrame * 1.0f / mFrames;

                float currentOffset = mInterpolator.getInterpolation(mProgress);
                final float rectOffset = getRectOffset(currentOffset);
                final float radiiOffset = getRadiiOffset(currentOffset);
                resetPath(rectOffset, radiiOffset);
            } else {
                mCurrentFrame = 0;
            }
            return inProgress;
        }

        public StarShape(float size) {
            this(size, 0, 0);
        }

        /**
         * 一个星星的图形，不是五角星,是圆角正方形。。。
         *
         * @param size 整个形状的尺寸
         */
        public StarShape(float size, float offsetX, float offsetY) {
            mOuterRadii = new float[8];
            Arrays.fill(this.mOuterRadii, 20);
            this.mInnerRadii = size / 1.85f;

            mPath = new Path();
            mMatrix = new Matrix();

            mRect.set(0, 0, size, size);
            mRect.offset(offsetX, offsetY);
        }

        public void reset(float size, float offsetX, float offsetY) {
            mRect.set(0, 0, size, size);
            mRect.offset(offsetX, offsetY);
        }

        @Override
        public void draw(Canvas canvas, Paint paint) {
            canvas.drawPath(mPath, paint);
        }

        @Override
        public void getOutline(Outline outline) {
            super.getOutline(outline);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Rect rect = new Rect();
                mRect.roundOut(rect);
                outline.setOval(rect);
            }
        }

        @Override
        protected void onResize(float width, float height) {
            super.onResize(width, height);
            resetPath(1.0f, 1.0f);
        }

        private void resetPath(float outScale, float innerScale) {
            mPath.reset();
            float increaseX = (mRect.right - mRect.left) * (1 - outScale) / 2;
            float increaseY = (mRect.bottom - mRect.top) * (1 - outScale) / 2;
            mRealRect.left = mRect.left + increaseX;
            mRealRect.right = mRect.right - increaseX;
            mRealRect.top = mRect.top + increaseY;
            mRealRect.bottom = mRect.bottom - increaseY;

            mPath.addRoundRect(mRealRect, mOuterRadii, Path.Direction.CW);

            float x = (mRect.right + mRect.left) / 2;
            float y = (mRect.top + mRect.bottom) / 2;
            mPath.addCircle(x, y, mInnerRadii * innerScale, Path.Direction.CCW);

            mMatrix.reset();
            mMatrix.postScale(.707f, .707f, x, y);
            mMatrix.postRotate(45, x, y);
            mPath.transform(mMatrix);
        }


        float getRectOffset(float global) {
            float progress = 4 * global - 4 * global * global + .5f;
            return getCenter(progress);
        }

        float getRadiiOffset(float global) {
            float progress = .5f - 2 * (global - .75f) * (global - .75f);
            return getCenter(progress);
        }

        float getCenter(float progress) {
            return Math.min(1, Math.max((progress), 0));
        }

    }


    public static class NightDrawable extends Drawable implements Animatable {

        private boolean mRunning = false;
        private final static long FRAME_DURATION = 1000 / 60;
        private PorterDuffColorFilter mTintFilter;

        StarProvider starProvider;
        private Paint mPaint;

        public NightDrawable() {
            starProvider = new StarProvider();
            mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        }

        @Override
        protected void onBoundsChange(Rect bounds) {
            super.onBoundsChange(bounds);
            starProvider.setBound(bounds);
        }

        public Paint getPaint() {
            return mPaint;
        }

        @Override
        public void draw(Canvas canvas) {
            final Rect r = getBounds();

            final Paint paint = mPaint;

            final int prevAlpha = paint.getAlpha();

            // only draw shape if it may affect output
            if (paint.getAlpha() != 0 || paint.getXfermode() != null) {
                final boolean clearColorFilter;
                if (mTintFilter != null && paint.getColorFilter() == null) {
                    paint.setColorFilter(mTintFilter);
                    clearColorFilter = true;
                } else {
                    clearColorFilter = false;
                }
                if (starProvider != null) {
                    // need the save both for the translate, and for the (unknown)
                    // Shape
                    final int count = canvas.save();
                    canvas.translate(r.left, r.top);
                    starProvider.onDraw(canvas, paint);
                    canvas.restoreToCount(count);
                } else {
                    canvas.drawRect(r, paint);
                }

                if (clearColorFilter) {
                    paint.setColorFilter(null);
                }
            }

            // restore
            paint.setAlpha(prevAlpha);
        }

        @Override
        public void setAlpha(int alpha) {

        }

        @Override
        public void setColorFilter(ColorFilter colorFilter) {

        }

        @Override
        public int getOpacity() {
            return 0;
        }

        @Override
        public void start() {
            if (!mRunning) {
                mRunning = true;
                nextFrame();
            }
        }

        @Override
        public void stop() {
            if (mRunning) {
                unscheduleSelf(mNextFrame);
                starProvider.reset();
            }
        }

        @Override
        public boolean isRunning() {
            return mRunning;
        }


        private void nextFrame() {
            unscheduleSelf(mNextFrame);
            scheduleSelf(mNextFrame, SystemClock.uptimeMillis() + FRAME_DURATION);
        }

        private final Runnable mNextFrame = new Runnable() {
            @Override
            public void run() {
                boolean hasFrame = starProvider.nextFrame();
                if (hasFrame) {
                    nextFrame();
                    invalidateSelf();
                } else {
                    starProvider.reset();
                    nextFrame();
                    invalidateSelf();
                }
            }
        };
    }


    /**
     * 提供所有星星的出现和调动下一帧的
     * Created by zhou on 16-1-25.
     */
    public static class StarProvider {

        public static final int SIZE = 50;
        public static final int COUNT = 5;
        public static final int FRAME_OFFSET = 8; //帧片，不是时间偏移


        Pools.Pool<SimpleLoadingView.StarShape> shapePool = new Pools.SimplePool<>(6);

        List<StarInfo> pendingShow = new ArrayList<>();
        List<StarShape> currentShape = new ArrayList<>(3);
        List<StarShape> pendingRemove = new ArrayList<>();
        List<StarInfo> pendingRemoveInfo = new ArrayList<>();

        private long timeLine = 0;
        private Rect bound;

        public void setBound(Rect bound) {
            this.bound = bound;
            startScenes();
        }

        public boolean nextFrame() {
            boolean inProgress = false;
            timeLine++;
            provideStarOnTime(timeLine);
            for (StarShape starShape : currentShape) {
                inProgress = starShape.nextFrames();
                if (!inProgress) {
                    pendingRemove.add(starShape);
                    shapePool.release(starShape);
                }
            }
            currentShape.removeAll(pendingRemove);
            pendingRemove.clear();
            return inProgress || !pendingShow.isEmpty();
        }

        public void reset() {
            timeLine = 0;
            currentShape.clear();
            pendingShow.clear();
            if (bound != null) {
                startScenes();
            }
        }

        public void onDraw(Canvas canvas, Paint paint) {
            for (StarShape starShape : currentShape) {
                paint.setAlpha(200);
                starShape.draw(canvas, paint);
            }
        }

        /**
         * 开始布景，构建整个流程。
         */
        public void startScenes() {
            pendingShow.clear();
            int top = bound.centerY() - SIZE / 2;
            int left = (int) (bound.centerX() - (COUNT * SIZE * 1.5f) / 2);

            for (int i = 0; i < COUNT; i++) {
                StarInfo starInfo = new StarInfo(left + (i * SIZE * 1.5f), top, FRAME_OFFSET * (i + 1), SIZE, Color.BLACK);
                pendingShow.add(starInfo);
            }
        }

        void provideStarOnTime(long timeLine) {
            for (StarInfo starInfo : pendingShow) {
                if (starInfo.timeOffset <= timeLine) {
                    currentShape.add(starInfo.getStarShape());
                    pendingRemoveInfo.add(starInfo);
                }
            }
            pendingShow.removeAll(pendingRemoveInfo);
            pendingRemoveInfo.clear();
        }

        private class StarInfo {
            float offsetX;
            float offsetY;
            int timeOffset;
            float size;
            int color;

            public StarInfo(float offsetX, float offsetY, int timeOffset, float size, int color) {
                this.offsetX = offsetX;
                this.offsetY = offsetY;
                this.timeOffset = timeOffset;
                this.size = size;
                this.color = color;
            }

            public StarShape getStarShape() {
                StarShape starShape = shapePool.acquire();
                if (starShape == null) {
                    starShape = new StarShape(SIZE);
                }
                starShape.reset(size, offsetX, offsetY);
                return starShape;
            }
        }

    }


}
