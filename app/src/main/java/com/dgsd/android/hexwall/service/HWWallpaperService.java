package com.dgsd.android.hexwall.service;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.WallpaperManager;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.view.SurfaceHolder;

import com.dgsd.android.hexwall.HWApp;
import com.dgsd.android.hexwall.R;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import timber.log.Timber;

public class HWWallpaperService extends WallpaperService {

    private static final long DEFAULT_COLOR_CHANGE_ANIM_DURATION = 200; //ms
    private static final int OPAQUE = 255;

    private static final Random RANDOM = new Random();

    private HWEngine engine;

    @Override
    public Engine onCreateEngine() {
        final HWApp app = (HWApp) getApplication();
        engine = new HWEngine(app.getAppServicesComponent().sharedPreferences());
        return engine;
    }


    private class HWEngine extends WallpaperService.Engine
            implements Runnable, SharedPreferences.OnSharedPreferenceChangeListener {

        private final SharedPreferences sharedPreferences;

        private final Paint bgPaint;

        private final Handler handler;

        private final ArgbEvaluator argbEvaluator;

        private final Calendar time;

        private ColorAnimInfo colorAnimInfo;

        private int touchOffset = 0;

        private long delayBetweenFrames = TimeUnit.SECONDS.toMillis(1);

        private HWEngine(SharedPreferences sharedPreferences) {
            this.sharedPreferences = sharedPreferences;
            this.sharedPreferences.registerOnSharedPreferenceChangeListener(this);

            ensureDelayBetweenFrames();

            bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
            bgPaint.setStyle(Paint.Style.FILL);

            argbEvaluator = new ArgbEvaluator();

            handler = new Handler();

            handler.post(this);

            time = GregorianCalendar.getInstance();
        }

        @Override
        public void run() {
            redrawBackground();
            handler.postDelayed(this, delayBetweenFrames);
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            if (visible) {
                this.sharedPreferences.registerOnSharedPreferenceChangeListener(this);
                handler.post(this);
            } else {
                removeCallbacks();
            }
        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            removeCallbacks();
            super.onSurfaceDestroyed(holder);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (getString(R.string.settings_key_color_change_duration).equals(key)) {
                ensureDelayBetweenFrames();
            }
        }

        @Override
        public Bundle onCommand(String action, int x, int y, int z, Bundle extras, boolean resultRequested) {
            if (WallpaperManager.COMMAND_TAP.equals(action)) {
                touchOffset = RANDOM.nextInt((int) TimeUnit.DAYS.toMillis(1));

                if (colorAnimInfo != null && colorAnimInfo.currentAnim != null) {
                    colorAnimInfo.currentAnim.cancel();
                }
                colorAnimInfo = null;
                redrawBackground();
            }

            return super.onCommand(action, x, y, z, extras, resultRequested);
        }

        private void removeCallbacks() {
            sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
            handler.removeCallbacks(this);
            if (colorAnimInfo != null) {
                if (colorAnimInfo.currentAnim != null) {
                    colorAnimInfo.currentAnim.cancel();
                }
                colorAnimInfo = null;
            }
        }

        private void invalidate() {
            final SurfaceHolder holder = getSurfaceHolder();
            Canvas canvas = null;
            try {
                canvas = holder.lockCanvas();
                if (canvas != null) {
                    draw(canvas);
                }
            } finally {
                if (canvas != null) {
                    holder.unlockCanvasAndPost(canvas);
                }
            }
        }

        private void draw(Canvas canvas) {
            if (colorAnimInfo != null) {
                bgPaint.setColor((int) argbEvaluator.evaluate(
                        colorAnimInfo.percentageDone, colorAnimInfo.startColor, colorAnimInfo.endColor));
            }

            canvas.drawPaint(bgPaint);
        }

        private int getNextTargetColor() {
            time.setTimeInMillis(System.currentTimeMillis() + touchOffset);
            final int color = Color.rgb(
                    getTimeFraction(time.get(Calendar.HOUR_OF_DAY), (int) TimeUnit.DAYS.toHours(1)),
                    getTimeFraction(time.get(Calendar.MINUTE), (int) TimeUnit.HOURS.toMinutes(1)),
                    getTimeFraction(time.get(Calendar.SECOND), (int) TimeUnit.MINUTES.toSeconds(1))
            );

            Timber.d("Setting color: %s", Integer.toHexString(color));

            return color;
        }

        private int getTimeFraction(int value, int maxValue) {
            float percentageDone = (1f * value) / maxValue;
            return (int) (percentageDone * OPAQUE);
        }

        private void ensureDelayBetweenFrames() {
            final String delayAsString = sharedPreferences.getString(
                    getString(R.string.settings_key_color_change_duration), String.valueOf(delayBetweenFrames));
            try {
                delayBetweenFrames = Long.valueOf(delayAsString);
            } catch (NullPointerException | NumberFormatException ex) {
                Timber.e(ex, "Error converting delay between background changes: %s", delayAsString);
            }
        }

        private void redrawBackground() {
            if (colorAnimInfo == null) {
                colorAnimInfo = new ColorAnimInfo(bgPaint.getColor(), getNextTargetColor());
                colorAnimInfo.animate();
            }

            invalidate();
        }

        private class ColorAnimInfo {
            final Integer startColor;
            final Integer endColor;
            float percentageDone;
            ValueAnimator currentAnim;

            private ColorAnimInfo(int startColor, int endColor) {
                this.startColor = startColor;
                this.endColor = endColor;
            }

            void animate() {
                if (currentAnim != null) {
                    currentAnim.cancel();
                }

                currentAnim = ValueAnimator.ofFloat(0f, 1f);
                currentAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        percentageDone = animation.getAnimatedFraction();
                        invalidate();
                    }
                });
                currentAnim.addListener(new Animator.AnimatorListener() {

                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        colorAnimInfo = null;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
                currentAnim.setDuration(Math.min(DEFAULT_COLOR_CHANGE_ANIM_DURATION, delayBetweenFrames / 2));
                currentAnim.start();
            }
        }
    }
}
