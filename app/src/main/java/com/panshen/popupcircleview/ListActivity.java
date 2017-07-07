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

import com.panshen.xps.popupcirclemenu.PopupButton;
import com.panshen.xps.popupcirclemenu.PopupCircleView;

public class ListActivity extends AppCompatActivity {
    ListView lv;
    ArrayList<bean> list = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listlayout);
        lv = (ListView) findViewById(R.id.lv);

        //模拟初始化数据--------------------------------------------------
        for (int i = 0; i < 20; i++)
            list.add(new bean());

        list.get(1).setLike(true);//勾选第二条的喜欢和收藏按钮
        list.get(1).setFavorite(true);

        list.get(2).setLike(true);//勾选第三条的喜欢按钮

        lv.setAdapter(new adapter());
        //结束初始化数据--------------------------------------------------
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
                    Toast.makeText(ListActivity.this, "Image Click", Toast.LENGTH_SHORT).show();
                }
            });

            /**
             * 按钮被选中时的回调
             * */
            vh.mPopupMenu.setmOnMenuEventListener(new PopupCircleView.OnMenuEventListener() {
                @Override
                public void onMenuToggle(PopupButton popupButton) {

                    switch(popupButton.getId()){
                        case R.id.pb_like:
                            b.setLike(popupButton.isChecked());
                            break;
                        case R.id.pb_favorite:
                            b.setFavorite(popupButton.isChecked());
                            break;
                        case R.id.pb_share:
                            b.setShare(popupButton.isChecked());
                            break;
                    }

                }
            });

            /**
             *在这里初始化按钮的勾选状态
             *you can initialize buttons check states here
             * */
            vh.mPopupMenu.setOnButtonPreparedListener(new PopupCircleView.OnButtonPreparedListener() {
                @Override
                public void onPrepared(ArrayList<PopupButton> bts) {

                    for (PopupButton pb : bts) {
                        if (b.isLiked())
                            if (pb.getId() == R.id.pb_like) {
                                pb.setChecked(true);
                            }
                        if (b.isShared())
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

   private class bean {
        boolean like;
        boolean share;
        boolean favorite;

        public bean() {
        }

        public boolean isLiked() {
            return like;
        }

        public void setLike(boolean like) {
            this.like = like;
        }

        public boolean isShared() {
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
