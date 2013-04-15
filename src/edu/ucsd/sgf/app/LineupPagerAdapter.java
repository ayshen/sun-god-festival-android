package edu.ucsd.sgf.app;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import edu.ucsd.sgf.R;
import edu.ucsd.sgf.util.Performance;
import edu.ucsd.sgf.util.Reflect;


public class LineupPagerAdapter extends FragmentPagerAdapter {

    private Performance[][] mLineups = null;
    private String[] mStageNames = null;


    public LineupPagerAdapter(FragmentManager fmgr, Resources res) {
        super(fmgr);

        mLineups = Reflect.loadLineupsFromStringArray(res,
                R.array.lineup_files);
        try { mStageNames = res.getStringArray(R.array.stage_names); }
        catch(Resources.NotFoundException rnfe) {
            Log.e(this.toString(), rnfe.toString());
        }
    }


    @Override
    public int getCount() {
        if(mLineups == null) return 0;
        return mLineups.length;
    }


    @Override
    public Fragment getItem(int position) {
        return new LineupFragment();
    }
}
