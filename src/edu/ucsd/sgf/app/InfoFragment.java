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
        InfoFragment f = new InfoFragment(layoutId);
        return f;
    }


    public InfoFragment() { mLayoutId = 0; }


    private InfoFragment(int id) {
        super();
        mLayoutId = id;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
//*
        if(mLayoutId == 0) {
            android.util.Log.e(this.toString(), "layout ID not set yet");
            return new View(getActivity());
        }
//*/
        return inflater.inflate(mLayoutId, container, false);
    }
}
