package com.belfoapps.qarib.viewmodels;

import androidx.hilt.lifecycle.ViewModelInject;
import androidx.lifecycle.ViewModel;

import com.belfoapps.qarib.models.SharedPreferencesHelper;
import com.belfoapps.qarib.utils.ConnectionsService;

public class MainViewModel extends ViewModel {
    private static final String TAG = "MainViewModel";

    /***********************************************************************************************
     * *********************************** Declarations
     */
    private ConnectionsService connections;
    private SharedPreferencesHelper mSharedPrefs;

    /***********************************************************************************************
     * *********************************** Constructor
     */
    @ViewModelInject
    public MainViewModel(ConnectionsService connections, SharedPreferencesHelper mSharedPrefs) {
        this.mSharedPrefs = mSharedPrefs;
        this.connections = connections;
    }

    /***********************************************************************************************
     * *********************************** Methods
     */
    public boolean isFirstRun() {
        return mSharedPrefs.isFirstRun();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        connections.stopBroadcasting();
    }
}
