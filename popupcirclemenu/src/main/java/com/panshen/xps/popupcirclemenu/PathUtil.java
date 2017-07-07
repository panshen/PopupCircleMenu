package com.panshen.xps.popupcirclemenu;

import android.content.Context;
import android.graphics.Path;
import android.graphics.RectF;

public class PathUtil {

    /**
     * 获得一个指定角度的path
     */
    public static Path genPath(RectF mArcRange, Context context, EnumOverScreen overScreen) {
        Path path = new Path();
        int start = 180;
        int overDis = Math.abs(overScreen.getOverScreenDistance());
        overDis = px2dip(context,overDis);
        int startDegree = 0;

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

    /**
     * 根据View的配置产生对应角度的path
     */
    public static Path genDriectPath(RectF mArcRange,int mOpenDriction) {
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

    private static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

}
