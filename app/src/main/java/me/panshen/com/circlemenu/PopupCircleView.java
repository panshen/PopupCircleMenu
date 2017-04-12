package me.panshen.com.circlemenu;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.ArrayList;

public class PopupCircleView extends RelativeLayout implements Handler.Callback {
    private String TAG = getClass().getName();
    private Activity mContext;
    private Popup mPopup;
    private Rect mTriggerRect;
    private ViewGroup mDecorView;
    private RelativeLayout.LayoutParams mLayoutParams;
    private ArrayList<PopupButton> mButtons = new ArrayList<>();
    private ValueAnimator mAlphAnimator;
    private OnMenuEventListener mOnMenuEventListener;

    private static boolean isshowing;
    private static final int ACTION_DOWN = 0;
    private static final int ACTION_UP = 1;

    public static final int UNDEFIEN = -1;
    public static final int RIGHT = 2;
    public static final int LEFT = 1;
    public int OPEN_DRIECTION = UNDEFIEN;

    private int mTriggerPixle = 25;

    private boolean mAbleToggle;

    private int mBtsize;
    private int mRadius;
    private int mBtbackcolor;
    private int mAnimDuration = 250;
    private Handler mHandler;

    public void setmOnMenuEventListener(OnMenuEventListener mOnMenuEventListener) {
        this.mOnMenuEventListener = mOnMenuEventListener;
    }

    public void initRes(Integer... res) {
        ArrayList<PopupButton> mbuttons = new ArrayList<>();
        mbuttons.add(new PopupButton(mContext, mBtsize, mBtbackcolor, mAnimDuration));
        for (Integer re : res) {
            mbuttons.add(new PopupButton(mContext, re, mBtsize, mBtbackcolor, mAnimDuration));
        }

        mPopup.setbts(mbuttons);
    }

