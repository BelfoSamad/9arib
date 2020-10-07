package com.belfoapps.qarib.base;

import android.app.Application;

import com.belfoapps.qarib.R;
import com.huawei.hms.nearby.NearbyApiContext;

import dagger.hilt.android.HiltAndroidApp;

@HiltAndroidApp
public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        NearbyApiContext.getInstance().setApiKey(getResources().getString(R.string.API_KEY));
    }
}
