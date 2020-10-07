package com.belfoapps.qarib.utils;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;

public class ResourcesUtils {
    private static final String TAG = "ConnectionUtils";

    public static boolean isBluetoothEnabled() {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return mBluetoothAdapter.isEnabled();
    }

    public static void enableBluetooth(Context context) {
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        context.startActivity(enableBtIntent);
    }

    public static boolean isLocationEnabled(Context context) {
        Object object = context.getSystemService(Context.LOCATION_SERVICE);
        if (!(object instanceof LocationManager)) {
            return false;
        }
        LocationManager locationManager = (LocationManager) object;
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public static void enableLocation(Context context) {
        Intent enableLocationIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        context.startActivity(enableLocationIntent);
    }
}
