package com.panshen.xps.popupcirclemenu;

import android.animation.FloatEvaluator;
import android.animation.ObjectAnimator;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;

public class AnimUtil {
    public static ObjectAnimator toAlphaOne(View view, int duration) {
        ObjectAnimator objectAnimator = ObjectAnimator.ofObject(view, "alpha", new FloatEvaluator(), 0.0f, 1.0f);
        objectAnimator.setDuration(duration);
        return objectAnimator;
    }

    public static ObjectAnimator toAlphaZero(View view, int duration) {
        ObjectAnimator objectAnimator = ObjectAnimator.ofObject(view, "alpha", new FloatEvaluator(), 1.0f, 0f);
        objectAnimator.setDuration(duration);
        return objectAnimator;
    }

}
