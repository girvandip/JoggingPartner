package com.example.batere3a.joggingpartner;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.batere3a.joggingpartner.database.FetchData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        SharedPreferences sharedPref =
                PreferenceManager.getDefaultSharedPreferences(this);
        String storedTheme = sharedPref.getString(SettingsActivity.KEY_PREF_THEME, "Green");
        if(storedTheme.equals("Green")) {
            setTheme(R.style.AppThemeGreen);
        } else if(storedTheme.equals("Orange")) {
            setTheme(R.style.AppThemeOrange);
        } else {
            setTheme(R.style.AppThemeBlue);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        //get current token
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String mUserId;
        TextView result = findViewById(R.id.result);
        if (user != null) {
            // Using getToken as recommended in the firebase docs.
            mUserId = user.getToken(true).toString();
            FetchData users = new FetchData("Users", "GET", result);
            users.execute();
        }

        Button saveButton = (Button) findViewById(R.id.SaveButton);
        final EditText nickName = (EditText) findViewById(R.id.Nickname);
        final EditText phoneNumber = (EditText) findViewById(R.id.Phone);
        saveButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String nickname = nickName.getText().toString();
                String phone = phoneNumber.getText().toString();
                Toast toast = Toast.makeText(getApplicationContext(), "Profile saved.", Toast.LENGTH_SHORT);
                toast.show();
                finish();
            }
        });
    }
}
