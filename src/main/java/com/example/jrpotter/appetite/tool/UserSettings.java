package com.example.jrpotter.appetite.tool;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.example.jrpotter.appetite.R;

/**
 * Used to query values set by default or by the user.
 *
 * Created by jrpotter on 1/2/15.
 */
public class UserSettings {

    // Static Members
    // ==================================================

    private static Activity mContext;
    private static UserSettings instance;

    public static final double CONVERSION_MI_2_M = 1609.34;


    // Public Methods
    // ==================================================

    /**
     * Allow the user to set the initial context of the storage.
     *
     * @param context ""
     * @return ""
     */
    public static UserSettings getInstance(Activity context) {
        if(instance == null) {
            mContext = context;
            instance = new UserSettings();
        }

        return instance;
    }


    // Settings Methods
    // ==================================================

    public int getRadius(boolean inMeters) {
        SharedPreferences sharedPref = UserStorage.getPreferences(mContext);
        int radiusDefault = mContext.getResources().getInteger(R.integer.settings_drawer_radius_def);
        int radius = sharedPref.getInt(UserStorage.PREF_SETTINGS_RADIUS, radiusDefault);
        if(inMeters) {
            return radius;
        } else {
            return (int) (radius * CONVERSION_MI_2_M);
        }
    }

}
