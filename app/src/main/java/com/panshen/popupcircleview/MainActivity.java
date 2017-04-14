package com.panshen.popupcircleview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import xps.panshen.com.popupcirclemenu.PopupButton;
import xps.panshen.com.popupcirclemenu.PopupCircleView;


public class MainActivity extends AppCompatActivity {
    PopupCircleView ppView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ppView = (PopupCircleView) findViewById(R.id.PopupMenu2);

        ppView.setmOnMenuEventListener(new PopupCircleView.OnMenuEventListener() {
            @Override
            public void onMenuToggle(PopupButton pb, int index) {
                switch(pb.getResId()){
                    case R.drawable.good:
                        Toast.makeText(MainActivity.this, "good", Toast.LENGTH_SHORT).show();
                        break;
                    case R.drawable.favorite:
                        Toast.makeText(MainActivity.this, "favorite", Toast.LENGTH_SHORT).show();
                        break;
                    case R.drawable.share:
                        Toast.makeText(MainActivity.this, "share", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });

        findViewById(R.id.iv_up).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "image click ", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.iv_bottom).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "image click ", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
