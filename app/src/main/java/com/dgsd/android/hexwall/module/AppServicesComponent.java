package com.dgsd.android.hexwall.module;

import android.content.SharedPreferences;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Dagger component to provide dependency injection
 */
@Singleton
@Component(modules = HWModule.class)
public interface AppServicesComponent {

    SharedPreferences sharedPreferences();
}
