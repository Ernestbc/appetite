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

    // Static Members
    // ==================================================

    private static Activity mContext;
    private static UserStorage instance;

    // Preferences
    private static String mSharedPrefFile;
    private static SharedPreferences mSharedPref;
    private static SharedPreferences.Editor mSharedPrefEditor;


    // Static Keys
    // ==================================================

    // Activity Lifecycle
    public static final String STATE_FRAGMENT_ADDED = "state_fragment_added";

    public static final String PREF_LOGGED_IN = "pref_logged_in";
    public static final String PREF_CREDENTIALS_NAME = "pref_credentials_name";
    public static final String PREF_CREDENTIALS_EMAIL = "pref_credentials_email";
    public static final String PREF_CREDENTIALS_PASSWORD = "pref_credentials_password";

    public static final String PREF_SETTINGS_RADIUS = "pref_settings_radius";


    // Public Methods
    // ==================================================

    /**
     *
     * @return ""
     */
    public static UserStorage getInstance() {
        return getInstance(null);
    }

    /**
     * Allow the user to set the initial context of the storage.
     *
     * @param context ""
     * @return ""
     */
    public static UserStorage getInstance(Activity context) {
        if(instance == null || context != null) {
            mContext = context;
            instance = new UserStorage();
        }

        return instance;
    }

    public final SharedPreferences getPref() {
        return mSharedPref;
    }

    public final SharedPreferences.Editor getPrefEditor() {
        return mSharedPrefEditor;
    }


    // Private Constructor
    // ==================================================

    /**
     * Setup preferences file and database.
     */
    private UserStorage() {
        mSharedPrefFile = mContext.getString(R.string.shared_preference_key);
        mSharedPref = mContext.getSharedPreferences(mSharedPrefFile, Context.MODE_PRIVATE);
        mSharedPrefEditor = mSharedPref.edit();
    }

}
