package com.newtouch.risklevelbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * 功能：自定义的风险等级
 *
 * @author xiehj
 * @time 2016/12/22 14:06
 */
public class RiskLevelBar extends ViewGroup {

    private int mViewWidth = 0;//view的宽度
    private int mViewHeight = 0;//view的高度
    private int leftAndRightMargin = 10;//左右两边的间距,可以用户自定义
    private int mLevelBarHeight = 20;//等级条的高度

    private Drawable mSlide;//滑块背景
    private SlideView mSlideView;//滑块view

    private String degs[]={"1","2","3","4","5","6","7","8","9","10"};      //尺子上标记刻度值
    private List<Integer> kedu= new ArrayList<Integer>();

    private int mCurrentSelected = 0;//默认当前选中1

    private OnCheckedListener listener;//滑动回调

    //创建一个画笔
    private Paint mPaint = new Paint();

    //初始化画笔
    private void initPaint() {
        mPaint.setColor(Color.BLACK);       //设置画笔颜色
        mPaint.setStyle(Paint.Style.FILL);  //设置画笔模式为填充
        mPaint.setStrokeWidth(10f);         //设置画笔宽度为10px
    }

    public RiskLevelBar(Context context) {
        this(context, null);
    }

    public RiskLevelBar(Context context, AttributeSet attrs) {
        super(context, attrs);

        /**
         * 获得我们所定义的自定义样式属性
         */
        TypedArray a = context.obtainStyledAttributes(attrs,R.styleable.RiskLevelBar);
        leftAndRightMargin = a.getDimensionPixelSize(R.styleable.RiskLevelBar_leftAndRightMargin,10);//左右间距
        mLevelBarHeight = a.getDimensionPixelSize(R.styleable.RiskLevelBar_barHeight,20);//bar高度

        a.recycle();

        initPaint();
        initView();
        initListener();
    }

    /**
     * 初始化滑块并添加
     */
    private void initView() {
        mSlide=getResources().getDrawable(R.drawable.btn_slider);

        mSlideView = new SlideView(getContext());
        mSlideView.setRiskLevel(this);
        mSlideView.setImageDrawable(mSlide);
        //添加滑块
        addView(mSlideView);
    }

