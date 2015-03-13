package guru.apps.llc.appetite.tool;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import guru.apps.llc.appetite.Appetite;
import guru.apps.llc.appetite.R;

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
     * @return ""
     */
    public static SharedPreferences getPreferences() {
        String sharedPrefFile = Appetite.getContext().getString(R.string.shared_preference_key);
        return Appetite.getContext().getSharedPreferences(sharedPrefFile, Context.MODE_PRIVATE);
    }

    /**
     * Return the corresponding editor for write access.
     *
     * @return ""
     */
    public static SharedPreferences.Editor getPreferencesEditor() {
        SharedPreferences pref = getPreferences();
        return pref.edit();
    }

}
