package com.panshen.xps.popupcirclemenu;

import android.app.Activity;
import android.content.Context;
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
import java.util.HashMap;
import java.util.Iterator;

public class PopupLayer extends RelativeLayout {
    private String TAG = getClass().getName();
    private View mShadowView;
    private int mRadius = 250;
    private Point mWindowCenterPoint;
    private Point mPoint;
    private RectF mArcRange;
    private Rect mRectWindowRange;
    /**
     * 把按钮所在rect存储起来 用来检测触摸事件所触发的按钮
     */
    private Rect btTempRect;
    public int mSelectedIndex;
    private EnumOverScreen mEnumOverScreen = EnumOverScreen.TOP;
    private int mOpenDriction = PopupCircleView.UNDEFIEN;
    private Context mContext;
    private ArrayList<PopupButton> mButtons = new ArrayList<>();
    private HashMap<PopupButton, TextLableView> kvs = new HashMap<>();
    int pointerCount = 0;
    private PopupButton pb;
    private TextLableView tv;

    public void setSelectedIndex(int mSelectedIndex) {
        this.mSelectedIndex = mSelectedIndex;
    }

    public int getSelectedIndex() {
        return mSelectedIndex;
    }

    public PopupLayer(Activity context, int radius) {
        super(context);
        mRadius = radius;
        mContext = context;
        Display display = context.getWindow().getWindowManager().getDefaultDisplay();
        mRectWindowRange = new Rect();
        btTempRect = new Rect();
        display.getRectSize(mRectWindowRange);
        mWindowCenterPoint = new Point(mRectWindowRange.centerX(), mRectWindowRange.centerY());
        mShadowView = new View(context);
        mShadowView.setBackgroundColor(Color.parseColor("#66000000"));
        mShadowView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        addView(mShadowView);
    }

    public void setbts(ArrayList<PopupButton> bts) {
        mButtons = bts;
        for (int i = 0; i < mButtons.size(); i++) {
            PopupButton pb = mButtons.get(i);
            TextLableView tv = new TextLableView(mContext);

            if (i == 0)//用来标记中间按钮对应的TextView 在layout时过滤掉
                tv.setTag(String.valueOf(Integer.MAX_VALUE));

            tv.setText(pb.getCurrentText());
            addView(pb);
            addView(tv);
            kvs.put(pb, tv);
        }
    }

    public void resetCenter(Point point, int dirction) {
        this.mPoint = point;
        this.mOpenDriction = dirction;
        invalidate();
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (pointerCount > 1) return;
        View view = getChildAt(0);//layout 背景
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        //layout 中心的按钮
        View centerButton = getChildAt(1);
        centerButton.layout(mPoint.x - centerButton.getMeasuredWidth() / 2, mPoint.y - centerButton.getMeasuredHeight() / 2, mPoint.x + centerButton.getMeasuredWidth() / 2, mPoint.y + centerButton.getMeasuredHeight() / 2);
        setPos(getPath());

        for (int i = 2; i < getChildCount(); i++) {
            View v = getChildAt(i);
            if (v instanceof PopupButton) {
                PopupButton pv = (PopupButton) v;

                pv.layout(pv.x, pv.y, pv.x + pv.getMeasuredWidth(), pv.y + pv.getMeasuredHeight());
                Path path = pv.getmPathExplode();
                path.moveTo(mPoint.x - v.getMeasuredWidth() / 2, mPoint.y - v.getMeasuredHeight() / 2);
                path.lineTo(pv.x, pv.y);
                pv.explode();

                TextLableView tv = kvs.get(pv);
                if (tv.getText().equals(String.valueOf(Integer.MAX_VALUE)) || pv.getCurrentText().equals("-1"))
                    continue;
                tv.layout(tv.x, tv.y, tv.x + tv.getMeasuredWidth(), tv.y + tv.getMeasuredHeight());
            }
        }
    }

    void setShadowViewAlpha(float f) {
        mShadowView.setAlpha(f);
    }

