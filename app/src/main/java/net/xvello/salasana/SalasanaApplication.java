package net.xvello.salasana;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;

public class SalasanaApplication extends Application {

    public void onCreate() {
        super.onCreate();

        SharedPreferences prefs = getApplicationContext().getSharedPreferences(getString(R.string.pref_name), Context.MODE_PRIVATE);
        if (!prefs.contains(getString(R.string.pref_night_name))) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }
        if (prefs.getBoolean(getString(R.string.pref_night_name), false)){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }
}