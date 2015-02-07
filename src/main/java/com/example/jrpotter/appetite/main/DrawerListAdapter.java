package com.example.jrpotter.appetite.main;

import android.app.Activity;
import android.content.res.TypedArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.example.jrpotter.appetite.R;
import com.example.jrpotter.appetite.drawer.view.DrawerItemView;
import com.example.jrpotter.appetite.drawer.view.ProfileView;

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


    // Constructor
    // ==================================================

    public DrawerListAdapter(Activity activity) {
        mActivity = activity;
        mDrawerItems = activity.getResources().obtainTypedArray(R.array.drawer_items);
    }


    // BaseAdapter Methods
    // ==================================================

    /**
     * We include an additional count to account for the profile view,
     * which is not specified in the typed array.
     *
     * @return ""
     */
    @Override
    public int getCount() {
        return mDrawerItems.length() / 2 + 1;
    }

    @Override
    public Object getItem(int position) {
        if(position == 0) {
            return new DrawerItem("", 0);
        } else {
            int index = position - 1;
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
        if(position == 0) {
            return new ProfileView(mActivity);
        } else {
            DrawerItem item = (DrawerItem) getItem(position);
            return new DrawerItemView(mActivity, item.name, item.iconId);
        }
    }
}
