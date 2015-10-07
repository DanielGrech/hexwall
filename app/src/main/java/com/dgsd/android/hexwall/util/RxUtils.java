package com.dgsd.android.hexwall.util;

import com.dgsd.android.hexwall.activity.BaseActivity;
import com.dgsd.android.hexwall.fragment.BaseFragment;

import rx.Observable;
import com.trello.rxlifecycle.RxLifecycle;

/**
 * Utility methods for working with RxJava observables
 */
public class RxUtils {

    RxUtils() {
        // No instances..
    }

    /**
     * Bind the given observable to the activity.
     * <p/>
     * This method ensures that observables are unsubscribed from at the correct time + dont
     * call back to the activity in an invalid state
     *
     * @param activity   The activity to bind to
     * @param observable The observable to bind
     */
    public static <T> Observable<T> bindActivity(BaseActivity activity, Observable<T> observable) {
        return observable.compose(RxLifecycle.<T>bindActivity(activity.lifecycle()));
    }

    /**
     * Bind the given observable to the fragment.
     * <p/>
     * This method ensures that observables are unsubscribed from at the correct time + dont
     * call back to the fragment in an invalid state
     *
     * @param fragment   The fragment to bind to
     * @param observable The observable to bind
     */
    public static <T> Observable<T> bindFragment(BaseFragment fragment, Observable<T> observable) {
        return observable.compose(RxLifecycle.<T>bindFragment(fragment.lifecycle()));
    }

}
