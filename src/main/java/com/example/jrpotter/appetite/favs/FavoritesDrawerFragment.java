package com.example.jrpotter.appetite.favs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.jrpotter.appetite.R;
import com.example.jrpotter.appetite.drawer.DrawerFragment;

/**
 *
 * Created by jrpotter on 1/3/15.
 */
public class FavoritesDrawerFragment extends DrawerFragment {

    // Hook Methods
    // ==================================================

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        containerId = R.id.drawer_favorites_fragment_container;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.drawer_favorites, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        setLoadingFragment();
    }
}
