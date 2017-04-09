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

public class PopupView extends RelativeLayout {
    private String TAG = getClass().getName();
    private Activity mContext;
    private Popup mPopup;
    private ViewGroup mDecorView;
    private RelativeLayout.LayoutParams layoutParams;
    private ArrayList<PopupButton> mButtons = new ArrayList<>();
    private int triggerPx = 25;
    private Rect triggerRect = null;

    private final int juageDispatch = 0;
    private final int fingerLeave = 1;

    private int currentX;
    private int currentY;
    private static boolean isshowing = false;
    private boolean ableToggle = false;
    private ValueAnimator mAlphAnimator = null;
    private OnMenuEventListener mOnMenuEventListener = null;
    private int btsize = 0;
    private int radius = 0;
    private int btbgcolor = 0;
    private  int mAnimDuration = 250;
    public static final int UNDEFIEN = -1;
    public static final int RIGHT = 2;
    public static final int LEFT = 1;

    public int OPEN_DRIECTION = UNDEFIEN;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == juageDispatch) {
                if (ableToggle && mPopup.getVisibility() == INVISIBLE && !isshowing) {
                    MotionEvent newEv = (MotionEvent) msg.obj;
                    isshowing = true;
                    mAlphAnimator.start();
                    mPopup.setVisibility(VISIBLE);
                    mDecorView.addView(mPopup, layoutParams);

                    if (OPEN_DRIECTION != UNDEFIEN)
                        mPopup.resetCenter(getViewCurrentCenter(), OPEN_DRIECTION);
                    else
                        mPopup.resetCenter(new Point((int) newEv.getRawX(), (int) newEv.getRawY()), OPEN_DRIECTION);

                    newEv.setAction(MotionEvent.ACTION_DOWN);
                    mPopup.dispatchTouchEvent(newEv);

                    getParent().requestDisallowInterceptTouchEvent(true);

                }
            } else if (msg.what == fingerLeave) {
                mPopup.setVisibility(INVISIBLE);
                mDecorView.removeView(mPopup);
                isshowing = false;
                ableToggle = false;
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
                    btbgcolor = a.getColor(attr, Color.WHITE);
                    break;
                case R.styleable.circlemenu_button_size:
                    btsize = a.getDimensionPixelSize(attr, (int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_SP, 40, getResources().getDisplayMetrics()));
                    break;
                case R.styleable.circlemenu_radius:
                    radius = a.getDimensionPixelSize(attr, (int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_SP, 100, getResources().getDisplayMetrics()));
                    break;
                case R.styleable.circlemenu_anim_duration:
                    mAnimDuration = a.getInt(attr, 200);
                    break;
                case R.styleable.circlemenu_open_direction:
                    OPEN_DRIECTION = a.getInt(attr, -1);
                    break;
                default:
                    break;
            }
        }
        init();
    }

    private Point getViewCurrentCenter() {
        Rect r = new Rect();
        Point p = new Point();
        getGlobalVisibleRect(r);
        p.set(r.centerX(), r.centerY());
        return p;
    }

    void initDefaultParam() {
        btsize = getResources().getDimensionPixelSize(R.dimen.default_busize);
        radius = getResources().getDimensionPixelSize(R.dimen.default_radius);
        btbgcolor = Color.WHITE;
    }

    void init() {
        layoutParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        mDecorView = (ViewGroup) mContext.getWindow().getDecorView();

        mButtons.add(new PopupButton(mContext, "MENU", btsize, btbgcolor, mAnimDuration));
        mButtons.add(new PopupButton(mContext, BitmapFactory.decodeResource(getResources(), R.drawable.audio), "EARPOD", btsize, btbgcolor, mAnimDuration));
        mButtons.add(new PopupButton(mContext, BitmapFactory.decodeResource(getResources(), R.drawable.display), "TV", btsize, btbgcolor, mAnimDuration));
        mButtons.add(new PopupButton(mContext, BitmapFactory.decodeResource(getResources(), R.drawable.heart), "HEART", btsize, btbgcolor, mAnimDuration));

        mPopup = new Popup(mContext, mButtons, radius);
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
                    triggerRect = new Rect(downX - triggerPx, downY - triggerPx, downX + triggerPx, downY + triggerPx);
                    getParent().requestDisallowInterceptTouchEvent(true);

                    ableToggle = true;
                    Message msg = Message.obtain();
                    msg.obj = ev;
                    msg.what = juageDispatch;
                    if (mPopup.getVisibility() == INVISIBLE) {
                        handler.sendMessageDelayed(msg, mAnimDuration);
                    }

                    return true;
                }
            case MotionEvent.ACTION_MOVE:
                currentX = (int) ev.getRawX();
                currentY = (int) ev.getRawY();

                if (triggerRect.contains(currentX, currentY)) {
                    if (mPopup.getVisibility() == VISIBLE && isshowing) {
                        mPopup.dispatchTouchEvent(ev);
                    }

                } else {
                    if (mPopup.getVisibility() == INVISIBLE) {
                        ableToggle = false;
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
            msg.what = fingerLeave;
            handler.sendMessageDelayed(msg, mAnimDuration);
            mPopup.dispatchTouchEvent(ev);

        } else {
            if (mPopup.getVisibility() == INVISIBLE && getChildAt(0) != null && triggerRect.contains((int) ev.getRawX(), (int) ev.getRawY())) {
                getChildAt(0).performClick();
            }
            handler.removeCallbacksAndMessages(null);
        }
    }
}
