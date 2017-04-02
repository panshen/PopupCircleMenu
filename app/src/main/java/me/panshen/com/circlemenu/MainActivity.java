package me.panshen.com.circlemenu;

import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    PPCircle ppCircle;
    RelativeLayout parent = null;
    ImageView iv_property_anim = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        parent = (RelativeLayout) findViewById(R.id.parent_re);
        ImageView im = new ImageView(this);
        iv_property_anim = (ImageView) findViewById(R.id.iv_property_anim);
        int dp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 60,
                getResources().getDisplayMetrics());

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(dp, dp);
        im.setScaleType(ImageView.ScaleType.CENTER_CROP);
        im.setLayoutParams(params);

        im.setImageResource(R.drawable.bg);

        parent.addView(im);

        ppCircle = (PPCircle) findViewById(R.id.ppcircle);
        ppCircle.setOnMenuEventListener(new PPCircle.OnMenuEventListener() {
            @Override
            public void onToggle(PopUpMenu popUpMenu, int index) {
                Toast.makeText(MainActivity.this, index + "", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
