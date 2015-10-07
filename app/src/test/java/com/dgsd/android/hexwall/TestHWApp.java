package com.dgsd.android.hexwall;

/**
 * App class used to run Robolectric tests.
 */
@SuppressWarnings("unused")
public class TestHWApp extends HWApp {

    @Override
    void enableDebugTools() {
        // Not whilst running tests
    }

    @Override
    void enableAppOnlyFunctionality() {
        // Not whilst running tests
    }
}