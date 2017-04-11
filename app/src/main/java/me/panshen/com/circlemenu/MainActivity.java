package me.panshen.com.circlemenu;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    PopupView ppCircle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ppCircle = (PopupView) findViewById(R.id.ppcircle);
        ppCircle.setmOnMenuEventListener(new PopupView.OnMenuEventListener() {
            @Override
            public void onMenuToggle(PopupButton pb, int index) {
                if(index==1){
                    Toast.makeText(MainActivity.this, "HEADSET", Toast.LENGTH_SHORT).show();
                }else if(index==2){
                    Toast.makeText(MainActivity.this, "TV", Toast.LENGTH_SHORT).show();
                }else if(index==3){
                    Toast.makeText(MainActivity.this, "HEART", Toast.LENGTH_SHORT).show();
                }
            }
        });

        findViewById(R.id.iv_up).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "image click up", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.iv_bottom).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "image click bottom", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
