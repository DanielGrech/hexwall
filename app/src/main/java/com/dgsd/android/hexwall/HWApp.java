package com.dgsd.android.hexwall;

import android.app.Application;
import android.os.StrictMode;
import android.support.v7.preference.PreferenceManager;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.dgsd.android.hexwall.util.CrashlyticsLogger;

import io.fabric.sdk.android.Fabric;
import timber.log.Timber;

/**
 *
 */
public class HWApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            enableDebugTools();
        }

        enableAppOnlyFunctionality();

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
    }

    void enableAppOnlyFunctionality() {
        if (!BuildConfig.DEBUG) {
            Fabric.with(this, new Crashlytics(), new Answers());
            Timber.plant(new CrashlyticsLogger());
        }
    }

    /**
     * Enables all debug-only functionality.
     * <p/>
     * Extract into a method to allow overriding in other modules/tests
     */
    void enableDebugTools() {
        Timber.plant(new Timber.DebugTree());
        StrictMode.enableDefaults();
    }
}
