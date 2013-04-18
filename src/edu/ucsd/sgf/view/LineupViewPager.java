package edu.ucsd.sgf.view;

import java.util.ArrayList;

import android.content.Context;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import edu.ucsd.sgf.app.LineupFragment;


public class LineupViewPager extends ViewPager
        implements GestureDetector.OnGestureListener,
        ScaleGestureDetector.OnScaleGestureListener {

    private GestureDetector mGesturer = null;
    private ScaleGestureDetector mScaler = null;


    public LineupViewPager(Context context) {
        super(context);
        mGesturer = new GestureDetector(context, this);
        mScaler = new ScaleGestureDetector(context, this);
    }


    public LineupViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        mGesturer = new GestureDetector(context, this);
        mScaler = new ScaleGestureDetector(context, this);
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
        return true;
    }


    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float vx, float vy) {
        return true;
    }


    @Override
    public void onLongPress(MotionEvent e) { }


    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2,
            float dx, float dy) {
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
        return true;
    }


    @Override
    public void onScaleEnd(ScaleGestureDetector detector) { }
}
