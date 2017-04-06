package me.panshen.com.circlemenu;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Rect;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.RelativeLayout;


public class MenuButton extends View {
    private final String TAG = this.getClass().getName();
    private int mAlpha = 0;
    private Bitmap mBitmap = null;
    private int mColor = 0;
    private Paint mPaint = new Paint();
    private int widthPx = 170;
    private int margin = 20;
    private int circleRadius = 0;
    public int x = 0;
    public int y = 0;
    Rect rect = new Rect();
    boolean outanimating = false;
    boolean inanimating = false;
    BUTTON_STATE buttonState = BUTTON_STATE.NORMAL;
    PopUpMenu popUpMenu = null;
    String name = "";
    ValueAnimator inanim = null;
    ValueAnimator outanim = null;

    int mAnimDuration = 0;
    ValueAnimator animeExplode = null;
    Path pathExplode = new Path();
    PathMeasure pathMeasureExplode = new PathMeasure();
    boolean reverse = false;
    boolean exploeded ;
    public Path getPathExplode() {
        if (pathExplode != null)
            pathExplode.reset();
        return pathExplode;
    }

    public MenuButton(Context context, Bitmap img, String name, int px, int color, int anim_duration) {
        super(context);
        this.mBitmap = img;
        this.name = name;
        this.widthPx = px;
        this.mColor = color;
        mAnimDuration = anim_duration;
        init();
    }

    public MenuButton(Context context, String name, int px, int color, int anim_duration) {
        super(context);
        this.name = name;
        this.widthPx = px;
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
                buttonState = BUTTON_STATE.BIG;
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
                buttonState = BUTTON_STATE.NORMAL;
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                outanimating = true;
            }
        });


        margin = widthPx / 10;
        circleRadius = widthPx / 2 - margin;

        mPaint.setAntiAlias(true);
        mPaint.setAlpha(mAlpha);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(mColor);
        mPaint.setStrokeWidth(margin / 2);

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(widthPx, widthPx);
        setLayoutParams(layoutParams);

        if (mBitmap != null)
            mBitmap = Bitmap.createScaledBitmap(mBitmap, circleRadius, circleRadius, true);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        popUpMenu = (PopUpMenu) getParent();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mBitmap == null) {
            mPaint.setStyle(Paint.Style.STROKE);
            canvas.drawCircle(widthPx / 2, widthPx / 2, circleRadius, mPaint);

        } else {
            mPaint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(widthPx / 2, widthPx / 2, circleRadius, mPaint);
            canvas.drawBitmap(mBitmap, circleRadius + margin - mBitmap.getWidth() / 2, circleRadius + margin - mBitmap.getHeight() / 2, mPaint);
        }

    }

    void explode() {
        reverse = false;
        pathMeasureExplode.setPath(pathExplode, false);

        PropertyValuesHolder propertyScaleAnim = PropertyValuesHolder.ofFloat("anim_scale", pathMeasureExplode.getLength(), 0f);
        PropertyValuesHolder propertyAlphaAnim = PropertyValuesHolder.ofFloat("anim_alpha", 0.0f, 0.0f, 0.0f, 0.7f, 1.0f);

        animeExplode = ValueAnimator.ofPropertyValuesHolder(propertyScaleAnim, propertyAlphaAnim);
        animeExplode.setDuration(mAnimDuration);
        animeExplode.setInterpolator(new LinearOutSlowInInterpolator());
        animeExplode.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float[] pos = {0, 0};
                float animscaledValue = Float.valueOf(animation.getAnimatedValue("anim_scale") + "");
                float currentDis = pathMeasureExplode.getLength() - animscaledValue;
                pathMeasureExplode.getPosTan(currentDis, pos, null);
                setX(pos[0]);
                setY(pos[1]);

                float animalphaValue = Float.valueOf(animation.getAnimatedValue("anim_alpha") + "");
                setAlpha(animalphaValue);
            }
        });
        animeExplode.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                exploeded = true;
            }
        });

        animeExplode.start();
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
                getHitRect(rect);
                if (rect.contains(x, y)) {
                    if (buttonState.equals(BUTTON_STATE.NORMAL) && !inanimating) {
                        inanim.start();
                    }
                } else if (buttonState.equals(BUTTON_STATE.BIG) && !outanimating && !inanimating) {
                    outanim.start();
                }
                break;

            case MotionEvent.ACTION_UP:
                outanimating = false;
                inanimating = false;
                buttonState = BUTTON_STATE.NORMAL;

                if (animeExplode != null && pathMeasureExplode != null && pathExplode != null) {
                    reverse = true;
                    animeExplode.reverse();
                }

                break;

            default:
                break;
        }
        return super.onTouchEvent(event);
    }

    private enum BUTTON_STATE {
        NORMAL, BIG;
    }

}
