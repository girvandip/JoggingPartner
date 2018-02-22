package com.example.batere3a.joggingpartner;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.batere3a.joggingpartner.database.FetchData;
import com.example.batere3a.joggingpartner.models.ChangeTheme;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class ProfileActivity extends AppCompatActivity {

    private String userId;
    private String userData = null;

    public void patchUser(final String uid, final String nickname, final String phone) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String patchURL = "https://android-544df.firebaseio.com/Users/" + uid + ".json";
                    URL url = new URL(patchURL);
                    HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                    Log.i("ASDF", "MASUK4");
                    conn.setRequestMethod("PATCH");
                    conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                    conn.setRequestProperty("Accept","application/json");
                    Log.i("ASDF", "MASUK5");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);
                    Log.i("ASDF", "MASUK6");
                    conn.connect();
                    Log.i("ASDF", "MASUK3");

                    String json = "";
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("Phone", phone);
                    jsonObject.put("Nickname", nickname);
                    Log.d("JSON", jsonObject.toString());
                    DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                    os.writeBytes(jsonObject.toString());
                    os.flush();
                    os.close();
                    Log.d("STATUS", String.valueOf(conn.getResponseCode()));
                    Log.d("MSG" , conn.getResponseMessage());

                    conn.disconnect();
                } catch (Exception e) {
                    Log.e("Error", "ERROR JSON EXCEPTION");
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Change theme according to prefererence
        ChangeTheme theme = new ChangeTheme(this);
        theme.change();

        //Access user Id
        final SharedPreferences sharedPref =
                android.preference.PreferenceManager.getDefaultSharedPreferences(ProfileActivity.this);
        userId = sharedPref.getString("userId","");
        TextView result = findViewById(R.id.resultProfile);

        //Get User Data
        String resource = "Users/" + userId;
        final FetchData users = new FetchData(resource, "GET", result);
        users.execute();
        String mPhone = "";
        String mNickname = "";
        try {
            userData = users.get();
            JSONObject userJson = new JSONObject(userData);
            mPhone = userJson.getString("Phone");
            mNickname = userJson.getString("Nickname");
            Log.d("Sabeb", userData);
            result.setText(userData);
        } catch (Exception e) {
            e.printStackTrace();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Button saveButton = (Button) findViewById(R.id.SaveButton);

        //Putting data into edit text
        EditText name = (EditText) findViewById(R.id.Name);
        name.setText(sharedPref.getString("userName",""));
        EditText email = (EditText) findViewById(R.id.Email);
        email.setText(sharedPref.getString("userEmail",""));
        EditText nickName = (EditText) findViewById(R.id.Nickname);
        nickName.setText(mNickname);
        EditText phoneNumber = (EditText) findViewById(R.id.Phone);
        phoneNumber.setText(mPhone);

        saveButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                final EditText nickName = (EditText) findViewById(R.id.Nickname);
                final EditText phoneNumber = (EditText) findViewById(R.id.Phone);
                String nickname = nickName.getText().toString();
                String phone = phoneNumber.getText().toString();
                //add to shared pref
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("userPhone", phone);
                editor.putString("userNickname", nickname);
                editor.commit();

                //Patch to database
                patchUser(userId, nickname, phone);

                Toast toast = Toast.makeText(getApplicationContext(), "Profile saved.", Toast.LENGTH_SHORT);
                toast.show();
                finish();
            }
        });
    }
}
