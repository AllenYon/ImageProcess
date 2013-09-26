package com.link.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.Scroller;

/**
 * Created with IntelliJ IDEA.
 * User: Link
 * Date: 13-3-21
 * Time: PM8:03
 * To change this template use File | Settings | File Templates.
 */
public class ScrollableBottomBar extends LinearLayout {
    private Scroller mScroller;
    private boolean mLocked = false;

    private static final Direction DEFAULT_DIRECTION = Direction.DOWN;
    private Direction mDirection;

    public enum Direction {
        Left, Right, UP, DOWN;
    }

    public boolean isLocked() {
        return mLocked;
    }

    public ScrollableBottomBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ScrollableBottomBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context ctx) {
        this.mScroller = new Scroller(ctx);
        this.mDirection = DEFAULT_DIRECTION;
    }

    public void setScrollDirection(Direction direction) {
        this.mDirection = direction;
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            postInvalidate();
        }
    }

    public synchronized void toggle() {
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        switch (mDirection) {
            case DOWN:
                if (mLocked) {
                    mScroller.startScroll(0, -height, 0, height);
                } else {
                    mScroller.startScroll(0, 0, 0, -height);
                }
                break;
            case UP:
                if (mLocked) {
                    mScroller.startScroll(0, height, 0, -height);
                } else {
                    mScroller.startScroll(0, 0, 0, height);
                }
                break;
            case Left:
                if (mLocked) {
                    mScroller.startScroll(width, 0, -width, 0);
                } else {
                    mScroller.startScroll(0, 0, width, 0);
                }
                break;
            case Right:
                if (mLocked) {
                    mScroller.startScroll(-width, 0, width, 0);
                } else {
                    mScroller.startScroll(0, 0, -width, 0);
                }
                break;
        }
//        int height = mDirection == Direction.DOWN ? getMeasuredHeight() : -getMeasuredHeight();
//        if (mLocked) {
//            mScroller.startScroll(0, -height, 0, height);
//        } else {
//            mScroller.startScroll(0, 0, 0, -height);
//        }
        mLocked = !mLocked;
        postInvalidate();
    }

}
