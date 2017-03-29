package me.panshen.com.circlemenu;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
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
import java.util.List;

public class PopUpMenu extends RelativeLayout {
    private String TAG = getClass().getName();
    View mask = null;
    int radius = 265;
    Point point = null;
    RectF arcRange = null;
    Rect rectWindowRange = null;
    Rect btTempRect = null;
    Point windowCenterPoint = null;
    ArrayList<MenuButton> bts = null;
    OverScreen enumOverScreen = OverScreen.NORMAL;
    Paint mPaint = null;
    int selectedIndex = 0;
    Path one = new Path();

    List<Path> paths = new ArrayList<Path>();
    List<PathMeasure> measure = new ArrayList<PathMeasure>();

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public void setSelectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;
    }

    public PopUpMenu(Activity context, ArrayList<MenuButton> bts) {
        super(context);

        Log.e(TAG, "init");
        setClickable(true);
        this.bts = bts;
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

            paths.add(mb.getSelfPath());
            measure.add(new PathMeasure());
            addView(mb);
        }

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(10);
        mPaint.setColor(Color.WHITE);
        setWillNotDraw(false);
    }

    public void resetCenter(Point point) {
        this.point = point;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        Log.e("PopUpMenu.onLayout", "onLayout");
        View view = getChildAt(0);//layout mask

        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        View centerButton = getChildAt(1);//layout 中间的按钮
        centerButton.layout(point.x - centerButton.getMeasuredWidth() / 2, point.y - centerButton.getMeasuredHeight() / 2, point.x + centerButton.getMeasuredWidth() / 2, point.y + centerButton.getMeasuredHeight() / 2);

        genPos(producePath(enumOverScreen));

        for (int i = 1; i < bts.size(); i++) {

            MenuButton v = bts.get(i);
            v.layout(v.x, v.y, v.x + v.getMeasuredWidth(), v.y + v.getMeasuredHeight());

            Path path = v.getSelfPath();
            path.moveTo(point.x, point.y);
            path.lineTo(v.x + v.getMeasuredWidth() / 2, v.y + v.getMeasuredWidth() / 2);
            measure.get(i).setPath(path, false);
        }

    }

    void updateMask(float f) {
        mask.setAlpha(f);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int action = ev.getAction();

        switch (action) {

            case MotionEvent.ACTION_DOWN:
                getDirection(ev);

                for (final MenuButton mb : bts) {
                    mb.dispatchTouchEvent(ev);
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

                    mb = bts.get(i);
                    mb.dispatchTouchEvent(ev);
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
        int overScrren = 0;
        RectF arcRect = new RectF(point.x - radius, point.y - radius, point.x + radius, point.y + radius);
        int centerX = windowCenterPoint.x;
        enumOverScreen = OverScreen.NORMAL;

        if (x < centerX) {
            overScrren = (int) arcRect.left;

            if (overScrren < 0) {
                enumOverScreen = OverScreen.LEFT;
                enumOverScreen.setOverScreenDistance(overScrren);
            }

        } else {
            arcRightXpos = (int) (arcRect.centerX() + radius);
            overScrren = arcRightXpos - rectWindowRange.width();
            if (arcRightXpos > rectWindowRange.width()) {
                enumOverScreen = OverScreen.RIGHT;
                enumOverScreen.setOverScreenDistance(overScrren);
            }
        }
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
        LEFT("LEFT", 0), RIGHT("RIGHT", 0), NORMAL("NORMAL", 0);
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
        int start = 170;
        int end = 370;
        arcRange = new RectF(point.x - radius, point.y - radius, point.x + radius, point.y + radius);

        switch (overScreen) {
            case LEFT:
                start -= overScreen.getOverScreenDistance() / 4;
                end -= overScreen.getOverScreenDistance() / 4;
                break;
            case RIGHT:
                start -= overScreen.getOverScreenDistance() / 4;
                end -= overScreen.getOverScreenDistance() / 4;
                break;
            case NORMAL:

                break;
        }

        orbit.addArc(arcRange, start, end - start);

        return orbit;
    }


}
