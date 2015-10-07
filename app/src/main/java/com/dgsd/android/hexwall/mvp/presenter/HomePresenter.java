package com.dgsd.android.hexwall.mvp.presenter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import com.dgsd.android.hexwall.activity.BaseActivity;
import com.dgsd.android.hexwall.fragment.BaseFragment;
import com.dgsd.android.hexwall.util.RxUtils;
import com.dgsd.android.hexwall.mvp.view.HomeMvpView;
import com.dgsd.android.hexwall.module.AppServicesComponent;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Base class for all presenters (In the Model-View-Presenter architecture) within the application
 */
public class HomePresenter extends Presenter<HomeMvpView> {

    public HomePresenter(@NonNull HomeMvpView view, AppServicesComponent component) {
        super(view, component);
        component.inject(this);
    }
}
