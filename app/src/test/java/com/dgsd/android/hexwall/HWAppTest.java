package com.dgsd.android.hexwall;

import android.app.Application;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RuntimeEnvironment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;

@RunWith(HWTestRunner.class)
public class HWAppTest {

    @After
    public void teardown() {
        Mockito.validateMockitoUsage();
    }

    @Test
    public void testGetApplicationComponent() {
        // Mostly for code coverage..
        final HWApp app = (HWApp) RuntimeEnvironment.application;
        assertThat(app.getAppServicesComponent()).isNotNull();
    }

    @Test
    public void testOnTrimMemoryClearsImageCacheWhenAboveUiLevel() {
        final HWApp app = (HWApp) RuntimeEnvironment.application;
        setupPicassoCache(app);
        app.onTrimMemory(Application.TRIM_MEMORY_COMPLETE);
        assertThat(app.picassoImageCache.size()).isZero();
    }

    @Test
    public void testOnTrimMemoryClearsImageCacheWhenUiHidden() {
        final HWApp app = (HWApp) RuntimeEnvironment.application;
        setupPicassoCache(app);
        app.onTrimMemory(Application.TRIM_MEMORY_UI_HIDDEN);
        assertThat(app.picassoImageCache.size()).isZero();
    }

    @Test
    public void testOnTrimMemoryDoesntClearCacheIfBelowUiHiddenLevel() {
        final HWApp app = (HWApp) RuntimeEnvironment.application;
        setupPicassoCache(app);
        app.onTrimMemory(Application.TRIM_MEMORY_RUNNING_LOW);
        Mockito.verifyZeroInteractions(app.picassoImageCache);
    }

    private void setupPicassoCache(HWApp app) {
        app.onCreate();
        try {
            app.createPicassoCache();
        } catch (IllegalStateException ex) {
            // Happens when picasso singleton set more than once .. ignore..
        }

        app.picassoImageCache = spy(app.picassoImageCache);
    }
}
