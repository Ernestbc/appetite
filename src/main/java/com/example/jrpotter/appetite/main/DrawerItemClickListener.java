package com.example.jrpotter.appetite.main;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.jrpotter.appetite.R;
import com.example.jrpotter.appetite.drawer.DrawerFragment;
import com.example.jrpotter.appetite.login.LoginActivity;
import com.example.jrpotter.appetite.tool.session.UserSession;

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

    // Listing of the string literals describing the name of the class to
    // instantiate on a navigation list item selection
    private String[] mDrawerClasses;

    // The drawer currently on display
    public DrawerFragment mActiveDrawerFragment;


    // Constructor
    // ==================================================

    public DrawerItemClickListener(Activity activity) {
        mActivity = activity;
        mDrawerClasses = activity.getResources().getStringArray(R.array.drawer_classes);
    }


    // OnItemClickListener Methods
    // ==================================================

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        // First, if the last item is selected (which is sign out), we must return
        // back to the main login screen, remove the activity off the stack, and
        // clear out all credentials
        if(position == mDrawerClasses.length) {

            UserSession.getInstance().logout();
            //UserSession.logoutAndClearCredentials(mActivity);

            Intent intent = new Intent(mActivity, LoginActivity.class);
            mActivity.startActivity(intent);
            mActivity.finish();


        // Next, if any item other than the header was selected, we must instantiate
        // the class referred to by the class name
        } else if(position > 0) {
            try {

                // Set next drawer onto the stack
                mActiveDrawerFragment = (DrawerFragment) Class.forName(mDrawerClasses[position - 1]).newInstance();
                FragmentTransaction transaction = mActivity.getFragmentManager().beginTransaction();
                transaction.replace(R.id.main_activity_content_frame, mActiveDrawerFragment);
                transaction.addToBackStack(null);
                transaction.commit();

                // Update the UI and close the drawer
                ListView drawerList = (ListView) mActivity.findViewById(R.id.main_activity_drawer);
                DrawerLayout drawerLayout = (DrawerLayout) mActivity.findViewById(R.id.activity_main_drawer_layout);

                drawerList.setItemChecked(position, true);
                drawerLayout.closeDrawer(drawerList);

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
