package guru.apps.llc.appetite.main;

import android.app.Activity;
import android.content.res.TypedArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import guru.apps.llc.appetite.R;
import guru.apps.llc.appetite.drawer.view.DrawerItemView;
import guru.apps.llc.appetite.drawer.view.ProfileView;

/**
 * Returns the corresponding navigation list items and item views
 * for the navigation drawer.
 *
 * Created by jrpotter on 2/7/15.
 */
public class DrawerListAdapter extends BaseAdapter {

    // The drawer items specify the icons and titles displayed
    // on each navigation list item view
    private TypedArray mDrawerItems;

    // The activity the adapter is tied to
    private Activity mActivity;

    // The index of the active drawer
    private int mActiveDrawerPosition;


    // Constructor
    // ==================================================

    public DrawerListAdapter(Activity activity) {
        mActivity = activity;
        mActiveDrawerPosition = 1;
        mDrawerItems = activity.getResources().obtainTypedArray(R.array.drawer_items);
    }


    // Adapter Methods
    // ==================================================

    // We must offset position if the passed position is offset
    // (i.e. greater than the last set position)
    public void setActiveDrawer(int position) {
        if(position < mActiveDrawerPosition) {
            mActiveDrawerPosition = position;
        } else {
            mActiveDrawerPosition = position + 1;
        }
        notifyDataSetChanged();
    }


    // BaseAdapter Methods
    // ==================================================

    /**
     * We include an additional count to account for the profile view,
     * which is not specified in the typed array. We also negate a count
     * since the current drawer should not be displayed.
     *
     * Note we have to offset drawer items if any position is
     * after the active drawer item position (accounting for
     * the hidden element).
     *
     * @return ""
     */
    @Override
    public int getCount() {
        return mDrawerItems.length() / 2;
    }

    @Override
    public Object getItem(int position) {
        if(position == 0) {
            return new DrawerItem("", 0);
        } else {

            int index = position - 1;
            if(position >= mActiveDrawerPosition) {
                index = position;
            }

            String name = mDrawerItems.getString(2 * index);
            int resourceId = mDrawerItems.getResourceId(2 * index + 1, 0);
            return new DrawerItem(name, resourceId);
        }
    }

    @Override
    public long getItemId(int position) {
        return ((DrawerItem) getItem(position)).iconId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // The first view will always display the profile view
        // This will not change during the entirety of the application,
        // so we maintain the same one if possible.
        if(position == 0) {
            if (convertView != null && convertView instanceof ProfileView) {
                return convertView;
            } else {
                return new ProfileView(mActivity);
            }
        } else {
            DrawerItem item = (DrawerItem) getItem(position);
            return new DrawerItemView(mActivity, item.name, item.iconId);
        }

    }
}
