package com.example.jrpotter.appetite.drawer;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.jrpotter.appetite.R;

/**
 *
 * Created by jrpotter on 01/03/2015
 */
public class LoadingFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_loading, container, false);
    }

}
