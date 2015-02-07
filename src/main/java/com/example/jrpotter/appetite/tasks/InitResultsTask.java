package com.example.jrpotter.appetite.tasks;

import android.util.Log;
import android.widget.AbsListView;

import com.example.jrpotter.appetite.drawer.DrawerFragment;
import com.example.jrpotter.appetite.results.ResultsFragment;
import com.example.jrpotter.appetite.tool.UserSettings;
import com.example.jrpotter.appetite.tool.Locu;
import com.example.jrpotter.appetite.tool.SiteConnection;
import com.example.jrpotter.appetite.tool.UserLocation;

import java.util.HashMap;

/**
 *
 * We create a Locu task here, and bind this to the ResultsFragment.
 * Only once the Locu task is bound should we replace the loading fragment out,
 * so that the initial view is of first VENUE_LOAD_INIT venues.
 *
 * Created by jrpotter on 1/4/15.
 */
public class InitResultsTask extends ResultsTask {

    // Constructor
    // ==================================================

    public InitResultsTask(DrawerFragment context) {
        super(context);
    }

    // Static Members
    // ==================================================

    public static final String EXTRA_SWING_SEARCH_METHOD = "swing_search";
    public static final String EXTRA_KEYWORD_SEARCH_METHOD = "keyword_search";


    // Asynchronous Methods
    // ==================================================

    /**
     * Make the initial connection to the server, and load in a Locu
     * stream to parse the JSON response returned.
     *
     * @param params ""
     * @return ""
     */
    @Override
    protected Locu doInBackground(String... params) {

        try {
            // Cannot send a request until we obtain our location
            // Also note the location is notified once its been initialized.
            UserLocation location = UserLocation.getInstance(null);
            synchronized (location) {
                while (location.getLatitude() == 0) {
                    location.wait();
                }
            }

            // Build Request Data
            HashMap<String, String> data = new HashMap<>();
            data.put("latitude", String.valueOf(location.getLatitude()));
            data.put("longitude", String.valueOf(location.getLongitude()));
            data.put("radius", "" + UserSettings.getInstance(null).getRadius(false));

            // User either searched implicitly (via mood swing) or explicitly
            if(params[0].equals(EXTRA_SWING_SEARCH_METHOD)) {
                data.put("mood_swing", params[1]);
            } else {
                data.put("search_terms", params[1]);
            }

            return new Locu(SiteConnection.CATER_SEARCH_URL, data);

        } catch (InterruptedException e) {
            Log.e("LOCU", "Could not sync Locu handler (InitResultsTask)");
            e.printStackTrace();

            return null;
        }
    }

    /**
     * Load in the VENUE_LOAD_INIT venues, replacing the main fragment
     * once the first VENUE_LOAD_INIT venues were successfully created.
     *
     * @param result ""
     */
    @Override
    protected void runInLocuThread(final Locu result) {
        result.mHandler.post(new Runnable() {
            @Override
            public void run() {
                result.loadNextPage();
                mContext.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ResultsFragment fragment = new ResultsFragment();
                        fragment.setAdapterSource(result);
                        fragment.setOnScrollListener(new OnScrollListener(result));
                        mContext.setSubDrawerFragment(fragment);
                    }
                });
            }
        });
    }


    // On Scroll Listener
    // ==================================================

    /**
     * The following methods are run when the end of the scroll view is reached.
     * This will make further requests to the server, reloading the listview.
     */
    private class OnScrollListener extends ResultsFragment.OnScrollListener {

        public OnScrollListener(Locu response) {
            super(mContext.getActionBarActivity(), response);
        }

        /**
         * We catch the view and set the tag to an object so that we
         * do not spawn multiple threads.
         *
         * @param view ""
         * @return
         */
        @Override
        protected boolean initiateInvalidation(AbsListView view) {
            if(view.getTag() == null && !response.loadedAllPages()) {
                view.setTag(new Object());
                return true;
            } else {
                return false;
            }
        }

        /**
         * Request to update the data set.
         */
        @Override
        protected boolean invalidateAdapter(AbsListView view) {
            response.loadNextPage();
            return true;
        }

        /**
         * Should be called after the data set has been updated.
         *
         * @param view ""
         */
        @Override
        protected void postInvalidateAdapter(AbsListView view) {
            super.postInvalidateAdapter(view);
            view.setTag(null);
        }

    }
}
