package com.panshen.popupcircleview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.panshen.xps.popupcirclemenu.PopupButton;
import com.panshen.xps.popupcirclemenu.PopupCircleView;


public class MainActivity extends AppCompatActivity {
    PopupCircleView ppView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ppView = (PopupCircleView) findViewById(R.id.pcv);
        ppView.setmOnMenuEventListener(new PopupCircleView.OnMenuEventListener() {
            @Override
            public void onMenuToggle(PopupButton popupButton, int index) {
                if (popupButton.getId() == R.id.pb_fav) {

                    if(!popupButton.isChecked()){
                        Toast.makeText(MainActivity.this, "取消收藏", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(MainActivity.this, "收藏", Toast.LENGTH_SHORT).show();
                    }

                } else if (popupButton.getId() == R.id.pb_thumbup) {
                    Toast.makeText(MainActivity.this, "喜欢", Toast.LENGTH_SHORT).show();
                } else if (popupButton.getId() == R.id.pb_share) {
                    Toast.makeText(MainActivity.this, "分享", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
