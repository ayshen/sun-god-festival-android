package edu.ucsd.sgf.app;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockFragment;

import edu.ucsd.sgf.R;
import edu.ucsd.sgf.view.LineupViewPager;


public class LineupPagerFragment extends SherlockFragment {

    private LineupPagerAdapter mAdapter;
    private LineupViewPager mPager;


    private final String tag() {
        return getClass().getName() + "@" + Integer.toHexString(hashCode());
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        if(container == null) {
            Log.e(tag(), "(fatal) no containing ViewGroup");
            return null;
        }

        if(getActivity() == null) {
            Log.e(tag(), "(fatal) no parent activity");
            return null;
        }

        View layout = inflater.inflate(R.layout.lineup_pager, container,
                false);

        mAdapter = new LineupPagerAdapter(
                this.getChildFragmentManager(),
                getActivity().getResources());

        mPager = (LineupViewPager)layout.findViewById(android.R.id.tabcontent);
        mPager.setAdapter(mAdapter);

        return layout;
    }
}
