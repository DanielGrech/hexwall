package com.dgsd.android.hexwall.module;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.dgsd.android.hexwall.HWApp;
import com.dgsd.android.hexwall.data.AppSettings;
import com.lacronicus.easydatastorelib.DatastoreBuilder;

import com.squareup.leakcanary.RefWatcher;

import java.util.LinkedList;
import java.util.List;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Dagger module to provide dependency injection
 */
@SuppressWarnings("unused")
@Module
public class HWModule {
    private HWApp application;

    public HWModule(HWApp app) {
        application = app;
    }

    @Provides
    @Singleton
    HWApp providesApp() {
        return application;
    }

    @Provides
    Context providesContext() {
        return application;
    }

    @Provides
    RefWatcher providesRefWatcher(HWApp app) {
        return app.getRefWatcher();
    }

    @Provides
    @Singleton
    SharedPreferences providesSharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Provides
    @Singleton
    AppSettings providesAppSettings(SharedPreferences prefs) {
        return new DatastoreBuilder(prefs).create(AppSettings.class);
    }
}
