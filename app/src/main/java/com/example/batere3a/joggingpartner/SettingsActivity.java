package com.example.batere3a.joggingpartner;

import android.content.SharedPreferences;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {
    public static final String
            KEY_PREF_THEME = "list_preference_1";
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
        getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }
}
