package com.example.jrpotter.appetite;

import android.app.FragmentTransaction;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.example.jrpotter.appetite.drawer.DrawerFragment;
import com.example.jrpotter.appetite.drawer.view.DrawerItemView;
import com.example.jrpotter.appetite.drawer.view.ProfileView;
import com.example.jrpotter.appetite.login.LoginActivity;
import com.example.jrpotter.appetite.main.MainDrawerFragment;
import com.example.jrpotter.appetite.tool.session.UserSession;
import com.example.jrpotter.appetite.tool.UserSettings;
import com.example.jrpotter.appetite.tool.UserStorage;
import com.example.jrpotter.appetite.tool.UserLocation;
import com.facebook.AppEventsLogger;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

/**
 * The root of the application, only accessible via a valid login/account creation.
 *
 * Because of usage of nested fragments, I manually go through different views and set
 * OnClickListeners to their corresponding parent fragment. This is to avoid overloading
 * this class with events.
 *
 * Created by jrpotter on 12/31/2014.
 */
public class MainActivity extends ActionBarActivity {

    private Toolbar mToolbar;

    private ListView mDrawerList;
    private String[] mDrawerClasses;

    private DrawerLayout mDrawerLayout;
    private DrawerFragment mActiveFragment;
    private ActionBarDrawerToggle mDrawerToggle;


    // Hook Methods
    // ==================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize all tools
        initializeTools();
        initializeToolbar();
        initializeNavigationDrawer();

        // Set Starting Fragment
        if(savedInstanceState == null) {
            mActiveFragment = new MainDrawerFragment();
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.add(R.id.main_activity_content_frame, mActiveFragment).commit();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putBoolean(UserStorage.STATE_FRAGMENT_ADDED, true);
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


    // Initialization Methods
    // ==================================================

    /**
     * Setup all singletons and libraries necessary for functionality of the application.
     */
    private void initializeTools() {

        // Basic Initialization
        UserStorage.getInstance(this);
        UserSession.getInstance(this);
        UserSettings.getInstance(this);
        UserLocation.getInstance(this);

        // UIL initialization
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).build();
        ImageLoader.getInstance().init(config);
    }

    /**
     * Replace the default action bar with a custom toolbar to allow
     * the navigation drawer to slide over all fragments (and the toolbar).
     */
    private void initializeToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.main_activity_toolbar);
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
    }

    /**
     * Setup the navigation drawer to properly slide out and open the
     * necessary fragments when a list item is selected.
     */
    private void initializeNavigationDrawer() {

        // Initialize
        mDrawerList = (ListView) findViewById(R.id.main_activity_drawer);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.activity_main_drawer_layout);
        mDrawerClasses = getResources().getStringArray(R.array.drawer_classes);

        // Set the adapter & listeners
        mDrawerList.setOnItemClickListener(new NavigationClickListener());
        mDrawerList.setAdapter(new NavigationListAdapter());

        // Allow Drawer Toggling
        mDrawerToggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, mToolbar, R.string.drawer_open, R.string.drawer_close);
        mDrawerLayout.setDrawerListener(mDrawerToggle);

    }


    // Navigation Methods
    // ==================================================

    @Override
    public void onBackPressed() {
        if(mActiveFragment == null || !mActiveFragment.onBackPressed()) {
            if(getFragmentManager().getBackStackEntryCount() > 0) {
                getFragmentManager().popBackStack();
            } else {
                super.onBackPressed();
            }
        }
    }


    // Navigation List Adapter
    // ==================================================

    /**
     * POD of a drawer list item.
     * Used solely in the getItem method of the NavigationListAdapter.
     */
    private class NavigationListItem {
        public int iconId = 0;
        public String name = "";
        public NavigationListItem(String name, int iconId) {
            this.name = name;
            this.iconId = iconId;
        }
    }

    /**
     * Drawer titles and icons are saved in a typed array.
     *
     * The following adapter pulls these resources and returns the view corresponding
     * to the list item.
     */
    private class NavigationListAdapter extends BaseAdapter {

        private TypedArray drawerItems;

        public NavigationListAdapter() {
            drawerItems = getResources().obtainTypedArray(R.array.drawer_items);
        }

        @Override
        public int getCount() {
            return drawerItems.length() / 2 + 1;
        }

        @Override
        public NavigationListItem getItem(int position) {
            if(position == 0) {
                return new NavigationListItem("", 0);
            } else {
                int index = position - 1;
                String name = drawerItems.getString(2 * index);
                int resourceId = drawerItems.getResourceId(2 * index + 1, 0);
                return new NavigationListItem(name, resourceId);
            }
        }

        @Override
        public long getItemId(int position) {
            return getItem(position).iconId;
        }

        /**
         * The very first view is a profile view displaying details about
         * the user. All views following allow for navigation between drawers.
         *
         * @param position ""
         * @param convertView ""
         * @param parent ""
         * @return ""
         */
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(position == 0) {
                return new ProfileView(MainActivity.this);
            } else {
                NavigationListItem item = getItem(position);
                return new DrawerItemView(MainActivity.this, item.name, item.iconId);
            }
        }
    }


    // Navigation Click Listener
    // ==================================================

    /**
     * Enables switching between different parts of the application.
     */
    private class NavigationClickListener implements ListView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            // Initiate sign out & return to login screen
            if(position == mDrawerClasses.length) {
                UserSession.getInstance().logout();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();

                // All other items (not the header)
            } else if(position > 0) {
                try {

                    // Build Fragment and commit transaction
                    mActiveFragment = (DrawerFragment) Class.forName(mDrawerClasses[position-1]).newInstance();
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.main_activity_content_frame, mActiveFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();

                    // Highlight the selected item, update the title, and close the drawer
                    mDrawerList.setItemChecked(position, true);
                    mDrawerLayout.closeDrawer(mDrawerList);

                } catch (ClassNotFoundException e) {
                    Log.e("NAV", "Invalid drawer class name");
                } catch (InstantiationException e) {
                    Log.e("NAV", "Could not instantiate fragment");
                } catch (IllegalAccessException e) {
                    Log.e("NAV", "Could not access fragment");
                }
            }

        }
    }

}