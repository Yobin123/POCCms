package com.st.cms.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import com.st.cms.entity.VitalSign;
import com.st.cms.utils.DensityUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import cms.st.com.poccms.R;

/**
 * Created by jt on 2016/9/6.
 */
public class SignsBaseView extends View {
    //计算总值
    public int sum = 0;
    //开始时间和结束时间
    public long begin = 0;
    public long end = 0;
    //中的毫秒数间隔
    public long totalInterval = 0;
    //原点的X坐标
    public float XPoint = DensityUtil.dip2px(getContext(), 50);
    //原点的Y坐标
    public float YPoint = DensityUtil.dip2px(getContext(), 50);
    //折线的间隔长度
    public float intervalLength = 0;
    //默认时间间隔1s一次
    public long interval = 1000l;
    //X的刻度长度
    public float XScale = 0;
    //Y的刻度长度
    public float YScale = 0;
    //X轴的长度
    public float XLength = 0;
    //Y轴的长度
    public float YLength = 0;
    //X的刻度
    public String[] XLabel;
    //Y的刻度
    public String[] YLabel;
    //数据
    public String[] Data;
    public float XVal = 0;
    public float YVal = 25;
    //显示的标题
    public String Title;
    public List<VitalSign> signs;
    public Context context;
    public Paint backPaint = new Paint();
    public Paint txtPaint = new Paint();
    //整个view的宽和高
    public int w = 0;
    public int h = 0;

    public SignsBaseView(Context context) {
        this(context, null);
    }

    public SignsBaseView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        initParam();
        measureXPrompt();
        //整个view的宽和高
        w = getWidth();
        h = getHeight();
        XLength = w - DensityUtil.dip2px(getContext(), 100);
        YLength = h - DensityUtil.dip2px(getContext(), 100);
        XScale = XLength / 4;
        YScale = YLength / 5;
        //开始绘制背景图渐变色
        Rect clientRect = new Rect(0,0,w,h);
        if(true)
        {
            int colors[] = new int[3];
            float positions[] = new float[3];

            // 第1个点
            colors[0] = getResources().getColor(R.color.light_green);
            positions[0] = 0;

            // 第2个点
            colors[1] = getResources().getColor(R.color.green_to_blue);
            positions[1] = 0.5f;

            // 第3个点
            colors[2] = getResources().getColor(R.color.dark_blue);
            positions[2] = 1;

            LinearGradient shader = new LinearGradient(
                    0, 0,
                    0, h,
                    colors,
                    positions,
                    Shader.TileMode.MIRROR);
            backPaint.setShader(shader);
        }
        canvas.drawRect(clientRect, backPaint);
        txtPaint.setStyle(Paint.Style.STROKE);
        txtPaint.setTextSize(16);
        txtPaint.setAntiAlias(true);//去锯齿
        txtPaint.setColor(getResources().getColor(R.color.white));//颜色
        //设置Y轴
        //轴线
//        canvas.drawLine(XPoint, YPoint, XPoint, (YPoint + YLength), txtPaint);
        for(int i = 0;i <= 4;i++)
        {
            canvas.drawLine((XPoint + (XScale * i)), YPoint, (XPoint + (XScale * i)), (YPoint + YLength), txtPaint);
            //文字
            canvas.drawText(XLabel[i] , XPoint + i * XScale - DensityUtil.dip2px(getContext(), 15),
                    (YPoint + YLength) + DensityUtil.dip2px(getContext(), 30), txtPaint);
        }
        //设置x轴
        //轴线
//        canvas.drawLine(XPoint, (YPoint + YLength), (XPoint + XLength), (YPoint + YLength), txtPaint);
        for(int i = 0;i <= 5;i++)
        {
            canvas.drawLine(XPoint, ((YPoint + YLength) - (YScale * i)), (XPoint + XLength), ((YPoint + YLength) - (YScale * i)), txtPaint);
            //文字
            canvas.drawText(YLabel[i] , XPoint - DensityUtil.dip2px(getContext(), 30),
                    ((YPoint + YLength) - (YScale * i)) + DensityUtil.dip2px(getContext(), 3), txtPaint);
        }

//        //箭头
//        canvas.drawLine(XPoint, YPoint, XPoint - DensityUtil.dip2px(getContext(), 3), YPoint + DensityUtil.dip2px(getContext(), 6), txtPaint);
//        canvas.drawLine(XPoint, YPoint, XPoint + DensityUtil.dip2px(getContext(), 3), YPoint + DensityUtil.dip2px(getContext(), 6), txtPaint);
//        //箭头
//        canvas.drawLine((XPoint + XLength) - DensityUtil.dip2px(getContext(), 6), (YPoint + YLength - DensityUtil.dip2px(getContext(), 3)), (XPoint + XLength), (YPoint + YLength), txtPaint);
//        canvas.drawLine((XPoint + XLength) - DensityUtil.dip2px(getContext(), 6), (YPoint + YLength + DensityUtil.dip2px(getContext(), 3)), (XPoint + XLength), (YPoint + YLength), txtPaint);


    }

    //计算x轴的时间刻度文字
    public void measureXPrompt()
    {
        begin = signs.get(0).getTimestamp();
        end = signs.get(signs.size() - 1).getTimestamp();
        totalInterval = end - begin;
        long timeScale = totalInterval / 4;
        SimpleDateFormat sdf = new SimpleDateFormat("dd:mm:ss");
        XLabel = new String[]{sdf.format(new Date(begin + timeScale * 0)), sdf.format(new Date(begin + timeScale * 1)),
                sdf.format(new Date(begin + timeScale * 2)), sdf.format(new Date(begin + timeScale * 3)),
                sdf.format(new Date(begin + timeScale * 4))};
    }
    //计算每隔interval时间，x轴的进度
    public void measureIntervalLength()
    {
        intervalLength = (Float.parseFloat(interval + "") / totalInterval) * XLength;
    }
    //当前点
    public class CurrentPoint
    {
        public float x = 0;
        public float y = 0;

        public CurrentPoint() {
        }

        public CurrentPoint(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }

    //绘制顶部的平均值
    public void drawTitle(Canvas canvas, String title)
    {
        txtPaint.setTextSize(30);
        txtPaint.setStrokeWidth(2);
        txtPaint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(title , w / 2,
                DensityUtil.dip2px(getContext(), 20), txtPaint);
    }
    public void initParam()
    {
        //计算总值
        sum = 0;
        //开始时间和结束时间
        begin = 0;
        end = 0;
        //中的毫秒数间隔
        totalInterval = 0;
        //折线的间隔长度
        intervalLength = 0;
        //X的刻度长度
        XScale = 0;
        //Y的刻度长度
        YScale = 0;
        //X轴的长度
        XLength = 0;
        //Y轴的长度
        YLength = 0;
        //X的刻度
        XLabel = null;
        //数据
        Data = null;
        //显示的标题
        Title = null;
        backPaint = new Paint();
        txtPaint = new Paint();
        //整个view的宽和高
        w = 0;
        h = 0;
    }
}
