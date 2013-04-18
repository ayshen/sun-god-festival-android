package edu.ucsd.sgf.view;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Paint;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.PagerAdapter;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import edu.ucsd.sgf.app.LineupFragment;
import edu.ucsd.sgf.app.LineupPagerAdapter;


public class LineupViewPager extends ViewPager
        implements GestureDetector.OnGestureListener,
        ScaleGestureDetector.OnScaleGestureListener {

    public final static int FONT_SIZE = 12;
    public final static float MINUTES_PER_TEXT_LINE = 5.0f;

    private ScrollFlingAnimator mAnimator = null;

    private GestureDetector mGesturer = null;
    private ScaleGestureDetector mScaler = null;

    private float scrollOffset = 0.0f;
    private float zoom = 1.0f;
    private float baseMaxY = 0.0f;
    private float maxScrollOffset = 0.0f;
    private float fontHeight = 0.0f;


    private void init(Context context) {
        mGesturer = new GestureDetector(context, this);
        mScaler = new ScaleGestureDetector(context, this);
        Paint p = new Paint();
        p.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                FONT_SIZE, context.getResources().getDisplayMetrics()));
        fontHeight = p.descent() - p.ascent();
    }


    public LineupViewPager(Context context) {
        super(context);
        init(context);
    }


    public LineupViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }


    @Override
    public void setAdapter(PagerAdapter adapter) {
        super.setAdapter(adapter);
        baseMaxY = getLineupAdapter().getTimeRange() * fontHeight /
                MINUTES_PER_TEXT_LINE;
    }


    @Override
    protected void onSizeChanged(int w, int h, int _w, int _h) {
        super.onSizeChanged(w, h, _w, _h);
        recomputeMaxScrollOffset();
    }


    @Override
    public boolean onTouchEvent(MotionEvent e) {
        boolean parentResult = super.onTouchEvent(e);
        boolean gesturerResult = mGesturer.onTouchEvent(e);
        boolean scalerResult = mScaler.onTouchEvent(e);
        return parentResult || gesturerResult || scalerResult;
    }


    @Override
    public boolean onDown(MotionEvent e) {
        if(mAnimator != null) {
            removeCallbacks(mAnimator);
            mAnimator = null;
        }
        return true;
    }


    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float vx, float vy) {
        post(mAnimator = new ScrollFlingAnimator((int)vy / 20));
        return true;
    }


    @Override
    public void onLongPress(MotionEvent e) { }


    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2,
            float dx, float dy) {
        scrollOffset = clamp(scrollOffset + dy, 0, maxScrollOffset);
        getLineupAdapter().update(scrollOffset, zoom);
        return true;
    }


    @Override
    public void onShowPress(MotionEvent e) { }


    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return true;
    }


    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        return true;
    }


    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        float ratio = (detector.getFocusY() + scrollOffset) / (zoom * baseMaxY);
        zoom *= detector.getScaleFactor();
        if(zoom < 1.0f)
            zoom = 1.0f;
        recomputeMaxScrollOffset();
        scrollOffset = clamp(ratio * zoom * baseMaxY - detector.getFocusY(),
                0, maxScrollOffset);
        getLineupAdapter().update(scrollOffset, zoom);
        return true;
    }


    @Override
    public void onScaleEnd(ScaleGestureDetector detector) { }


    private LineupPagerAdapter getLineupAdapter() {
        return (LineupPagerAdapter)getAdapter();
    }


    private float clamp(float value, float min, float max) {
        if(value < min)
            return min;
        if(value > max)
            return max;
        return value;
    }


    private void recomputeMaxScrollOffset() {
        maxScrollOffset = baseMaxY * zoom - getHeight() +
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 48,
                getContext().getResources().getDisplayMetrics());
    }


    public class ScrollFlingAnimator implements Runnable {

        private final static int PIXEL_VELOCITY_THRESHOLD = 10;
        private final static int DENORMALIZED_DECELERATION = 2;
        private final static int SCROLL_REPEAT_INTERVAL = 16;
        private final static float FRICTION_COEFFICIENT = 0.7f;

        private int mDeltaY;
        private int mSignDeltaY;

        public ScrollFlingAnimator(int vy) {
            mDeltaY = (int)Math.abs(vy);
            if(mDeltaY == 0)
                mSignDeltaY = 1;
            else
                mSignDeltaY = vy / mDeltaY;
        }

        @Override
        public void run() {
            if(mDeltaY <= PIXEL_VELOCITY_THRESHOLD) {
                mDeltaY -= DENORMALIZED_DECELERATION;
                if(mDeltaY < 0)
                    mDeltaY = 0;
            }
            else
                mDeltaY = (int)(mDeltaY * FRICTION_COEFFICIENT);

            if(mSignDeltaY == 1)
                scrollOffset -= mDeltaY;
            else
                scrollOffset += mDeltaY;

            if(scrollOffset < 0) {
                scrollOffset = 0;
                mDeltaY = 0;
            }
            if(scrollOffset > maxScrollOffset) {
                scrollOffset = maxScrollOffset;
                mDeltaY = 0;
            }

            if(mDeltaY == 0)
                mAnimator = null;
            else
                postDelayed(this, SCROLL_REPEAT_INTERVAL);

            getLineupAdapter().update(scrollOffset, zoom);
            invalidate();
        }
    }
}
