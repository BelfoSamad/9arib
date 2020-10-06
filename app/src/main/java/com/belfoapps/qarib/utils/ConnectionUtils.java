package com.belfoapps.qarib.utils;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;

import com.belfoapps.qarib.views.MainActivity;

public class ConnectionUtils {
    private static final String TAG = "ConnectionUtils";

    public static boolean isBluetoothEnabled() {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return mBluetoothAdapter.isEnabled();
    }

    public static void enableBluetooth(MainActivity activity) {
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        activity.startActivityForResult(enableBtIntent, Constants.REQUEST_ENABLE_BT);
    }

    public static boolean isLocationEnabled(Context context) {
        Object object = context.getSystemService(Context.LOCATION_SERVICE);
        if (!(object instanceof LocationManager)) {
            return false;
        }
        LocationManager locationManager = (LocationManager) object;
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public static void enableLocation(MainActivity activity) {
        Intent enableLocationIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        activity.startActivityForResult(enableLocationIntent, Constants.REQUEST_ENABLE_LOCATION);
    }
}
