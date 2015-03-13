package guru.apps.llc.appetite.tasks;

import android.widget.AbsListView;

import guru.apps.llc.appetite.drawer.DrawerFragment;
import guru.apps.llc.appetite.results.ResultsFragment;
import guru.apps.llc.appetite.tool.Locu;
import guru.apps.llc.appetite.tool.SiteConnection;

import java.util.HashMap;

/**
 *
 * We create a Locu task here, and bind this to the ResultsFragment.
 * Only once the Locu task is bound should we replace the loading fragment out,
 * so that the initial view is of first VENUE_LOAD_INIT venues.
 *
 * Created by jrpotter on 1/4/15.
 */
public class InitRecentsTask extends ResultsTask {

    // Constructor
    // ==================================================

    public InitRecentsTask(DrawerFragment context) {
        super(context);
    }


    // Asynchronous Methods
    // ==================================================

    @Override
    protected Locu doInBackground(String... params) {
        super.doInBackground(params);

        // Build Request Data
        HashMap<String, String> data = new HashMap<>();
        data.put("count", "" + Locu.VENUE_LOAD_COUNT);

        return new Locu(SiteConnection.CATER_RECENT_LOAD_URL, data);

    }

    /**
     * Load in the number of delta threads. Note this will exhaust the
     * Locu result. During the next load, we must initiate a new Locu
     * object, append all the venues, and resume.
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