    /**
     * 初始化滑块事件
     */
    private void initListener() {
        mSlideView.setOnCheckedListener(new SlideView.OnCheckedListener() {
            @Override
            public void onCheckedChange(int i) {
                mCurrentSelected = i;
                invalidate();

                if(listener!=null){
                    listener.onCheckedChange(i);
                }
            }
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measureChildren(widthMeasureSpec,heightMeasureSpec);    //测量子控件

//        mViewWidth = getMySize(100, widthMeasureSpec);
//        mViewHeight = getMySize(100, heightMeasureSpec);

        //默认测量模式为EXACTLY，否则请使用上面的方法并指定默认的宽度和高度
        mViewWidth = MeasureSpec.getSize(widthMeasureSpec);
        mViewHeight = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(mViewWidth, mViewHeight);

        initDegree();
    }

    /**
     * 初始化刻度值 对应的位置信息
     */
    private void initDegree() {
        for(int i=0;i<degs.length;i++) {
            float degX = i * (mViewWidth-leftAndRightMargin*2)/10 + (mViewWidth-leftAndRightMargin*2)/10/ 2 + leftAndRightMargin;
            kedu.add((int)degX);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        //设置滑块在父布局的位置
        mSlideView.layout(kedu.get(mCurrentSelected)-mSlideView.getMeasuredWidth()/2,mViewHeight/2-mSlideView.getMeasuredHeight()/2,kedu.get(mCurrentSelected)+mSlideView.getMeasuredWidth()/2,mViewHeight/2+mSlideView.getMeasuredHeight()/2);      //设置在父布局的位置
    }

    private int getMySize(int defaultSize, int measureSpec) {
        int mySize = defaultSize;

        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);

        switch (mode) {
            case MeasureSpec.UNSPECIFIED: {//如果没有指定大小，就设置为默认大小
                mySize = defaultSize;
                break;
            }
            case MeasureSpec.AT_MOST: {//如果测量模式是最大取值为size
                //我们将大小取最大值,你也可以取其他值
                mySize = size;
                break;
            }
            case MeasureSpec.EXACTLY: {//如果是固定的大小，那就不要去改变它
                mySize = size;
                break;
            }
        }
        return mySize;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawRiskLevelBar(canvas);//画条形bar

        drawRule(canvas);//画刻度等

        mSlideView.setKedu(kedu);
    }

    /**
     * 画风险等级条
     * @param canvas
     */
    private void drawRiskLevelBar(Canvas canvas) {
        //渐变色
        LinearGradient linearGradient = new LinearGradient(0, 0, getMeasuredWidth(), 0,new int[]{getResources().getColor(R.color.start),getResources().getColor(R.color.end)}, null, LinearGradient.TileMode.CLAMP);
        mPaint.setShader(linearGradient);

        //矩形左上角和右下角的点的坐标
        RectF rectF = new RectF(leftAndRightMargin,mViewHeight/2-mLevelBarHeight/2,mViewWidth-leftAndRightMargin,mViewHeight/2+mLevelBarHeight/2);
        canvas.drawRoundRect(rectF, 4, 4, mPaint);
    }

    /**
     * 画刻度尺
     * @param canvas
     */
    protected void drawRule(Canvas canvas){

        int LINE_HEIGHT=25; //刻度线的高度
        int radius = 10;//下边圆的半径

        int mPartWidth = (mViewWidth-leftAndRightMargin*2)/10;//每个尺度的宽度

        Paint paint=new Paint();
        paint.setStrokeWidth(1);
        paint.setColor(getResources().getColor(R.color.gray));
        paint.setTextSize(20);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setAntiAlias(true);

        //绘制 文本 线 圆形
        for(int i=0;i<degs.length;i++){

            float degX=i*mPartWidth+mPartWidth/2+leftAndRightMargin;

            if(i==mCurrentSelected){
                Paint paint1=new Paint();
                paint1.setStrokeWidth(1);
                paint1.setColor(getResources().getColor(R.color.end));
                paint1.setTextSize(30);
                paint1.setTextAlign(Paint.Align.CENTER);
                paint1.setAntiAlias(true);
                canvas.drawText(degs[i],degX,mViewHeight/2-mLevelBarHeight/2-LINE_HEIGHT-18,paint1);//画1~10等级的文字
            }else{
                canvas.drawText(degs[i],degX,mViewHeight/2-mLevelBarHeight/2-LINE_HEIGHT-18,paint);//画1~10等级的文字
            }

            canvas.drawLine(degX,mViewHeight/2-mLevelBarHeight/2-LINE_HEIGHT-8,degX,mViewHeight/2-mLevelBarHeight/2-8,paint);//画上面的竖线
            canvas.drawLine(degX,mViewHeight/2+mLevelBarHeight/2+8,degX,mViewHeight/2+mLevelBarHeight/2+LINE_HEIGHT+8,paint);//画下面的竖线

            paint.setColor(getResources().getColor(R.color.circle01+i));
            canvas.drawCircle(degX,mViewHeight/2+mLevelBarHeight/2+LINE_HEIGHT+28,radius,paint);//画下面的实心圆圈
            paint.setColor(getResources().getColor(R.color.gray));
        }
    }

    /**
     * 设置选中的等级
     * @param target   1.2...10
     */
    public void setCurrentSelected(int target){
        if(target<=10&&target>=1){
            mCurrentSelected=target-1;

            mSlideView.layout(kedu.get(mCurrentSelected)-mSlideView.getMeasuredWidth()/2,mViewHeight/2-mSlideView.getMeasuredHeight()/2,kedu.get(mCurrentSelected)+mSlideView.getMeasuredWidth()/2,mViewHeight/2+mSlideView.getMeasuredHeight()/2);      //设置在父布局的位置

            if(listener!=null){
                listener.onCheckedChange(mCurrentSelected);
            }
        }else{
            Toast.makeText(this.getContext(), "目标的不在范围", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 获取当前选中的等级  1,2,。。。10
     * @return
     */
    public int getCurrentSelected(){
        return mCurrentSelected +1 ;
    }

    public void setOnCheckedListener(OnCheckedListener listener) {
        this.listener = listener;
    }

    public interface OnCheckedListener{
        void onCheckedChange(int i);
    }
}
