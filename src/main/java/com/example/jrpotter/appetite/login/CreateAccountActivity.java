package com.example.jrpotter.appetite.login;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.jrpotter.appetite.R;
import com.example.jrpotter.appetite.tool.SiteConnection;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 *
 * Created by jrpotter on 01/18/2015.
 */
public class CreateAccountActivity extends LoginSubActivity {

    // Hook Methods
    // ==================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.activity_create_account);
    }


    // Event Methods
    // ==================================================

    public void createAccount(View view) {
        new CreateAccountTask().execute();
    }


    // Asynchronous Task
    // ==================================================

    protected class CreateAccountTask extends UserAccountTask {

        public CreateAccountTask() {
            requestURL = SiteConnection.CATER_CREATE_URL;
            name = (EditText) findViewById(R.id.activity_create_account_name);
            email = (EditText) findViewById(R.id.activity_create_account_email);
            password = (EditText) findViewById(R.id.activity_create_account_password);
        }

        @Override
        protected JSONObject doInBackground(Void... params) {
            try {
                HashMap<String, String> data = new HashMap<>();
                data.put("name", name.getText().toString());
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
                        Toast.makeText(CreateAccountActivity.this, "Account Created", Toast.LENGTH_SHORT).show();
                        login(result.getString("name"), email.getText().toString(), password.getText().toString());
                    } else {
                        // TODO: Show error messages
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

    }

}
