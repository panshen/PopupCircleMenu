package me.panshen.com.circlemenu;

import android.content.Context;
import android.graphics.Rect;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MotionEvent;
import android.widget.ImageView;


public class cccView extends android.support.v7.widget.AppCompatImageView {
    int i = 0;
    public cccView(Context context) {
        super(context);
    }

    public cccView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public cccView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int action = event.getAction();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                i++;
                Log.e("onTouchEvent","ACTION_DOWN");
                return true;
            case MotionEvent.ACTION_MOVE:

                Log.e("onTouchEvent","ACTION_MOVE");
                Log.e("i===",""+i);

                return true;
            case MotionEvent.ACTION_UP:
                Log.e("onTouchEvent","ACTION_UP");

                return true;
            case MotionEvent.ACTION_CANCEL:
                Log.e("onTouchEvent","ACTION_CANCEL");
                return true;
        }
        return super.onTouchEvent(event);
    }
}
