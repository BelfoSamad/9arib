package com.belfoapps.qarib.views;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.belfoapps.qarib.R;
import com.belfoapps.qarib.base.MainListener;
import com.belfoapps.qarib.utils.Constants;
import com.belfoapps.qarib.utils.ResourcesUtils;
import com.belfoapps.qarib.viewmodels.MainViewModel;
import com.tbruyelle.rxpermissions3.RxPermissions;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity implements MainListener {
    private static final String TAG = "MainActivity";

    /***********************************************************************************************
     * *********************************** Declarations
     */
    private MainViewModel mViewModel;
    private boolean confChange = false;

    /***********************************************************************************************
     * *********************************** LifeCycle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Set Theme
        setTheme(R.style.AppTheme);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            confChange = true;
        } else {
            //Set ViewModel
            mViewModel = new ViewModelProvider(this).get(MainViewModel.class);
            //Request Permission
            requestPermissions();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!confChange && !mViewModel.isFirstRun())
            feed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Constants.REQUEST_ENABLE_BT:
                if (resultCode == RESULT_OK)
                    startService(Constants.REQUEST_ENABLE_LOCATION);
                else {
                    Toast.makeText(this, "You should enable Bluetooth", Toast.LENGTH_SHORT).show();
                }
                break;
            case Constants.REQUEST_ENABLE_LOCATION:
                break;
        }
    }

    /***********************************************************************************************
     * *********************************** Methods
     */
    @Override
    public void requestPermissions() {
        RxPermissions perm = new RxPermissions(this);
        perm.requestEach(Manifest.permission.ACCESS_FINE_LOCATION)
                .subscribe(permission -> {
                    if (permission.shouldShowRequestPermissionRationale) {
                        Toast.makeText(this, "Should I Request Again?", Toast.LENGTH_LONG).show();
                    } else if (!permission.granted) {
                        Toast.makeText(this, "Go to Settings", Toast.LENGTH_LONG).show();
                    }
                });
    }

    @Override
    public void startService(int code) {
        switch (code) {
            case Constants.REQUEST_ENABLE_BT:
                if (!ResourcesUtils.isBluetoothEnabled())
                    ResourcesUtils.enableBluetooth(this);
                break;
            case Constants.REQUEST_ENABLE_LOCATION:
                if (!ResourcesUtils.isLocationEnabled(this))
                    ResourcesUtils.enableLocation(this);
                break;
        }
    }

    @Override
    public void start() {
        Navigation.findNavController(this, R.id.nav_host_fragment).navigate(R.id.next);
    }

    @Override
    public void feed() {
        Navigation.findNavController(this, R.id.nav_host_fragment).navigate(R.id.start);
    }

    @Override
    public void profile() {
        Navigation.findNavController(this, R.id.nav_host_fragment).navigate(R.id.profile);
    }

    @Override
    public void chatroom() {
        Navigation.findNavController(this, R.id.nav_host_fragment).navigate(R.id.chatroom);
    }

    @Override
    public void privateChatroom(String chatroom) {
        Bundle bundle = new Bundle();
        bundle.putString("chatroom", chatroom);
        Navigation.findNavController(this, R.id.nav_host_fragment).navigate(R.id.private_chatroom, bundle);
    }

    @Override
    public void goBack() {
        onBackPressed();
    }
}