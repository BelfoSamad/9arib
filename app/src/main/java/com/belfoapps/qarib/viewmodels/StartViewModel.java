package com.belfoapps.qarib.viewmodels;

import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.ViewModel;

import com.belfoapps.qarib.models.SharedPreferencesHelper;
import com.belfoapps.qarib.pojo.User;

public class StartViewModel extends ViewModel {
    private static final String TAG = "StartViewModel";

    /***********************************************************************************************
     * *********************************** Declarations
     */
    private SharedPreferencesHelper mSharedPrefs;

    /***********************************************************************************************
     * *********************************** Constructor
     */
    @ViewModelInject
    public StartViewModel(SharedPreferencesHelper mSharedPrefs) {
        this.mSharedPrefs = mSharedPrefs;
    }

    /***********************************************************************************************
     * *********************************** Methods
     */
    public void setUser(String user) {
        mSharedPrefs.setUser(user);
    }
}