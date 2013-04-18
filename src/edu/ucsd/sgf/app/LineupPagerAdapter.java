package edu.ucsd.sgf.app;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import edu.ucsd.sgf.R;
import edu.ucsd.sgf.util.Performance;
import edu.ucsd.sgf.util.Time;
import edu.ucsd.sgf.util.Reflect;
import edu.ucsd.sgf.view.LineupViewPager;


public class LineupPagerAdapter extends FragmentPagerAdapter {

    private Performance[][] mLineups = null;
    private String[] mStageNames = null;
    private int timeRange = 0;

    private LineupFragment[] mFragments = null;


    public LineupPagerAdapter(FragmentManager fmgr, Resources res) {
        super(fmgr);

        // Load the lineups from their XML files.
        mLineups = Reflect.loadLineupsFromStringArray(res,
                R.array.lineup_files);

        // Compute the maximum time range.
        computeTimeRange();

        // Load the stage names.
        try { mStageNames = res.getStringArray(R.array.stage_names); }
        catch(Resources.NotFoundException rnfe) {
            Log.e(this.toString(), rnfe.toString());
        }

        // Make sure there's enough names to go around.
        // If there are more lineups than stages, we should use the number
        // of stage names as the number of lineups.
        if(mLineups.length != mStageNames.length) {
            Log.e(this.toString(), "mismatch between number of lineups (" +
                    mLineups.length + ") and stage names (" +
                    mStageNames.length + ')');
            if(mLineups.length > mStageNames.length) {
                String[] revisedStageNames = new String[mLineups.length];
                int i = 0;
                for(; i < mStageNames.length; ++i)
                    revisedStageNames[i] = mStageNames[i];
                for(; i < mLineups.length; ++i)
                    revisedStageNames[i] = "";
                mStageNames = revisedStageNames;
            }
        }

        // Create the fragment cache.
        mFragments = new LineupFragment[mStageNames.length];
        for(int i = 0; i < mFragments.length; ++i)
            mFragments[i] = null;
    }


    @Override
    public int getCount() {
        if(mLineups == null || mStageNames == null) return 0;
        return mStageNames.length;
    }


    @Override
    public CharSequence getPageTitle(int position) {
        return mStageNames[position];
    }


    @Override
    public Fragment getItem(int position) {
        if(mFragments[position] == null) {
            // Create and cache the fragment.
            mFragments[position] = LineupFragment.instantiate(
                    mLineups[position], timeRange);
        }
        // Yield the cached fragment.
        return mFragments[position];
    }


    public Performance[][] getLineups() {
        return mLineups;
    }


    public void update(float scrollOffset, float zoom) {
        for(LineupFragment f: mFragments)
            if(f != null && f.getLineupView() != null)
                f.getLineupView().update(scrollOffset, zoom);
    }


    private void computeTimeRange() {
        int minBegin = (new Time(24, 0)).intValue();
        int maxEnd = (new Time(0, 0)).intValue();

        for(Performance[] lineup: mLineups) {
            for(Performance p: lineup) {
                if(p.begin.intValue() < minBegin)
                    minBegin = p.begin.intValue();
                if(p.end.intValue() > maxEnd)
                    maxEnd = p.end.intValue();
            }
        }

        timeRange = maxEnd - minBegin;
    }


    public int getTimeRange() {
        return timeRange;
    }
}
