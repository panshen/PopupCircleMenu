package me.panshen.com.circlemenu;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.LinearLayout;


public class MenuButton extends View {
    private final String TAG = this.getClass().getName();
    private int mAlpha = 0;
    private Bitmap mBitmap = null;
    private int mColor = Color.WHITE;
    private Paint mPaint = new Paint();
    private int width = 170;
    private int margin = 20;
    private int circleRadius = 0;
    public int x = 0;
    public int y = 0;
    Rect rect = new Rect();
    boolean outanimating = false;
    boolean inanimating = false;
    private int strokeWidth = 8;
    BUTTON_STATE buttonState = BUTTON_STATE.NORMAL;
    PopUpMenu popUpMenu = null;
    String name = "";
    ValueAnimator inanim = null;
    ValueAnimator outanim = null;


    public MenuButton(Context context, Bitmap img, String name) {
        super(context);
        this.mBitmap = img;
        this.name = name;
        init();
    }

    public MenuButton(Context context, String name) {
        super(context);
        this.name = name;
        init();
    }

    void init() {
        inanim = ValueAnimator.ofFloat(1.0f, 1.1f);
        inanim.setDuration(100);
        inanim.setInterpolator(new LinearInterpolator());
        inanim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                MenuButton.this.setScaleX(Float.valueOf(animation.getAnimatedValue() + ""));
                MenuButton.this.setScaleY(Float.valueOf(animation.getAnimatedValue() + ""));
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
        outanim.setDuration(100);
        outanim.setInterpolator(new LinearInterpolator());
        outanim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                MenuButton.this.setScaleX(Float.valueOf(animation.getAnimatedValue() + ""));
                MenuButton.this.setScaleY(Float.valueOf(animation.getAnimatedValue() + ""));
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

        mPaint.setAntiAlias(true);
        mPaint.setAlpha(mAlpha);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(strokeWidth);
        mPaint.setColor(mColor);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width, width);
        setLayoutParams(layoutParams);
        if (mBitmap != null)
            mBitmap = getScaledBitap();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        popUpMenu = (PopUpMenu) getParent();
    }

    public String getName() {
        return name;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        circleRadius = width / 2 - margin;


        if(mBitmap==null){
            mPaint.setStyle(Paint.Style.STROKE);
            canvas.drawCircle(width / 2, width / 2, circleRadius + circleRadius / 12, mPaint);
        }else {
            mPaint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(width / 2, width / 2, circleRadius, mPaint);
            canvas.drawBitmap(mBitmap, circleRadius + margin - mBitmap.getWidth() / 2, circleRadius + margin - mBitmap.getHeight() / 2, mPaint);
        }
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

                break;

            default:
                break;
        }
        return super.onTouchEvent(event);
    }

    public Bitmap getScaledBitap() {
        Matrix matrix = new Matrix();
        matrix.postScale(0.4f, 0.4f);
        Bitmap dstbmp = Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.getWidth(), mBitmap.getHeight(),
                matrix, true);
        return dstbmp;
    }

    enum BUTTON_STATE {
        NORMAL, BIG;
    }

}
