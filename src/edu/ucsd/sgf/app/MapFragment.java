package edu.ucsd.sgf.app;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockFragment;

import edu.ucsd.sgf.R;
import edu.ucsd.sgf.view.MapView;


public class MapFragment extends SherlockFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        if(container == null) {
            Log.e(this.toString(), "no container ViewGroup");
            return null;
        }

        if(getActivity() == null) {
            Log.e(this.toString(), "no parent Activity");
            return null;
        }

        return new MapView(getActivity());
    }
}