    public PopupCircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = (Activity) context;
        mHandler = new Handler(Looper.getMainLooper(), this);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.circlemenu, 0, 0);
        int n = a.getIndexCount();
        initDefaultParam();

        for (int i = 0; i < n; i++) {
            int attr = a.getIndex(i);
            switch (attr) {
                case R.styleable.circlemenu_button_color:
                    mBtbackcolor = a.getColor(attr, Color.WHITE);
                    break;
                case R.styleable.circlemenu_button_size:
                    mBtsize = a.getDimensionPixelSize(attr, (int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_SP, 40, getResources().getDisplayMetrics()));
                    break;
                case R.styleable.circlemenu_radius:
                    mRadius = a.getDimensionPixelSize(attr, (int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_SP, 100, getResources().getDisplayMetrics()));
                    break;
                case R.styleable.circlemenu_anim_duration:
                    mAnimDuration = a.getInt(attr, 250);
                    break;
                case R.styleable.circlemenu_open_direction:
                    OPEN_DRIECTION = a.getInt(attr, UNDEFIEN);
                    break;
                default:
                    break;
            }
        }
        init();
    }

    private Point getViewCenterPoint() {
        Rect r = new Rect();
        Point p = new Point();
        getGlobalVisibleRect(r);
        p.set(r.centerX(), r.centerY());
        return p;
    }

    private void initDefaultParam() {
        mBtsize = getResources().getDimensionPixelSize(R.dimen.default_busize);
        mRadius = getResources().getDimensionPixelSize(R.dimen.default_radius);
        mBtbackcolor = Color.WHITE;
    }

    boolean juageCallback() {
        return mOnMenuEventListener != null &&
                mPopup.bts != null &&
                mPopup.mSelectedIndex != -1 &&
                mPopup.mSelectedIndex != 0 &&
                mPopup.bts.size() > 0;
    }

    private void init() {
        mLayoutParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        mDecorView = (ViewGroup) mContext.getWindow().getDecorView();
        mButtons.add(new PopupButton(mContext, mBtsize, mBtbackcolor, mAnimDuration));
        mButtons.add(new PopupButton(mContext, R.drawable.trashbin, mBtsize, mBtbackcolor, mAnimDuration));
        mButtons.add(new PopupButton(mContext, R.drawable.unlike, mBtsize, mBtbackcolor, mAnimDuration));
        mButtons.add(new PopupButton(mContext, R.drawable.like, mBtsize, mBtbackcolor, mAnimDuration));

        mPopup = new Popup(mContext, mButtons, mRadius);
        mPopup.setVisibility(INVISIBLE);
        mAlphAnimator = new ValueAnimator();
        mAlphAnimator.setFloatValues(0.0f, 1.0f);
        mAlphAnimator.setDuration(mAnimDuration);
        mAlphAnimator.setInterpolator(new LinearOutSlowInInterpolator());
        mAlphAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mPopup.setShadowViewAlpha(Float.valueOf(animation.getAnimatedValue() + ""));
            }
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int action = ev.getAction();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                int downX = (int) ev.getRawX();
                int downY = (int) ev.getRawY();

                if (isshowing) {
                    return false;
                } else {
                    mTriggerRect = new Rect(downX - mTriggerPixle, downY - mTriggerPixle, downX + mTriggerPixle, downY + mTriggerPixle);
                    getParent().requestDisallowInterceptTouchEvent(true);

                    mAbleToggle = true;
                    Message msg = Message.obtain();
                    msg.obj = ev;
                    msg.what = ACTION_DOWN;
                    if (mPopup.getVisibility() == INVISIBLE) {
                        mHandler.sendMessageDelayed(msg, mAnimDuration);
                    }

                    return true;
                }
            case MotionEvent.ACTION_MOVE:
                int mCurrentX = (int) ev.getRawX();
                int mCurrentY = (int) ev.getRawY();

                if (mTriggerRect.contains(mCurrentX, mCurrentY)) {
                    if (mPopup.getVisibility() == VISIBLE && isshowing) {
                        mPopup.dispatchTouchEvent(ev);
                    }
                } else {
                    if (mPopup.getVisibility() == INVISIBLE) {
                        mAbleToggle = false;
                        getParent().requestDisallowInterceptTouchEvent(false);
                    } else {
                        mPopup.dispatchTouchEvent(ev);
                    }
                }

                break;
            case MotionEvent.ACTION_UP:
                dismiss(ev);
                break;
            case MotionEvent.ACTION_CANCEL:
                dismiss(ev);
                break;
        }
        return false;
    }

    @Override
    public boolean handleMessage(Message msg) {
        if (msg.what == ACTION_DOWN) {
            if (mAbleToggle && mPopup.getVisibility() == INVISIBLE && !isshowing) {
                MotionEvent newEv = (MotionEvent) msg.obj;
                isshowing = true;
                mAlphAnimator.start();
                mPopup.setVisibility(VISIBLE);
                mDecorView.addView(mPopup, mLayoutParams);

                if (OPEN_DRIECTION != UNDEFIEN)
                    mPopup.resetCenter(getViewCenterPoint(), OPEN_DRIECTION);
                else
                    mPopup.resetCenter(new Point((int) newEv.getRawX(), (int) newEv.getRawY()), OPEN_DRIECTION);

                newEv.setAction(MotionEvent.ACTION_DOWN);
                mPopup.dispatchTouchEvent(newEv);
                getParent().requestDisallowInterceptTouchEvent(true);
            }
        } else if (msg.what == ACTION_UP) {
            mPopup.setVisibility(INVISIBLE);
            mDecorView.removeView(mPopup);
            isshowing = false;
            mAbleToggle = false;
            getParent().requestDisallowInterceptTouchEvent(false);
            if (juageCallback()) {
                mOnMenuEventListener.onMenuToggle(mPopup.bts.get(mPopup.getmSelectedIndex()), mPopup.getmSelectedIndex());
            }
        }
        return false;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    interface OnMenuEventListener {
        void onMenuToggle(PopupButton popupButton, int index);
    }

    private void dismiss(MotionEvent ev) {
        isshowing = false;
        if (mPopup.getVisibility() == VISIBLE) {
            mAlphAnimator.reverse();
            Message msg = Message.obtain();
            msg.obj = ev;
            msg.what = ACTION_UP;
            mHandler.sendMessageDelayed(msg, mAnimDuration);
            mPopup.dispatchTouchEvent(ev);
        } else {
            if (mPopup.getVisibility() == INVISIBLE && getChildAt(0) != null && mTriggerRect.contains((int) ev.getRawX(), (int) ev.getRawY()))
                getChildAt(0).performClick();
            mHandler.removeMessages(ACTION_DOWN);
        }
    }
}
