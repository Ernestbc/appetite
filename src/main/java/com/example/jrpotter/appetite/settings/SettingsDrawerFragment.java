package com.example.jrpotter.appetite.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.jrpotter.appetite.R;
import com.example.jrpotter.appetite.drawer.DrawerFragment;
import com.example.jrpotter.appetite.tool.UserSettings;
import com.example.jrpotter.appetite.tool.UserStorage;

/**
 *
 */
public class SettingsDrawerFragment extends DrawerFragment {

    // Hook Methods
    // ==================================================

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.drawer_settings, container, false);

        initializeRadius(view);

        return view;
    }


    // Initialization Methods
    // ==================================================

    private void initializeRadius(View view) {

        SeekBar seekBar = (SeekBar) view.findViewById(R.id.drawer_settings_radius);
        final int seekBarMinimum = getResources().getInteger(R.integer.settings_drawer_radius_min);
        final TextView seekBarValue = (TextView) view.findViewById(R.id.drawer_settings_radius_value);

        // Update values when changing bar. Note, since we cannot set the minimum on the seekbar,
        // we instead add the minimum to all values we are working with.
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                seekBarValue.setText("" + (progress + seekBarMinimum));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Intentionally empty
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                SharedPreferences.Editor editor = UserStorage.getInstance(null).getPrefEditor();
                editor.putInt(UserStorage.PREF_SETTINGS_RADIUS, seekBar.getProgress() + seekBarMinimum);
                editor.commit();
            }
        });

        // Set Initial radius position
        seekBar.setProgress(UserSettings.getInstance(null).getRadius(true) - seekBarMinimum);

    }

}
