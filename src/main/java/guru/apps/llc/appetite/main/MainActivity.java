package guru.apps.llc.appetite.main;

import android.app.FragmentTransaction;
import android.app.SearchManager;
import android.content.Context;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import guru.apps.llc.appetite.Appetite;
import guru.apps.llc.appetite.R;
import guru.apps.llc.appetite.mood.MoodDrawerFragment;
import guru.apps.llc.appetite.tool.UserLocation;
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

        // UIL initialization
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).build();
        ImageLoader.getInstance().init(config);

        // Location Initialization
        UserLocation.getInstance().init();

    }

    private void initializeDrawer() {

        mListAdapter = new DrawerListAdapter(this);
        mClickListener = new DrawerItemClickListener(this);

        // Initialize first drawer
        mClickListener.setActiveDrawer(new MoodDrawerFragment());

        // Setup Navigation Drawer
        ListView drawerList = (ListView) findViewById(R.id.main_activity_drawer);
        drawerList.setOnItemClickListener(mClickListener);
        drawerList.setAdapter(mListAdapter);

        // Allow Drawer Toggling
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.activity_main_drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, mToolbar, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.setDrawerListener(mDrawerToggle);

        // We must hide the shuffle icon whenever the main drawer item is not selected
        // Here we also perform all parent level modifications
        mClickListener.addCallback(new DrawerItemCallback() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // The idea is to allow each drawer to designate what occurs
                // If I also pass in the list adapter, perhaps I don't have to do any math
                // Simply see which one is selected and hide it from the drawer activity
                // mClickListener.getActiveDrawer().performActiveTransition(MainActivity.this);
                View shuffle = mToolbar.findViewById(R.id.action_bar_shuffle);
                shuffle.setVisibility(id == R.drawable.ic_home ? View.VISIBLE : View.INVISIBLE);
            }
        });

        // We also hide any view in the drawer list that was currently selected
        mClickListener.addCallback(new DrawerItemCallback() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position > 0 && id != R.drawable.ic_sign_out) {
                    mListAdapter.setActiveDrawer(position);
                }
            }
        });

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

        // Set starting fragment
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.add(R.id.main_activity_content_frame, mClickListener.getActiveDrawer());
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
        if(mClickListener.getActiveDrawer() == null || !mClickListener.getActiveDrawer().onBackPressed()) {
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
        // SearchManager searchManager =
        //         (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        // SearchView searchView =
        //         (SearchView) menu.findItem(R.id.action_bar_search).getActionView();
        // searchView.setSearchableInfo(
        //         searchManager.getSearchableInfo(getComponentName()));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Delegate to navigation drawer if possible
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        // If shuffle is selected, need to reload the main moods drawer fragment
        if (item.getItemId() == R.id.action_bar_shuffle) {
            mClickListener.setActiveDrawer(new MoodDrawerFragment());
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.main_activity_content_frame, mClickListener.getActiveDrawer());
            transaction.commit();
        }

        // Otherwise
        return super.onOptionsItemSelected(item);
    }

}