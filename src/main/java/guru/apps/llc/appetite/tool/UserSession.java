package guru.apps.llc.appetite.tool;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;

import guru.apps.llc.appetite.Appetite;
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

    public enum SESSION_TYPE { GOOGLE_PLUS, FACEBOOK, APPETITE, NONE }

    // Allows different parts of the application to pull profile
    // information from the correct server
    public static SESSION_TYPE sessionType = SESSION_TYPE.NONE;


    // Session Methods
    // ==================================================

    /**
     * Save credentials into shared preferences for pulling later on (without
     * having to requery the server each time).
     *
     * @param name "User's Name"
     * @param email "User's Email"
     * @param password "User's Password"
     * @param activity ""
     */
    public static void loginToAppetite(String name, String email, String password) {
        SharedPreferences.Editor editor = UserStorage.getPreferencesEditor();
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
    public static void logoutAndClearCredentials() {

        // Attempt to logout of Google+
        new GooglePlusLogoutHandler().logout();

        // Attempt to logout of Facebook
        Session activeSession = Session.getActiveSession();
        if(activeSession != null && activeSession.isOpened()) {
            activeSession.closeAndClearTokenInformation();
        }

        // Remove Appetite Credentials
        SharedPreferences.Editor editor = UserStorage.getPreferencesEditor();
        editor.remove(UserStorage.PREF_CREDENTIALS_PASSWORD);
        editor.remove(UserStorage.PREF_CREDENTIALS_EMAIL);
        editor.remove(UserStorage.PREF_CREDENTIALS_NAME);
        editor.remove(UserStorage.PREF_LOGGED_IN);
        editor.commit();

    }


    // Google+ Handler
    // ==================================================

    /**
     * Setups callbacks to properly disconnect the user's Google+ account from
     * Appetite.
     *
     * Created by jrpotter on 2/6/15.
     */
    private static class GooglePlusLogoutHandler implements GoogleApiClient.ConnectionCallbacks {

        // Private Members
        // ==================================================

        private GoogleApiClient mGoogleApiClient;


        // Constructor
        // ==================================================

        public GooglePlusLogoutHandler() {
            mGoogleApiClient = new GoogleApiClient.Builder(Appetite.getContext())
                    .addConnectionCallbacks(this)
                    .addApi(Plus.API)
                    .addScope(Plus.SCOPE_PLUS_LOGIN)
                    .build();
        }


        // Connection Callbacks
        // ==================================================

        public void logout() {
            mGoogleApiClient.connect();
        }

        @Override
        public void onConnected(Bundle bundle) {
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            mGoogleApiClient.disconnect();
        }

        @Override
        public void onConnectionSuspended(int i) {
            // Intentionally empty
        }
    }

}
