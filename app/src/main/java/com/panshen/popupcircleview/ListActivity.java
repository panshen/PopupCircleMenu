package com.panshen.popupcircleview;

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

import xps.panshen.com.popupcirclemenu.PopupButton;
import xps.panshen.com.popupcirclemenu.PopupCircleView;

public class ListActivity extends AppCompatActivity {
    ListView lv;
    ArrayList<String> list = new ArrayList<>();
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listlayout);
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
                convertView = LayoutInflater.from(ListActivity.this).inflate(R.layout.item, null);
                vh.mPopupMenu2 = (PopupCircleView) convertView.findViewById(R.id.PopupMenu2);
                vh.mPopupMenu1 = (PopupCircleView) convertView.findViewById(R.id.PopupMenu1);
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

            vh.mPopupMenu1.setmOnMenuEventListener(new PopupCircleView.OnMenuEventListener() {
                @Override
                public void onMenuToggle(PopupButton pb, int index) {
                    switch(pb.getResId()){
                        case R.drawable.good:
                            Toast.makeText(ListActivity.this, "good", Toast.LENGTH_SHORT).show();
                            break;
                        case R.drawable.favorite:
                            Toast.makeText(ListActivity.this, "favorite", Toast.LENGTH_SHORT).show();
                            break;
                        case R.drawable.share:
                            Toast.makeText(ListActivity.this, "share", Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            });

            Integer[] res = {R.drawable.tv,R.drawable.heart,R.drawable.headset};
            vh.mPopupMenu2.initRes(res);
            vh.mPopupMenu2.setmOnMenuEventListener(new PopupCircleView.OnMenuEventListener() {
                @Override
                public void onMenuToggle(PopupButton pb, int index) {

                    switch(pb.getResId()){
                        case R.drawable.tv:
                            Toast.makeText(ListActivity.this, "tv", Toast.LENGTH_SHORT).show();
                            break;
                        case R.drawable.heart:
                            Toast.makeText(ListActivity.this, "heart", Toast.LENGTH_SHORT).show();
                            break;
                        case R.drawable.headset:
                            Toast.makeText(ListActivity.this, "headset", Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            });



            return convertView;
        }

        class VH {
            ImageView tv_1, iv;
            PopupCircleView mPopupMenu1, mPopupMenu2;
        }
    }
}
