package com.panshen.xps.popupcirclemenu;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;

public class PopupButton extends View {
    final String TAG = this.getClass().getName();
    Bitmap mBitmap;
    Paint mPaint = new Paint();
    int mWidth;
    int mMargin;
    int mCircleRadius;
    public int x;
    public int y;
    Rect mRect = new Rect();
    boolean outanimating;
    boolean inanimating;
    BUTTON_STATE mButtonState = BUTTON_STATE.NORMAL;
    ValueAnimator inanim;
    ValueAnimator outanim;
    ValueAnimator mAnimeExplode;
    Path mPathExplode = new Path();
    PathMeasure mPathMeasureExplode = new PathMeasure();
    boolean isExploded;

    int mColorNormal;
    int mColorChecked;
    Bitmap mBackgroundChecked;
    Bitmap mBackground;
    boolean mChecked;
    boolean mCheckable;
    int mAnimDuration;
    String mTextOn;
    String mTextOff;

    public Path getmPathExplode() {
        mPathExplode.reset();
        return mPathExplode;
    }

    public void setmAnimDuration(int duration) {
        mAnimDuration = duration;
    }

    /**
     * @return  根据button的勾选状态
     *          返回相应的文字
     * */
    public String getCurrentText() {
        if (!TextUtils.isEmpty(mTextOff) && !TextUtils.isEmpty(mTextOn)) {
            if (isChecked()) return mTextOff;
            else return mTextOn;
        }else return "-1";

    }

    public PopupButton(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        initCofig(context, attrs);
        init();
    }