    /**
     * 计算菜单展开的路径
     */
    private Path getPath() {
        if (mOpenDriction == PopupCircleView.UNDEFIEN) {
            return PathUtil.genPath(mArcRange, getContext(), mEnumOverScreen);
        } else
            return PathUtil.genDriectPath(mArcRange, mOpenDriction);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int x = (int) ev.getRawX();
        int y = (int) ev.getRawY();
        int action = ev.getAction();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                initDirection(ev);
                refreshTextLable();
                pointerCount++;
                for (int i = 1; i < mButtons.size(); i++) {
                    pb = mButtons.get(i);
                    pb.dispatchTouchEvent(ev);
                }

                break;
            case MotionEvent.ACTION_MOVE:

                for (int i = 1; i < mButtons.size(); i++) {
                    pb = mButtons.get(i);
                    pb.dispatchTouchEvent(ev);

                    tv = kvs.get(pb);
                    pb.getHitRect(btTempRect);
                    if (btTempRect.contains(x, y) && pb.isExploded()) {
                        tv.startShowAnim();
                    } else {
                        tv.startHideAnim();
                    }

                }

                break;
            case MotionEvent.ACTION_UP:
                pointerCount = 0;
                for (int i = 1; i < mButtons.size(); i++) {
                    mButtons.get(i).dispatchTouchEvent(ev);
                }

                for (int i = 1; i < mButtons.size(); i++) {
                    pb = mButtons.get(i);

                    pb.getHitRect(btTempRect);
                    if (btTempRect.contains(x, y)) {
                        setSelectedIndex(i);
                        break;
                    } else {
                        setSelectedIndex(-1);
                    }
                }

                break;
            default:
                break;
        }

        return false;
    }

    void refreshTextLable() {
        Iterator<PopupButton> i = kvs.keySet().iterator();

        while (i.hasNext()) {
            PopupButton pb = i.next();
            TextLableView v = kvs.get(pb);
            v.setText(pb.getCurrentText());//切换TextLable上的文字
            v.setAlpha(0f);
        }

    }

    private void initDirection(MotionEvent ev) {
        int x = (int) ev.getRawX();
        int arcRightXpos;
        int overScreen;
        mArcRange = new RectF(mPoint.x - mRadius, mPoint.y - mRadius, mPoint.x + mRadius, mPoint.y + mRadius);
        int centerX = mWindowCenterPoint.x;
        mEnumOverScreen = EnumOverScreen.TOP;

        if (x < centerX) {
            overScreen = (int) mArcRange.left;
            if (overScreen < 0) {
                mEnumOverScreen = EnumOverScreen.LEFT;
                mEnumOverScreen.setOverScreenDistance(overScreen);
            }

        } else {
            arcRightXpos = (int) (mArcRange.centerX() + mRadius);
            overScreen = arcRightXpos - mRectWindowRange.width();
            if (arcRightXpos > mRectWindowRange.width()) {
                mEnumOverScreen = EnumOverScreen.RIGHT;
                mEnumOverScreen.setOverScreenDistance(overScreen);
            }
        }
    }

    /**
     * 用来给每一个button设置一个中心点
     *
     * @param orbit 一个特定角度的path
     */
    private void setPos(Path orbit) {
        PathMeasure measure = new PathMeasure(orbit, false);
        TextLableView tv;
        for (int i = 0; i < mButtons.size(); i++) {
            PopupButton pp = mButtons.get(i);
            tv = kvs.get(pp);
            float[] coords = new float[]{0f, 0f};
            int length = (int) ((i) * measure.getLength() / mButtons.size());
            measure.getPosTan(length, coords, null);
            int px = (int) coords[0] - pp.getMeasuredWidth() / 2;
            int py = (int) coords[1] - pp.getMeasuredHeight() / 2;
            int tvx = (int) coords[0] - tv.getMeasuredWidth() / 2;
            tv.x = tvx;
            tv.y = py - 60;
            pp.x = px;
            pp.y = py;
        }
    }
}
