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
//        findViewById(R.id.iv_1).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(MainActivity.this,"Image Click",0).show();
//            }
//        });
    }
}
