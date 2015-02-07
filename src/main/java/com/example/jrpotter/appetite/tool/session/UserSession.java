package com.example.jrpotter.appetite.tool.session;

import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.jrpotter.appetite.tool.UserStorage;
import com.facebook.Session;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;

/**
 *
 * Created by jrpotter on 2/6/15.
 */
public class UserSession {

    // Static Members
    // ==================================================

    private static Activity mContext;
    private static UserSession instance;

    public enum SESSION_TYPE { GOOGLE_PLUS, FACEBOOK, APPETITE, NONE }


    // Instance Members
    // ==================================================

    public SESSION_TYPE sessionType = SESSION_TYPE.NONE;


    // Session Methods
    // ==================================================

    /**
     * An Appetite specific login method, saving user credentials so repeated
     * login is unnecessary.
     *
     * @param email User's email
     * @param password User's password
     */
    public void login(String name, String email, String password) {

        // Save Credentials
        SharedPreferences.Editor editor = UserStorage.getInstance().getPrefEditor();
        editor.putBoolean(UserStorage.PREF_LOGGED_IN, true);
        editor.putString(UserStorage.PREF_CREDENTIALS_NAME, name);
        editor.putString(UserStorage.PREF_CREDENTIALS_EMAIL, email);
        editor.putString(UserStorage.PREF_CREDENTIALS_PASSWORD, password);
        editor.apply();

    }

    /**
     * Invalidate any sessions (Google+ / Facebook) and clear out credentials,
     * lastly launching back into the login activity.
     */
    public void logout() {

        // Attempt to logout of Google+
        new GooglePlusLogoutHandler(mContext).logout();

        // Attempt to logout of Facebook
        Session activeSession = Session.getActiveSession();
        if(activeSession != null && activeSession.isOpened()) {
            activeSession.closeAndClearTokenInformation();
        }

        // Remove Appetite Credentials
        SharedPreferences.Editor editor = UserStorage.getInstance().getPrefEditor();
        editor.remove(UserStorage.PREF_CREDENTIALS_PASSWORD);
        editor.remove(UserStorage.PREF_CREDENTIALS_EMAIL);
        editor.remove(UserStorage.PREF_CREDENTIALS_NAME);
        editor.remove(UserStorage.PREF_LOGGED_IN);
        editor.commit();

    }

    /**
     * Finds out user information.
     *
     * @param type ""
     */
    public void instantiateSession(SESSION_TYPE type) {
        sessionType = type;
    }


    // Singleton Methods
    // ==================================================

    public static UserSession getInstance() {
        return getInstance(null);
    }

    public static UserSession getInstance(Activity context) {
        if(instance == null) {
            mContext = context;
            instance = new UserSession();
        }

        return instance;
    }


    // Private Constructor
    // ==================================================

    private UserSession() {
        UserStorage.getInstance(mContext);
    }

}
