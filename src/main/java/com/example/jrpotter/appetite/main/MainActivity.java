package com.example.jrpotter.appetite.main;

import android.app.FragmentTransaction;
import android.app.SearchManager;
import android.content.Context;
import android.content.res.Configuration;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.example.jrpotter.appetite.R;
import com.example.jrpotter.appetite.mood.MoodDrawerFragment;
import com.example.jrpotter.appetite.tool.session.UserSession;
import com.example.jrpotter.appetite.tool.UserSettings;
import com.example.jrpotter.appetite.tool.UserLocation;
import com.facebook.AppEventsLogger;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

/**
 * The following is the main activity of the Appetite application.
 *
 * It contains a navigation drawer and a container to hold the selected
 * navigation drawer activity.
 *
 * Created by jrpotter on 12/31/2014.
 */
public class MainActivity extends ActionBarActivity {

    // In order to allow the navigation drawer to go over the
    // action bar, we remove the action bar entirely and set a
    // custom toolbar, which is placed in the activity like any
    // other view.
    private Toolbar mToolbar;

    // Enables the navigation drawer to come out over the screen or not
    // This must be synchronized over different configuration changes
    private ActionBarDrawerToggle mDrawerToggle;

    // Provides views when queried
    private DrawerListAdapter mListAdapter;

    // Delegates actions on list item clicks
    private DrawerItemClickListener mClickListener;


    // Initialization Methods
    // ==================================================

    private void initializeTools() {

        // Basic Initialization
        UserSession.getInstance(this);
        UserSettings.getInstance(this);
        UserLocation.getInstance(this);

        // UIL initialization
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).build();
        ImageLoader.getInstance().init(config);
    }

    private void initializeDrawer() {

        mListAdapter = new DrawerListAdapter(this);
        mClickListener = new DrawerItemClickListener(this);

        // Setup Navigation Drawer
        ListView drawerList = (ListView) findViewById(R.id.main_activity_drawer);
        drawerList.setOnItemClickListener(mClickListener);
        drawerList.setAdapter(mListAdapter);

        // Allow Drawer Toggling
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.activity_main_drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, mToolbar, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.setDrawerListener(mDrawerToggle);

    }

    private void initializeToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.main_activity_toolbar);
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
    }


    // Hook Methods
    // ==================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initializations
        initializeTools();
        initializeDrawer();
        initializeToolbar();

        // Set Starting Fragment
        mClickListener.mActiveDrawerFragment = new MoodDrawerFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.add(R.id.main_activity_content_frame, mClickListener.mActiveDrawerFragment);
        transaction.commit();
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        // TODO: Save active fragment
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onResume() {
        super.onResume();
        AppEventsLogger.activateApp(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        AppEventsLogger.deactivateApp(this);
    }

    @Override
    public void onBackPressed() {
        if(mClickListener.mActiveDrawerFragment == null || !mClickListener.mActiveDrawerFragment.onBackPressed()) {
            if(getFragmentManager().getBackStackEntryCount() > 0) {
                getFragmentManager().popBackStack();
            } else {
                super.onBackPressed();
            }
        }
    }


    // Search Methods
    // ==================================================

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.action_bar_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Delegate to navigation drawer if possible
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        // Otherwise
        return super.onOptionsItemSelected(item);
    }

}