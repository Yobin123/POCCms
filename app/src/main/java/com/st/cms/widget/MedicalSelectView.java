package com.st.cms.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.widget.EditText;

import com.st.cms.utils.DensityUtil;

import cms.st.com.poccms.R;

/**
 * Created by jt on 2016/8/30.
 */
public class MedicalSelectView extends EditText{

//    pfloat oldY = 0;
    //文本颜色
    public int mtv_textColor;
    //文本内容
    public String mtv_text;
    //文本字体大小
    public int mtv_textSize;
    //背景颜色
    public int mtv_backgroundColor;
    //背景图片
    public int mtv_src;
    /**
     * 绘制时控制文本绘制的范围
     */
    private Rect mBound;
    private Paint mPaint;

    public MedicalSelectView(Context context) {
        this(context, null);
    }
    public MedicalSelectView(Context context, AttributeSet attrs) {
        //这里构造方法也很重要，不加这个很多属性不能再XML里面定义
        this(context, attrs, 0);
    }

    public MedicalSelectView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initData();
        /**
         * 获得我们所定义的自定义样式属性
         */
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.MedicalSelectView, defStyleAttr, 0);
        //获取参数个数
        int n = a.getIndexCount();
        for (int i = 0; i < n; i++)
        {
            int attr = a.getIndex(i);
            switch (attr)
            {
                case R.styleable.MedicalSelectView_mtv_text:
                    mtv_text = getResources().getString(a.getResourceId(attr, R.string.default_text));
                    break;
                case R.styleable.MedicalSelectView_mtv_textSize:
                    // 默认设置为16sp，TypeValue也可以把sp转化为px
                    mtv_textSize = a.getDimensionPixelSize(attr, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                            16, getResources().getDisplayMetrics()));
                    break;
                case R.styleable.MedicalSelectView_mtv_textColor:
                    mtv_textColor = getResources().getColor(a.getResourceId(attr, R.color.lime));
                    break;
                case R.styleable.MedicalSelectView_mtv_backgroundColor:
                    mtv_backgroundColor = getResources().getColor(a.getResourceId(attr, R.color.switch_thumb_normal_material_light));
                    break;
                case R.styleable.MedicalSelectView_mtv_src:
                    mtv_src = a.getResourceId(attr, R.mipmap.dropdown_ic_arrow_normal_holo_dark);
                    break;
                default:
                    break;
            }
        }
        a.recycle();
        /**
         * 获得绘制文本的宽和高
         */
        mPaint = new Paint();
        mPaint.setTextSize(mtv_textSize);
        mBound = new Rect();
        mPaint.getTextBounds(mtv_text, 0, mtv_text.length(), mBound);
    }

    //设置参数的默认值
    public void initData()
    {
        mtv_textColor = getResources().getColor(R.color.lime);
        mtv_text = getResources().getString(R.string.default_text);
        mtv_textSize = DensityUtil.dip2px(getContext(), 16);
        mtv_backgroundColor = getResources().getColor(R.color.switch_thumb_normal_material_light);
        mtv_src = R.mipmap.dropdown_ic_arrow_normal_holo_dark;
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int width;
        int height ;
        if (widthMode == MeasureSpec.EXACTLY)
        {
            width = widthSize;
        } else
        {
            mPaint.setTextSize(mtv_textSize);
            mPaint.getTextBounds(mtv_text, 0, mtv_text.length(), mBound);
            float textWidth = mBound.width();
            int desired = (int) (getPaddingLeft() + textWidth + getPaddingRight());
            width = desired;
        }

        if (heightMode == MeasureSpec.EXACTLY)
        {
            height = heightSize;
        } else
        {
            mPaint.setTextSize(mtv_textSize);
            mPaint.getTextBounds(mtv_text, 0, mtv_text.length(), mBound);
            float textHeight = mPaint.getFontMetrics().bottom - mPaint.getFontMetrics().top;
            textHeight = Math.max((mBound.height() + mBound.bottom - mBound.top), textHeight);
            int desired = (int) (getPaddingTop() + textHeight + getPaddingBottom());
            height = desired;
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mPaint.setColor(mtv_backgroundColor);
        canvas.drawRect(0, 0, getMeasuredWidth(), getMeasuredHeight(), mPaint);
        mPaint.setColor(mtv_textColor);
        if(this.getText() == null || this.getText().length() == 0)
        {
            canvas.drawText(mtv_text, getWidth() / 2 - mBound.width() / 2, getHeight() / 2 + mBound.height() / 2, mPaint);
        }
        canvas.drawLine(0, getMeasuredHeight() - 1, getMeasuredWidth(), getMeasuredHeight() - 1, mPaint);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), mtv_src);
        int bitmapHeight = bitmap.getHeight();
        int bitmapWidth = bitmap.getWidth();
        drawImage(canvas, bitmap, (getMeasuredWidth() - bitmapWidth), (getMeasuredHeight() - bitmapHeight));
        super.onDraw(canvas);
    }
    /*---------------------------------
     * 绘制图片
     * @param       x屏幕上的x坐标
     * @param       y屏幕上的y坐标
     * @param       w要绘制的图片的宽度
     * @param       h要绘制的图片的高度
     * @param       bx图片上的x坐标
     * @param       by图片上的y坐标
     *
     * @return      null
     ------------------------------------*/
    public void drawImage(Canvas canvas, Bitmap blt, int x, int y,
                          int w, int h, int bx, int by) {
        Rect src = new Rect();// 图片 >>原矩形
        Rect dst = new Rect();// 屏幕 >>目标矩形

        src.left = bx;
        src.top = by;
        src.right = bx + w;
        src.bottom = by + h;

        dst.left = x;
        dst.top = y;
        dst.right = x + w;
        dst.bottom = y + h;
        // 画出指定的位图，位图将自动--》缩放/自动转换，以填补目标矩形
        // 这个方法的意思就像 将一个位图按照需求重画一遍，画后的位图就是我们需要的了
        canvas.drawBitmap(blt, null, dst, null);
        src = null;
        dst = null;
    }
    /**
     * 绘制一个Bitmap
     *
     * @param canvas 画布
     * @param bitmap 图片
     * @param x 屏幕上的x坐标
     * @param y 屏幕上的y坐标
     */

    public static void drawImage(Canvas canvas, Bitmap bitmap, int x, int y) {
        // 绘制图像 将bitmap对象显示在坐标 x,y上
        canvas.drawBitmap(bitmap, x, y, null);
    }

//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        switch (event.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                Log.e("TEST", "action down");
//                oldY = event.getY();
//                requestFocus();
//                break;
//            case MotionEvent.ACTION_MOVE:
//                float newY = event.getY();
//                if (Math.abs(oldY - newY) > 20) {
//                    clearFocus();
//                }
//                break;
//            case MotionEvent.ACTION_UP:
//                break;
//            default:
//                break;
//        }
//        return super.onTouchEvent(event);
//    }
}
