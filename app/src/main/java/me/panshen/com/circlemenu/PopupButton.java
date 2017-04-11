package me.panshen.com.circlemenu;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Rect;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.RelativeLayout;


public class PopupButton extends View {
    private final String TAG = this.getClass().getName();
    private int mAlpha = 0;
    private Bitmap mBitmap = null;
    private int mColor = 0;
    private Paint mPaint = new Paint();
    private int mWidth = 170;
    private int mMargin = 20;
    private int mCircleRadius = 0;
    public int x = 0;
    public int y = 0;
    private Rect mRect = new Rect();
    private boolean outanimating = false;
    private boolean inanimating = false;
    private BUTTON_STATE mButtonState = BUTTON_STATE.NORMAL;
    private ValueAnimator inanim = null;
    private ValueAnimator outanim = null;

    private int mAnimDuration = 0;
    private ValueAnimator mAnimeExplode = null;
    private Path mPathExplode = new Path();
    private PathMeasure mPathMeasureExplode = new PathMeasure();

    public Path getmPathExplode() {
        if (mPathExplode != null)
            mPathExplode.reset();
        return mPathExplode;
    }

    public PopupButton(Context context, Bitmap img, int px, int color, int anim_duration) {
        super(context);
        this.mBitmap = img;
        this.mWidth = px;
        this.mColor = color;
        this.mAnimDuration = anim_duration;
        init();
    }

    public PopupButton(Context context, int px, int color, int anim_duration) {
        super(context);
        this.mWidth = px;
        this.mColor = color;
        this.mAnimDuration = anim_duration;
        init();
    }

    void init() {

        inanim = ValueAnimator.ofFloat(1.0f, 1.1f);
        inanim.setDuration(150);
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
                mButtonState = BUTTON_STATE.BIG;
            }
        });

        outanim = ValueAnimator.ofFloat(1.1f, 1.0f);
        outanim.setDuration(150);
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

        mMargin = mWidth / 10;
        mCircleRadius = mWidth / 2 - mMargin;

        mPaint.setAntiAlias(true);
        mPaint.setAlpha(mAlpha);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(mColor);
        mPaint.setStrokeWidth(mMargin / 2);

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(mWidth, mWidth);
        setLayoutParams(layoutParams);

        if (mBitmap != null)
            mBitmap = Bitmap.createScaledBitmap(mBitmap, mCircleRadius, mCircleRadius, true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mBitmap == null) {
            mPaint.setStyle(Paint.Style.STROKE);
            canvas.drawCircle(mWidth / 2, mWidth / 2, mCircleRadius, mPaint);

        } else {
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

        mAnimeExplode.start();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                setScaleX(1f);
                setScaleY(1f);
                break;
            case MotionEvent.ACTION_MOVE:
                int x = (int) event.getRawX();
                int y = (int) event.getRawY();
                getHitRect(mRect);
                if (mRect.contains(x, y)) {
                    if (mButtonState.equals(BUTTON_STATE.NORMAL) && !inanimating) {
                        inanim.start();
                    }
                } else if (mButtonState.equals(BUTTON_STATE.BIG) && !outanimating && !inanimating) {
                    outanim.start();
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

    private enum BUTTON_STATE {
        NORMAL, BIG
    }

}
