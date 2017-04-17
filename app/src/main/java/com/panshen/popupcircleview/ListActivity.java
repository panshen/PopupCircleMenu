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
    ArrayList<bean> list = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listlayout);
        lv = (ListView) findViewById(R.id.lv);
        for (int i = 0; i < 20; i++)
            list.add(new bean());

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
            final bean b = list.get(position);
            if (convertView == null) {
                vh = new VH();
                convertView = LayoutInflater.from(ListActivity.this).inflate(R.layout.item, null);
                vh.mPopupMenu = (PopupCircleView) convertView.findViewById(R.id.PopupMenu);
                vh.mIv = (ImageView) convertView.findViewById(R.id.iv);
                convertView.setTag(vh);
            } else {
                vh = (VH) convertView.getTag();
            }

            vh.mIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(ListActivity.this, "ImageClick", 0).show();
                }
            });

            //按钮被选中回调
            vh.mPopupMenu.setmOnMenuEventListener(new PopupCircleView.OnMenuEventListener() {
                @Override
                public void onMenuToggle(PopupButton popupButton, int index) {
                    if (popupButton.getId() == R.id.pb_like) {
                        b.setLike(popupButton.isChecked());
                    } else if (popupButton.getId() == R.id.pb_favorite) {
                        b.setFavorite(popupButton.isChecked());
                    } else if (popupButton.getId() == R.id.pb_share) {
                        b.setShare(popupButton.isChecked());
                    }
                }
            });

            //按钮可用时的回调
            vh.mPopupMenu.setOnButtonPreparedListener(new PopupCircleView.OnButtonPreparedListener() {
                @Override
                public void onPrepared(ArrayList<PopupButton> bts) {

                    for (PopupButton pb : bts) {
                        if (b.isLike())
                            if (pb.getId() == R.id.pb_like) {
                                pb.setChecked(true);
                            }

                        if (b.isShare())
                            if (pb.getId() == R.id.pb_share) {
                                pb.setChecked(true);
                            }

                        if (b.isFavorite())
                            if (pb.getId() == R.id.pb_favorite) {
                                pb.setChecked(true);
                            }

                    }
                }
            });

            return convertView;
        }

        class VH {
            ImageView mIv;
            PopupCircleView mPopupMenu;
        }

    }

    class bean {
        boolean like;
        boolean share;
        boolean favorite;

        public bean() {
        }


        public boolean isLike() {
            return like;
        }

        public void setLike(boolean like) {
            this.like = like;
        }

        public boolean isShare() {
            return share;
        }

        public void setShare(boolean share) {
            this.share = share;
        }

        public boolean isFavorite() {
            return favorite;
        }

        public void setFavorite(boolean favorite) {
            this.favorite = favorite;
        }
    }
}
