package com.example.jrpotter.appetite.tasks;

import android.os.AsyncTask;
import android.os.Bundle;

import com.example.jrpotter.appetite.drawer.DrawerFragment;
import com.example.jrpotter.appetite.drawer.SubDrawerFragment;
import com.example.jrpotter.appetite.main.MoodsFragment;
import com.example.jrpotter.appetite.tool.SiteConnection;

import java.util.HashMap;


/**
 * Makes a call to the server for 6 random moods.
 * The user will select these and move to the proper two swings.
 */
public class InitMoodsTask extends AsyncTask<String, Void, String> {

    private DrawerFragment mContext;

    // Constructor
    // ==================================================

    public InitMoodsTask(DrawerFragment context) {
        mContext = context;
    }


    // Asynchronous Methods
    // ==================================================

    @Override
    protected String doInBackground(String... params) {
        HashMap<String, String> data = new HashMap<>(); data.put("count", "6");
        return SiteConnection.getStringResponse(SiteConnection.CATER_MOOD_URL, data);
    }

    @Override
    protected final void onPostExecute(String result) {

        // Setup fragment
        SubDrawerFragment fragment = new MoodsFragment();
        Bundle args = new Bundle();
        args.putString(DrawerFragment.FRAGMENT_PARAM, result);
        fragment.setArguments(args);

        mContext.setSubDrawerFragment(fragment);

    }

}
