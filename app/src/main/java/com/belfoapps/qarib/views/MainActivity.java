package com.belfoapps.qarib.views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.Navigation;

import android.os.Bundle;

import com.belfoapps.qarib.R;
import com.belfoapps.qarib.base.MainListener;
import com.belfoapps.qarib.viewmodels.MainViewModel;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity implements MainListener {
    private static final String TAG = "MainActivity";

    /***********************************************************************************************
     * *********************************** Declarations
     */
    private MainViewModel mViewModel;

    /***********************************************************************************************
     * *********************************** LifeCycle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /***********************************************************************************************
     * *********************************** Methods
     */
    @Override
    public void start() {
        Navigation.findNavController(this, R.id.nav_host_fragment).navigate(R.id.next);
    }

    @Override
    public void feed() {
        Navigation.findNavController(this, R.id.nav_host_fragment).navigate(R.id.start);
    }

    @Override
    public void chatroom() {
        Navigation.findNavController(this, R.id.nav_host_fragment).navigate(R.id.chatroom);
    }

    @Override
    public void goBack() {
        onBackPressed();
    }
}