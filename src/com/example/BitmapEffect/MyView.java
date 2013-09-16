package com.example.BitmapEffect;

import android.content.Context;
import android.graphics.*;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

public class MyView extends View {

    private static final float MINP = 0.25f;
    private static final float MAXP = 0.75f;

    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Path mPath;
    private Paint mBitmapPaint;

    private Paint mPaint;
    private MaskFilter mEmboss;
    private MaskFilter mBlur;

    public MyView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public MyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyView(Context c) {
        super(c);
        init();
    }

    private void init() {
        mPath = new Path();
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(0xFFFF0000);
//        mPaint.setColor(0x00000000);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(12);

        mEmboss = new EmbossMaskFilter(new float[]{1, 1, 1},
                0.4f, 6, 3.5f);

        mBlur = new BlurMaskFilter(8, BlurMaskFilter.Blur.NORMAL);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(0x00000000);

        canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);

        canvas.drawPath(mPath, mPaint);
    }

    private float mX, mY;
    private static final float TOUCH_TOLERANCE = 4;

    private void touch_start(float x, float y) {
        mPath.reset();
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
    }

    private void touch_move(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            mX = x;
            mY = y;
        }
    }

    private void touch_up() {
        mPath.lineTo(mX, mY);
        // commit the path to our offscreen
        mCanvas.drawPath(mPath, mPaint);
        // kill this so we don't double draw
        mPath.reset();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touch_start(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                touch_move(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                touch_up();
                invalidate();
                break;
        }
        return true;
    }


    public void setPaintColorAndStrokeWidth(int color, int width) {
        mPaint.setColor(color);
        mPaint.setStrokeWidth(width);
    }

    public enum Mode {
        Draw,
        Erase
    }

    private Mode mMode;

    public void setMode(Mode mode) {
        this.mMode = mode;
        updateMode();
    }

    public void updateMode() {
        switch (mMode) {
            case Draw:
                mPaint.setXfermode(null);
                break;
            case Erase:
                mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
                break;
        }
    }


    /**
     *  private static final int COLOR_MENU_ID = Menu.FIRST;
     private static final int EMBOSS_MENU_ID = Menu.FIRST + 1;
     private static final int BLUR_MENU_ID = Menu.FIRST + 2;
     private static final int ERASE_MENU_ID = Menu.FIRST + 3;
     private static final int SRCATOP_MENU_ID = Menu.FIRST + 4;

     @Override public boolean onCreateOptionsMenu(Menu menu) {
     super.onCreateOptionsMenu(menu);

     menu.add(0, COLOR_MENU_ID, 0, "Color").setShortcut('3', 'c');
     menu.add(0, EMBOSS_MENU_ID, 0, "Emboss").setShortcut('4', 's');
     menu.add(0, BLUR_MENU_ID, 0, "Blur").setShortcut('5', 'z');
     menu.add(0, ERASE_MENU_ID, 0, "Erase").setShortcut('5', 'z');
     menu.add(0, SRCATOP_MENU_ID, 0, "SrcATop").setShortcut('5', 'z');

     return true;
     }

     @Override public boolean onPrepareOptionsMenu(Menu menu) {
     super.onPrepareOptionsMenu(menu);
     return true;
     }

     @Override public boolean onOptionsItemSelected(MenuItem item) {
     mPaint.setXfermode(null);
     mPaint.setAlpha(0xFF);

     switch (item.getItemId()) {
     case COLOR_MENU_ID:
     new ColorPickerDialog(this, this, mPaint.getColor()).show();
     return true;
     case EMBOSS_MENU_ID:
     if (mPaint.getMaskFilter() != mEmboss) {
     mPaint.setMaskFilter(mEmboss);
     } else {
     mPaint.setMaskFilter(null);
     }
     return true;
     case BLUR_MENU_ID:
     if (mPaint.getMaskFilter() != mBlur) {
     mPaint.setMaskFilter(mBlur);
     } else {
     mPaint.setMaskFilter(null);
     }
     return true;
     case ERASE_MENU_ID:
     mPaint.setXfermode(new PorterDuffXfermode(
     PorterDuff.Mode.CLEAR));
     return true;
     case SRCATOP_MENU_ID:
     mPaint.setXfermode(new PorterDuffXfermode(
     PorterDuff.Mode.SRC_ATOP));
     mPaint.setAlpha(0x80);
     return true;
     }
     return super.onOptionsItemSelected(item);
     }
     */
}