package com.example.jrpotter.appetite.mood;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.jrpotter.appetite.drawer.DrawerFragment;
import com.example.jrpotter.appetite.R;
import com.example.jrpotter.appetite.tasks.InitMoodsTask;
import com.example.jrpotter.appetite.tasks.InitResultsTask;
import com.example.jrpotter.appetite.tasks.InitSwingsTask;

/**
 * Main fragment listing moods -> results.
 *
 * The main fragment pulled open when the "Concierge" option is selected
 * from the navigation drawer. This is the first activity open when the user
 * logins to the application.
 *
 * Created by jrpotter on 12/31/14.
 */
public class MoodDrawerFragment extends DrawerFragment {

    // Hook Methods
    // ==================================================

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        containerId = R.id.drawer_main_fragment_container;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.drawer_main, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        setLoadingFragment();
        new InitMoodsTask(this).execute();
    }


    // Event Methods
    // ==================================================

    /**
     * Load the swings corresponding to the mood.
     * @param view ""
     */
    public void selectMood(View view) {
        setLoadingFragment();
        String name = (String) view.getTag(R.id.tags_main_swings_mood);
        new InitSwingsTask(this, name).execute("" + view.getId());
    }

    /**
     * Load results corresponding to the swing.
     * @param view ""
     */
    public void selectSwing(View view) {
        setLoadingFragment();
        new InitResultsTask(this)
            .execute(InitResultsTask.EXTRA_SWING_SEARCH_METHOD, "" + view.getId());
    }

    /**
     * Load results corresponding to given search terms.
     * @param view ""
     */
    public void searchVenues(View view) {
        setLoadingFragment();
        new InitResultsTask(this)
            .execute(InitResultsTask.EXTRA_SWING_SEARCH_METHOD, "" + view.getId());
    }

}
