package com.st.cms.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;

import com.st.cms.entity.VitalSign;
import com.st.cms.utils.DensityUtil;

import java.util.List;

/**
 * Created by jt on 2016/9/5.
 */
public class Spo2View extends SignsBaseView{

    public Spo2View(Context context) {
        this(context, null);
    }

    public Spo2View(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        YLabel = new String[]{"75%", "80%", "85%", "90%", "95%", "100%"};
    }

    public void setSigns(List<VitalSign> signs) {
        this.signs = signs;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBrokenLine(canvas);
        drawTitle(canvas, "SpO2 " + (sum / signs.size()) + "%");
    }
    //绘制折线
    public void drawBrokenLine(Canvas canvas)
    {
        //开始绘制折线
        //设置画笔粗细
        txtPaint.setStrokeWidth(DensityUtil.dip2px(getContext(), 3));
        CurrentPoint currentPoint = new CurrentPoint(XPoint, ((float)(100 - signs.get(0).getSpo2()) / 25 * YLength + YPoint));
        sum = signs.get(0).getSpo2();
        measureIntervalLength();
        int index = 1;
        for(long i = begin;i < end;i+=interval)
        {
            if(index < signs.size() && (i < signs.get(index).getTimestamp() && (i + interval) > signs.get(index).getTimestamp()))
            {
                sum += signs.get(index).getSpo2();
                if(index == (signs.size() - 1))
                {
                    canvas.drawLine(currentPoint.x, currentPoint.y, (XPoint + XLength), ((float)(100 - signs.get(index).getSpo2()) / 25 * YLength + YPoint), txtPaint);
                    break;
                }
                canvas.drawLine(currentPoint.x, currentPoint.y, (currentPoint.x + intervalLength), ((float)(100 - signs.get(index).getSpo2()) / 25 * YLength + YPoint), txtPaint);
                currentPoint.y = ((float)(100 - signs.get(index).getSpo2()) / 25 * YLength + YPoint);
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
