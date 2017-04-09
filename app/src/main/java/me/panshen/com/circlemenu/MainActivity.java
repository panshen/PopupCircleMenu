package me.panshen.com.circlemenu;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
            public void onMenuToggle(ArrayList<PopupButton> mbs, int index) {
                Toast.makeText(MainActivity.this, mbs.get(index).name + "", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
