package com.example.jrpotter.appetite;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.example.jrpotter.appetite.login.LoginActivity;
import com.example.jrpotter.appetite.tool.UserStorage;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;

/**
 * This is the very first activity opened, which determines which activity to open next.
 *
 * Note this is completely invisible, and should never be noticed by the user.
 * It first attempts to log in via Google+, checking that it was successfully logged in or
 * not. If this isn't the case, it next tries logging in via Facebook. Lastly, it attempts
 * to login via Appetite credentials.
 *
 * Created by jrpotter on 12/31/2014.
 */
public class SplashActivity extends Activity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    // Facebook Members
    // ==================================================

    private UiLifecycleHelper uiHelper;

    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            if(session != null && state.isOpened()) {
                onSessionStateChange();
            }
        }
    };


    // Hook Methods
    // ==================================================

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Google+ Setup
        new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .build().connect();

        // Facebook Setup
        uiHelper = new UiLifecycleHelper(this, callback);
        uiHelper.onCreate(savedInstanceState);
    }

    @Override
    protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
        uiHelper.onActivityResult(requestCode, responseCode, intent);
    }

    @Override
    public void onResume() {
        super.onResume();

        // For scenarios where the main activity is launched and user
        // session is not null, the session state change notification
        // may not be triggered. Trigger it if it's open/closed.
        Session session = Session.getActiveSession();
        if (session != null && session.isOpened()) {
            onSessionStateChange();
        }

        uiHelper.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }


    // Google+ Login
    // ==================================================

    /**
     * Successfully connected to Google+, so will go to the MainActivity,
     * notifying the UserSession where to populate data from.
     *
     * @param bundle ""
     */
    @Override
    public void onConnected(Bundle bundle) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    /**
     * Should now try to login via Facebook.
     *
     * @param connectionResult ""
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        // Try to login via Facebook
        Session session = Session.getActiveSession();
        if (session != null && session.isOpened()) {
            onSessionStateChange();

        // Try to login via Appetite
        } else {
            SharedPreferences pref = UserStorage.getInstance(SplashActivity.this).getPref();
            if(!pref.getBoolean(UserStorage.PREF_LOGGED_IN, false)) {
                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
            }
        }
    }


    // Facebook Login
    // ==================================================

    private void onSessionStateChange() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
