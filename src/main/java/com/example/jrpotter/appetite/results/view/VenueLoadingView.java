package com.example.jrpotter.appetite.results.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.example.jrpotter.appetite.R;

/**
 * The following is used to provide a visual on whether or not more
 * venues are being loaded. It will notify the user if no more venues
 * are available.
 *
 * Created by jrpotter on 1/3/15.
 */
public class VenueLoadingView extends RelativeLayout {

    // Constructor
    // ==================================================

    public VenueLoadingView(Context context) {
        super(context);

        // Setup Layout
        // TODO: Instead of SVGs, replace with scaled PNGs and remove use of SVG libraries entirely
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_venue_loading, this);
    }


    // View Methods
    // ==================================================

    /**
     * Hide the progress bar and show our message.
     */
    public void displayFinishedMessage() {
        findViewById(R.id.view_venue_loading_progress).setVisibility(View.GONE);
        findViewById(R.id.view_venue_loading_finished).setVisibility(View.VISIBLE);
    }

}
