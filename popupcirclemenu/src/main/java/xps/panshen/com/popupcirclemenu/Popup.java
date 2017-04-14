package xps.panshen.com.popupcirclemenu;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.ArrayList;

public class Popup extends RelativeLayout {
    private String TAG = getClass().getName();
    private View mShadowView;
    private int mRadius = 250;
    private Point mPoint;
    private RectF mArcRange;
    private Rect mRectWindowRange;
    private Rect btTempRect;
    private Point mWindowCenterPoint;
    public ArrayList<PopupButton> bts;
    public int mSelectedIndex ;
    private OverScreen mEnumOverScreen = OverScreen.TOP;
    private int mOpenDriction = PopupCircleView.UNDEFIEN;

    public int getmSelectedIndex() {
        return mSelectedIndex;
    }

    public void setmSelectedIndex(int mSelectedIndex) {
        this.mSelectedIndex = mSelectedIndex;
    }

    public Popup(Activity context, ArrayList<PopupButton> bts, int radius) {
        super(context);
        this.bts = bts;
        mRadius = radius;

        Display display = context.getWindow().getWindowManager().getDefaultDisplay();
        mRectWindowRange = new Rect();
        btTempRect = new Rect();
        display.getRectSize(mRectWindowRange);
        mWindowCenterPoint = new Point(mRectWindowRange.centerX(), mRectWindowRange.centerY());

        mShadowView = new View(context);
        mShadowView.setBackgroundColor(Color.parseColor("#66000000"));
        mShadowView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        addView(mShadowView);

        for (PopupButton mb : bts) {
            addView(mb);
        }

    }

    public void setbts(ArrayList<PopupButton> bts) {
        this.bts.clear();
        this.bts.addAll(bts);

        removeViews(1, getChildCount() - 1);
        for (PopupButton mb : bts) {
            addView(mb);
        }

    }

    public void resetCenter(Point point, int dirction) {
        this.mPoint = point;
        this.mOpenDriction = dirction;
        invalidate();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        View view = getChildAt(0);
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        View centerButton = getChildAt(1);
        centerButton.layout(mPoint.x - centerButton.getMeasuredWidth() / 2, mPoint.y - centerButton.getMeasuredHeight() / 2, mPoint.x + centerButton.getMeasuredWidth() / 2, mPoint.y + centerButton.getMeasuredHeight() / 2);

        setPos(getPath());

        for (int i = 1; i < bts.size(); i++) {
            PopupButton v = bts.get(i);
            v.layout(v.x, v.y, v.x + v.getMeasuredWidth(), v.y + v.getMeasuredHeight());

            Path path = v.getmPathExplode();
            path.moveTo(mPoint.x - v.getMeasuredWidth() / 2, mPoint.y - v.getMeasuredHeight() / 2);
            path.lineTo(v.x, v.y);
            v.explode();
        }
    }

    void setShadowViewAlpha(float f) {
        mShadowView.setAlpha(f);
    }

    private Path getPath() {
        if (mOpenDriction == PopupCircleView.UNDEFIEN) {
            return producePath(mEnumOverScreen);
        } else
            return genDriectPath();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int action = ev.getAction();

        switch (action) {

            case MotionEvent.ACTION_DOWN:
                getDirection(ev);
                for (PopupButton mb : bts) {
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
                PopupButton mb;

                for (int i = 0; i < bts.size(); i++) {
                    bts.get(i).dispatchTouchEvent(ev);
                }

                for (int i = 0; i < bts.size(); i++) {
                    mb = bts.get(i);
                    mb.getHitRect(btTempRect);
                    if (btTempRect.contains(x, y)) {
                        setmSelectedIndex(i);
                        break;
                    } else setmSelectedIndex(-1);
                }

                return false;

            default:
                break;
        }

        return false;
    }

    private void getDirection(MotionEvent ev) {
        int x = (int) ev.getRawX();
        int arcRightXpos = 0;
        int overScreen = 0;
        mArcRange = new RectF(mPoint.x - mRadius, mPoint.y - mRadius, mPoint.x + mRadius, mPoint.y + mRadius);
        int centerX = mWindowCenterPoint.x;
        mEnumOverScreen = OverScreen.TOP;

        if (x < centerX) {
            overScreen = (int) mArcRange.left;

            if (overScreen < 0) {
                mEnumOverScreen = OverScreen.LEFT;
                mEnumOverScreen.setOverScreenDistance(overScreen);
            }

        } else {
            arcRightXpos = (int) (mArcRange.centerX() + mRadius);
            overScreen = arcRightXpos - mRectWindowRange.width();
            if (arcRightXpos > mRectWindowRange.width()) {
                mEnumOverScreen = OverScreen.RIGHT;
                mEnumOverScreen.setOverScreenDistance(overScreen);
            }
        }
    }

    private void setPos(Path orbit) {
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

    private int px2dip(float pxValue) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    private Path producePath(OverScreen overScreen) {
        Path path = new Path();
        int start = 180;
        int overDis = Math.abs(overScreen.getOverScreenDistance());
        overDis = px2dip(overDis);
        int startDegree =0;

        switch (overScreen) {
            case LEFT:
                if (start + overDis > 270) {
                    startDegree = 270;
                } else {
                    startDegree = start + overDis;
                }

                break;
            case RIGHT:
                if (start - overDis < 90) {
                    startDegree = 90;
                } else {
                    startDegree = start - overDis;
                }

                break;
            case TOP:
                startDegree = 180;
                break;
        }

        path.addArc(mArcRange, startDegree, 180);
        return path;
    }

    private Path genDriectPath() {
        Path path = new Path();
        int startDegree = 0;
        if (mOpenDriction == PopupCircleView.LEFT) {
            startDegree = 225;
        } else if (mOpenDriction == PopupCircleView.RIGHT) {
            startDegree = 135;
        }
        path.addArc(mArcRange, startDegree, 180);
        return path;
    }
}
