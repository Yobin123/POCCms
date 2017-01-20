package com.st.cms.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import cms.st.com.poccms.R;

/**
 * Created by CW on 2016/9/5.
 */
public class StatesView extends View {
    private Paint axisLinePaint;//坐标轴轴线的画笔
    private Paint hLinePaint;//坐标轴水平内部虚线
    private Paint titlePaint;//绘制文本画笔
    private Paint recPaint; //矩形画笔，柱状图的样式信息
    private Paint incitePaint;//指示gcs的总和
    private Paint axisNamePaint;//坐标名称；
    private Paint borderPaint;//
    private int step;
    private int[] eyeOpening;
    private int[] verbalResponse;
    private int[] motorResponse;
    private String[] yTitles = new String[]{"16", "14", "12", "10", "8", "6", "4", "2", "0"};
    private String[] xTitles;

    public StatesView(Context context) {
        super(context);
        init(context, null);

    }

    public StatesView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        axisLinePaint = new Paint();
        hLinePaint = new Paint();
        titlePaint = new Paint();
        recPaint = new Paint();
        axisNamePaint = new Paint();
        incitePaint = new Paint();
        borderPaint = new Paint();

        //设置相应的画笔颜色
        axisLinePaint.setColor(Color.LTGRAY);
        hLinePaint.setColor(Color.LTGRAY);
        titlePaint.setColor(Color.BLACK);
        axisNamePaint.setColor(Color.BLACK);
        axisNamePaint.setTextSize(14);
        Typeface font = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD);
        axisNamePaint.setTypeface(font);

        borderPaint.setColor(Color.LTGRAY);
    }

    public void updateOpenData(int[] openData, String[] time) {
        eyeOpening = openData;
        xTitles = time;
        this.postInvalidate();//可以在子线程中进行相应的更新
    }

    public void updateverbalData(int[] verbalData, String[] time) {
        verbalResponse = verbalData;
        xTitles = time;
        this.postInvalidate();//可以在子线程中进行相应的更新
    }

    public void updateMotorData(int[] motorData, String[] time) {
        motorResponse = motorData;
        xTitles = time;
        this.postInvalidate();//可以在子线程中进行相应的更新
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();

        //1.绘制坐标线
        //①绘制y轴
        canvas.drawLine(100, 30, 100, 370, axisLinePaint);
        //②绘制x轴
        canvas.drawLine(100, 370, width - 100, 370, axisLinePaint);

        //2.绘制坐标内部的水平线
        int leftHeight = 320;
        int hPerHight = leftHeight / 8;

        for (int i = 0; i < 8; i++) {
            canvas.drawLine(100, 50 + i * hPerHight, width - 100, 50 + i * hPerHight, hLinePaint);
        }

        //3.绘制y轴的坐标点
        Paint.FontMetrics metrics = titlePaint.getFontMetrics();
        float descent = metrics.descent;
        titlePaint.setTextAlign(Paint.Align.RIGHT);
        for (int i = 0; i < yTitles.length; i++) {
            canvas.drawText(yTitles[i], 80, 50 + i * hPerHight + descent, titlePaint);
        }


        //4.绘制x轴的坐标点
        if (xTitles != null && xTitles.length > 0) {
            int xAxisLength = width - 150;
            int columCount = xTitles.length + 1;
            int step1 = xAxisLength / columCount;
            step = step1;
            for (int i = 0; i < xTitles.length; i++) {
                if (i==0){
                    canvas.drawText(xTitles[i], 100 + step * (i + 1) -50, 390, titlePaint);

                }else if (i==1){
                    canvas.drawText(xTitles[i], 100 + step * (i + 1) -40, 390, titlePaint);

                }else {
                    canvas.drawText(xTitles[i], 100 + step * (i + 1)-50, 390, titlePaint);

                }
            }
        }

        //y轴的标题
        canvas.drawText("Points", 80, 20, axisNamePaint);
        canvas.drawText("Time", width - 90, 370, axisNamePaint);


        //绘制motorresponse矩形
        if (motorResponse != null && motorResponse.length > 0) {
            int eyeOpenCount = motorResponse.length;
            for (int i = 0; i < eyeOpenCount; i++) {
                int value = motorResponse[i];
                int num = 16 - value;//这是确定剩余的高度
                recPaint.setColor(getResources().getColor(R.color.motor_response_bg));

                Rect rect = new Rect();
                rect.left = 100 + step * (i + 1) - 30 - 80;
                rect.right = 100 + step * (i + 1) + 30 - 80;

                //当前的相对高度
                int rh = (leftHeight * num) / 16;
                rect.top = rh + 50;
                rect.bottom = 370;
                canvas.drawRect(rect, recPaint);
                //绘制相应的gcs的背景
                Rect rect_boder = new Rect();
                rect_boder.left = 100 + step * (i + 1) - 30 - 80;
                rect_boder.right = 100 + step * (i + 1) + 30 - 80;
                rect_boder.top = rh + 10;
                rect_boder.bottom = rh + 40;
                canvas.drawRect(rect_boder, borderPaint);
                //绘制相应的gcs总和信息
                canvas.drawText(String.valueOf(value), 100 + step * (i + 1) - 85, rh + 30, incitePaint);
//                //给相应的gcs画边框
//                //①画两条垂直线
//               canvas.drawLine(100+step*(i+1)-30-80,rh+10,100+step*(i+1)-30-80,rh+40,borderPaint);
//               canvas.drawLine(100+step*(i+1)+30-80,rh+10,100+step*(i+1)+30-80,rh+40,borderPaint);
//               //②画水平线
//                canvas.drawLine(100+step*(i+1)-30-80,rh+10,100+step*(i+1)+30-80,rh+10,borderPaint);
//                canvas.drawLine(100+step*(i+1)-30-80,rh+40,100+step*(i+1)+30-80,rh+40,borderPaint);

            }
        }
        //绘制verbalresponse矩形
        if (verbalResponse != null && verbalResponse.length > 0) {
            int eyeOpenCount = verbalResponse.length;
            for (int i = 0; i < eyeOpenCount; i++) {
                int value = verbalResponse[i];
                int num = 16 - value;//这是确定剩余的高度
                recPaint.setColor(getResources().getColor(R.color.verbal_response_bg));


                Rect rect = new Rect();
                rect.left = 100 + step * (i + 1) - 30 - 80;
                rect.right = 100 + step * (i + 1) + 30 - 80;

                //当前的相对高度
                int rh = (leftHeight * num) / 16;
                rect.top = rh + 50;
                rect.bottom = 370;

                canvas.drawRect(rect, recPaint);
            }
        }
        //绘制eyeopening的矩形,
        if (eyeOpening != null && eyeOpening.length > 0) {
            int eyeOpenCount = eyeOpening.length;
            for (int i = 0; i < eyeOpenCount; i++) {
                int value = eyeOpening[i];
                int num = 16 - value;//这是确定剩余的高度
                recPaint.setColor(getResources().getColor(R.color.eye_open_bg));

                Rect rect = new Rect();
                rect.left = 100 + step * (i + 1) - 30 - 80;
                rect.right = 100 + step * (i + 1) + 30 - 80;

                //当前的相对高度
                int rh = (leftHeight * num) / 16;

                rect.top = rh + 50;
                rect.bottom = 370;

                canvas.drawRect(rect, recPaint);
            }
        }


    }
}
