package com.dgsd.android.hexwall.module;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Dagger component to provide dependency injection
 */
@Singleton
@Component(modules = HWModule.class)
public interface AppServicesComponent {

}
