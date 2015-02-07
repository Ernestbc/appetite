package com.example.jrpotter.appetite.results.view;

import android.content.Context;
import android.widget.ImageView;

import com.example.jrpotter.appetite.tool.Locu;

/**
 *
 * Created by jrpotter on 1/4/15.
 */
public abstract class VenueIconView extends ImageView {

    protected Locu.Venue mVenue;

    // Constructor
    // ==================================================

    /**
     * Draws the proper icon on the view.
     *
     * @param venue "The venue POD"
     * @param resId "ID of image to draw on IconView"
     * @param context ""
     */
    public VenueIconView(Locu.Venue venue, int resId, Context context) {
        super(context);
        mVenue = venue;
        setImageResource(resId);
    }

}
