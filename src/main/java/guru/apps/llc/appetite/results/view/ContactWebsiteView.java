package guru.apps.llc.appetite.results.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.View;

import guru.apps.llc.appetite.R;
import guru.apps.llc.appetite.tool.Locu;

/**
 * Allows the user to navigate the venue in question's website.
 *
 * Created by jrpotter on 1/4/15.
 */
public class ContactWebsiteView extends ContactIconView {

    // Constructor
    // ==================================================

    public ContactWebsiteView(Locu.Venue venue, Context context) {
        super(venue, R.drawable.ic_sphere, context);
        setOnClickListener(new WebsiteViewClickListener());
    }


    // On Click Listener
    // ==================================================

    private class WebsiteViewClickListener implements OnClickListener {

        @Override
        public void onClick(View v) {

            // Notify user the browser is about to open
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMessage("Open " + mVenue.website_url + "?")
                    .setTitle(R.string.view_venue_peek_website_title);
            builder.setNegativeButton(R.string.cancel, null);

            // Initiate browser redirect
            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(mVenue.website_url));
                    getContext().startActivity(intent);
                }
            });

            builder.create().show();

        }

    }

}
