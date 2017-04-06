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
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.ArrayList;

public class PPCircle extends RelativeLayout {
    private String TAG = getClass().getName();
    private Activity mContext;
    PopUpMenu popUpMenu;
    ViewGroup mDecorView;
    RelativeLayout.LayoutParams layoutParams;
    ArrayList<MenuButton> bts = new ArrayList<>();
    int showDistance = 25;
    Rect triggerArea = null;
    
    final int juageDispatch = 0;
    final int fingerLeave = 1;

    int currentX;
    int currentY;
    static boolean isshowing = false;
    boolean ableToggle = false;
    ValueAnimator alphaAnim = null;
    OnMenuEventListener onMenuEventListener = null;
    int btsize = 0;
    int radius = 0;
    int btbgcolor = 0;
    int anim_duration = 250;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (msg.what == juageDispatch) {
                if (ableToggle && popUpMenu.getVisibility() == INVISIBLE) {
                    MotionEvent newEv = (MotionEvent) msg.obj;
                    isshowing = true;
                    alphaAnim.start();
                    popUpMenu.setVisibility(VISIBLE);
                    mDecorView.addView(popUpMenu, layoutParams);
                    popUpMenu.resetCenter(new Point((int) newEv.getRawX(), (int) newEv.getRawY()));
                    newEv.setAction(MotionEvent.ACTION_DOWN);
                    popUpMenu.dispatchTouchEvent(newEv);
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
            } else if (msg.what == fingerLeave) {
                popUpMenu.setVisibility(INVISIBLE);
                mDecorView.removeView(popUpMenu);
                isshowing = false;
                ableToggle = false;
                getParent().requestDisallowInterceptTouchEvent(false);
                onMenuEventListener.onMenuToggle(popUpMenu.bts, popUpMenu.getSelectedIndex());
            }
        }
    };

    public void setOnMenuEventListener(OnMenuEventListener onMenuEventListener) {
        this.onMenuEventListener = onMenuEventListener;
    }

    public PPCircle(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = (Activity) context;
        setClickable(true);
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
                    anim_duration = a.getInt(attr, 200);
                    break;
                default:
                    break;
            }
        }
        initother();
    }

    void initDefaultParam() {
        btsize = getResources().getDimensionPixelSize(R.dimen.default_busize);
        radius = getResources().getDimensionPixelSize(R.dimen.default_radius);
        btbgcolor = Color.WHITE;
    }

    public PPCircle(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    void initother() {
        layoutParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        mDecorView = (ViewGroup) mContext.getWindow().getDecorView();

        bts.add(new MenuButton(mContext, "菜单", btsize, btbgcolor, anim_duration));
        bts.add(new MenuButton(mContext, BitmapFactory.decodeResource(getResources(), R.drawable.audio), "--", btsize, btbgcolor, anim_duration));
        bts.add(new MenuButton(mContext, BitmapFactory.decodeResource(getResources(), R.drawable.display), "--", btsize, btbgcolor, anim_duration));
        bts.add(new MenuButton(mContext, BitmapFactory.decodeResource(getResources(), R.drawable.heart), "--", btsize, btbgcolor, anim_duration));

        popUpMenu = new PopUpMenu(mContext, bts, radius);
        popUpMenu.setVisibility(INVISIBLE);
        alphaAnim = new ValueAnimator();
        alphaAnim.setFloatValues(0.0f, 1.0f);
        alphaAnim.setDuration(anim_duration);
        alphaAnim.setInterpolator(new LinearOutSlowInInterpolator());
        alphaAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                popUpMenu.updateMask(Float.valueOf(animation.getAnimatedValue() + ""));
            }
        });
    }

    public PPCircle(Context context) {
        super(context);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        int downX;
        int downY;

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                Log.e(TAG, "ACTION_DOWN");

                if (isshowing) {
                    return false;
                } else {
                    downX = (int) ev.getRawX();
                    downY = (int) ev.getRawY();
                    triggerArea = new Rect(downX - showDistance, downY - showDistance, downX + showDistance, downY + showDistance);
                    getParent().requestDisallowInterceptTouchEvent(true);

                    /**
                     * 此处设计
                     * 首先发出触发菜单消息 如果在n毫秒之内又检测到离开触发区域 则代表手指移动了设置flag = false不执行消息内容
                     * */
                    ableToggle = true;
                    Message msg = Message.obtain();
                    msg.obj = ev;
                    msg.what = juageDispatch;
                    if (popUpMenu.getVisibility() == INVISIBLE)
                        handler.sendMessageDelayed(msg, anim_duration);

                    return true;
                }
            case MotionEvent.ACTION_MOVE:
                currentX = (int) ev.getRawX();
                currentY = (int) ev.getRawY();

                if (triggerArea.contains(currentX, currentY)) {
                    if (popUpMenu.getVisibility() == VISIBLE && isshowing)
                        popUpMenu.dispatchTouchEvent(ev);

                } else {
                    if (popUpMenu.getVisibility() == INVISIBLE) {
                        ableToggle = false;
                        getParent().requestDisallowInterceptTouchEvent(false);
                    } else {
                        popUpMenu.dispatchTouchEvent(ev);
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
        void onMenuToggle(ArrayList<MenuButton> popUpMenu, int index);

//        void onMenuFouce(ArrayList<MenuButton> popUpMenu, int index);
    }

    void dismiss(MotionEvent ev) {
        isshowing = false;

        if (popUpMenu.getVisibility() == VISIBLE) {
            alphaAnim.reverse();
            Message msg = Message.obtain();
            msg.obj = ev;
            msg.what = fingerLeave;
            handler.sendMessageDelayed(msg, anim_duration);
            popUpMenu.dispatchTouchEvent(ev);
        } else {
            handler.removeCallbacksAndMessages(null);
        }
    }

}
