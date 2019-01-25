package com.example.lizhe.qqsilemenu;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Random;


public class MainActivity extends AppCompatActivity {
    private ListView menu_listview;
    private ListView main_listview;
    private SlideMenu slideMenu;
    private ImageView iv_head;
    private MyLinearLayout my_layout;
    private ListViewAdapter mListViewAdapter;
    private ObjectAnimator mAnim;
    private ObjectAnimator mMAnim2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
    }

    private void initData() {
        //        mListViewAdapter=new ListViewAdapter(MainActivity.this,R.layout.item_listview,Constant.sCheeseStrings);
//        menu_listview.setAdapter(mListViewAdapter);
        //第二种
        menu_listview.setAdapter(new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, Constant.sCheeseStrings) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView textView = (TextView) super.getView(position, convertView, parent);
                textView.setTextColor(Color.WHITE);
                return textView;
            }
        });
        main_listview.setAdapter(new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, Constant.NAMES) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = convertView == null ? super.getView(position, convertView, parent) : convertView;
                //以属性动画缩小放大
                mAnim = ObjectAnimator.ofFloat(view, "scaleX", 0.5f, 1f);
                mMAnim2 = ObjectAnimator.ofFloat(view, "scaleY", 0.5f, 1f);
                mAnim.setDuration(300);
                mAnim.start();
                mMAnim2.setDuration(300);
                mMAnim2.start();
                return view;
            }
        });
//        startActivity(new Intent(MainActivity.this,TestActivity.class));
        slideMenu.setOnDragStateChangeListener(new SlideMenu.onDragStateChangeListener() {
            @Override
            public void onOpen() {
                menu_listview.smoothScrollToPosition(new Random().nextInt(menu_listview.getCount()));
            }

            @Override
            public void onClose() {
                ObjectAnimator animator = ObjectAnimator.ofFloat(iv_head, "translationX", 15, -15);
                animator.setRepeatCount(1);
                animator.setDuration(500);
                animator.start();
            }

            @Override
            public void onDraging(float fraction) {
                iv_head.setAlpha(1 - fraction);
            }
        });
        my_layout.setSlideMenu(slideMenu);
    }

    private void initView() {
        slideMenu = findViewById(R.id.slideMenu);
        iv_head = findViewById(R.id.iv_head);
        my_layout = findViewById(R.id.my_layout);
        menu_listview = findViewById(R.id.menu_listview);
        main_listview = findViewById(R.id.main_listview);
    }
}
