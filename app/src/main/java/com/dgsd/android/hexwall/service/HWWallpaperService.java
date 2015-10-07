package com.dgsd.android.hexwall.service;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.view.SurfaceHolder;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

import timber.log.Timber;

public class HWWallpaperService extends WallpaperService {

    private static final long DELAY_BETWEEN_FRAMES = 1000; //ms
    private static final long COLOR_CHANGE_ANIM_DURATION = 200; //ms
    private static final int OPAQUE = 255;

    @Override
    public Engine onCreateEngine() {
        return new HWEngine();
    }

    private class HWEngine extends WallpaperService.Engine implements Runnable {

        private final Paint paint;

        private final Handler handler;

        private final ArgbEvaluator argbEvaluator;

        private ColorAnimInfo colorAnimInfo;

        private final Calendar time;

        private HWEngine() {
            paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
            paint.setStyle(Paint.Style.FILL);

            argbEvaluator = new ArgbEvaluator();

            handler = new Handler();

            handler.postDelayed(this, DELAY_BETWEEN_FRAMES);

            time = GregorianCalendar.getInstance();
        }

        @Override
        public void run() {
            if (colorAnimInfo == null) {
                colorAnimInfo = new ColorAnimInfo(paint.getColor(), getNextTargetColor());
                colorAnimInfo.animate();
            }

            invalidate();
            handler.postDelayed(this, DELAY_BETWEEN_FRAMES);
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            if (visible) {
                handler.postDelayed(this, DELAY_BETWEEN_FRAMES);
            } else {
                removeCallbacks();
            }
        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            removeCallbacks();
            super.onSurfaceDestroyed(holder);
        }

        private void removeCallbacks() {
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
                paint.setColor((int) argbEvaluator.evaluate(
                        colorAnimInfo.percentageDone, colorAnimInfo.startColor, colorAnimInfo.endColor));
            }

            canvas.drawPaint(paint);
        }

        private int getNextTargetColor() {
            time.setTimeInMillis(System.currentTimeMillis());
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
                currentAnim.setDuration(Math.min(COLOR_CHANGE_ANIM_DURATION, DELAY_BETWEEN_FRAMES / 2));
                currentAnim.start();
            }
        }
    }
}
