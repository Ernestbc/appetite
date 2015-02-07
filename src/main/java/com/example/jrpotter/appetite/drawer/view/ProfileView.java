package com.example.jrpotter.appetite.drawer.view;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.jrpotter.appetite.R;
import com.example.jrpotter.appetite.tool.SiteConnection;
import com.example.jrpotter.appetite.tool.UserStorage;
import com.example.jrpotter.appetite.tool.session.UserSession;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 *
 * Created by jrpotter on 1/5/15.
 */
public class ProfileView extends RelativeLayout implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    // Instance Members
    // ==================================================

    private TextView nameView;
    private TextView emailView;
    private ImageView imageView;

    private Activity mContext;
    private GoogleApiClient mGoogleApiClient;


    // Constructor
    // ==================================================

    public ProfileView(Activity context) {
        super(context);
        mContext = context;

        // Setup Layout
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.view_profile, this);
        nameView = (TextView) view.findViewById(R.id.view_profile_name);
        emailView = (TextView) findViewById(R.id.view_profile_email);
        imageView = (ImageView) findViewById(R.id.view_profile_image);

        // Instantiate View
        UserSession.SESSION_TYPE type = UserSession.getInstance().sessionType;
        switch(type) {

            case APPETITE: {
                SharedPreferences pref = UserStorage.getPreferences(context);
                nameView.setText(pref.getString(UserStorage.PREF_CREDENTIALS_NAME, ""));
                emailView.setText(pref.getString(UserStorage.PREF_CREDENTIALS_EMAIL, ""));
                break;
            }

            case FACEBOOK: {
                Session activeSession = Session.getActiveSession();
                Request me = Request.newMeRequest(activeSession, new Request.GraphUserCallback() {
                    @Override
                    public void onCompleted(final GraphUser graphUser, Response response) {
                        nameView.setText(graphUser.getName());
                        emailView.setText((String) graphUser.asMap().get("email"));
                        initializeImageView(SiteConnection.getFacebookImageURL(graphUser));
                    }
                });

                Bundle params = me.getParameters();
                params.putString("fields", "email,name");
                me.setParameters(params);
                me.executeAsync();

                break;
            }

            case GOOGLE_PLUS: {
                mGoogleApiClient = new GoogleApiClient.Builder(context)
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this)
                        .addApi(Plus.API)
                        .addScope(Plus.SCOPE_PLUS_LOGIN)
                        .build();

                mGoogleApiClient.connect();
                break;
            }

            default:
                break;
        }
    }


    // Convenience Method
    // ==================================================

    /**
     * Setup rounded bitmap.
     *
     * @param url ""
     */
    private void initializeImageView(final String url) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final Bitmap bmap = ImageLoader.getInstance().loadImageSync(url);
                final RoundedBitmapDrawable rbmap = RoundedBitmapDrawableFactory.create(getResources(), bmap);
                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        rbmap.setCornerRadius(100);
                        imageView.setImageDrawable(rbmap);
                    }
                });
            }
        }).start();
    }


    // Google+ Methods
    // ==================================================

    @Override
    public void onConnected(Bundle bundle) {

        Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);

        nameView.setText(currentPerson.getDisplayName());
        emailView.setText(Plus.AccountApi.getAccountName(mGoogleApiClient));

        String url = currentPerson.getImage().getUrl();
        initializeImageView(url.substring(0, url.indexOf('?')) + "?sz=200");
    }

    @Override
    public void onConnectionSuspended(int i) {
        // Intentionally empty
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // Intentionally empty
    }

}
