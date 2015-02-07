package com.example.jrpotter.appetite.tasks;

import android.os.AsyncTask;
import android.os.Bundle;

import com.example.jrpotter.appetite.drawer.DrawerFragment;
import com.example.jrpotter.appetite.main.SwingsFragment;
import com.example.jrpotter.appetite.tool.SiteConnection;

import java.util.HashMap;

/**
 * Displays the two mood swings related to the mood selected. Upon selecting a swing,
 * will display results corresponding to the swing.
 *
 * Created by jrpotter on 12/25/14.
 */
public class InitSwingsTask extends AsyncTask<String, Void, String> {

    // Construction
    private String mMood;
    private DrawerFragment mContext;

    // Constructor
    // ==================================================

    public InitSwingsTask(DrawerFragment context, String mood) {
        mContext = context;
        mMood = mood;
    }


    // Asynchronous Methods
    // ==================================================

    @Override
    protected String doInBackground(String... params) {
        HashMap<String, String> data = new HashMap<>(); data.put("mood", "" + params[0]);
        return SiteConnection.getStringResponse(SiteConnection.CATER_SWING_URL, data);
    }

    @Override
    protected final void onPostExecute(String result) {

        // Setup fragment
        SwingsFragment fragment = new SwingsFragment();
        Bundle args = new Bundle();
        args.putString(DrawerFragment.FRAGMENT_PARAM, result);
        args.putString(DrawerFragment.FRAGMENT_PARAM_2, mMood);
        fragment.setArguments(args);

        mContext.setSubDrawerFragment(fragment);

    }

}
