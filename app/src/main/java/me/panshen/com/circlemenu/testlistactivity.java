package me.panshen.com.circlemenu;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class testlistactivity extends AppCompatActivity {
    ListView lv;
    ArrayList<String> d = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.testlayout);
        lv = (ListView) findViewById(R.id.lv);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        for (int i = 0; i < 20; i++) {
            d.add(i + "");
        }

        lv.setAdapter(new adapter());
    }

    private class adapter extends BaseAdapter {

        @Override
        public int getCount() {
            return d.size();
        }

        @Override
        public Object getItem(int position) {
            return d.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        VH vh;

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                vh = new VH();
                convertView = LayoutInflater.from(testlistactivity.this).inflate(R.layout.testitem, null);
                vh.ppcircle = (PPCircle) convertView.findViewById(R.id.ppcircle);
                convertView.setTag(vh);
            } else {
                vh = (VH) convertView.getTag();
            }
            vh.ppcircle.setOnMenuEventListener(new PPCircle.OnMenuEventListener() {
                @Override
                public void onMenuToggle(ArrayList<MenuButton> popUpMenu, int index) {
//                    if (index != -1)
//                        Toast.makeText(testlistactivity.this, popUpMenu.get(index).name + "", Toast.LENGTH_SHORT).show();
//                    else
//                        Toast.makeText(testlistactivity.this, index + "", Toast.LENGTH_SHORT).show();
                }
            });
            return convertView;
        }

        class VH {
            TextView tv_1;
            PPCircle ppcircle;
        }
    }
}
