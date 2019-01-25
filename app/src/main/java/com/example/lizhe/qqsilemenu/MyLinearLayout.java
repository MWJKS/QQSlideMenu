package com.example.lizhe.qqsilemenu;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

/**
 * 当slideMenu打开的时候，拦截并消费掉触摸事件
 */
public class MyLinearLayout extends LinearLayout {
    public MyLinearLayout(Context context) {
        super(context);
    }

    public MyLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private SlideMenu mSlideMenu;

    public void setSlideMenu(SlideMenu slideMenu) {
        this.mSlideMenu = slideMenu;
    }

    /**
     * 打开的监听方法
     *
     * @param ev
     * @return
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (mSlideMenu != null && mSlideMenu.getCurrentState() == SlideMenu.DragState.Open) {
            //如果slideMenu打开则应该拦截并消费掉事件
            return true;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mSlideMenu != null && mSlideMenu.getCurrentState() == SlideMenu.DragState.Open) {
            //抬起
            if (event.getAction()==MotionEvent.ACTION_UP)
            {
                mSlideMenu.close();
            }
            //如果slideMenu打开则应该拦截并消费掉事件
            return true;
        }
        return super.onTouchEvent(event);
    }
}
