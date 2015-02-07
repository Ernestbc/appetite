package com.example.jrpotter.appetite.tool;

import android.content.SharedPreferences;
import android.util.Log;

import com.facebook.model.GraphUser;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.HashMap;

/**
 * Used to interact with the server.
 *
 * Created by jrpotter on 12/25/14.
 */
public class SiteConnection {

    // Static Members
    // ==================================================

    // URLs
    public static final String CATER_URL = "http://site-cater.rhcloud.com/";
    public static final String CATER_STATIC_URL = CATER_URL + "static/";
    public static final String CATER_MOOD_URL = CATER_URL + "tags/mood/";
    public static final String CATER_SWING_URL = CATER_URL + "tags/mood_swing/";
    public static final String CATER_SEARCH_URL = CATER_URL + "tags/venue_search/";
    public static final String CATER_CREATE_URL = CATER_URL + "user/create/";
    public static final String CATER_LOGIN_URL = CATER_URL + "user/login/";
    public static final String CATER_PROFILE_URL = CATER_URL + "user/profile/";
    public static final String CATER_RECENT_SAVE_URL = CATER_URL + "recent/save/";
    public static final String CATER_RECENT_LOAD_URL = CATER_URL + "recent/load/";


    // Facebook Methods
    // ==================================================

    public static String getFacebookImageURL(GraphUser user) {
        return "http://graph.facebook.com/"+ user.getId()+ "/picture?width=200&height=200";
    }


    // Internet Methods
    // ==================================================

    public static String getPost(HashMap<String, String> data) {

        // Add credentials to data (if logged in)
        SharedPreferences pref = UserStorage.getInstance(null).getPref();
        if(pref.getBoolean(UserStorage.PREF_LOGGED_IN, false)) {
            data.put("email", pref.getString(UserStorage.PREF_CREDENTIALS_EMAIL, ""));
            data.put("password", pref.getString(UserStorage.PREF_CREDENTIALS_PASSWORD, ""));
        }

        // Build up request
        StringBuilder request = new StringBuilder();
        for(String key : data.keySet()) {
            try {
                request.append(URLEncoder.encode(key, "UTF-8"));
                request.append("=");
                request.append(URLEncoder.encode(data.get(key), "UTF-8"));
                request.append("&");
            } catch (UnsupportedEncodingException e) {
                Log.e("URL", "Could not encode data (SiteConnection)");
                e.printStackTrace();
            }
        }

        return request.toString();
    }

    /**
     *
     * @param data
     * @return
     */
    public static byte[] getPostData(HashMap<String, String> data) {
        return getPost(data).getBytes(Charset.forName("utf-8"));
    }

    /**
     * Returns the buffered reader corresponding to the URL connection.
     * It is up to the user to make sure to close the BufferedReader.
     *
     * @param requestURL ""
     * @param data ""
     * @return ""
     */
    public static HttpURLConnection getConnection(String requestURL, HashMap<String, String> data) {

        // Build up response
        try {

            byte[] postData = getPostData(data);

            // Connect to site
            java.net.URL url = new URL(requestURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Charset", "UTF-8");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Content-Length", Integer.toString(postData.length));
            conn.setUseCaches(false);

            // Write Data
            DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
            wr.write(postData);
            wr.close();

            return conn;

        } catch (MalformedURLException e) {
            Log.e("URL", "Malformed URL (SiteConnection)");
            e.printStackTrace();
        } catch (ProtocolException e) {
            Log.e("URL", "Protocol Exception (SiteConnection)");
            e.printStackTrace();
        } catch (IOException e) {
            Log.e("URL", "IO Exception (SiteConnection)");
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Note this is a separate function as calling this requires the user
     * to manually close the BufferedReader.
     *
     * @param requestURL ""
     * @param data ""
     * @return ""
     */
    public static BufferedReader getBufferedResponse(String requestURL, HashMap<String, String> data) {
        try {
            HttpURLConnection conn = getConnection(requestURL, data);
            InputStream in = conn.getInputStream();
            if(in != null) {
                return new BufferedReader(new InputStreamReader(in));
            } else {
                return null;
            }
        } catch (IOException e) {
            Log.e("URL", "BufferedReader Error (SiteConnection)");
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Returns the totality of the response, as opposed to a stream.
     *
     * @param requestURL ""
     * @param data ""
     */
    public static String getStringResponse(String requestURL, HashMap<String, String> data) {

        StringBuilder response = new StringBuilder();
        BufferedReader reader = getBufferedResponse(requestURL, data);

        if(reader != null) {
            try {
                for (String s = ""; s != null; s = reader.readLine()) {
                    response.append(s);
                }
                reader.close();
            } catch (IOException e) {
                Log.e("URL", "Could not read request (SiteConnection");
                e.printStackTrace();
            }
        }

        return response.toString();
    }

}
