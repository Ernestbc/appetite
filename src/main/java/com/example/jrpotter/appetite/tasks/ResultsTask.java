package com.example.jrpotter.appetite.tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.example.jrpotter.appetite.drawer.DrawerFragment;
import com.example.jrpotter.appetite.tool.Locu;

/**
 * The following allows for some functionality to be run after
 * waiting on the Locu thread's handler to be set.
 *
 * Created by jrpotter on 1/4/15.
 */
public class ResultsTask extends AsyncTask<String, Void, Locu> {

    protected DrawerFragment mContext;

    // Constructor
    // ==================================================

    public ResultsTask(DrawerFragment context) {
        mContext = context;
    }


    // Allow running in post execution
    // ==================================================

    /**
     * Run after the result's handler is set.
     *
     * @param result ""
     */
    protected void runInLocuThread(Locu result) {
        // Intentionally blank
    }


    // Asynchronous Methods
    // ==================================================

    @Override
    protected Locu doInBackground(String... params) {

        return null;
    }

    @Override
    protected void onPostExecute(final Locu result) {
        try {

            // Begin thread
            result.start();
            synchronized (result) {
                while (result.mHandler == null) {
                    result.wait();
                }
            }

            runInLocuThread(result);

        // TODO: Replace fragment with error message
        } catch(NullPointerException e) {
            Log.e("LOCU", "Could not execute Locu thread (ResultsTask)");
            e.printStackTrace();
        } catch (InterruptedException e) {
            Log.e("THREAD", "Could not execute Locu thread (ResultsTask)");
            e.printStackTrace();
        }

    }
}
