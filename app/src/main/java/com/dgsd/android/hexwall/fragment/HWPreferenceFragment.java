package com.dgsd.android.hexwall.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.text.TextUtils;

import com.dgsd.android.hexwall.BuildConfig;
import com.dgsd.android.hexwall.R;
import com.dgsd.android.hexwall.util.IntentUtils;

import de.psdev.licensesdialog.LicensesDialog;

public class HWPreferenceFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preferences);
    }

    @Override
    public void onResume() {
        super.onResume();
        final Preference buildInfoPref = findPreference(R.string.settings_key_build_info);
        if (buildInfoPref != null) {
            if (BuildConfig.DEBUG) {
                buildInfoPref.setSummary(getString(R.string.settings_summary_build_info, BuildConfig.BUILD_TIME, BuildConfig.GIT_SHA));
            } else {
                final PreferenceCategory aboutCategory = (PreferenceCategory) findPreference(R.string.settings_cat_key_about);
                aboutCategory.removePreference(buildInfoPref);
            }
        }

        final Preference appVersionPref = findPreference(R.string.settings_key_app_version);
        appVersionPref.setSummary(BuildConfig.VERSION_NAME);
    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        if (!super.onPreferenceTreeClick(preference)) {
            final String prefKey = preference.getKey();
            if (TextUtils.equals(prefKey, getString(R.string.settings_key_support))) {
                final Intent emailIntent = IntentUtils.getEmailIntent(
                        BuildConfig.SUPPORT_EMAIL,
                        getString(R.string.support_email_subject_template, getString(R.string.app_name), BuildConfig.VERSION_NAME)
                );

                if (IntentUtils.isAvailable(getActivity(), emailIntent)) {
                    startActivity(emailIntent);
                }
                return true;
            } else if (TextUtils.equals(prefKey, getString(R.string.settings_key_rate))) {
                final Intent playStoreIntent = IntentUtils.getPlayStoreIntent();
                if (IntentUtils.isAvailable(getActivity(), playStoreIntent)) {
                    startActivity(playStoreIntent);
                }

                return true;
            } else if (TextUtils.equals(prefKey, getString(R.string.settings_key_licenses))) {
                new LicensesDialog.Builder(getActivity())
                        .setTitle(R.string.settings_title_licenses)
                        .setIncludeOwnLicense(true)
                        .setNotices(R.raw.licenses)
                        .build()
                        .show();
                return true;
            }
        }

        return false;
    }

    private Preference findPreference(@StringRes int prefKey) {
        return findPreference(getString(prefKey));
    }
}
