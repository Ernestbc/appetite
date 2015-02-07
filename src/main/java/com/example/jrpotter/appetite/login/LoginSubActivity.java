package com.example.jrpotter.appetite.login;

import android.content.Intent;
import android.content.IntentSender;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.example.jrpotter.appetite.MainActivity;
import com.example.jrpotter.appetite.R;
import com.example.jrpotter.appetite.tool.session.UserSession;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;

import org.json.JSONObject;

/**
 *
 * Created by jrpotter on 1/18/15.
 */
public class LoginSubActivity extends ActionBarActivity implements
        View.OnClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    // Google+ Members
    // ==================================================

    // Track whether the sign-in button has been clicked so that we know to resolve
    // all issues preventing sign-in without waiting.
    private boolean mSignInClicked;

    // Check whether an intent is currently happening
    private boolean mIntentInProgress;

    // Store the connection result from onConnectionFailed callbacks so that we can
    // resolve them when the user clicks sign-in.
    private ConnectionResult mConnectionResult;

    // The actual interface used to interact with Google
    private GoogleApiClient mGoogleApiClient;


    // Facebook Members
    // ==================================================

    private UiLifecycleHelper uiHelper;

    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };


    // Hook Methods
    // ==================================================

    protected void onCreate(Bundle savedInstanceState, int contentView) {
        super.onCreate(savedInstanceState);
        setContentView(contentView);

        // Google+ Setup
        findViewById(R.id.include_login_method_google_plus).setOnClickListener(this);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .build();

        // Facebook Setup
        uiHelper = new UiLifecycleHelper(this, callback);
        uiHelper.onCreate(savedInstanceState);
    }

    /**
     * Resets the state of the flags when control returns to your `Activity` in `onActivityResult`.
     * @param requestCode ""
     * @param responseCode ""
     * @param intent ""
     */
    protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
        if (requestCode == 0) {
            mIntentInProgress = false;
            if (responseCode != RESULT_OK) {
                mSignInClicked = false;
            }
            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
        }

        uiHelper.onActivityResult(requestCode, responseCode, intent);
    }

    @Override
    public void onResume() {
        super.onResume();

        // For scenarios where the main activity is launched and user
        // session is not null, the session state change notification
        // may not be triggered. Trigger it if it's open/closed.
        Session session = Session.getActiveSession();
        if (session != null && (session.isOpened() || session.isClosed())) {
            onSessionStateChange(session, session.getState(), null);
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


    // Event Methods
    // ==================================================

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.include_login_method_google_plus && !mGoogleApiClient.isConnecting()) {
            mSignInClicked = true;
            mGoogleApiClient.connect();
        }
    }


    // Google+ Methods
    // ==================================================

    @Override
    public void onConnected(Bundle bundle) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    /* A helper method to resolve the current ConnectionResult error. */
    private void resolveSignInError() {
        if (mConnectionResult.hasResolution()) {
            try {
                mIntentInProgress = true;
                startIntentSenderForResult(mConnectionResult.getResolution().getIntentSender(),
                        0, null, 0, 0, 0);
            } catch (IntentSender.SendIntentException e) {
                mIntentInProgress = false;
                mGoogleApiClient.connect();
            }
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (!mIntentInProgress) {
            mConnectionResult = result;
            if (mSignInClicked) {
                resolveSignInError();
            }
        }
    }


    // Facebook Methods
    // ==================================================

    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        if(state.isOpened()) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }


    // Loading Methods
    // ==================================================

    /**
     * Fades the main view and adds a progress bar to the middle of it.
     */
    public void setLoadingOverview() {

        // Setup parameters
        RelativeLayout.LayoutParams overlayParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        RelativeLayout.LayoutParams barParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        barParams.addRule(RelativeLayout.CENTER_IN_PARENT);

        // Build Overlay
        RelativeLayout overlay = new RelativeLayout(this);
        overlay.addView(new ProgressBar(this), barParams);
        overlay.setId(R.id.loading_overview_id);
        overlay.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        // Add Overlay
        ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
        root.setOnTouchListener(null);
        root.addView(overlay, overlayParams);
        root.setAlpha(0.3f);
    }

    /**
     * Reverses the effect of addLoadingOverview()
     */
    public void removeLoadingOverview() {
        ViewGroup root = (ViewGroup) findViewById(android.R.id.content);
        root.removeView(findViewById(R.id.loading_overview_id));
        root.setEnabled(true);
        root.setAlpha(1.0f);
    }


    // Login Methods
    // ==================================================

    /**
     * Lets the user into the application, saving their credentials.
     */
    public void login(String email, String password) {
        UserSession.getInstance().login(email, password);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }


    // Asynchronous Task
    // ==================================================

    /**
     * An asynchronous task to enable moving past the LoginActivity.
     *
     * The user uses this class to either create a new account, or log in to
     * an already created account.
     */
    protected class UserAccountTask extends AsyncTask<Void, Void, JSONObject> {

        // User Input
        protected EditText name;
        protected EditText email;
        protected EditText password;

        // URL Members
        protected String requestURL;

        @Override
        protected JSONObject doInBackground(Void... params) {

            return null;
        }

        @Override
        protected void onPreExecute() {
            setLoadingOverview();
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            removeLoadingOverview();
        }
    }

}
