package com.panshen.xps.popupcirclemenu;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.TextView;

public class TextLableView extends TextView {
    public int x;
    public int y;
    public ObjectAnimator mShowAnim;
    public ValueAnimator mHideAnim;
    boolean mShowingAnim;
    boolean mHindingAnim;

    public TextLableView(Context context) {
        super(context);
        init();
    }

   private void init() {
        setPadding(30,0,30,0);
        setGravity(Gravity.CENTER);
        setBackgroundResource(R.drawable.lableshape);
        setTextColor(Color.WHITE);
        setAlpha(0f);
        mShowAnim = AnimUtil.toAlphaOne(this, 200);
        mShowAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mHindingAnim = false;
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                mHindingAnim = true;
            }
        });

        mHideAnim = AnimUtil.toAlphaZero(this, 200);
        mHideAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mShowingAnim = false;
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                mShowingAnim = true;
            }
        });
    }

    public void startShowAnim() {
        if (!mShowAnim.isRunning()&&getAlpha()==0)
            mShowAnim.start();
    }

    public void startHideAnim() {
        if (!mHideAnim.isRunning()&&getAlpha()==1)
            mHideAnim.start();
    }

    public TextLableView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

}
