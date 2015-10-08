package com.dgsd.android.hexwall;

import android.app.Application;
import android.os.StrictMode;
import android.support.v7.preference.PreferenceManager;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.facebook.stetho.Stetho;
import com.facebook.stetho.timber.StethoTree;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;
import com.dgsd.android.hexwall.module.AppServicesComponent;
import com.dgsd.android.hexwall.module.DaggerAppServicesComponent;
import com.dgsd.android.hexwall.module.HWModule;
import com.dgsd.android.hexwall.util.Api;
import com.dgsd.android.hexwall.util.CrashlyticsLogger;

import io.fabric.sdk.android.Fabric;
import timber.log.Timber;

/**
 *
 */
public class HWApp extends Application {

    private AppServicesComponent appServicesComponent;

    private RefWatcher refWatcher = RefWatcher.DISABLED;

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            enableDebugTools();
        }

        enableAppOnlyFunctionality();

        appServicesComponent = DaggerAppServicesComponent.builder()
                .hWModule(getModule())
                .build();

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
    }


    HWModule getModule() {
        return new HWModule(this);
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
        Timber.plant(new StethoTree());

        Stetho.initialize(Stetho.newInitializerBuilder(this)
                .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                .build());

        StrictMode.enableDefaults();

        if (BuildConfig.DEBUG && Api.isUpTo(Api.LOLLIPOP)) {
            // LeakCanary causes a crash on M Developer Preview
            refWatcher = LeakCanary.install(this);
        }
    }

     /**
     * @return an {@link AppServicesComponent} which holds all the necessary dependencies
     * other application components may want to use for injection purposes
     */
    public AppServicesComponent getAppServicesComponent() {
        return appServicesComponent;
    }

    public RefWatcher getRefWatcher() {
        return refWatcher;
    }
}
