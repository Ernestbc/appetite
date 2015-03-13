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
 * Allows the user to call the venue in question.
 *
 * Created by jrpotter on 1/4/15.
 */
public class ContactPhoneView extends ContactIconView {

    // Constructor
    // ==================================================

    public ContactPhoneView(Locu.Venue venue, Context context) {
        super(venue, R.drawable.ic_phone, context);
        setOnClickListener(new PhoneViewClickListener());
    }


    // On Click Listener
    // ==================================================

    private class PhoneViewClickListener implements OnClickListener {

        @Override
        public void onClick(View v) {

            // Notify user a phone call is about to begin
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMessage("Call " + mVenue.contact.phone + "?")
                    .setTitle(R.string.view_venue_peek_phone_title);
            builder.setNegativeButton(R.string.cancel, null);

            // Initiate phone call
            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse("tel:" + mVenue.contact.phone));
                    getContext().startActivity(intent);
                }
            });

            builder.create().show();
        }
    }
}
