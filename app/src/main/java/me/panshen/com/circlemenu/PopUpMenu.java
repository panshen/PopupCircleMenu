package me.panshen.com.circlemenu;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.ListIterator;

public class PopUpMenu extends RelativeLayout {
    private String TAG = getClass().getName();
    View mask = null;
    int radius = 250;
    Point point = null;
    RectF arcRange = null;
    Rect rectWindowRange = null;
    Rect btTempRect = null;
    Point windowCenterPoint = null;
    ArrayList<MenuButton> bts = null;
    OverScreen enumOverScreen = OverScreen.TOP;
    int selectedIndex = 0;
    int mOpenDriction = PPCircle.UNDEFIEN;

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public void setSelectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;
    }

    public PopUpMenu(Activity context, ArrayList<MenuButton> bts, int radius) {
        super(context);
        Log.e(TAG, "init");
        setClickable(true);
        this.bts = bts;
        this.radius = radius;

        Display display = context.getWindow().getWindowManager().getDefaultDisplay();
        rectWindowRange = new Rect();
        btTempRect = new Rect();
        display.getRectSize(rectWindowRange);
        windowCenterPoint = new Point(rectWindowRange.centerX(), rectWindowRange.centerY());

        RelativeLayout.LayoutParams rl = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mask = new View(context);
        mask.setBackgroundColor(Color.parseColor("#66000000"));
        mask.setLayoutParams(rl);
        addView(mask);

        for (MenuButton mb : bts) {
            addView(mb);
        }

    }

    public void resetCenter(Point point, int dirction) {
        this.point = point;
        this.mOpenDriction = dirction;
        invalidate();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        mask.setClickable(true);
        mask.setOnTouchListener(null);
        View view = getChildAt(0);

        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        View centerButton = getChildAt(1);
        centerButton.layout(point.x - centerButton.getMeasuredWidth() / 2, point.y - centerButton.getMeasuredHeight() / 2, point.x + centerButton.getMeasuredWidth() / 2, point.y + centerButton.getMeasuredHeight() / 2);

        setPos(getPath());

        for (int i = 1; i < bts.size(); i++) {
            MenuButton v = bts.get(i);
            v.layout(v.x, v.y, v.x + v.getMeasuredWidth(), v.y + v.getMeasuredHeight());

            Path path = v.getPathExplode();
            path.moveTo(point.x - v.getMeasuredWidth() / 2, point.y - v.getMeasuredHeight() / 2);
            path.lineTo(v.x, v.y);
            v.explode();
        }
    }

    void updateMask(float f) {
        mask.setAlpha(f);
    }

    Path getPath(){
        if(mOpenDriction==PPCircle.UNDEFIEN){
            return  producePath(enumOverScreen);
        }else
            return produceDriectPath();

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int action = ev.getAction();

        switch (action) {

            case MotionEvent.ACTION_DOWN:
                getDirection(ev);
                ListIterator listIterator = bts.listIterator();
                while (listIterator.hasNext()) {
                    ((MenuButton) listIterator.next()).dispatchTouchEvent(ev);
                }

                return false;
            case MotionEvent.ACTION_MOVE:
                for (int i = 1; i < bts.size(); i++) {
                    bts.get(i).dispatchTouchEvent(ev);
                }
                return false;
            case MotionEvent.ACTION_UP:
                int x = (int) ev.getRawX();
                int y = (int) ev.getRawY();
                MenuButton mb;

                for (int i = 0; i < bts.size(); i++) {
                    bts.get(i).dispatchTouchEvent(ev);
                }

                for (int i = 0; i < bts.size(); i++) {
                    mb = bts.get(i);
                    mb.getHitRect(btTempRect);
                    if (btTempRect.contains(x, y)) {
                        setSelectedIndex(i);
                        break;
                    } else setSelectedIndex(-1);
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
        int overScreen = 0;
        arcRange = new RectF(point.x - radius, point.y - radius, point.x + radius, point.y + radius);
        int centerX = windowCenterPoint.x;
        enumOverScreen = OverScreen.TOP;

        if (x < centerX) {
            overScreen = (int) arcRange.left;

            if (overScreen < 0) {
                enumOverScreen = OverScreen.LEFT;
                enumOverScreen.setOverScreenDistance(overScreen);
            }

        } else {
            arcRightXpos = (int) (arcRange.centerX() + radius);//圆的最大X位置
            overScreen = arcRightXpos - rectWindowRange.width();//圆超出屏幕的像素
            if (arcRightXpos > rectWindowRange.width()) {
                enumOverScreen = OverScreen.RIGHT;
                enumOverScreen.setOverScreenDistance(overScreen);
            }
        }
    }

    public void setPos(Path orbit) {
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

    private enum OverScreen {
        LEFT("LEFT", 0), RIGHT("RIGHT", 0), TOP("TOP", 0);
        private String type;
        private int overScreenDistance;

        OverScreen(String name, int index) {
            this.type = name;
            this.overScreenDistance = index;
        }

        public int getOverScreenDistance() {
            return overScreenDistance;
        }

        public void setOverScreenDistance(int overScreenDistance) {
            this.overScreenDistance = overScreenDistance;
        }
    }

    public int px2dip(float pxValue) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public Path producePath(OverScreen overScreen) {
        Path path = new Path();
        int start = 180;
        int overdis = Math.abs(overScreen.getOverScreenDistance());
        overdis = px2dip(overdis);
        int startDegree = 0;

        switch (overScreen) {
            case LEFT:
                if (start + overdis > 270) {
                    startDegree = 270;
                } else {
                    startDegree = start + overdis;
                }

                break;
            case RIGHT:
                if (start - overdis < 90) {
                    startDegree = 90;
                } else {
                    startDegree = start - overdis;
                }

                break;
            case TOP:
                startDegree = 180;

                break;
        }

        path.addArc(arcRange, startDegree, 180);

        return path;
    }

    Path produceDriectPath(){
        Path path = new Path();
        int startDegree = 0;
        if(mOpenDriction==PPCircle.LEFT){
            startDegree = 225;
        }else if(mOpenDriction==PPCircle.RIGHT){
            startDegree = 135;
        }

        path.addArc(arcRange, startDegree, 180);
        return path;
    }
}
