package com.dgsd.android.hexwall.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;

import com.dgsd.android.hexwall.BuildConfig;

import java.util.List;

/**
 * Common Intent-related functionality
 */
public class IntentUtils {

    IntentUtils() {
        // No instances..
    }

    /**
     * Check if the given Intent has a component to handle it
     *
     * @param context Context used to query the {@link PackageManager}
     * @param intent  The intent to check
     * @return <code>true</code> if a component is available, <code>false</code> otherwise
     */
    public static boolean isAvailable(Context context, Intent intent) {
        final List<ResolveInfo> info = getResolveInfo(context, intent);
        return info != null && !info.isEmpty();
    }

    public static Intent getPlayStoreIntent() {
        return new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + BuildConfig.APPLICATION_ID));
    }

    public static Intent getEmailIntent(String email, String subject) {
        return new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", email, null))
                .putExtra(Intent.EXTRA_SUBJECT, subject);
    }

    /**
     * Query the package manager with the given intent
     *
     * @param context Context used to query the {@link PackageManager}
     * @param intent  The intent to check
     * @return A list of components on the device which can handle the intent
     */
    static List<ResolveInfo> getResolveInfo(Context context, Intent intent) {
        final PackageManager manager = context.getPackageManager();
        return manager.queryIntentActivities(intent, 0);
    }
}
