package com.example.batere3a.joggingpartner;

import android.content.SharedPreferences;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.widget.Toast;

import com.example.batere3a.joggingpartner.models.ChangeTheme;

public class SettingsActivity extends AppCompatActivity {
    public static final String
            KEY_PREF_THEME = "list_preference_1";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ChangeTheme theme = new ChangeTheme(this);
        theme.change();
        super.onCreate(savedInstanceState);
        getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }
}
