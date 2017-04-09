package me.panshen.com.circlemenu;

import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    PPCircle ppCircle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ppCircle = (PPCircle) findViewById(R.id.ppcircle);
        ppCircle.setOnMenuEventListener(new PPCircle.OnMenuEventListener() {
            @Override
            public void onMenuToggle(ArrayList<MenuButton> popUpMenu, int index) {
//                if (index != -1)
//                    Toast.makeText(MainActivity.this, popUpMenu.get(index).name + "", Toast.LENGTH_SHORT).show();
//                else
//                    Toast.makeText(MainActivity.this, index + "", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
