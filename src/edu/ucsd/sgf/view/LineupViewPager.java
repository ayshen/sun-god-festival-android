package edu.ucsd.sgf.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;


public class LineupViewPager extends ViewPager {

    public LineupViewPager(Context context) {
        super(context);
    }

    public LineupViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        boolean parentResult = super.onTouchEvent(e);
        return parentResult;
    }
}
