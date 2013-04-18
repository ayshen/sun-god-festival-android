package edu.ucsd.sgf.app;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.PagerTabStrip;
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

        PagerTabStrip tabs = (PagerTabStrip)layout.findViewById(
                android.R.id.tabs);
        tabs.setTabIndicatorColor(getActivity().getResources().getColor(
                R.color.actionbar_divider_color));
        tabs.setGravity(android.view.Gravity.CENTER_VERTICAL);

        mPager = (LineupViewPager)layout.findViewById(android.R.id.tabcontent);
        mAdapter = new LineupPagerAdapter(
                this.getChildFragmentManager(),
                getActivity().getResources(),
                mPager);

        mPager.setAdapter(mAdapter);

        return layout;
    }
}
