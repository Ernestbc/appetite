package com.example.jrpotter.appetite.tool;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.example.jrpotter.appetite.R;

/**
 *
 * Created by jrpotter on 1/2/15.
 */
public class UserStorage {

    // Static Keys
    // ==================================================

    public static final String PREF_LOGGED_IN = "pref_logged_in";
    public static final String PREF_CREDENTIALS_NAME = "pref_credentials_name";
    public static final String PREF_CREDENTIALS_EMAIL = "pref_credentials_email";
    public static final String PREF_CREDENTIALS_PASSWORD = "pref_credentials_password";

    public static final String PREF_SETTINGS_RADIUS = "pref_settings_radius";


    // Public Methods
    // ==================================================

    /**
     * Return the file where settings are saved.
     *
     * @param activity ""
     * @return ""
     */
    public static SharedPreferences getPreferences(Activity activity) {
        String sharedPrefFile = activity.getString(R.string.shared_preference_key);
        return activity.getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE);
    }

    /**
     * Return the corresponding editor for write access.
     *
     * @param activity ""
     * @return ""
     */
    public static SharedPreferences.Editor getPreferencesEditor(Activity activity) {
        SharedPreferences pref = getPreferences(activity);
        return pref.edit();
    }

}
