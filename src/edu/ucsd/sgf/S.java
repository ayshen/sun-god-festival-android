package edu.ucsd.sgf;

import android.os.Bundle;

import com.actionbarsherlock.app.SherlockFragmentActivity;

public class S extends SherlockFragmentActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new android.view.View(this));
    }
}
