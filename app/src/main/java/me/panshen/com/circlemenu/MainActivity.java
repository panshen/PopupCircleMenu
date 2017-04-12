package me.panshen.com.circlemenu;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

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
                    case R.drawable.like:
                        Toast.makeText(MainActivity.this, "like", Toast.LENGTH_SHORT).show();
                        break;
                    case R.drawable.unlike:
                        Toast.makeText(MainActivity.this, "unlike", Toast.LENGTH_SHORT).show();
                        break;
                    case R.drawable.trashbin:
                        Toast.makeText(MainActivity.this, "trashbin", Toast.LENGTH_SHORT).show();
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
