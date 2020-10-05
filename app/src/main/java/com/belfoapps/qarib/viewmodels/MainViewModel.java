package com.belfoapps.qarib.viewmodels;

import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.ViewModel;

import com.belfoapps.qarib.models.SharedPreferencesHelper;

public class MainViewModel extends ViewModel {
    private static final String TAG = "MainViewModel";

    /***********************************************************************************************
     * *********************************** Declarations
     */
    private SharedPreferencesHelper mSharedPrefs;

    /***********************************************************************************************
     * *********************************** Constructor
     */
    @ViewModelInject
    public MainViewModel(SharedPreferencesHelper mSharedPrefs) {
        this.mSharedPrefs = mSharedPrefs;
    }

    /***********************************************************************************************
     * *********************************** Methods
     */
    public boolean isFirstRun() {
        return mSharedPrefs.isFirstRun();
    }
}
