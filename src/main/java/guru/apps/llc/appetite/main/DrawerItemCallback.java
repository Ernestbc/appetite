package guru.apps.llc.appetite.main;

import android.view.View;
import android.widget.AdapterView;

/**
 * Allows for additional callbacks to be run whenever a drawer item is selected.
 * These will be run after the new drawer is set.
 *
 * Created by jrpotter on 3/12/15.
 */
public interface DrawerItemCallback {

    public void onItemClick(AdapterView<?> parent, View view, int position, long id);

}
