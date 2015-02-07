package com.example.jrpotter.appetite.drawer;

import android.app.Fragment;
import android.os.Bundle;

/**
 * Base class for SubDrawer fragments.
 *
 * These are fragments that are placed into a drawer fragment. They maintain
 * a reference to the drawer fragment and related components.
 *
 * Created by jrpotter on 1/3/15.
 */
public class SubDrawerFragment extends Fragment {

    protected DrawerFragment mParent;

    // Hook Methods
    // ==================================================

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mParent = (DrawerFragment) getParentFragment();
    }

}
