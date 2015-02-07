package com.example.jrpotter.appetite.recents;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.jrpotter.appetite.R;
import com.example.jrpotter.appetite.drawer.DrawerFragment;
import com.example.jrpotter.appetite.tasks.InitRecentsTask;

/**
 *
 * Created by jrpotter on 1/3/15.
 */
public class RecentsDrawerFragment extends DrawerFragment {


    // Hook Methods
    // ==================================================

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        containerId = R.id.drawer_recents_fragment_container;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.drawer_recents, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        setLoadingFragment();
        new InitRecentsTask(this).execute();
    }
}
