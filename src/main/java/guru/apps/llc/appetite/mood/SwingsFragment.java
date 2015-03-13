package guru.apps.llc.appetite.mood;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import guru.apps.llc.appetite.R;
import guru.apps.llc.appetite.drawer.DrawerFragment;
import guru.apps.llc.appetite.drawer.SubDrawerFragment;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Contains two options, depending on the mood selected, to further filter results.
 *
 * Note swings are often in stark contrast with one another, and are related to the mood
 * selected. When one of these are selected, results are pulled in from the call to the
 * server.
 *
 * Created by jrpotter on 12/25/31.
 */
public class SwingsFragment extends SubDrawerFragment {

    private String mood;
    private JSONArray response;


    // Hook Methods
    // ==================================================

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            try {
                response = new JSONArray(getArguments().getString(DrawerFragment.FRAGMENT_PARAM));
                mood = getArguments().getString(DrawerFragment.FRAGMENT_PARAM_2);
            } catch (JSONException e) {
                response = null;
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_swings, container, false);
        if (response != null) {
            try {

                // Set the header
                TextView header = (TextView) view.findViewById(R.id.fragment_main_swings_header_start);
                header.setText(mood);

                // Set Click Listeners
                // We add an OnClickListener here, since we do not want to overload
                // MainActivity with event calls.
                View.OnClickListener listener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((MoodDrawerFragment) mParent).selectSwing(v);
                    }
                };

                // Cycle through options and connect to results page
                int[] groupIds = new int[]{ R.id.fragment_main_swings_top, R.id.fragment_main_swings_bottom };
                for(int i = 0; i < groupIds.length; i++) {

                    ViewGroup current = (ViewGroup) view.findViewById(groupIds[i]);
                    current.setId(response.getJSONObject(i).getInt("pk"));
                    current.setOnClickListener(listener);

                    TextView name = (TextView) current.findViewById(R.id.include_main_swing_name);
                    name.setText(response.getJSONObject(i).getString("name"));

                    TextView description = (TextView) current.findViewById(R.id.include_main_swing_description);
                    description.setText("(" + response.getJSONObject(i).getString("description") + ")");
                }

            } catch (JSONException e) {
                // Log.e("JSON", "Invalid server response (SwingsFragment)");
            }
        }

        return view;
    }

}
