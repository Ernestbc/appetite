package guru.apps.llc.appetite.main;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.concurrent.Callable;

import guru.apps.llc.appetite.R;
import guru.apps.llc.appetite.drawer.DrawerFragment;
import guru.apps.llc.appetite.login.LoginActivity;
import guru.apps.llc.appetite.mood.MoodDrawerFragment;
import guru.apps.llc.appetite.settings.SettingsDrawerFragment;
import guru.apps.llc.appetite.tool.UserSession;

/**
 * Delegates what happens when each navigation item is clicked.
 *
 * In the case of the profile (index 0), nothing should occur.
 * In the case of sign_out (index last), should pop to logout screen.
 * Otherwise, should open the requested drawer.
 *
 * Created by jrpotter on 2/7/15.
 */
public class DrawerItemClickListener implements ListView.OnItemClickListener {

    // The activity the adapter is tied to
    private Activity mActivity;

    // The drawer currently on display
    private DrawerFragment mActiveDrawerFragment;

    // Allow for additional callbacks to be run when a drawer item is selected
    private ArrayList<DrawerItemCallback> callbacks;


    // Constructor
    // ==================================================

    public DrawerItemClickListener(Activity activity) {
        mActivity = activity;
        callbacks = new ArrayList<>();
    }


    // Callback Methods
    // ==================================================

    public void addCallback(DrawerItemCallback callback) {
        callbacks.add(callback);
    }


    // OnItemClickListener Methods
    // ==================================================

    public void setActiveDrawer(DrawerFragment activeDrawer) {
        mActiveDrawerFragment = activeDrawer;
    }

    public DrawerFragment getActiveDrawer() {
        return mActiveDrawerFragment;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        // First, if the last item is selected (which is sign out), we must return
        // back to the main login screen, remove the activity off the stack, and
        // clear out all credentials
        if(id == R.drawable.ic_sign_out) {

            UserSession.logoutAndClearCredentials();

            Intent intent = new Intent(mActivity, LoginActivity.class);
            mActivity.startActivity(intent);
            mActivity.finish();

        // Next, if any item other than the header was selected, we must instantiate
        // the class referred to by the class name
        } else {

            if(id == R.drawable.ic_home) {
                mActiveDrawerFragment = new MoodDrawerFragment();
            } else if(id == R.drawable.ic_settings) {
                mActiveDrawerFragment = new SettingsDrawerFragment();
            }

            // Replace with designated fragment
            FragmentTransaction transaction = mActivity.getFragmentManager().beginTransaction();
            transaction.replace(R.id.main_activity_content_frame, mActiveDrawerFragment);
            transaction.addToBackStack(null);
            transaction.commit();

            // Update the UI and close the drawer
            ListView drawerList = (ListView) mActivity.findViewById(R.id.main_activity_drawer);
            DrawerLayout drawerLayout = (DrawerLayout) mActivity.findViewById(R.id.activity_main_drawer_layout);
            drawerList.setItemChecked(position, true);
            drawerLayout.closeDrawer(drawerList);

        }

        // Lastly, run any callbacks hooked into the click listener
        for(DrawerItemCallback callback : callbacks) {
            callback.onItemClick(parent, view, position, id);
        }

    }

}
