package com.st.cms.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.st.cms.entity.PointSimple;

import java.util.ArrayList;

import cms.st.com.poccms.R;

public class ImageLayout extends FrameLayout implements View.OnClickListener {

    private ImageLayout imageLayout;
    private ArrayList<PointSimple> points;

    private FrameLayout layouPoints;

    private ImageView imgBg;
    private OnClickViewListener onClickViewListener;

    Context mContext;
    private static int width;
    private static int height;
    //标记的宽高
    private LayoutParams layoutParams;

    public ImageLayout(Context context) {
        this(context, null);
    }

    public ImageLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ImageLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        imageLayout = this;
        initView(context, attrs);
    }


    private void initView(Context context, AttributeSet attrs) {

        mContext = context;

        View imgPointLayout = inflate(context, R.layout.layout_imgview_point, this);

        imgBg = (ImageView) imgPointLayout.findViewById(R.id.imgBg);
        layouPoints = (FrameLayout) imgPointLayout.findViewById(R.id.layouPoints);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    public void setImgBg(int width, int height, String imgUrl) {

        ViewGroup.LayoutParams lp = imgBg.getLayoutParams();
        lp.width = width;
        lp.height = height;

        imgBg.setLayoutParams(lp);

        ViewGroup.LayoutParams lp1 = layouPoints.getLayoutParams();
        lp1.width = width;
        lp1.height = height;

        layouPoints.setLayoutParams(lp1);

        Glide.with(mContext).load(imgUrl).asBitmap().into(imgBg);

        addPoints();

    }
    public void setImgBg(int width, int height) {

        ViewGroup.LayoutParams lp = imgBg.getLayoutParams();
        lp.width = width;
        lp.height = height;

        imgBg.setLayoutParams(lp);

        addPoints();

    }
    public void setWidthHeight(int width, int height)
    {
        this.width = width;
        this.height = height;
        ViewGroup.LayoutParams lp1 = layouPoints.getLayoutParams();
        lp1.width = width;
        lp1.height = height;

        layouPoints.setLayoutParams(lp1);
    }
    public void setPoints(ArrayList<PointSimple> points) {

        this.points = points;
    }

    public void addPoint(PointSimple pointSimple)
    {
        points.add(pointSimple);
    }
    public void addPoints() {
        layouPoints.removeAllViews();

        for (int i = 0; i < points.size(); i++) {
            drawPoint(points.get(i));
//            double width_scale = points.get(i).width_scale;
//            double height_scale = points.get(i).height_scale;
//
//
//            LinearLayout view = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.layout_img_point, this, false);
//            ImageView imageView = (ImageView) view.findViewById(R.id.imgPoint);
//            imageView.setTag(i);
//
//            AnimationDrawable animationDrawable = (AnimationDrawable) imageView.getDrawable();
//            animationDrawable.start();
//
//            LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
//
//            layoutParams.leftMargin = (int) (width * width_scale);
//            layoutParams.topMargin = (int) (height * height_scale);
//
//            imageView.setOnClickListener(this);
//
//            layouPoints.addView(view, layoutParams);
        }
    }

    public View drawPoint(final PointSimple pointSimple)
    {

        final View view = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.layout_img_point, this, false);

//        double width_scale = pointSimple.width_scale;
//        double height_scale = pointSimple.height_scale;
        final ImageView imageView = (ImageView) view.findViewById(R.id.imgPoint);
        imageView.setTag(points.indexOf(pointSimple));

        AnimationDrawable animationDrawable = (AnimationDrawable) imageView.getDrawable();
        animationDrawable.start();

//        final LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
//        layoutParams.leftMargin = (int) (width * width_scale);
//        layoutParams.topMargin = (int) (height * height_scale);
//        layoutParams.leftMargin = (int) (width * width_scale) > 50 ? (int) (width * width_scale) - 50 : (int) (width * width_scale);
//        layoutParams.topMargin = (int) (height * height_scale) > 50 ? (int) (height * height_scale) - 50 : (int) (height * height_scale);
//        Log.e("TEST", layoutParams.leftMargin + "--" + layoutParams.topMargin);

        layouPoints.addView(view);
        imageView.post(new Runnable() {
            @Override
            public void run() {
                double width_scale = pointSimple.width_scale;
                double height_scale = pointSimple.height_scale;
                imageView.setOnClickListener(imageLayout);
                layoutParams = (LayoutParams) view.getLayoutParams();
                layoutParams.leftMargin = (int) (width * width_scale) - imageView.getMeasuredWidth() / 2;
                layoutParams.topMargin = (int) (height * height_scale) - imageView.getMeasuredHeight() / 2;
//        layoutParams.leftMargin = (int) (width * width_scale) > 50 ? (int) (width * width_scale) - 50 : (int) (width * width_scale);
//        layoutParams.topMargin = (int) (height * height_scale) > 50 ? (int) (height * height_scale) - 50 : (int) (height * height_scale);
                view.setLayoutParams(layoutParams);
                imageView.setVisibility(VISIBLE);
            }
        });
        return view;
    }

    @Override
    public void onClick(View view) {
        onClickViewListener.showPopup(view);
    }
    public interface OnClickViewListener
    {
        public void showPopup(View view);
    }

    public void setOnClickViewListener(OnClickViewListener onClickViewListener) {
        this.onClickViewListener = onClickViewListener;
    }

    public ArrayList<PointSimple> getPoints() {
        return points;
    }

    public FrameLayout getLayouPoints() {
        return layouPoints;
    }
}
