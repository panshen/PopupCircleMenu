package me.panshen.com.circlemenu;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
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
    int currentX;
    int currentY;
    boolean ableToggle = false;
    boolean isshowing = false;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (msg.what == juageDispatch) {
                if (ableToggle&&popUpMenu.getVisibility()==INVISIBLE) {
                    MotionEvent newEv = (MotionEvent) msg.obj;
                    isshowing = true;
                    popUpMenu.setVisibility(VISIBLE);
                    mDecorView.addView(popUpMenu, layoutParams);
                    popUpMenu.resetCenter(new Point((int) newEv.getRawX(), (int) newEv.getRawY()));
                    newEv.setAction(MotionEvent.ACTION_DOWN);
                    popUpMenu.dispatchTouchEvent(newEv);
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
            }
        }
    };

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


    }

    public PPCircle(Context context) {
        super(context);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int action = ev.getAction();

        int downX = 0;
        int downY = 0;

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                Log.e(TAG, "dispatchTouchEvent ACTION_DOWN");

                downmillionSecond = System.currentTimeMillis();

                downX = (int) ev.getRawX();
                downY = (int) ev.getRawY();

                downArea = new Rect(downX - distanceShow, downY - distanceShow, downX + distanceShow, downY + distanceShow);
                Log.e("dispatchTouchEvent", downArea.toString());
                getParent().requestDisallowInterceptTouchEvent(true);

                return true;
            case MotionEvent.ACTION_MOVE:
                Log.e(TAG, "dispatchTouchEvent ACTION_MOVE");
                currentX = (int) ev.getRawX();
                currentY = (int) ev.getRawY();

                //此处设计 检测到了在触发区域内会首先发出触发消息 如果在300毫秒之内又检测到离开触发区域 则代表手指移动了 设置flag不执行消息内容
                if (downArea.contains(currentX, currentY)) {
                    ableToggle = true;

                    Message msg = Message.obtain();
                    msg.obj = ev;
                    msg.what = juageDispatch;

                    if (popUpMenu.getVisibility() == INVISIBLE)
                        handler.sendMessageDelayed(msg, 300);
                    else {
                        if (popUpMenu.getVisibility() == VISIBLE&&isshowing)
                            popUpMenu.dispatchTouchEvent(ev);
                    }
                } else {

                    if (popUpMenu.getVisibility() == INVISIBLE) {
                        ableToggle = false;
                        getParent().requestDisallowInterceptTouchEvent(false);
                    }else
                        popUpMenu.dispatchTouchEvent(ev);

                }
                return false;
            case MotionEvent.ACTION_UP:
                Log.e(TAG, "dispatchTouchEvent ACTION_UP");
                popUpMenu.dispatchTouchEvent(ev);
                popUpMenu.setVisibility(INVISIBLE);
                mDecorView.removeView(popUpMenu);
                isshowing = false;
                ableToggle = false;
                getParent().requestDisallowInterceptTouchEvent(false);
                return false;
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                Log.e(TAG, "onTouchEvent ACTION_DOWN");
                return true;
            case MotionEvent.ACTION_MOVE:
                Log.e(TAG, "onTouchEvent ACTION_MOVE");
                return true;
            case MotionEvent.ACTION_UP:
                Log.e(TAG, "onTouchEvent ACTION_UP");
                return true;
        }

        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                Log.e(TAG, "onInterceptTouchEvent ACTION_DOWN");
                return false;
            case MotionEvent.ACTION_MOVE:
                Log.e(TAG, "onInterceptTouchEvent ACTION_MOVE");
                return false;
            case MotionEvent.ACTION_UP:
                Log.e(TAG, "onInterceptTouchEvent ACTION_UP");
                return false;
        }

        return false;
    }

}
