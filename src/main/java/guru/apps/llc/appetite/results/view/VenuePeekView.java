package guru.apps.llc.appetite.results.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import guru.apps.llc.appetite.R;
import guru.apps.llc.appetite.tool.Locu;

/**
 *
 * Created by jrpotter on 12/31/14.
 */
public class VenuePeekView extends RelativeLayout {

    private Locu.Venue venue;

    // All layouts in view (need to be cleared when reused)
    private LinearLayout mContactLayout;


    // Constructor
    // ==================================================

    public VenuePeekView(Locu.Venue venue, Context context) {
        super(context);

        // Setup Layout
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_venue_peek, this);

        // Setup layout ids
        mContactLayout = (LinearLayout) findViewById(R.id.view_venue_peek_contact);

        // Populate view
        initialize(venue);
    }


    // Initialization Methods
    // ==================================================

    public void initialize(Locu.Venue venue) {

        this.venue = venue;

        // Clear out layouts
        mContactLayout.removeAllViews();

        // Build Menu
        initializeDefaults();
        initializeContacts();
    }


    // Default Initialization Methods
    // ==================================================

    /**
     * This consists of all initializations that are mandatory for
     * every VenuePeekView (menu icon, name, and address).
     */
    private void initializeDefaults() {

        // Must reset the name (in case we are using a convertView)
        TextView name = (TextView) findViewById(R.id.view_venue_preview_name);
        name.setText((venue.name != null) ? venue.name : "");

        // Must reset the location, like the name
        TextView address = (TextView) findViewById(R.id.view_venue_preview_address);
        if(venue.location != null && venue.location.address1 != null) {
            address.setText(venue.location.address1);
        } else {
            address.setText("");
        }

    }


    // Contact Initialization Methods
    // ==================================================

    /**
     * Try to load in views containing information regarding all
     * means of contacting the venue.
     */
    private void initializeContacts() {

        // Allows the user to call the venue from the preview
        if(venue.contact != null && venue.contact.phone != null) {
            mContactLayout.addView(new ContactPhoneView(venue, getContext()));
        }

        // Allows the user to open the venue's website from the preview
        if(venue.website_url != null) {
            mContactLayout.addView(new ContactWebsiteView(venue, getContext()));
        }

    }

}