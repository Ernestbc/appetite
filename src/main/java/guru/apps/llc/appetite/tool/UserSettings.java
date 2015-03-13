package guru.apps.llc.appetite.tool;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;

import guru.apps.llc.appetite.Appetite;
import guru.apps.llc.appetite.R;

/**
 * Used to query values set by default or by the user.
 *
 * Created by jrpotter on 1/2/15.
 */
public class UserSettings {

    // Static Members
    // ==================================================

    public static final double CONVERSION_MI_2_M = 1609.34;


    // Settings Methods
    // ==================================================

    public static int getRadius(boolean inMeters) {
        SharedPreferences sharedPref = UserStorage.getPreferences();

        Resources res = Appetite.getContext().getResources();
        int radiusDefault = res.getInteger(R.integer.settings_drawer_radius_def);
        int radius = sharedPref.getInt(UserStorage.PREF_SETTINGS_RADIUS, radiusDefault);

        return (inMeters) ? radius : (int) (radius * CONVERSION_MI_2_M);
    }

}
