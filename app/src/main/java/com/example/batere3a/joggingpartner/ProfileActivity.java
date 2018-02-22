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

public class ProfileActivity extends AppCompatActivity {

    private String userId;
    private String userPhone;
    private String userData = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Change theme according to prefererence
        ChangeTheme theme = new ChangeTheme(this);
        theme.change();
        //Access user Id
        SharedPreferences sharedPref =
                android.preference.PreferenceManager.getDefaultSharedPreferences(ProfileActivity.this);
        userPhone = sharedPref.getString("userPhone","test");
        userId = sharedPref.getString("userId","");
        Log.d("UserID", userId);
        Log.d("UserPhone", userPhone);
        TextView result = findViewById(R.id.resultProfile);
        String resource = "Users/" + "8CJB9g1hSJYMUpHYLu7XH9V5hXD2";
        FetchData users = new FetchData(resource, "GET", result);
        users.execute();
        try {
            userData = users.get();
            Log.d("Sabeb", userData);
            result.setText(userData);
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

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
