package com.example.jrpotter.appetite.results.view;

import android.content.Context;
import android.widget.LinearLayout;

import com.example.jrpotter.appetite.tool.Locu;

/**
 *
 * Created by jrpotter on 1/4/15.
 */
public abstract class ContactIconView extends VenueIconView {

    protected Locu.Venue mVenue;

    // Constructor
    // ==================================================

    public ContactIconView(Locu.Venue venue, int resId, Context context) {
        super(venue, resId, context);
        mVenue = venue;

        // Draw Image
        setImageResource(resId);

        // Setup with equal size & spacing
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(45, 45);
        params.setMargins(0, 10, 0, 10);
        setLayoutParams(params);
    }

}
