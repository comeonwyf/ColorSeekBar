package com.example.colorseekbarlibrary;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wuyufeng    on  2018/9/7 0007.
 * interface by
 */

public class ColorSeekBar extends View {

    private final String TAG="ColorSeekBar";
    private final Paint paint = new Paint();
    private final Path sPath = new Path();
    private List<int[]> mColors = new ArrayList<>();
    private float sLeft, sTop, sRight, sBottom;
    private float sWidth,sHeight;

    private float x,y;
    private float mRadius;
    private float  mProgress;
    private OnStateChangeListener onStateChangeListener;
    private int mStartColor;
    private int mEndColor;
    private int mThumbColor = Color.WHITE;
    private int mThumbBorderColor;
    private int[] mColorArray = new int[2];
    private int mDivideCount = 1;
    private boolean mGradient = false;
    private int borderWidth = 2;
    private boolean mCanMove = true;

    public ColorSeekBar(Context context) {
        this(context,null);
    }

    public ColorSeekBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public ColorSeekBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setLayerType(LAYER_TYPE_SOFTWARE, null);
        //默认的起始颜色
        mStartColor = ContextCompat.getColor(context,R.color.colorSeekBar_start_color);
        //默认的结束颜色
        mEndColor = ContextCompat.getColor(context,R.color.colorSeekBar_end_color);
        
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ColorSeekBar);
        //是否可拖动
        mGradient = ta.getBoolean(R.styleable.ColorSeekBar_can_move, true);
        
        mStartColor = ta.getColor(R.styleable.ColorSeekBar_seekbar_start_color,mStartColor);
        
        mEndColor = ta.getColor(R.styleable.ColorSeekBar_seekbar_end_color,mEndColor);
        
        mColorArray[0] = mStartColor;
        mColorArray[1] = mEndColor;
        
        ta.recycle();
    }

    /**
     * 需要渐变的时候调用
     * 一个线段需要对应2个颜色值（起始和终止颜色） 例如 divideCount = 2,则需要设置2种颜色数组
     * @param divideCount
     * @param colors
     */
    public void setColorByGadient(int divideCount,boolean canMove,int progerss,int[]... colors){
        mColors.clear();
        for (int[] color : colors) {
            mColors.add(color);
        }

        if(divideCount != mColors.size()){
            Log.e(TAG,"一个线段需要对应2个颜色值（起始和终止颜色） 例如 divideCount = 2,则需要设置2种颜色数组");
            String excep = null;
            excep.length();
        }
        mDivideCount = divideCount;
        mGradient = true;//是渐变
        mCanMove = canMove;
        mProgress = progerss;
    }

    /**
     * 不是渐变的时候调用
     * 一个线段需要对应2个颜色值（起始和终止颜色） 例如 divideCount = 2,则需要设置2种颜色
     * @param divideCount
     * @param colors
     */
    public void setColor(int divideCount,boolean canMove,int progerss,int... colors){
        mColors.clear();
        for (int color : colors) {
            int[] colorArray = new int[]{color,color};
            mColors.add(colorArray);
        }

        if(divideCount != mColors.size()){
            Log.e(TAG,"一个线段需要对应1个颜色值， 例如 divideCount = 2,则需要设置2种颜色");
            String excep = null;
            excep.length();
        }
        mDivideCount = divideCount;
        mGradient = false;//不是渐变
        mCanMove = canMove;
        mProgress = progerss;
    }
    

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec * 2);
        setMeasuredDimension(widthSize,heightSize);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mRadius=h-borderWidth*2;
        
        sLeft = 0; // 背景左的坐标
        sTop = h*0.25f;//top位置
        sRight = w; // 背景的宽的全部
        sBottom = h*0.75f; // 背景底部
        sWidth = sRight - sLeft; // 背景的宽度
        sHeight = sBottom - sTop; // 背景的高度
        
        
        x = mProgress/100*sWidth;

        //RectF sRectF = new RectF(sLeft, sTop, sBottom, sBottom);
        //sPath.arcTo(sRectF, 90, 180);
        //sRectF.left = sRight - sBottom;
        //sRectF.right = sRight;
        //sPath.arcTo(sRectF, 270, 180);
        //sPath.close();    // path准备背景的路径
        
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBackground(canvas);
        drawCircle(canvas);
        paint.reset();
    }
    
    public boolean onTouchEvent(MotionEvent event) {
        if(!mCanMove){
            return true;
        }
        this.x = event.getX();
        float length = x-mRadius<0?0:x-mRadius;
        float width = length-(sWidth-mRadius*2)>0?length:sWidth-mRadius*2;
        mProgress =length/width*100;
        switch(event.getAction()) {

            case 0://ACTION_DOWN

                Log.i(TAG, "onTouchEvent: x: "+x+" y: "+y +" max : "+event.getSize()+" "+" "+sWidth);
                this.invalidate();
                break;
            case 1://ACTION_UP
                if (onStateChangeListener!=null){
                    onStateChangeListener.onStopTrackingTouch(mProgress);
                }
                break;
            case 2://ACTION_MOVE

                if (onStateChangeListener!=null){
                    onStateChangeListener.OnStateChangeListener(mProgress);
                }
                this.invalidate();
                break;
        }
        return true;
    }

    private void drawCircle(Canvas canvas){
        Paint thumbPaint = new Paint();
        thumbPaint.setAntiAlias(true);
        x =x<(mRadius/2)?(mRadius/2)+borderWidth:x;//判断thumb边界
        x=x>sWidth-mRadius/2?sWidth-mRadius/2-borderWidth:x;
        
        int index = (int) ((x-1)/(sWidth/mDivideCount));
        int[] colors = mColors.get(index);
        mThumbBorderColor = colors[1];
        
        thumbPaint.setStyle(Paint.Style.FILL);
        thumbPaint.setColor(mThumbColor);
        canvas.drawCircle(x, mRadius / 2 +borderWidth, mRadius / 2, thumbPaint);
        thumbPaint.setStyle(Paint.Style.STROKE);
   
        thumbPaint.setColor(mThumbBorderColor);
        thumbPaint.setStrokeWidth(borderWidth);
        canvas.drawCircle(x, mRadius / 2+borderWidth, mRadius / 2, thumbPaint);
    }

    private void drawBackground(Canvas canvas){
        float length = sWidth/mDivideCount;
        mDivideCount = mColors.size();
        for(int i = 1;i<=mDivideCount;i++){
            float left = sLeft+length*(i-1);
            float top = sTop;
            float width = length*i;
            float height = sHeight;
            int[] colorArray;
            if(mColors.size()==0){
                colorArray = mColorArray;
            }else {
                colorArray = mColors.get(i-1);
            }
            
            if(i==1){
                RectF sRectF = new RectF(left, sTop, sBottom, sBottom);
                sPath.arcTo(sRectF, 90, 180);
                sRectF.left = sBottom/2;
                sRectF.right = width;
                sPath.addRect(sRectF, Path.Direction.CW);
            }else if(i == mDivideCount){
                RectF sRectF = new RectF(left, sTop, width-sBottom/2, sBottom);
                sPath.addRect(sRectF, Path.Direction.CW);
                sRectF.left = width-sBottom;
                sRectF.right = width;
                sPath.arcTo(sRectF, 270, 180);
            }else {
                RectF sRectF = new RectF(left, sTop, width, sBottom);
                sPath.addRect(sRectF, Path.Direction.CCW);
            }
            
            LinearGradient linearGradient = new LinearGradient(left,top,width,height, colorArray,null, Shader.TileMode.REPEAT);
            paint.setAntiAlias(true);
            paint.setStyle(Paint.Style.FILL);
            //设置渲染器
            paint.setShader(linearGradient);
            canvas.drawPath(sPath, paint);
            paint.reset();
            sPath.reset(); 
            
        }
     
    }

    public interface OnStateChangeListener{
        void OnStateChangeListener(float progress);
        void onStopTrackingTouch(float progress);
    }
    
    public void setOnStateChangeListener(OnStateChangeListener onStateChangeListener1){
        this.onStateChangeListener = onStateChangeListener1;
    }

}
