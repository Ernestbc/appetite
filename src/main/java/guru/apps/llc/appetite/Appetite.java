package guru.apps.llc.appetite;

import android.app.Application;
import android.content.Context;

/**
 * Allows for global access to context, for use by tools specifically.
 *
 * Created by jrpotter on 2/7/15.
 */
public class Appetite extends Application {

    // Context Methods
    // ==================================================

    private static Context context;

    public static Context getContext() {
        return context;
    }

    // Hook Methods
    // ==================================================

    @Override
    public void onCreate(){
        super.onCreate();
        context = getApplicationContext();
    }

}
