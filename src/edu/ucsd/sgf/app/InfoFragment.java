package edu.ucsd.sgf.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.ucsd.sgf.R;


public class InfoFragment extends Fragment {

    private int mLayoutId = 0;


    public static InfoFragment instantiate(int layoutId) {
        InfoFragment f = new InfoFragment();
        f.mLayoutId = layoutId;
        return f;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(mLayoutId, container, false);
    }
}
