package edu.ucsd.sgf;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.app.SherlockFragment;


public class S extends SherlockFragmentActivity
        implements ActionBar.OnNavigationListener {

    // Bundle key for the index of the current application section.
    private final static String SECTION = "currentSection";


    // The names of fragment classes that are responsible for implementing
    // the application content.
    private String[] mFragmentClassNames = null;

    // The index of the current application section.
    private int currentSection = 0;


    /** Set up this activity.
    Prepare the action bar and navigation system.
    @param savedInstanceState The previous state of the application, if any.
    Usually <code>null</code>.
    */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Use a basic content layout that only has one item, which will be
        // replaced by the content for the current application section.
        setContentView(R.layout.main);

        // For our own usage, be verbose in the system log about how fragments
        // are used.
        FragmentManager.enableDebugLogging(true);

        // Load the names of the fragment classes for each section.
        try { mFragmentClassNames = getResources().getStringArray(
                R.array.navigation_fragment_class_names); }
        catch(Resources.NotFoundException rnfe) {
            Log.e(this.toString(), "no R.array.navigation_fragment_class_names");
            mFragmentClassNames = null;
        }

        // Set up the action bar to use a navigation spinner.
        ActionBar ab = getSupportActionBar();
        ab.setTitle("");
        ab.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        ab.setListNavigationCallbacks(ArrayAdapter.createFromResource(
                        this, R.array.navigation_spinner_items,
                        android.R.layout.simple_spinner_dropdown_item), this);

        // Restore knowledge of the current application section, if applicable.
        if(savedInstanceState != null) {
            currentSection = savedInstanceState.getInt(SECTION, 0);
        }

        // Display the first application section.
        if(mFragmentClassNames != null &&
                mFragmentClassNames.length > currentSection)
            getSupportFragmentManager().beginTransaction().replace(
                    R.id.fragment_container, SherlockFragment.instantiate(this,
                            mFragmentClassNames[currentSection])).commit();
    }


    /** Change sections.
    This callback handles a change in the navigation spinner's selected item,
    which triggers a change of application sections.
    @param position The index of the selected item in the navigation spinner.
    @param id Not used.
    @return <code>true</code>
    */
    @Override
    public boolean onNavigationItemSelected(int position, long id) {
        // Set the current section index.
        currentSection = position;

        // Display the application section.
        // TODO restore state for the section as well.
        if(mFragmentClassNames != null &&
                mFragmentClassNames.length > currentSection)
            getSupportFragmentManager().beginTransaction().replace(
                    R.id.fragment_container, SherlockFragment.instantiate(this,
                            mFragmentClassNames[currentSection])).commit();

        return true;
    }


    /** Save instance state.
    @param outState {@link android.os.Bundle} that will hold application state.
    */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SECTION, currentSection);
    }
}
