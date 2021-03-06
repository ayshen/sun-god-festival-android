package edu.ucsd.sgf.app;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockFragment;

import edu.ucsd.sgf.util.Performance;
import edu.ucsd.sgf.view.LineupView;
import edu.ucsd.sgf.view.LineupViewPager;


public class LineupFragment extends SherlockFragment {

    private Performance[] mLineup = null;
    private int mRange;

    private LineupView mView = null;


    public static LineupFragment instantiate(Performance[] lineup,
            int timeRange) {
        LineupFragment f = new LineupFragment();
        f.mLineup = lineup;
        f.mRange = timeRange;
        return f;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        if(container == null) {
            Log.w(this.toString(), "no container");
            return null;
        }

        if(getActivity() == null) {
            Log.e(this.toString(), "no activity");
            return null;
        }

        mView = LineupView.instantiate(getActivity(), mLineup, mRange);
        return mView;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }


    public LineupView getLineupView() { return mView; }
}
