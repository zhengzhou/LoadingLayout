package com.zayn.loadingview.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Animatable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.zayn.loadingview.library.NestedLoadingLayout;

/**
 * Created by zhou on 16-3-25.
 */
public class PullLoadView extends View implements Animatable {

    private int maxOffset = 200;
    private int radius = 0;
    private float resistance = 1.6f;

    private float offsetScroll = 0;
    private Paint shapePaint;
    private Paint loadingPaint;
    private Path path;
    private Path outLine;
    private boolean isRunning = false;
    private int progress = 0;

    private static int[] colors = {0xFF2095F2, 0xFFCCDB38, 0xFF4BAE4F, 0xFF6639B6};

    public PullLoadView(Context context) {
        this(context, null);
    }

    public PullLoadView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PullLoadView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ll_Pull_Refresh_View, defStyleAttr, R.style.ll__Simple_Load_Style);
        int color = a.getColor(R.styleable.ll_Pull_Refresh_View_ll__ViewColor, 0xffFF4081);
        radius = a.getDimensionPixelSize(R.styleable.ll_Pull_Refresh_View_ll__ViewRadius, 100);
        a.recycle();
        loadingPaint = new Paint();
        loadingPaint.setAntiAlias(true);
        shapePaint = new Paint();
        shapePaint.setColor(color);//set color
        shapePaint.setAntiAlias(true);

        path = new Path();
        outLine = new Path();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        outLine.reset();
        outLine.addCircle(getWidth()/2, maxOffset, radius, Path.Direction.CW);
    }

    public void setMaxOffset(int maxOffset) {
        this.maxOffset = maxOffset;

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(offsetScroll > 0) {
            if(maxOffset - offsetScroll > 10)
                canvas.drawPath(path, shapePaint);

            int centerX = getWidth() / 2;
            canvas.drawCircle(centerX, offsetScroll, Math.min(radius, offsetScroll / 3), shapePaint);

            drawConcentric(canvas);
        }
    }

    float currentRadius = 0;

    void doScroll(int offset){
        Log.d(NestedLoadingLayout.TAG, "offsetScroll :"+offsetScroll);

        if(offsetScroll >= maxOffset || offsetScroll < 0){

            return;
        }
        if(offsetScroll + offset >= maxOffset) {
            offsetScroll = maxOffset;
        }else {
            offsetScroll += offset;
        }

        final int width = getWidth();

        float start = (offsetScroll * width * 1.0f) / (maxOffset * 2.0f);

        path.reset();
        path.moveTo(start, 0);

        currentRadius = Math.min(radius, offsetScroll / 3);

        float y = (float) Math.sqrt(currentRadius* currentRadius + offsetScroll * offsetScroll /4) - offsetScroll/2;
        float x = (float) Math.sqrt(y*offsetScroll);
        float x1 = width/2 - x;
        float x2 = width/2 + x;
        float y1 = y + offsetScroll;

        path.cubicTo(width/4 + start/2, 0, width/4 + start/2 + x/2, offsetScroll/2, x1, y1);
        path.lineTo(x2, y1);
        path.cubicTo(width - width/4 - start/2 - x/2, offsetScroll/2, width*3/4 - start/2, 0, width - start, 0);
        path.close();
        invalidate();
    }

    void doLoading(){
        if(!isRunning){
            start();
        }
    }

    void reset(){
        path.reset();
        offsetScroll = 0;
        invalidate();
        stop();
    }

    void drawConcentric(Canvas canvas){
        if (isRunning) {
            int flag = canvas.save();
            canvas.clipPath(outLine);
            int centerX = getWidth() / 2;
            int interval = 10;
            for (int color : colors) {
                loadingPaint.setColor(color);
//                JLog.d("" + progress +".  "+ interval );
                if (progress - interval > 0) {
                    canvas.drawCircle(centerX, maxOffset, (progress - interval) % (radius + 10), loadingPaint);
                }
                interval += radius/4;
            }
            progress += 2;
            canvas.restoreToCount(flag);
            postInvalidateDelayed(16);
        }
    }

    @Override
    public void start() {
        isRunning = true;
        postInvalidate();
    }

    @Override
    public void stop() {
        isRunning = false;
    }

    @Override
    public boolean isRunning() {
        return isRunning;
    }

}
