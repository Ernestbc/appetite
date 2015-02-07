package com.example.jrpotter.appetite.mood;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jrpotter.appetite.R;
import com.example.jrpotter.appetite.drawer.DrawerFragment;
import com.example.jrpotter.appetite.drawer.SubDrawerFragment;
import com.example.jrpotter.appetite.tool.SiteConnection;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Contains six random moods from the server.
 *
 * These moods allow the user to select how they are feeling, and returns
 * the appropriate mood "swings" for further filtering of results.
 */
public class MoodsFragment extends SubDrawerFragment {

    private JSONArray response;

    // Static Members
    // ==================================================

    // Convenience Members
    private static int[] activity_mood_ids = new int[] {
            R.id.activity_moods_mood1,
            R.id.activity_moods_mood2,
            R.id.activity_moods_mood3,
            R.id.activity_moods_mood4,
            R.id.activity_moods_mood5,
            R.id.activity_moods_mood6
    };


    // Hook Methods
    // ==================================================

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            try {
                response = new JSONArray(getArguments().getString(DrawerFragment.FRAGMENT_PARAM));
            } catch (JSONException e) {
                response = null;
            }
        }
    }

    /**
     * Sets the text and image properties.
     *
     * @param inflater ""
     * @param container ""
     * @param savedInstanceState ""
     * @return ""
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_moods, container, false);

        // Loads in the current day
        SimpleDateFormat format = new SimpleDateFormat("EEEE");
        String now = format.format(new Date()) + " ";

        // Loads in current time
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        if(hour < 11) {
            now += "Morning";
        } else if(hour >= 11 && hour <= 17) {
            now += "Afternoon";
        } else {
            now += "Night";
        }

        // Update Text
        TextView timeText = (TextView) view.findViewById(R.id.fragment_main_moods_time);
        timeText.setText(now);

        // Loads in the moods
        if(response != null && response.length() == activity_mood_ids.length) {
            try {
                for(int i = 0; i < activity_mood_ids.length; i++) {

                    View subView = view.findViewById(activity_mood_ids[i]);

                    // Allow Clicks
                    ViewGroup content = (ViewGroup) view.findViewById(R.id.include_main_mood_container);
                    content.setId(response.getJSONObject(i).getInt("pk"));
                    content.setTag(R.id.tags_main_swings_mood, response.getJSONObject(i).getString("name"));
                    content.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ((MoodDrawerFragment) mParent).selectMood(v);
                        }
                    });

                    // Populate TextField
                    TextView textView = (TextView) subView.findViewById(R.id.include_main_mood_text);
                    textView.setText(response.getJSONObject(i).getString("name"));

                    // Set the id of the image so onClick we know which mood we have
                    // We add an OnClickListener here, since we do not want to overload
                    // MainActivity with event calls.
                    ImageView imageView = (ImageView) subView.findViewById(R.id.include_main_mood_image);
                    if(!response.getJSONObject(i).isNull("image_url")) {
                        ImageLoader il = ImageLoader.getInstance();
                        String image_url = response.getJSONObject(i).getString("image_url");
                        il.displayImage(SiteConnection.CATER_STATIC_URL + image_url, imageView);
                    }

                }
            } catch (JSONException e) {
                Log.e("JSON", "Invalid JSON (MoodInitializationTask)");
            }
        }

        return view;
    }

}
