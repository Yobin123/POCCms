package com.st.cms.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.st.cms.entity.VitalSign;
import com.st.cms.utils.DensityUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import cms.st.com.poccms.R;

/**
 * Created by jt on 2016/9/5.
 */
public class BPMView extends SignsBaseView{

    public BPMView(Context context) {
        this(context, null);
    }

    public BPMView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        YLabel = new String[]{"50", "55", "60", "65", "70", "75"};
    }

    public void setSigns(List<VitalSign> signs) {
        this.signs = signs;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBrokenLine(canvas);
        drawTitle(canvas, "BPM " + (sum / signs.size()));
    }
    //绘制折线
    public void drawBrokenLine(Canvas canvas)
    {
        //开始绘制折线
        //设置画笔粗细
        txtPaint.setStrokeWidth(DensityUtil.dip2px(getContext(), 3));
        CurrentPoint currentPoint = new CurrentPoint(XPoint, ((float)(75 - signs.get(0).getBpm()) / 25 * YLength + YPoint));
        sum = signs.get(0).getBpm();
        measureIntervalLength();
        int index = 1;
        for(long i = begin;i < end;i+=interval)
        {
            if(index < signs.size() && (i < signs.get(index).getTimestamp() && (i + interval) > signs.get(index).getTimestamp()))
            {
                sum += signs.get(index).getBpm();
                if(index == (signs.size() - 1))
                {
                    canvas.drawLine(currentPoint.x, currentPoint.y, (XPoint + XLength), ((float)(75 - signs.get(index).getBpm()) / 25 * YLength + YPoint), txtPaint);
                    break;
                }
                canvas.drawLine(currentPoint.x, currentPoint.y, (currentPoint.x + intervalLength), ((float)(75 - signs.get(index).getBpm()) / 25 * YLength + YPoint), txtPaint);
                currentPoint.y = ((float)(75 - signs.get(index).getBpm()) / 25 * YLength + YPoint);
                index += 1;
            }
            else
            {
                canvas.drawLine(currentPoint.x, currentPoint.y, (currentPoint.x + intervalLength), currentPoint.y, txtPaint);
            }
            currentPoint.x += intervalLength;
        }
    }
}
