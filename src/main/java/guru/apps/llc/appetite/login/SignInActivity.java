package guru.apps.llc.appetite.login;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import guru.apps.llc.appetite.R;
import guru.apps.llc.appetite.tool.SiteConnection;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 *
 * Created by jrpotter on 01/18/2015.
 */
public class SignInActivity extends LoginSubActivity  {

    // Hook Methods
    // ==================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.activity_sign_in);
    }


    // Event Methods
    // ==================================================

    public void signIn(View view) {
        new SignInTask().execute();
    }


    // Asynchronous Task
    // ==================================================

    protected class SignInTask extends UserAccountTask {

        public SignInTask() {
            requestURL = SiteConnection.CATER_LOGIN_URL;
            email = (EditText) findViewById(R.id.activity_sign_in_email);
            password = (EditText) findViewById(R.id.activity_sign_in_password);
        }

        @Override
        protected JSONObject doInBackground(Void... params) {
            try {
                HashMap<String, String> data = new HashMap<>();
                data.put("email", email.getText().toString());
                data.put("password", password.getText().toString());
                String response = SiteConnection.getStringResponse(requestURL, data);
                return new JSONObject(response);
            } catch (JSONException e) {
                Log.e("JSON", "Invalid JSON response (SignInActivity)");
                return null;
            }
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            super.onPostExecute(result);
            if(result != null) {
                try {
                    if(result.getInt("code") == 0) {
                        Toast.makeText(SignInActivity.this, "Logged In", Toast.LENGTH_SHORT).show();
                        login(result.getString("name"), email.getText().toString(), password.getText().toString());
                    } else {
                        // TODO: Show error message
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

    }

}
