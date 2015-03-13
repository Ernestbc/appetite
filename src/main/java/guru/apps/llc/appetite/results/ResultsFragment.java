package guru.apps.llc.appetite.results;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import guru.apps.llc.appetite.R;
import guru.apps.llc.appetite.drawer.SubDrawerFragment;
import guru.apps.llc.appetite.results.view.VenueLoadingView;
import guru.apps.llc.appetite.results.view.VenuePeekView;
import guru.apps.llc.appetite.tool.Locu;
import guru.apps.llc.appetite.tool.SiteConnection;

import java.util.HashMap;

/**
 * Lists flashcards containing quick information on results returned from server.
 *
 * Note these flashcards display short information about the restaurant- selecting
 * one will show more verbose information.
 *
 * Created by jrpotter on 12/25/31.
 */
public class ResultsFragment extends SubDrawerFragment {

    private AbsListView.OnScrollListener mScrollListener;

    private volatile Locu response;


    // Hook Methods
    // ==================================================

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_results, container, false);
        if(response != null) {
            ListView listView = (ListView) view.findViewById(R.id.fragment_results_list);
            listView.setAdapter(new ResultsListAdapter());
            listView.setOnScrollListener(mScrollListener);
        }

        return view;
    }


    // Public Methods
    // ==================================================

    /**
     * This must be set after an asynchronous call to the server.
     *
     * The adapter source allows streaming of venue objects, which are used
     * to populate the list view.
     *
     * @param source "The Locu thread that loads in venues"
     */
    public void setAdapterSource(Locu source) {
        if(response != null) {
            response.terminate();
        }
        response = source;
    }

    /**
     * Allow for custom listeners to be set on the list view.
     *
     * For instance, the InitRecentsTask will override the scroll listener
     * to make new calls to the server (since responses are offset).
     */
    public void setOnScrollListener(AbsListView.OnScrollListener listener) {
        mScrollListener = listener;
    }


    // List Adapter
    // ==================================================

    /**
     * The following uses the Locu source object to stream list items.
     *
     * Once the user has scrolled far enough, the adapter is notified to load
     * more sources. If no venues are left, instead we show a view telling the user this.
     */
    private class ResultsListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return response.venues.size() + 1;
        }

        @Override
        public Object getItem(int position) {
            if(position == response.venues.size()) {
                return new Object();
            } else {
                return response.venues.get(position);
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            // Hit end, should show loading/finished message
            if(position == response.venues.size()) {
                VenueLoadingView loadingView = new VenueLoadingView(getActivity());
                if (response.loadedAllPages()) {
                    loadingView.displayFinishedMessage();
                }
                return loadingView;
            }

            // Attempt to assign to a convertable view
            VenuePeekView preview;

            // Obtain the current venue
            final Locu.Venue current = response.venues.get(position);

            // Need to clear out layouts and rebuild the window
            if(convertView != null && convertView instanceof VenuePeekView) {
                preview = (VenuePeekView) convertView;
                preview.initialize(current);

            // Otherwise, load in a new preview
            } else {
                preview = new VenuePeekView(current, getActivity());
                preview.setBackgroundResource(R.drawable.shape_rectangular_frame);
                preview.setPadding(15, 15, 15, 15);
            }

            // We switch fragments if the user selects the menu icon. When this occurs, we also
            // notify the server of the request to save recently viewed venues
            preview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mParent.setLoadingFragment();
                    if (current.locu_id != null) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                // HashMap<String, String> data = new HashMap<>();
                                // data.put("locu_id", current.locu_id);
                                // SiteConnection.getStringResponse(SiteConnection.CATER_RECENT_SAVE_URL, data);
                                mParent.getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        VenueDetailFragment fragment = new VenueDetailFragment();
                                        fragment.setVenue(current);
                                        mParent.setSubDrawerFragment(fragment);
                                    }
                                });
                            }
                        }).start();
                    }
                }
            });

            return preview;
        }
    }


    // List Scroll Listener
    // ==================================================

    public static class OnScrollListener implements AbsListView.OnScrollListener {

        // Allow querying the Locu object
        protected Locu response;

        // Allow running on the UI thread
        private ActionBarActivity context;


        // Constructor
        // --------------------------------------------------

        public OnScrollListener(ActionBarActivity context, Locu response) {
            this.response = response;
            this.context = context;
        }


        // Update Methods
        // --------------------------------------------------

        /**
         * Determine whether the scroll event should be triggered.
         *
         * @param view ""
         * @return ""
         */
        protected boolean initiateInvalidation(AbsListView view) {
            return true;
        }

        /**
         * Request to update the data set.
         * This will be run in the spawned Locu thread.
         *
         * @param view ""
         * @return ""
         */
        protected boolean invalidateAdapter(AbsListView view) {

            return false;
        }

        /**
         * Should be called after the data set has been updated.
         * This will be run on the main UI thread.
         *
         * @param view ""
         */
        protected void postInvalidateAdapter(AbsListView view) {
            BaseAdapter adapter = (BaseAdapter) view.getAdapter();
            adapter.notifyDataSetChanged();
        }


        // Scroll Methods
        // --------------------------------------------------

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            // Intentionally blank
        }

        /**
         * Called when the bottom of the list view is reached.
         *
         * @param view ""
         * @param firstVisibleItem ""
         * @param visibleItemCount ""
         * @param totalItemCount ""
         */
        @Override
        public void onScroll(final AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            // Only trigger the on scroll event if the last event has been seen
            int lastVisibleItem = firstVisibleItem + visibleItemCount;

            // Try and load in more if the loading screen can be seen
            // We set the tag to avoid calling this function multiple times
            if(lastVisibleItem >= totalItemCount - Locu.VENUE_LOAD_COUNT && initiateInvalidation(view)) {
                response.mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(invalidateAdapter(view)) {
                            context.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    postInvalidateAdapter(view);
                                }
                            });
                        }
                    }
                });
            }
        }
    }
}