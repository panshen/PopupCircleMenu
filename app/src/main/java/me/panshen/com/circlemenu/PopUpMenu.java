package me.panshen.com.circlemenu;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.ArrayList;

//做overscreen的话就把menu的半径超出屏幕的距离区分左右的添加到 startAngel 和 endAngel里，不再区分上下
public class PopUpMenu extends RelativeLayout {
    private String TAG = getClass().getName();
    ArrayList<MenuButton> bts = null;
    ValueAnimator animator = null;
    Point point = null;
    View mask = null;
    Rect rectWindowRange;
    Point windowCenterPoint = null;
    RectF arcRange = null;
    int radius = 250;
    int overScreenDistance = 0;
    OverScreen enumOverScreen = OverScreen.NORMAL;
    public PopUpMenu(Activity context, ArrayList<MenuButton> bts) {
        super(context);
        Log.e(TAG, "init");
        setClickable(true);
        this.bts = bts;
        Display display = context.getWindow().getWindowManager().getDefaultDisplay();
        rectWindowRange = new Rect();
        display.getRectSize(rectWindowRange);
        windowCenterPoint = new Point(rectWindowRange.centerX(), rectWindowRange.centerY());

        animator = new ValueAnimator();
        animator.setFloatValues(0.0f, 1.0f);
        animator.setDuration(300);
        animator.setInterpolator(new LinearOutSlowInInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mask.setAlpha(Float.valueOf(animation.getAnimatedValue() + ""));
            }
        });

        RelativeLayout.LayoutParams rl = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mask = new View(context);
        mask.setBackgroundColor(Color.parseColor("#66000000"));
        mask.setLayoutParams(rl);
        addView(mask);
        for (MenuButton mb : bts) {
            addView(mb);
        }
        mPaint = new Paint();

        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(10);
        mPaint.setColor(Color.WHITE);
        setWillNotDraw(false);
    }

    Paint mPaint = null;

    public void resetCenter(Point point) {
        this.point = point;
        animator.start();
        invalidate();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        Log.e("PopUpMenu.onLayout", point.toString());
        View view = getChildAt(0);//layout背景
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        View centerButton = getChildAt(1);//layout 中间的按钮
        centerButton.layout(point.x - centerButton.getMeasuredWidth() / 2, point.y - centerButton.getMeasuredHeight() / 2, point.x + centerButton.getMeasuredWidth() / 2, point.y + centerButton.getMeasuredHeight() / 2);

        genPos(producePath(enumOverScreen));
        for (int i = 1; i < bts.size(); i++) {
            MenuButton v = bts.get(i);
            v.layout(v.x, v.y, v.x + v.getMeasuredWidth(), v.y + v.getMeasuredHeight());
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        int x = (int) ev.getRawX();
        int y = (int) ev.getRawY();
        switch (action) {

            case MotionEvent.ACTION_DOWN:
                  getDirection(ev);

                Log.e(TAG, "ACTION_DOWN");
                for (final MenuButton mb : bts) {
                    mb.dispatchTouchEvent(ev);
                }
                return false;
            case MotionEvent.ACTION_MOVE:
                Log.e(TAG, "ACTION_MOVE");
                for (int i = 0; i < bts.size(); i++) {
                    if (i == 0) continue;
                    bts.get(i).dispatchTouchEvent(ev);
                }
                return false;
            case MotionEvent.ACTION_UP:
                Log.e(TAG, "ACTION_UP");
                for (final MenuButton mb : bts) {
                    mb.dispatchTouchEvent(ev);
                }
                return false;

            default:
                break;
        }
        return false;
    }

    public void getDirection(MotionEvent ev) {
        int x = (int) ev.getRawX();
        int arcRightXpos = 0;
        int overScrren = 0;
        RectF arcRect = new RectF(point.x - radius, point.y - radius, point.x + radius, point.y + radius);
        int centerX = windowCenterPoint.x;
        enumOverScreen = OverScreen.NORMAL;

        if (x < centerX) {
            overScrren = (int) arcRect.left;

            if(overScrren<0){
                enumOverScreen = OverScreen.LEFT;
                enumOverScreen.setOverScreenDistance(overScrren);
            }

        }else{
            arcRightXpos = (int) (arcRect.centerX()+radius);
            overScrren =   arcRightXpos-rectWindowRange.width();
            if(arcRightXpos>rectWindowRange.width()){
                enumOverScreen = OverScreen.RIGHT;
                enumOverScreen.setOverScreenDistance(overScrren);
            }
        }

        Log.e("getDirection",enumOverScreen.toString());
    }

    public void genPos(Path orbit) {
        PathMeasure measure = new PathMeasure(orbit, false);
        int divisor = bts.size();

        for (int i = 1; i < bts.size(); i++) {
            float[] coords = new float[]{0f, 0f};
            int length = (int) ((i) * measure.getLength() / divisor);
            measure.getPosTan(length, coords, null);
            int x = (int) coords[0] - bts.get(i).getMeasuredWidth() / 2;
            int y = (int) coords[1] - bts.get(i).getMeasuredHeight() / 2;
            bts.get(i).x = x;
            bts.get(i).y = y;
        }
    }

    enum OverScreen {
        LEFT("LEFT", 0), RIGHT("RIGHT", 0),NORMAL("NORMAL",0);
        private String type;
        private int overScreenDistance;
        OverScreen(String name, int index) {
            this.type = name;
            this.overScreenDistance = index;
        }

        public String getType() {
            return type;
        }
        public void setType(String type) {
            this.type = type;
        }
        public int getOverScreenDistance() {
            return overScreenDistance;
        }
        public void setOverScreenDistance(int overScreenDistance) {
            this.overScreenDistance = overScreenDistance;
        }

        @Override
        public String toString() {
            return "OverScreen{" +
                    "type='" + type + '\'' +
                    ", overScreenDistance=" + overScreenDistance +
                    '}';
        }
    }

    public Path producePath(OverScreen overScreen) {
        Path orbit = new Path();
        int startAngle = 180;
        int endAngle = 360;
        arcRange = new RectF(point.x - radius, point.y - radius, point.x + radius, point.y + radius);

        switch(overScreen){
            case LEFT:
                startAngle -= overScreen.getOverScreenDistance()/5;
                endAngle -= overScreen.getOverScreenDistance()/5;
                break;
            case RIGHT:
                startAngle -= overScreen.getOverScreenDistance()/5;
                endAngle -= overScreen.getOverScreenDistance()/5;
                break;
            case NORMAL:

                break;
        }

        orbit.addArc(arcRange, startAngle, endAngle - startAngle);

        return orbit;
    }
}
