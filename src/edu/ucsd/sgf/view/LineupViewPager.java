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

    private LineupFragment[] mGestureObservers = null;
    private GestureDetector mGesturer = null;
    private ScaleGestureDetector mScaler = null;


    public LineupViewPager(Context context) {
        super(context);
        mGestureObservers = new LineupFragment[3];
        for(int i = 0; i < mGestureObservers.length; ++i)
            mGestureObservers[i] = null;
        mGesturer = new GestureDetector(context, this);
        mScaler = new ScaleGestureDetector(context, this);
    }


    public LineupViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        mGestureObservers = new LineupFragment[3];
        for(int i = 0; i < mGestureObservers.length; ++i)
            mGestureObservers[i] = null;
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
        for(int i = 0; i < mGestureObservers.length; ++i)
            if(mGestureObservers[i] != null)
                mGestureObservers[i].getLineupView().pointerDown();
        return true;
    }


    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float vx, float vy) {
        for(int i = 0; i < mGestureObservers.length; ++i)
            if(mGestureObservers[i] != null)
                mGestureObservers[i].getLineupView().fling(vy);
        return true;
    }


    @Override
    public void onLongPress(MotionEvent e) { }


    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2,
            float dx, float dy) {
        for(int i = 0; i < mGestureObservers.length; ++i)
            if(mGestureObservers[i] != null)
                mGestureObservers[i].getLineupView().scroll(dy);
        return true;
    }


    @Override
    public void onShowPress(MotionEvent e) { }


    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        FragmentPagerAdapter adapter = (FragmentPagerAdapter)getAdapter();
        LineupFragment fragment = (LineupFragment)adapter.getItem(
                getCurrentItem());
        fragment.getLineupView().click(e);
        return true;
    }


    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        return true;
    }


    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        for(int i = 0; i < mGestureObservers.length; ++i)
            if(mGestureObservers[i] != null)
                mGestureObservers[i].getLineupView().scale(
                        detector.getScaleFactor());
        return true;
    }


    @Override
    public void onScaleEnd(ScaleGestureDetector detector) { }


    public boolean addGestureObserver(LineupFragment f) {
        for(int i = 0; i < mGestureObservers.length; ++i) {
            if(mGestureObservers[i] == null) {
                mGestureObservers[i] = f;
                return true;
            }
        }
        return false;
    }


    public boolean removeGestureObserver(LineupFragment f) {
        for(int i = 0; i < mGestureObservers.length; ++i) {
            if(mGestureObservers[i] == f) {
                mGestureObservers[i] = null;
                return true;
            }
        }
        return false;
    }
}
