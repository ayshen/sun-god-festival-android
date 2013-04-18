package edu.ucsd.sgf.app;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import edu.ucsd.sgf.R;


public class InfoPagerAdapter extends FragmentPagerAdapter {

    private String[] mTitles = null;
    private InfoFragment[] mFragments = null;


    public Integer getLayoutIdForName(String name) {
        java.lang.reflect.Field f = null;

        try { f = R.layout.class.getField(name); }
        catch(NoSuchFieldException nsfe) {
            Log.e(this.toString(), "no field R.layout." + name);
            return null;
        }
        catch(NullPointerException npe) {
            Log.e(this.toString(), "tried to get R.layout.<null>");
            return null;
        }
        catch(SecurityException se) {
            Log.e(this.toString(), "not allowed to reflect on R.layout");
            return null;
        }

        try { return new Integer(f.getInt(null)); }
        catch(IllegalAccessException iaxe) {
            Log.e(this.toString(), "R.layout." + name + " inaccessible");
            return null;
        }
        catch(IllegalArgumentException iae) {
            Log.e(this.toString(), iae.toString());
            return null;
        }
        catch(NullPointerException npe) {
            Log.wtf(this.toString(), "expected an instance of R.layout");
            return null;
        }
        catch(ExceptionInInitializerError eiie) {
            Log.wtf(this.toString(), "tried to instantiate R.layout");
            return null;
        }
    }


    public InfoPagerAdapter(FragmentManager fmgr, Resources res) {
        super(fmgr);

        mTitles = res.getStringArray(R.array.info_titles);
        String[] layoutNames = res.getStringArray(R.array.info_layout_names);

        if(layoutNames.length != mTitles.length) {
            android.util.Log.w(this.toString(),
                    "mismatch between " + mTitles.length +
                    " information section titles and " + mFragments.length +
                    " information section layouts");
        }

        mFragments = new InfoFragment[layoutNames.length];
        for(int i = 0; i < layoutNames.length; ++i) {
            Integer id = getLayoutIdForName(layoutNames[i]);
            if(id == null) {
                Log.w(this.toString(), "failed to get id for R.layout." +
                        layoutNames[i]);
                mFragments[i] = null;
                continue;
            }
            mFragments[i] = InfoFragment.instantiate(id.intValue());
        }
    }


    @Override
    public int getCount() {
        if(mFragments.length > mTitles.length)
            return mTitles.length;
        return mFragments.length;
    }


    @Override
    public CharSequence getPageTitle(int position) {
        if(position >= mTitles.length)
            return "";
        return mTitles[position];
    }


    @Override
    public Fragment getItem(int position) {
        if(position >= mFragments.length)
            return null;
        return mFragments[position];
    }
}
