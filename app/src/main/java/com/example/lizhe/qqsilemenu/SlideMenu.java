package com.example.lizhe.qqsilemenu;

import android.animation.FloatEvaluator;
import android.animation.IntEvaluator;
import android.animation.TypeEvaluator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;


public class SlideMenu extends FrameLayout {

    private View mMenuView; //菜单View
    private View mMainView; //主界面View
    private float draRange;//拖拽范围
    private ViewDragHelper mViewDragHelper;

    private int mWidth;
    //计算百分比方法
    private IntEvaluator mIntEvaluator = new IntEvaluator();
    private FloatEvaluator mEvaluator = new FloatEvaluator();

    public SlideMenu(Context context) {
        super(context);
        init();
    }

    //定义状态常量值
    enum DragState {
        Open, Close
    }

    private DragState currentState = DragState.Close;//当前状态默认是关闭

    public SlideMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mViewDragHelper = ViewDragHelper.create(this, callback);
    }

    /**
     * 接收子View
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        //简单的异常处理
        if (getChildCount() != 2) {
            throw new IllegalArgumentException("布局文件里只能有2个子View");
        }
        mMenuView = getChildAt(0);
        mMainView = getChildAt(1);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mViewDragHelper.processTouchEvent(event);
        return true;
    }

    /**
     * 该方法在onMeasure执行完之后执行，那么可以在该方法中初始化自己和子View的宽高
     *
     * @param w
     * @param h
     * @param oldw
     * @param oldh
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = getMeasuredWidth();
        draRange = mWidth * 0.6f;
    }

    /**
     * 获取当前状态
     *
     * @return
     */
    public DragState getCurrentState() {
        return currentState;
    }

    /**
     * 判断是否拦截
     *
     * @param ev
     * @return
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mViewDragHelper.shouldInterceptTouchEvent(ev);
    }

    private ViewDragHelper.Callback callback = new ViewDragHelper.Callback() {
        @Override
        public boolean tryCaptureView(@NonNull View view, int i) {
            return view == mMainView || view == mMenuView;
        }

        @Override
        public int getViewHorizontalDragRange(@NonNull View child) {
            return (int) draRange;
        }

        @Override
        public int clampViewPositionHorizontal(@NonNull View child, int left, int dx) {
            if (child == mMainView) {
                if (left < 0)
                    left = 0;
                if (left > draRange)
                    left = (int) draRange;
            }

            return left;
        }

        /**
         * 通常用于子View的伴随移动
         * @param changedView
         * @param left
         * @param top
         * @param dx
         * @param dy
         */
        @Override
        public void onViewPositionChanged(@NonNull View changedView, int left, int top, int dx, int dy) {
            if (changedView == mMenuView) {
                //固定住MenuView
                mMenuView.layout(0, 0, mMenuView.getMeasuredWidth(), mMenuView.getMeasuredHeight());
                //让mainView移动起来
                int newLeft = mMainView.getLeft() + dx;
                if (newLeft < 0) newLeft = 0;
                if (newLeft > draRange) newLeft = (int) draRange;
                mMainView.layout(newLeft, mMainView.getTop() + dy, mMainView.getRight() + dx, mMainView.getBottom() + dy);


            } //计算滑动百分比
            float fraction = mMainView.getLeft() / draRange;
            //执行伴随的动画
            executeAnim(fraction);

            //更改状态回调方法
            if (fraction == 0 && currentState != DragState.Close) {
                currentState = DragState.Close;
                if (mListener != null) mListener.onClose();
            } else if (fraction == 1f && currentState != DragState.Open) {
                currentState = DragState.Open;
                if (mListener != null) mListener.onOpen();
            }
            /**
             * 将drag的fraction暴露给外界
             */
            if (mListener != null) {
                mListener.onDraging(fraction);
            }
        }

        @Override
        public void onViewReleased(@NonNull View releasedChild, float xvel, float yvel) {
            if (mMainView.getLeft() < draRange / 2) {
                //在左半边
                close();
            } else {
                //在右半边
                open();
            }

            //处理用户的稍微滑动
            if (xvel > 200 && currentState != DragState.Open) {
                open();
            } else if (xvel < -20 && currentState != DragState.Close) {
                close();
            }
        }
    };

    /**
     * 打开菜单
     */
    public void close() {
        mViewDragHelper.smoothSlideViewTo(mMainView, 0, mMainView.getTop());
        ViewCompat.postInvalidateOnAnimation(SlideMenu.this);
    }

    private void open() {
        mViewDragHelper.smoothSlideViewTo(mMainView, (int) draRange, mMainView.getTop());
        ViewCompat.postInvalidateOnAnimation(SlideMenu.this);
    }

    /**
     * 执行伴随动画
     *
     * @param fraction
     */
    private void executeAnim(float fraction) {
        //缩小mainView
        //计算方法
        mMainView.setScaleX(mEvaluator.evaluate(fraction, 1f, 0.85f));
        mMainView.setScaleY(mEvaluator.evaluate(fraction, 1f, 0.85f));
        //移动menuView
        mMenuView.setTranslationX(mIntEvaluator.evaluate(fraction, -mMenuView.getMeasuredWidth() / 2, 0));
        //放大menuView
        mMenuView.setScaleX(mEvaluator.evaluate(fraction, 0.5f, 1f));
        mMenuView.setScaleY(mEvaluator.evaluate(fraction, 0.5f, 1f));
        //改变menuView的透明度
        mMenuView.setAlpha(mEvaluator.evaluate(fraction, 0.3f, 1f));
        //让slide背景添加黑色的遮罩效果
        getBackground().setColorFilter((Integer) ColorUtil.evaluateColor(fraction, Color.BLACK, Color.TRANSPARENT), PorterDuff.Mode.SRC_OVER);


    }

    @Override
    public void computeScroll() {
        if (mViewDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(SlideMenu.this);
        }
    }

    private onDragStateChangeListener mListener;

    public void setOnDragStateChangeListener(onDragStateChangeListener mListener) {
        this.mListener = mListener;
    }

    public interface onDragStateChangeListener {
        /**
         * 打开的回调
         */
        void onOpen();

        /**
         * 关闭的回调
         */
        void onClose();

        /**
         * 拖拽中的回调
         */
        void onDraging(float fraction);
    }
}
