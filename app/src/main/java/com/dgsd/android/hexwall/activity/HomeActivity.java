package com.dgsd.android.hexwall.activity;

import android.content.Context;
import android.os.Bundle;

import com.dgsd.android.hexwall.R;
import com.dgsd.android.hexwall.mvp.presenter.HomePresenter;
import com.dgsd.android.hexwall.mvp.view.HomeMvpView;
import com.dgsd.android.hexwall.module.AppServicesComponent;

public class HomeActivity extends PresentableActivity<HomePresenter> implements HomeMvpView {

    @Override
    protected HomePresenter createPresenter(AppServicesComponent component) {
        return new HomePresenter(this, component);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.act_home;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public Context getContext() {
        return this;
    }

}
