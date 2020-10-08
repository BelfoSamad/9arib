package com.belfoapps.qarib.utils;

import android.content.Context;

import androidx.room.Room;

import com.belfoapps.qarib.base.AppDatabase;
import com.belfoapps.qarib.models.SharedPreferencesHelper;
import com.huawei.hms.analytics.HiAnalytics;
import com.huawei.hms.analytics.HiAnalyticsInstance;
import com.huawei.hms.analytics.HiAnalyticsTools;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ActivityComponent;
import dagger.hilt.android.components.FragmentComponent;
import dagger.hilt.android.qualifiers.ApplicationContext;

@Module
@InstallIn({ActivityComponent.class, FragmentComponent.class})
public class Modules {
    public static final String DATABASE_NAME = "9arib";

    @Provides
    public static SharedPreferencesHelper providesSharedPreferences(@ApplicationContext Context context) {
        return new SharedPreferencesHelper(context.getSharedPreferences("BASIC", Context.MODE_PRIVATE));
    }

    @Provides
    public static AppDatabase providesAppDatabase(@ApplicationContext Context context) {
        return Room.databaseBuilder(context,
                AppDatabase.class, DATABASE_NAME).build();
    }

    @Provides
    public static HiAnalyticsInstance providesInstance(@ApplicationContext Context context) {
        HiAnalyticsTools.enableLog();
        return HiAnalytics.getInstance(context);
    }
}
