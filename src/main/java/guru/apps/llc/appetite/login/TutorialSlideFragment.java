package guru.apps.llc.appetite.login;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import guru.apps.llc.appetite.R;

/**
 *
 */
public class TutorialSlideFragment extends Fragment {

    private int index;

    // Static Members
    // ==================================================

    private static final String ARG_PARAM1 = "param1";

    public static String[] slideText = new String[] {
        "Appetite is a new approach for finding venues.",
        "It allows you to choose a mood and \"food swings.\"",
        "So that you can find your personal appetite, fast."
    };


    // Factory Methods
    // ==================================================

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param index Position of slide.
     * @return A new instance of fragment IntroductionSlideFragment.
     */
    public static TutorialSlideFragment newInstance(int index) {
        TutorialSlideFragment fragment = new TutorialSlideFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, index);
        fragment.setArguments(args);
        return fragment;
    }


    // Hook Methods
    // ==================================================

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tutorial_slide, container, false);

        TextView text = (TextView) view.findViewById(R.id.fragment_tutorial_slide_text);
        text.setText(slideText[index]);

        ImageView image = (ImageView) view.findViewById(R.id.fragment_tutorial_slide_image);
        int imageId = R.drawable.tutorial_slide_1;
        if(index == 1) {
            imageId = R.drawable.tutorial_slide_2;
        } else if(index == 2) {
            imageId = R.drawable.tutorial_slide_3;
        }
        image.setImageResource(imageId);

        return view;
    }

}