    public PopupButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initCofig(context, attrs);
        init();
    }

    void initCofig(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CircleButton);
        mColorNormal = a.getColor(R.styleable.CircleButton_pb_color, Color.parseColor("#ffffff"));
        mColorChecked = a.getColor(R.styleable.CircleButton_pb_color_checked, Color.parseColor("#FFFF000D"));
        mTextOn = a.getString(R.styleable.CircleButton_pb_text_on);
        mTextOff = a.getString(R.styleable.CircleButton_pb_text_off);
        BitmapDrawable bdmBackgroundChecked = (BitmapDrawable) a.getDrawable(R.styleable.CircleButton_pb_background_checked);
        mCheckable = a.getBoolean(R.styleable.CircleButton_pb_checkable, false);

        if (bdmBackgroundChecked != null)
            mBackgroundChecked = bdmBackgroundChecked.getBitmap();

        BitmapDrawable bdmBackground = (BitmapDrawable) a.getDrawable(R.styleable.CircleButton_pb_background);
        if (bdmBackground != null)
            mBackground = bdmBackground.getBitmap();

        a.recycle();
    }

    void init() {
        inanim = ValueAnimator.ofFloat(1.0f, 1.1f);
        inanim.setDuration(100);
        inanim.setInterpolator(new LinearInterpolator());
        inanim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float s = Float.valueOf(animation.getAnimatedValue() + "");
                setScaleX(s);
                setScaleY(s);
            }
        });
        inanim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                inanimating = false;
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                inanimating = true;
                mButtonState = BUTTON_STATE.CHECKED;

            }
        });

        outanim = ValueAnimator.ofFloat(1.1f, 1.0f);
        outanim.setDuration(100);
        outanim.setInterpolator(new LinearInterpolator());
        outanim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float s = Float.valueOf(animation.getAnimatedValue() + "");
                setScaleX(s);
                setScaleY(s);
            }
        });

        outanim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                outanimating = false;
                inanimating = false;
                mButtonState = BUTTON_STATE.NORMAL;
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                outanimating = true;

            }
        });
        post(new Runnable() {
            @Override
            public void run() {
                mWidth = getWidth();
                mMargin = mWidth / 10;
                mCircleRadius = mWidth / 2 - mMargin;

                mPaint.setAntiAlias(true);
                mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
                mPaint.setColor(mColorNormal);
                mPaint.setStrokeWidth(mMargin / 2);

                if (mBackground != null) {
                    mBitmap = Bitmap.createScaledBitmap(mBackground, mCircleRadius, mCircleRadius, true);
                }
            }
        });
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mBitmap == null) {
            mPaint.setStyle(Paint.Style.STROKE);
            canvas.drawCircle(mWidth / 2, mWidth / 2, mCircleRadius, mPaint);
        } else {
            if (isChecked()) {
                mPaint.setColor(mColorChecked);
                if (mBackgroundChecked != null)
                    mBitmap = Bitmap.createScaledBitmap(mBackgroundChecked, mCircleRadius, mCircleRadius, true);
            } else {
                mPaint.setColor(mColorNormal);
                if (mBackground != null)
                    mBitmap = Bitmap.createScaledBitmap(mBackground, mCircleRadius, mCircleRadius, true);
            }
            mPaint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(mWidth / 2, mWidth / 2, mCircleRadius, mPaint);
            canvas.drawBitmap(mBitmap, mCircleRadius + mMargin - mBitmap.getWidth() / 2, mCircleRadius + mMargin - mBitmap.getHeight() / 2, mPaint);
        }

    }

    void explode() {
        mPathMeasureExplode.setPath(mPathExplode, false);
        PropertyValuesHolder propertyScaleAnim = PropertyValuesHolder.ofFloat("anim_scale", mPathMeasureExplode.getLength(), 0f);
        PropertyValuesHolder propertyAlphaAnim = PropertyValuesHolder.ofFloat("anim_alpha", 0.0f, 0.0f, 0.3f, 0.5f, 1.0f);

        mAnimeExplode = ValueAnimator.ofPropertyValuesHolder(propertyScaleAnim, propertyAlphaAnim);
        mAnimeExplode.setDuration(mAnimDuration);
        mAnimeExplode.setInterpolator(new LinearOutSlowInInterpolator());
        mAnimeExplode.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float[] pos = {0, 0};
                float animscaledValue = Float.valueOf(animation.getAnimatedValue("anim_scale") + "");
                float currentDis = mPathMeasureExplode.getLength() - animscaledValue;
                mPathMeasureExplode.getPosTan(currentDis, pos, null);
                setX(pos[0]);
                setY(pos[1]);

                float animalphaValue = Float.valueOf(animation.getAnimatedValue("anim_alpha") + "");
                setAlpha(animalphaValue);
            }
        });
        mAnimeExplode.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                isExploded = true;
            }
        });
        mAnimeExplode.start();

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        int x = (int) event.getRawX();
        int y = (int) event.getRawY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                setScaleX(1f);
                setScaleY(1f);
                isExploded = false;

                if (!mCheckable) {
                    mChecked = false;
                }

                break;
            case MotionEvent.ACTION_MOVE:
                getHitRect(mRect);
                if (isExploded)
                    if (mRect.contains(x, y)) {
                        if (mButtonState.equals(BUTTON_STATE.NORMAL) && !inanimating) {
                            inanim.start();
                            toggleCheck();
                        }
                    } else if (mButtonState.equals(BUTTON_STATE.CHECKED) && !outanimating && !inanimating) {
                        outanim.start();
                        toggleCheck();
                    }

                break;
            case MotionEvent.ACTION_UP:
                outanimating = false;
                inanimating = false;
                mButtonState = BUTTON_STATE.NORMAL;

                if (mAnimeExplode != null && mPathMeasureExplode != null && mPathExplode != null) {
                    mAnimeExplode.reverse();
                }

                break;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }

    public boolean isExploded(){
        return isExploded;
    }

    void toggleCheck() {
        if (isChecked()) {
            unCheck();
            setChecked(false);
        } else {
            check();
            setChecked(true);
        }
    }

    void check() {
        if (mBackgroundChecked == null) return;
        mBitmap = Bitmap.createScaledBitmap(mBackgroundChecked, mCircleRadius, mCircleRadius, true);
        mPaint.setColor(mColorChecked);
        invalidate();
    }

    void unCheck() {
        if (mBackground == null) return;
        mBitmap = Bitmap.createScaledBitmap(mBackground, mCircleRadius, mCircleRadius, true);
        mPaint.setColor(mColorNormal);
        invalidate();
    }

    public boolean isChecked() {
        return mChecked;
    }

    public void setChecked(boolean mChecked) {
        this.mChecked = mChecked;
        invalidate();
    }

    private enum BUTTON_STATE {
        NORMAL, CHECKED
    }

}
