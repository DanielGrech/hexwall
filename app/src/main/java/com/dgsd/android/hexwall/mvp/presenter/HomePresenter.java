package com.dgsd.android.hexwall.mvp.presenter;

import android.support.annotation.NonNull;

import com.dgsd.android.hexwall.module.AppServicesComponent;
import com.dgsd.android.hexwall.mvp.view.HomeMvpView;

/**
 * Base class for all presenters (In the Model-View-Presenter architecture) within the application
 */
public class HomePresenter extends Presenter<HomeMvpView> {

    public HomePresenter(@NonNull HomeMvpView view, AppServicesComponent component) {
        super(view, component);
        component.inject(this);
    }
}
