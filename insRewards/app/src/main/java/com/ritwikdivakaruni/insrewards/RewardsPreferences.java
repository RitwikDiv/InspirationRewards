package com.ritwikdivakaruni.insrewards;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

public class RewardsPreferences {
    private static final String TAG = "RewardsPreferences";
    private SharedPreferences prefs;
    private Editor editor;

    public RewardsPreferences(Activity activity) {
        super();
        Log.d(TAG, "RewardsPreferences: Constructor");
        prefs = activity.getSharedPreferences(activity.getString(R.string.prefFile), Context.MODE_PRIVATE);
        editor = prefs.edit();
    }
    public void save(String key, String text) {
        Log.d(TAG, "save: " + key + ":" + text);
        editor.putString(key, text);
        editor.apply();
    }
    public void saveBool(String key, Boolean text) {
        Log.d(TAG, "save: " + key + ":" + text);
        editor.putBoolean(key, text);
        editor.apply();
    }
    public String getValue(String key) {
        String text = prefs.getString(key, "");
        Log.d(TAG, "getValue: " + key + " = " + text);
        return text;
    }
    public Boolean getBoolValue(String key) {
        Boolean text = prefs.getBoolean(key, false);
        Log.d(TAG, "getValue: " + key + " = " + text);
        return text;
    }
}
