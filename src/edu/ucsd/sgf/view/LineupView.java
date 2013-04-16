package edu.ucsd.sgf.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;


public class LineupView extends View {

    private ScrollFlingAnimator mAnimator = null;
    private Paint mPaint = new Paint();

    private float scrollOffset = 0.0f;
    private float maxScrollOffset = 0.0f;


    public LineupView(Context context) {
        super(context);
    }


    @Override
    public void onDraw(Canvas canvas) {
    }


    public void pointerDown() {
        if(mAnimator != null) {
            removeCallbacks(mAnimator);
            mAnimator = null;
        }
    }


    public void fling(float vy) {
        if(mAnimator != null)
            removeCallbacks(mAnimator);
        post(mAnimator = new ScrollFlingAnimator(vy));
    }


    public void scroll(float dy) {
    }


    public void click(MotionEvent e) {
    }


    public void scale(float scaleFactor) {
    }


    public class ScrollFlingAnimator implements Runnable {

        public ScrollFlingAnimator(float vy) {
        }

        @Override
        public void run() {
        }
    }
}
