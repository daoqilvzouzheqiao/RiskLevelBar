package com.newtouch.risklevelbar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

import java.util.List;

/** 
 * 功能：滑块view
 * @author xiehj
 * @time 2016/12/23 16:04
 */ 
public class SlideView extends ImageView {
    private int mDownX=0;//按下的x
    private int mWidth;//滑块的宽度
    private Rect rect;
    private boolean mIsMoving;      //游标是否正在移动

    private int mCenterX;   //游标的中心位置

    private int mLeftLimit=0;//滑动范围
    private int mRightLimit= Integer.MAX_VALUE;//默认可以无限向右滑动

    private OnCheckedListener listener;//滑动回调

    private List<Integer> kedu= null;

    public void setKedu(List kedu) {
        this.kedu = kedu;
    }

    private RiskLevelBar mRiskLevel;

    public SlideView(Context context) {
        this(context,null);
    }

    public SlideView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setRiskLevel(RiskLevelBar riskLevel) {
        mRiskLevel = riskLevel;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        mWidth=getMeasuredWidth();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        rect=new Rect(left,top,right,bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        invalidate(rect);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownX = (int) event.getX();
                mIsMoving=false;

                break;
            case MotionEvent.ACTION_MOVE:
                int nowX = (int) event.getX();

                int left = rect.left + nowX - mDownX;
                int right = rect.right + nowX - mDownX;
                mIsMoving=true;
                setCenterX((left+right)/2);

                //滑动过程中刻度变小和变灰
                if(listener!=null){
                    listener.onCheckedChange(-1);
                }

                break;
            case MotionEvent.ACTION_UP:
                mIsMoving=false;
//                rangeSeekBar.invalidate();

                int nowX1 = (int) event.getX();
                int left1 = rect.left + nowX1 - mDownX;
                int right1 = rect.right + nowX1 - mDownX;

                int target = 0;
                int chaj = Math.abs(kedu.get(0)-(left1+right1)/2);
                for(int i=0;i<kedu.size();i++){
                    if(Math.abs(kedu.get(i)-(left1+right1)/2)<chaj){
                        chaj = Math.abs(kedu.get(i)-(left1+right1)/2);
                        target = i;
                    }
                }

                //设置滑块中心位置
                setCenterX(kedu.get(target));

                //将临近的刻度文本变大
                if(listener!=null){
                    listener.onCheckedChange(target);
                }

                break;
        }
        return true;
    }

    /**
     * 设置中心位置，不超过左右的limit，就刷新整个控件，并且回调onThumbChange()
     * @param centerX
     */
    public void setCenterX(int centerX) {
        int left = centerX - mWidth / 2;
        int right = centerX + mWidth / 2;
        if (centerX < mLeftLimit) {
            left = mLeftLimit - mWidth / 2;
            right = mLeftLimit + mWidth / 2;
        }

        if (centerX > mRightLimit) {
            left = mRightLimit - mWidth / 2;
            right = mRightLimit + mWidth / 2;
        }

        this.mCenterX = (left + right) / 2;

        if (left != rect.left || right != rect.right) {
            rect.union(left, rect.top, right, rect.bottom);
            layout(left, rect.top, right, rect.bottom);
            invalidate(rect);
            mRiskLevel.invalidate();

            //            if(listener!=null){
            //                listener.onThumbChange(100*((left+right)/2-mLeftLimit)/(mRightLimit-mLeftLimit));
            //            }
        }
    }

    public void setOnCheckedListener(OnCheckedListener listener) {
        this.listener = listener;
    }

    public interface OnCheckedListener{
        void onCheckedChange(int i);
    }
}
