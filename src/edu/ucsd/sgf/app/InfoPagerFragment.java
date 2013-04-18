package edu.ucsd.sgf.app;

import android.os.Bundle;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockFragment;

import edu.ucsd.sgf.R;
import edu.ucsd.sgf.app.InfoPagerAdapter;


public class InfoPagerFragment extends SherlockFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.info_pager, container, false);

        PagerTabStrip tabs = (PagerTabStrip)v.findViewById(android.R.id.tabs);
        tabs.setTabIndicatorColor(getActivity().getResources().getColor(
                R.color.actionbar_divider_color));
        tabs.setGravity(android.view.Gravity.CENTER_VERTICAL);
        ((ViewPager)v.findViewById(android.R.id.tabcontent)).setAdapter(
                new InfoPagerAdapter(getChildFragmentManager(),
                        getActivity().getResources()));

        return v;
    }
}
