package com.example.jrpotter.appetite.tool.session;

import android.app.Activity;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;

/**
 *
 * Created by jrpotter on 2/6/15.
 */
public class GooglePlusLogoutHandler implements GoogleApiClient.ConnectionCallbacks {

    // Private Members
    // ==================================================

    private GoogleApiClient mGoogleApiClient;


    // Constructor
    // ==================================================

    public GooglePlusLogoutHandler(Activity context) {
        mGoogleApiClient = new GoogleApiClient.Builder(context)
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

    }
}
