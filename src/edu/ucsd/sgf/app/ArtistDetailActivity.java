package edu.ucsd.sgf.app;


import com.actionbarsherlock.app.SherlockActivity;
import android.os.Bundle;
import android.widget.FrameLayout;

import edu.ucsd.sgf.R;


public class ArtistDetailActivity extends SherlockActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.artist_detail);

        String artistName = getIntent().getStringExtra("artist_name");
        if(artistName == null) artistName = "Artist";
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(artistName);

        FrameLayout artistImageContainer = (FrameLayout)findViewById(R.id.artist_image_container);
        FrameLayout artistTextContainer = (FrameLayout)findViewById(R.id.artist_text_container);

        // TODO
        android.widget.ImageView artistImagePlaceholder = new android.widget.ImageView(this);
        artistImagePlaceholder.setImageResource(R.drawable.artist_image_placeholder);
        artistImageContainer.addView(artistImagePlaceholder);

        // TODO
        android.widget.TextView artistTextPlaceholder = new android.widget.TextView(this);
        artistTextPlaceholder.setText("Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.");
        int pad = (int)android.util.TypedValue.applyDimension(android.util.TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics());
        artistTextPlaceholder.setPadding(pad, pad, pad, pad);
        artistTextContainer.addView(artistTextPlaceholder);
    }


    @Override
    public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return false;
    }
}
