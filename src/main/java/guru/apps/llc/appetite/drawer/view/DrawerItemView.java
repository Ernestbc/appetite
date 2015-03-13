package guru.apps.llc.appetite.drawer.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import guru.apps.llc.appetite.R;

/**
 *
 * Created by jrpotter on 1/1/15.
 */
public class DrawerItemView extends LinearLayout {

    public DrawerItemView(Context context, String name, int iconId) {
        super(context);

        // Setup Layout
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_drawer_item, this);

        // Setup Icon
        ImageView imageView = (ImageView) findViewById(R.id.view_drawer_item_icon);
        imageView.setImageResource(iconId);

        // Initialize Text
        TextView textView = (TextView) findViewById(R.id.view_drawer_item_name);
        textView.setText(name);
    }

}
