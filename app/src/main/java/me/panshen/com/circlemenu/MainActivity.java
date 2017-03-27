package me.panshen.com.circlemenu;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    PPCircle ppCircle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ppCircle = (PPCircle) findViewById(R.id.ppcircle);
        ppCircle.setOnMenuEventListener(new PPCircle.OnMenuEventListener() {
            @Override
            public void onToggle(PopUpMenu popUpMenu, String index) {
                Toast.makeText(MainActivity.this,index+"",0).show();
            }
        });
    }
}
