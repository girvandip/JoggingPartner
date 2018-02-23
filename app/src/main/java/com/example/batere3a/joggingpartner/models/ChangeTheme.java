package com.example.batere3a.joggingpartner.models;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.batere3a.joggingpartner.R;
import com.example.batere3a.joggingpartner.SettingsActivity;

/**
 * Created by DELL on 22-Feb-18.
 */

public class ChangeTheme {
    private Context context;

    public ChangeTheme(Context context) {
        this.context = context;
    }

    public void change() {
        PreferenceManager.setDefaultValues(context, R.xml.preferences, false);
        SharedPreferences sharedPref =
                PreferenceManager.getDefaultSharedPreferences(context);
        String storedTheme = sharedPref.getString(SettingsActivity.KEY_PREF_THEME, "Green");

        if(storedTheme.equals("Green")) {
            context.setTheme(R.style.AppThemeGreen);
        } else if(storedTheme.equals("Orange")) {
            context.setTheme(R.style.AppThemeOrange);
        } else {
            context.setTheme(R.style.AppThemeBlue);
        }
    }
}
