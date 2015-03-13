package guru.apps.llc.appetite.drawer;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;


/**
 * Base class for drawer fragments.
 *
 * These are the main fragments replaced when a list item is selected in the navigation
 * drawer. These contain a single fragment container, which will be populated with a
 * SubDrawerFragment.
 *
 * Created by jrpotter on 12/31/2014.
 */
public class DrawerFragment extends Fragment {

    // Each drawer fragment contains a single layout item that has
    // SubDrawerFragments placed in; this id should be set when the
    // DrawerFragment is initialized.
    public int containerId;


    // Static Members
    // ==================================================

    // Used for initializing fragments
    public static final String FRAGMENT_PARAM = "fragment_param";
    public static final String FRAGMENT_PARAM_2 = "fragment_param_2";
    public static final String FRAGMENT_LOADING_TAG = "fragment_loading_tag";


    // Hook Methods
    // ==================================================

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }


    // Public Methods
    // ==================================================

    /**
     * Allow for access to action bar if necessary.
     *
     * @return ""
     */
    public ActionBarActivity getActionBarActivity() {
        return (ActionBarActivity) super.getActivity();
    }


    // Fragment Methods
    // ==================================================

    public void setSubDrawerFragment(SubDrawerFragment fragment) {
        FragmentManager manager = getChildFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(containerId, fragment);
        transaction.addToBackStack(null);
        transaction.commitAllowingStateLoss();
    }


    // Event Methods
    // ==================================================

    /**
     * Gives user feedback that something is going. When the back button is pressed,
     * these loading fragments must be skipped.
     */
    public void setLoadingFragment() {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(containerId, new LoadingFragment());
        transaction.addToBackStack(FRAGMENT_LOADING_TAG);
        transaction.commit();
    }

    /**
     * This allows me to write custom back press code.
     * @return Should tell whether or not to call default onBackPressed method.
     */
    public boolean onBackPressed() {

        // Pop to last loading screen (inclusive) or exit
        // I believe the back stack entry count is only updated after this call so I return
        // whether the back stack count > 2 and otherwise call onBackPressed again.
        FragmentManager manager = getChildFragmentManager();
        if (manager.getBackStackEntryCount() > 1){
            manager.popBackStack(FRAGMENT_LOADING_TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            return manager.getBackStackEntryCount() > 2;
        } else {
            return false;
        }

    }

}
