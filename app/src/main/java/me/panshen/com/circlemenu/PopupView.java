package me.panshen.com.circlemenu;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PopupView extends RelativeLayout {
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

    private final int mTriggerPixle = 25;

    private boolean mAbleToggle;

    private int mBtsize = 0;
    private int mRadius = 0;
    private int mBtbackcolor = 0;
    private int mAnimDuration = 250;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
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
                    mOnMenuEventListener.onMenuToggle(mPopup.bts, mPopup.getSelectedIndex());
                }
            }

        }
    };

    boolean juageCallback() {
        return mOnMenuEventListener != null &&
                mPopup.bts != null &&
                mPopup.selectedIndex != -1 &&
                mPopup.selectedIndex != 0 &&
                mPopup.bts.size() > 0;
    }

    public void setmOnMenuEventListener(OnMenuEventListener mOnMenuEventListener) {
        this.mOnMenuEventListener = mOnMenuEventListener;
    }

    public void initRes(Integer... res) {
        ArrayList<PopupButton> mbuttons = new ArrayList<>();

        mbuttons.add(new PopupButton(mContext, "---", mBtsize, mBtbackcolor, mAnimDuration));
        mbuttons.add(new PopupButton(mContext, BitmapFactory.decodeResource(getResources(), R.drawable.audio), "---", mBtsize, mBtbackcolor, mAnimDuration));
        mbuttons.add(new PopupButton(mContext, BitmapFactory.decodeResource(getResources(), R.drawable.display), "---", mBtsize, mBtbackcolor, mAnimDuration));
        mbuttons.add(new PopupButton(mContext, BitmapFactory.decodeResource(getResources(), R.drawable.heart), "---", mBtsize, mBtbackcolor, mAnimDuration));

        mPopup.setbts(mbuttons);

    }

    public PopupView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = (Activity) context;
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

    void initDefaultParam() {
        mBtsize = getResources().getDimensionPixelSize(R.dimen.default_busize);
        mRadius = getResources().getDimensionPixelSize(R.dimen.default_radius);
        mBtbackcolor = Color.WHITE;
    }

    void init() {
        mLayoutParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        mDecorView = (ViewGroup) mContext.getWindow().getDecorView();
        mButtons.add(new PopupButton(mContext, "---", mBtsize, mBtbackcolor, mAnimDuration));
        mButtons.add(new PopupButton(mContext, BitmapFactory.decodeResource(getResources(), R.drawable.audio), "---", mBtsize, mBtbackcolor, mAnimDuration));
        mButtons.add(new PopupButton(mContext, BitmapFactory.decodeResource(getResources(), R.drawable.display), "---", mBtsize, mBtbackcolor, mAnimDuration));
        mButtons.add(new PopupButton(mContext, BitmapFactory.decodeResource(getResources(), R.drawable.heart), "---", mBtsize, mBtbackcolor, mAnimDuration));

        mPopup = new Popup(mContext, mButtons, mRadius);
        mPopup.setVisibility(INVISIBLE);
        mAlphAnimator = new ValueAnimator();
        mAlphAnimator.setFloatValues(0.0f, 1.0f);
        mAlphAnimator.setDuration(mAnimDuration);
        mAlphAnimator.setInterpolator(new LinearOutSlowInInterpolator());
        mAlphAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mPopup.updateMask(Float.valueOf(animation.getAnimatedValue() + ""));
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
                        handler.sendMessageDelayed(msg, mAnimDuration);
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

    interface OnMenuEventListener {
        void onMenuToggle(ArrayList<PopupButton> popUpMenu, int index);
    }

    void dismiss(MotionEvent ev) {
        isshowing = false;
        if (mPopup.getVisibility() == VISIBLE) {
            mAlphAnimator.reverse();
            Message msg = Message.obtain();
            msg.obj = ev;
            msg.what = ACTION_UP;
            handler.sendMessageDelayed(msg, mAnimDuration);
            mPopup.dispatchTouchEvent(ev);

        } else {
            if (mPopup.getVisibility() == INVISIBLE && getChildAt(0) != null && mTriggerRect.contains((int) ev.getRawX(), (int) ev.getRawY())) {
                getChildAt(0).performClick();
            }
            handler.removeCallbacksAndMessages(null);
        }
    }
}
