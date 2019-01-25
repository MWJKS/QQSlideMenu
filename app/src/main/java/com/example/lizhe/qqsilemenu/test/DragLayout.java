package com.example.lizhe.qqsilemenu.test;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.example.lizhe.qqsilemenu.ColorUtil;


/**
 * 如果对子控件没有特殊的测量需求可以继承FrameLayout
 */
public class DragLayout extends FrameLayout {


    private View mRedView;
    private View mBlueView;
    private ViewDragHelper mViewDragHelper;

    public DragLayout(Context context) {
        super(context);
        init();
    }


    /**
     * 落了这个构造器哦
     *
     * @param context
     * @param attrs
     */
    public DragLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mViewDragHelper = ViewDragHelper.create(this, callback);
    }

    /**
     * 当DragLayout的xml布局的结束标签被读取完成会执行该方法，此时会知道自己有几个子View了
     * 一般是用来初始化子View的引用
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        //获得第一个View
        mRedView = getChildAt(0);
        mBlueView = getChildAt(1);
    }

    /**
     * 设置空间宽高
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        //要测量自己的子View
////        int size=MeasureSpec.makeMeasureSpec(R.dimen.width)
//        //第一个参数传长度，第二个参数是数据模式（精确的值）
////        int measureSpec = MeasureSpec.makeMeasureSpec(mRedView.getLayoutParams().width, MeasureSpec.EXACTLY);
////        mRedView.measure(measureSpec, measureSpec);
////        mBlueView.measure(measureSpec, measureSpec);
//
//        //第一个传控件，第二个传父布局的测量规格
//        measureChild(mRedView, widthMeasureSpec, heightMeasureSpec);
//        measureChild(mBlueView, widthMeasureSpec, heightMeasureSpec);
//    }

    /**
     * 实现控件的位置
     *
     * @param changed
     * @param l       左边
     * @param t       上边
     * @param r       右边
     * @param b
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int left = 0;
        int top = getPaddingTop();
        mRedView.layout(left, top, left + mRedView.getMeasuredWidth(), top + mRedView.getMeasuredHeight());
        mBlueView.layout(left, mRedView.getBottom(), left + mBlueView.getMeasuredWidth(), mRedView.getBottom() + mBlueView.getMeasuredHeight());
    }


    @Override
    public boolean onInterceptHoverEvent(MotionEvent event) {
        //让ViewDragHelper帮我们判断是否拦截
        boolean result = mViewDragHelper.shouldInterceptTouchEvent(event);
        return result;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //将触摸事件交给ViewDragHelper来解析处理
        mViewDragHelper.processTouchEvent(event);
        return true;
    }


    /**
     * 控件移动的接口
     */
    private ViewDragHelper.Callback callback = new ViewDragHelper.Callback() {

        /**
         * 用于判断是否捕获当前child的触摸事件
         * @param view  当前触摸的子View
         * @param i  true（捕获并解析） false（不处理）
         * @return
         */
        @Override
        public boolean tryCaptureView(@NonNull View view, int i) {
            return view == mBlueView || view == mRedView;
        }

        /**
         * 当View被开始捕获和解析的回调
         * @param capturedChild 当前被捕获的子View
         * @param activePointerId
         */
        @Override
        public void onViewCaptured(@NonNull View capturedChild, int activePointerId) {
            super.onViewCaptured(capturedChild, activePointerId);
            Log.e("tag", "onViewCaptured");
        }

        /**
         * 获取View在水平方向上的拖拽范围,目前不能限制边界，返回的值目前用在手指抬起的时候view缓慢移动的动画时间计算上面
         * 最好不要反回0
         * @param child
         * @return
         */
        @Override
        public int getViewHorizontalDragRange(@NonNull View child) {
            return getMeasuredWidth() - child.getMeasuredWidth();
        }

        /**
         * 获取View在竖直方向上的拖拽范围，最好不要反回0
         * @param child
         * @return
         */
        @Override
        public int getViewVerticalDragRange(@NonNull View child) {
            return super.getViewVerticalDragRange(child);
        }

        /**
         * 控制child在水平方线的移动
         * @param child
         * @param left  表示ViewDragHelper认为你想让当前child的left改变的值,left=chile.getLeft()+dx
         * @param dx  本次child水平方向上移动的距离
         * @return 表示你想让child的left变成的值
         */
        @Override
        public int clampViewPositionHorizontal(@NonNull View child, int left, int dx) {
            if (left < 0) {
                left = 0;
            } else if (left > getMeasuredWidth() - child.getMeasuredWidth()) {
                left = getMeasuredWidth() - child.getMeasuredWidth();
            }
            return left;
//         return left-dx; 表示不会移动
        }

        /**
         * 控制View在竖直方向上移动
         * @param child  表示ViewDragHelper认为你想让当前child的top改变的值,top=chile.getTop()+dy
         * @param top  本次child竖直方向上移动的距离
         * @param dy  表示你想让child的top变成的值
         * @return
         */
        @Override
        public int clampViewPositionVertical(@NonNull View child, int top, int dy) {
            if (top < 0) {
                top = 0;
            } else if (top > getMeasuredHeight() - child.getMeasuredHeight()) {
                top = getMeasuredHeight() - child.getMeasuredHeight();
            }
            return top;
        }

        /**
         * 当child的位置改变的时候执行，一般用来做其他子view的跟随移动
         * @param changedView  位置改变的child
         * @param left  child当前最新的left
         * @param top  child当前最新的top
         * @param dx  本次移动的水平距离
         * @param dy  本次移动的竖直距离
         */
        @Override
        public void onViewPositionChanged(@NonNull View changedView, int left, int top, int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);
            if (changedView == mBlueView) {
                //mBlueView移动的时候让mRedView跟随移动
                mRedView.layout(mRedView.getLeft() + dx, mRedView.getTop() + dy, mRedView.getRight() + dx, mRedView.getBottom() + dy);
            } else if (changedView == mRedView) {
                mBlueView.layout(mBlueView.getLeft() + dx, mBlueView.getTop() + dy, mBlueView.getRight() + dx, mBlueView.getBottom() + dy);
            }

            //计算view移动的百分比
            float fraction=changedView.getLeft()*1f/(getMeasuredWidth()-changedView.getMeasuredWidth());
            //执行一系列的伴随动画
            executeAnim(fraction);
        }

        /**
         * 手指抬起的执行该方法
         * @param releasedChild  当前抬起的view
         * @param xvel  x方向移动的速度  正：向右边移动， 负：向左移动
         * @param yvel  y方向移动的速度
         */
        @Override
        public void onViewReleased(@NonNull View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);
            int centerLeft=getMeasuredWidth()/2-releasedChild.getMeasuredWidth()/2;
            if (releasedChild.getLeft()<centerLeft)
            {
                //说明在左半边，应该向左缓慢移动
                mViewDragHelper.smoothSlideViewTo(releasedChild,0,releasedChild.getTop());
                ViewCompat.postInvalidateOnAnimation(DragLayout.this);
            }else{
                //说明在右半边，应该向右缓慢移动
                mViewDragHelper.smoothSlideViewTo(releasedChild,getMeasuredWidth()-releasedChild.getMeasuredWidth(),releasedChild.getTop());
                ViewCompat.postInvalidateOnAnimation(DragLayout.this);
            }

        }

    };

    /**
     * 动画
     * @param fraction
     */
    private void executeAnim(float fraction) {
        //缩放效果
//        mRedView.setScaleX(1+0.5f*fraction);
//        mRedView.setScaleY(1+0.5f*fraction);

        //旋转效果
//        mRedView.setRotation(360*fraction);//围绕z轴旋转
//        mRedView.setRotationX(360*fraction);//围绕x轴
//        mRedView.setRotationY(360*fraction);//围绕y轴旋转

        //平移
//        mRedView.setTranslationX(80*fraction);

        //透明
//        mRedView.setAlpha(1-fraction);

        //设置过度颜色的渐变
        mRedView.setBackgroundColor((Integer) ColorUtil.evaluateColor(fraction,Color.RED,Color.GREEN));
    }


    @Override
    public void computeScroll() {
        if (mViewDragHelper.continueSettling(true))
        {
            ViewCompat.postInvalidateOnAnimation(DragLayout.this);
        }
    }


}
