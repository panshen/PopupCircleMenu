package me.panshen.com.circlemenu;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.util.AttributeSet;
import android.util.Log;
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
    int distanceShow = 50;
    long downmillionSecond = 0;
    Rect downArea = null;
    static final int juageDispatch = 0;
    static final int fingerLeave = 1;
    int currentX;
    int currentY;
    boolean ableToggle = false;
    boolean isshowing = false;
    ValueAnimator alphaAnim = null;
    OnMenuEventListener onMenuEventListener = null;
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
            }
            if (msg.what == fingerLeave) {
                MotionEvent ev = (MotionEvent) msg.obj;
                popUpMenu.dispatchTouchEvent(ev);//发送UP事件  在up事件检测的哪按按钮被激活 所以回调只能在这一行之后调用
                popUpMenu.setVisibility(INVISIBLE);
                mDecorView.removeView(popUpMenu);
                isshowing = false;
                ableToggle = false;
                getParent().requestDisallowInterceptTouchEvent(false);
                onMenuEventListener.onToggle(popUpMenu, popUpMenu.getSelectedIndex());

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

        layoutParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        mDecorView = (ViewGroup) ((Activity) context).getWindow().getDecorView();

        bts.add(new MenuButton(context, "菜单"));
        bts.add(new MenuButton(context, BitmapFactory.decodeResource(getResources(), R.drawable.a4), "音乐"));
        bts.add(new MenuButton(context, BitmapFactory.decodeResource(getResources(), R.drawable.a1), "衬衫"));
        bts.add(new MenuButton(context, BitmapFactory.decodeResource(getResources(), R.drawable.a3), "喜欢"));

        popUpMenu = new PopUpMenu(mContext, bts);
        popUpMenu.setVisibility(INVISIBLE);
        alphaAnim = new ValueAnimator();
        alphaAnim.setFloatValues(0.0f, 1.0f);
        alphaAnim.setDuration(300);
        alphaAnim.setInterpolator(new LinearOutSlowInInterpolator());
        alphaAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                popUpMenu.updateMask(Float.valueOf(animation.getAnimatedValue() + ""));
            }
        });

        alphaAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                super.onAnimationRepeat(animation);
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
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

                downmillionSecond = System.currentTimeMillis();

                downX = (int) ev.getRawX();
                downY = (int) ev.getRawY();

                downArea = new Rect(downX - distanceShow, downY - distanceShow, downX + distanceShow, downY + distanceShow);
                getParent().requestDisallowInterceptTouchEvent(true);

                return true;
            case MotionEvent.ACTION_MOVE:
                currentX = (int) ev.getRawX();
                currentY = (int) ev.getRawY();

                /**
                 * 此处设计 检测到了在触发区域内会首先发出触发消息 如果在300毫秒之内又检测到离开触发区域 则代表手指移动了 设置flag不执行消息内容
                 * */
                if (downArea.contains(currentX, currentY)) {
                    ableToggle = true;

                    Message msg = Message.obtain();
                    msg.obj = ev;
                    msg.what = juageDispatch;

                    if (popUpMenu.getVisibility() == INVISIBLE)
                        handler.sendMessageDelayed(msg, 300);
                    else {
                        if (popUpMenu.getVisibility() == VISIBLE && isshowing)
                            popUpMenu.dispatchTouchEvent(ev);
                    }
                } else {
                    if (popUpMenu.getVisibility() == INVISIBLE) {
                        ableToggle = false;
                        getParent().requestDisallowInterceptTouchEvent(false);
                    } else
                        popUpMenu.dispatchTouchEvent(ev);
                }
                return false;
            case MotionEvent.ACTION_UP:
                if (popUpMenu.getVisibility() == VISIBLE) {
                    alphaAnim.reverse();
                    Message msg = Message.obtain();
                    msg.obj = ev;
                    msg.what = fingerLeave;
                    handler.sendMessageDelayed(msg, 300);
                }
                else
                    handler.removeCallbacksAndMessages(null);
                return false;
        }
        return false;
    }

    public interface OnMenuEventListener {
        void onToggle(PopUpMenu popUpMenu, int index);
    }

}
