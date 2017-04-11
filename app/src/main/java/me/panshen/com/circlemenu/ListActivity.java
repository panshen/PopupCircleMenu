package me.panshen.com.circlemenu;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class ListActivity extends AppCompatActivity {
    ListView lv;
    ArrayList<String> list = new ArrayList<>();
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.testlayout);
        lv = (ListView) findViewById(R.id.lv);
        for (int i = 0; i < 10; i++) {
            list.add(i + "");
        }
        lv.setAdapter(new adapter());
    }

    private class adapter extends BaseAdapter {
        VH vh = null;
        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                vh = new VH();
                convertView = LayoutInflater.from(ListActivity.this).inflate(R.layout.testitem, null);
                vh.ppcircle = (PopupView) convertView.findViewById(R.id.ppcircle);
                vh.tv_1 = (ImageView) convertView.findViewById(R.id.iv_left);
                vh.iv = (ImageView) convertView.findViewById(R.id.iv);
                convertView.setTag(vh);
            } else {
                vh = (VH) convertView.getTag();
            }

            vh.tv_1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(ListActivity.this, "Menu click",  Toast.LENGTH_SHORT).show();
                }
            });

            vh.iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(ListActivity.this, "Image click", Toast.LENGTH_SHORT).show();
                }
            });

            Integer[] drawables = {R.drawable.audio, R.drawable.display,R.drawable.heart};
            vh.ppcircle.initRes(drawables);
            vh.ppcircle.setmOnMenuEventListener(new PopupView.OnMenuEventListener() {
                @Override
                public void onMenuToggle(PopupButton pb, int index) {
                    if(index==1){
                        Toast.makeText(ListActivity.this, "HEADSET", Toast.LENGTH_SHORT).show();
                    }else if(index==2){
                        Toast.makeText(ListActivity.this, "TV", Toast.LENGTH_SHORT).show();
                    }else if(index==3){
                        Toast.makeText(ListActivity.this, "HEART", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            return convertView;
        }

        class VH {
            ImageView tv_1, iv;
            PopupView ppcircle;
        }
    }
}
