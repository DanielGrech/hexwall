package com.dgsd.android.hexwall.module;

import com.dgsd.android.hexwall.mvp.presenter.HomePresenter;
import javax.inject.Singleton;

import dagger.Component;

/**
 * Dagger component to provide dependency injection
 */
@Singleton
@Component(modules = HWModule.class)
public interface AppServicesComponent {

	void inject(HomePresenter presenter);
}
