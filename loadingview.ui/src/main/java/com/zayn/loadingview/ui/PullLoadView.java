package com.zayn.loadingview.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.zayn.loadingview.library.NestedLoadingLayout;

/**
 * Created by zhou on 16-3-25.
 */
public class PullLoadView extends View {

    private float offsetScroll = 0;
    private Paint circlePaint;
    private Paint pathPaint;
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
        pathPaint = new Paint();
        pathPaint.setColor(Color.BLACK);
        pathPaint.setAntiAlias(true);
        circlePaint = new Paint();
        circlePaint.setColor(color);//set color
        circlePaint.setAntiAlias(true);
        //circlePaint.setShader(Shader)
//        circlePaint.setShadowLayer(3, 2, 1, Color.WHITE);
        path = new Path();
    }

    public void setMaxOffset(int maxOffset) {
        this.maxOffset = maxOffset;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(offsetScroll > 0) {
            if(maxOffset - offsetScroll > 10)
                canvas.drawPath(path, circlePaint);

            int centerX = getWidth() / 2;
            canvas.drawCircle(centerX, offsetScroll, Math.min(radius, offsetScroll / 3), circlePaint);
        }
    }

    float currentRadius = 0;

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

        currentRadius = Math.min(radius, offsetScroll / 3);

//        float x = (width/2-start) * offsetScroll/(2* offsetScroll + Math.min(radius, offsetScroll / 3));
        float y = (float) Math.sqrt(currentRadius* currentRadius + offsetScroll * offsetScroll /4) - offsetScroll/2;
        float x = (float) Math.sqrt(y*offsetScroll);
        float x1 = width/2 - x;
        float x2 = width/2 + x;
        float y1 = y + offsetScroll;

        path.cubicTo(width/4 + start/2, 0, width/4 + start/2 + x/2, offsetScroll/2, x1, y1);
        path.lineTo(x2, y1);
        //path.lineTo(width / 2, offsetScroll);
//        path.lineTo(width - start, 0);
        path.cubicTo(width - width/4 - start/2 - x/2, offsetScroll/2, width*3/4 - start/2, 0, width - start, 0);
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
