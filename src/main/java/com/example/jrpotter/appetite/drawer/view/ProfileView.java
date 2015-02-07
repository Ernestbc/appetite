package com.example.jrpotter.appetite.drawer.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.jrpotter.appetite.R;
import com.example.jrpotter.appetite.tool.UserStorage;

/**
 *
 * Created by jrpotter on 1/5/15.
 */
public class ProfileView extends RelativeLayout {

    public ProfileView(Context context) {
        super(context);

        // Setup Layout
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_profile, this);

        // Retrieve Email
        SharedPreferences pref = UserStorage.getInstance(null).getPref();
        TextView email = (TextView) findViewById(R.id.view_profile_email);
        email.setText(pref.getString(UserStorage.PREF_CREDENTIALS_EMAIL, ""));

    }
}
