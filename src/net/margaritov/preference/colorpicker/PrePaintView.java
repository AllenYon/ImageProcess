package net.margaritov.preference.colorpicker;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created with IntelliJ IDEA.
 * User: Link
 * Date: 13-9-11
 * Time: AM10:31
 * To change this template use File | Settings | File Templates.
 */
public class PrePaintView extends View {
    public PrePaintView(Context context) {
        super(context);
        init();
    }

    public PrePaintView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PrePaintView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public int mColor;
    public int mStrokeWidth;
    public Paint mPaint;

    int w;
    int h;

    private void init() {
        mColor = Color.BLACK;
        mStrokeWidth = 10;
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(mColor);
        mPaint.setStrokeWidth(mStrokeWidth);
//        mPaint.setStrokeCap(Paint.Cap.ROUND);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.w = w;
        this.h = h;

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.WHITE);
        canvas.drawLine(5, h * 0.5f, w - 5, h * 0.5f, mPaint);
    }

    public void setStrokeWidth(int progress) {
        this.mStrokeWidth = progress;
        mPaint.setStrokeWidth(mStrokeWidth);
        invalidate();
    }

    public void setPreColor(int color) {
        this.mColor = color;
        mPaint.setColor(color);
        invalidate();

    }
}
