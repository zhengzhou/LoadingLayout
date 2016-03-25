package com.zayn.loadingview.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.zayn.loadingview.library.NestedLoadingLayout;

/**
 * Created by zhou on 16-3-25.
 */
public class PullLoadView extends View {

    private int offsetScroll = 0;
    private Paint circlePaint;
    private Path path;

    private int maxOffset = 200;
    private int radius = 0;

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
        circlePaint = new Paint();
        circlePaint.setColor(color);//set color
        circlePaint.setAntiAlias(true);
        path = new Path();
    }

    public void setMaxOffset(int maxOffset) {
        this.maxOffset = maxOffset;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(offsetScroll > 0) {
            int centerX = getWidth() / 2;
            canvas.drawCircle(centerX, offsetScroll, Math.min(radius, offsetScroll / 3), circlePaint);

            if(maxOffset - offsetScroll > 10)
                canvas.drawPath(path, circlePaint);
        }
    }

    void doScroll(int offset){
        Log.d(NestedLoadingLayout.TAG, "offsetScroll :"+offsetScroll);

        if(offsetScroll >= maxOffset || offsetScroll < 0){
            return;
        }

        final int width = getWidth();
        offsetScroll += offset;

        float start = (offsetScroll * width * 1.0f) / (maxOffset * 2.0f);

        path.reset();
        path.moveTo(start, 0);
        path.lineTo(width / 2, offsetScroll);
        path.lineTo(width - start, 0);
        path.close();
        invalidate();
    }

    void doLoading(){

    }

    void reset(){
        path.reset();
        offsetScroll = 0;
        invalidate();
    }
}
