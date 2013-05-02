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

    public final static int FONT_SIZE = 14;
    public final static float MINUTES_PER_TEXT_LINE = 5.0f;

    private final static int TOUCH_MODE_INITIAL_STATE = 0;
    private final static int TOUCH_MODE_VSCROLL = 1;
    private final static int TOUCH_MODE_PAGE = 2;

    private ScrollFlingAnimator mAnimator = null;

    private GestureDetector mGesturer = null;
    private ScaleGestureDetector mScaler = null;

    private float scrollOffset = 0.0f;
    private float zoom = 1.0f;
    private float baseMaxY = 0.0f;
    private float maxScrollOffset = 0.0f;
    private float fontHeight = 0.0f;

    private int mTouchMode = TOUCH_MODE_INITIAL_STATE;

    private static int PAGERTABSTRIP_HEIGHT_DIP = 48;
    public static float PAGERTABSTRIP_HEIGHT = 0.0f;


    /** Set up gesture detectors and measurement constants.
    Register this view to respond to scroll, fling, and scale gestures. Also
    decide on how tall five minutes will be when we are zoomed all the way out.
    @param context {@link android.content.Context} object used for display
    metrics, to help determine how tall five minutes is.
    */
    private void init(Context context) {
        mGesturer = new GestureDetector(context, this);
        mScaler = new ScaleGestureDetector(context, this);
        Paint p = new Paint();
        p.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                FONT_SIZE, context.getResources().getDisplayMetrics()));
        fontHeight = p.descent() - p.ascent();

        // This is a magic number for getting a little space back from
        // the PagerTabStrip that we've anchored at the top of this
        // view for displaying the stage names.
        PAGERTABSTRIP_HEIGHT = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, PAGERTABSTRIP_HEIGHT_DIP,
                context.getResources().getDisplayMetrics());
    }


    public LineupViewPager(Context context) {
        super(context);
        init(context);
    }


    public LineupViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }


    /** Set the adapter that provides the Fragments for this view.
    Catch the adapter on the way in so that we can compute how tall we
    expect the view to be when it's zoomed all the way out.
    */
    @Override
    public void setAdapter(PagerAdapter adapter) {
        super.setAdapter(adapter);
        baseMaxY = getLineupAdapter().getTimeRange() * fontHeight /
                MINUTES_PER_TEXT_LINE;
    }


    /** Be notified when the dimensions of this view change.
    We'll use it to compute limits for vertical scrolling.
    */
    @Override
    protected void onSizeChanged(int w, int h, int _w, int _h) {
        super.onSizeChanged(w, h, _w, _h);
        recomputeMaxScrollOffset();
    }


    @Override
    public boolean onTouchEvent(MotionEvent e) {
        boolean parentResult = false;

        // Pass the touch event to the gesture detectors, so we can detect
        // things like scrolling, fling, and pinch-to-zoom.
        boolean gesturerResult = mGesturer.onTouchEvent(e);
        boolean scalerResult = mScaler.onTouchEvent(e);

        // If the user hasn't locked this view into vertical scrolling,
        // allow the ViewPager implementation to have an opportunity to change
        // pages. 
        if(mTouchMode == TOUCH_MODE_INITIAL_STATE ||
                mTouchMode == TOUCH_MODE_PAGE)
            parentResult = super.onTouchEvent(e);

        if(e.getAction() == MotionEvent.ACTION_UP)
            mTouchMode = TOUCH_MODE_INITIAL_STATE;

        return parentResult || gesturerResult || scalerResult;
    }


    @Override
    public boolean onDown(MotionEvent e) {
        // Stop any currently running fling animation. This gives an impression
        // of having "caught" the view.
        if(mAnimator != null) {
            removeCallbacks(mAnimator);
            mAnimator = null;
        }

        // Reset the touch mode, because we don't know what the user is going
        // to do next.
        mTouchMode = TOUCH_MODE_INITIAL_STATE;

        return true;
    }


    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float vx, float vy) {
        // If we are scrolling vertically, let the user throw the view around.
        // Horizontal flings are handled by the ViewPager implementation, which
        // should be called if this one isn't triggered.
        if(mTouchMode == TOUCH_MODE_VSCROLL)
            post(mAnimator = new ScrollFlingAnimator((int)vy / 20));
        return true;
    }


    @Override
    public void onLongPress(MotionEvent e) { }


    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2,
            float dx, float dy) {
        // Scroll vertically. This happens regardless of whether the user is
        // changing pages.
        scrollOffset = clamp(scrollOffset + dy, 0, maxScrollOffset);

        // Update all the sub-views.
        getLineupAdapter().update(scrollOffset, zoom);

        // Try to infer if the user is trying to scroll vertically or
        // horizontally. If so, set the touch mode so we can lock into vertical
        // scrolling if necessary.
        if(mTouchMode == TOUCH_MODE_INITIAL_STATE) {

            if(Math.abs(dy) >= 2 * Math.abs(dx)) {
                // Set the touch mode to vertical scrolling only.
                mTouchMode = TOUCH_MODE_VSCROLL;

                // Force the ViewPager implementation to return to resting
                // state on the current page, if it has been tensioned out of
                // place during scroll.
                MotionEvent e3 = MotionEvent.obtain(e2);
                e3.setAction(MotionEvent.ACTION_CANCEL);
                super.onTouchEvent(e3);
            }
            else if(Math.abs(dx) >= 2 * Math.abs(dy)) {
                mTouchMode = TOUCH_MODE_PAGE;
            }
        }

        return true;
    }


    @Override
    public void onShowPress(MotionEvent e) { }


    /** Handle a lineup view being tapped.
    @param e {@link android.view.MotionEvent} with the information about where
    the lineup view was touched.
    */
    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        LineupFragment f = (LineupFragment)getLineupAdapter().getItem(
                this.getCurrentItem());
        return f.getLineupView().onSingleTapUp(e);
        //return true;
    }


    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        return true;
    }


    /** Handle the view being scaled with a pinch-to-zoom gesture.
    @param detector {@link android.view.ScaleGestureDetector} with all the
    information about the gesture.
    */
    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        // Preserve the relative scroll offset (from 0.0 to 1.0 as a percentage
        // of the height of the view).
        float ratio = (detector.getFocusY() + scrollOffset) / (zoom * baseMaxY);

        // Zoom the view. Make sure it doesn't get too small or too big.
        zoom = clamp(zoom * detector.getScaleFactor(), 1.0f, 8.0f);

        // Recompute the vertical scroll limits.
        recomputeMaxScrollOffset();

        // Restore the relative scroll offset. This gives the impression that
        // the scaling was centered around the gesture focus.
        scrollOffset = clamp(ratio * zoom * baseMaxY - detector.getFocusY(),
                0, maxScrollOffset);

        // Re-render all the sub-views.
        getLineupAdapter().update(scrollOffset, zoom);
        return true;
    }


    @Override
    public void onScaleEnd(ScaleGestureDetector detector) { }


    /** Internal access to the LineupPagerAdapter that controls the sub-views.
    Mostly here for convenience, so we don't have to cast getAdapter() every
    time we use it for updating the sub-views.
    */
    private LineupPagerAdapter getLineupAdapter() {
        return (LineupPagerAdapter)getAdapter();
    }


    /** Utility function for clamping a value to a range.
    @param value the input value.
    @param min the minimum allowed value.
    @param max the maximum allowed value.
    @return min if value is too small; max if value is too big; or value.
    */
    private float clamp(float value, float min, float max) {
        if(value < min)
            return min;
        if(value > max)
            return max;
        return value;
    }


    /** Recompute the maximum offset allowed for the view content, which
    determines where the perceived bottom of the view content will be.
    This is used to update the vertical scroll properties of this view after
    it is subjected to scale gestures.
    */
    private void recomputeMaxScrollOffset() {
        // The max scroll offset is the length of the content under its current
        // zoom level, less the height of the view because we're drawing from
        // the top.
        maxScrollOffset = baseMaxY * zoom - getHeight() + PAGERTABSTRIP_HEIGHT;
    }


    /*
    Animation function used to continue vertical scrolling after a fling
    gesture. The scroll gradually slows down.
    */
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
            // Apply a drag force the view.
            if(mDeltaY <= PIXEL_VELOCITY_THRESHOLD) {

                // Velocity is too small to use the exponential decay function.
                // Just bring it closer to 0.
                mDeltaY -= DENORMALIZED_DECELERATION;
                if(mDeltaY < 0)
                    mDeltaY = 0;
            }
            else
                mDeltaY = (int)(mDeltaY * FRICTION_COEFFICIENT);

            // Update the scroll state.
            if(mSignDeltaY == 1)
                scrollOffset -= mDeltaY;
            else
                scrollOffset += mDeltaY;

            // Clamp the scroll state to the content limits.
            if(scrollOffset < 0) {
                scrollOffset = 0;
                mDeltaY = 0;
            }
            if(scrollOffset > maxScrollOffset) {
                scrollOffset = maxScrollOffset;
                mDeltaY = 0;
            }

            // Schedule this animation to be run again if there's any visible
            // change left to display.
            if(mDeltaY == 0)
                mAnimator = null;
            else
                postDelayed(this, SCROLL_REPEAT_INTERVAL);

            // Redraw all the sub-views to corroborate the new view state.
            getLineupAdapter().update(scrollOffset, zoom);
            invalidate();
        }
    }
}
