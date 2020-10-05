package com.belfoapps.qarib.models;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;

public class SharedPreferencesHelper {
    private static final String TAG = "SharedPreferencesHelper";
    public static final String USER = "user";

    /***********************************************************************************************
     * *********************************** Declarations
     */
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    /***********************************************************************************************
     * *********************************** Constructor
     */
    @SuppressLint("CommitPrefEdits")
    public SharedPreferencesHelper(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
        editor = sharedPreferences.edit();
    }

    /***********************************************************************************************
     * *********************************** Methods
     */

    //First Run
    public boolean isFirstRun() {
        return sharedPreferences.getString(USER, null) == null;
    }

    public void setUser(String username) {
        editor.putString(USER, username).apply();
    }

    public String getUser() {
        return sharedPreferences.getString(USER, null);
    }
}
