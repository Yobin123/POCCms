package com.st.cms.fragment;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;
import com.st.cms.entity.Detail;
import com.st.cms.entity.Injury;
import com.st.cms.entity.Point;
import com.st.cms.entity.PointSimple;
import com.st.cms.entity.Prioritise;
import com.st.cms.entity.SitePoint;
import com.st.cms.entity.Winjury;
import com.st.cms.utils.Constants;
import com.st.cms.utils.DensityUtil;
import com.st.cms.utils.NfcUtils;
import com.st.cms.utils.OkHttpClientHelper;
import com.st.cms.widget.ImageLayout;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cms.st.com.poccms.R;

/**
 * Created by jt on 2016/8/25.
 */
public class MarkPointFragment extends TreatmentBaseFragment{
    private ImageView iv_mark_front_image;
    private ImageView iv_mark_back_image;
    private FloatingActionButton fab_switch;
    private FloatingActionButton fab_save;
    private static MarkPointFragment fragment = null;
    //fab需要的onclicklistener
    private View.OnClickListener fabListener = null;
    //记录当前的图片是front还是back的(0代表正面，1代表反面，默认为0)
    private int surface = 0;
    private int preIndex = -1;
    private MyBackListener myBackListener;
    //这边预留伤处的信息集合
    private Point point = null;
    private MyOnTouchListener myOnTouchListener;
    private MyOnLongClickListener myOnLongClickListener;
    //点击标记的事件
    private MyOnClickViewListener myOnClickViewListener;
    //弹出框
    private PopupWindow myPopup;
    private View popupView;
    private TextView tv_point_name;
    private EditText et_profile;
    //前身和后身的标记的布局类
    private ImageLayout il_points_front;
    private ImageLayout il_points_back;
    //
    private ArrayList<PointSimple> pointSimples_front;
    private ArrayList<PointSimple> pointSimples_back;

    //用于标记伤处的点
    private PointSimple pointSimple = null;
    //用于初始化伤处的点
    private PointSimple initPointSimple = null;
//    private PointSimple save_pointSimple = null;
    int width = 0;
    int height = 0;


    //Image的真实宽高
    private int dw = 0;
    private int dh = 0;
    //图片在imageview中的实际尺寸
    //图片在imageview中的实际位移
    private float translate_x = 0;
    private float translate_y = 0;
    //图片在imageview中x和y方向的缩放系数
    private float sx = 0;
    private float sy = 0;
    //计算Image在屏幕上实际绘制的宽高
    private int cw = 0;
    private int ch = 0;
    //记录需要保存的点的个数
    private int save_point_count = 0;
    private TextView tv_save_btn;

    private int current_index = -1;
    private View current_view = null;
    private List<PointSimple> pointSimples_for_save = null;

    //已经保存的伤处
    private List<Winjury> winjuries = null;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_mark, container, false);
        il_points_front = (ImageLayout)view.findViewById(R.id.il_points_front);
        il_points_back = (ImageLayout)view.findViewById(R.id.il_points_back);

        addPoints();

        myOnClickViewListener = new MyOnClickViewListener();
        il_points_front.setOnClickViewListener(myOnClickViewListener);
        il_points_back.setOnClickViewListener(myOnClickViewListener);

        myOnTouchListener = new MyOnTouchListener();
        myOnLongClickListener = new MyOnLongClickListener();

        iv_mark_front_image = (ImageView)view.findViewById(R.id.iv_mark_front_image);
        iv_mark_front_image.post(new Runnable() {
            @Override
            public void run() {
                //获得ImageView中Image的真实宽高，
                dw = iv_mark_front_image.getDrawable().getBounds().width();
                dh = iv_mark_front_image.getDrawable().getBounds().height();
//                Log.e("TEST", "drawable_X = " + dw + ", drawable_Y = " + dh);
                //获得ImageView中Image的变换矩阵
                Matrix m = iv_mark_front_image.getImageMatrix();
                float[] values = new float[10];
                m.getValues(values);

                //Image在绘制过程中的变换矩阵，从中获得x和y方向的缩放系数
                sx = values[0];
                sy = values[4];
                //x、y的位移量
                translate_x = values[2];
                translate_y = values[5];
//                Log.e("TEST", "scale_X = " + sx + ", scale_Y = " + sy);
                Log.e("TEST", "Translate_X = " + values[2] + ", Translate_Y = " + values[5]);


                //计算Image在屏幕上实际绘制的宽高
                cw = (int)(dw * sx);
                ch = (int)(dh * sy);
                Log.e("TEST", "caculate_W = " + cw + ", caculate_H = " + ch);

                //测试用数据
//                Log.e("TEST", width + "---" + height);
//                PointSimple pointSimple1 = new PointSimple();
//                pointSimple1.width_scale = (float)(0 + translate_x) / width;
//                pointSimple1.height_scale = (float)(0 + translate_y) / height;
//                Log.e("TEST", pointSimple1.width_scale + "--" + pointSimple1.height_scale);
//                PointSimple pointSimple2 = new PointSimple();
//                pointSimple2.width_scale = (float)(cw + translate_x) / width;
//                pointSimple2.height_scale = (float)(0 + translate_y) / height;
//                Log.e("TEST", pointSimple2.width_scale + "--" + pointSimple2.height_scale);
//
//
//                PointSimple pointSimple3 = new PointSimple();
//                pointSimple3.width_scale = (float)(0 + translate_x) / width;
//                pointSimple3.height_scale = (float)(ch + translate_y) / height;
//                Log.e("TEST", pointSimple3.width_scale + "--" + pointSimple3.height_scale);
//
//                PointSimple pointSimple4 = new PointSimple();
//                pointSimple4.width_scale = (float)(cw + translate_x) / width;
//                pointSimple4.height_scale = (float)(ch + translate_y) / height;
//                Log.e("TEST", pointSimple4.width_scale + "--" + pointSimple4.height_scale);
//                pointSimples.add(pointSimple1);
//                pointSimples.add(pointSimple2);
//                pointSimples.add(pointSimple3);
//                pointSimples.add(pointSimple4);
//                il_points.addPoints();
            }
        });
        iv_mark_back_image = (ImageView)view.findViewById(R.id.iv_mark_back_image);

        iv_mark_back_image.setOnLongClickListener(myOnLongClickListener);
        iv_mark_back_image.setOnTouchListener(myOnTouchListener);
        iv_mark_front_image.setOnLongClickListener(myOnLongClickListener);
        iv_mark_front_image.setOnTouchListener(myOnTouchListener);
        fab_switch = (FloatingActionButton)view.findViewById(R.id.fab_switch);
        fab_save = (FloatingActionButton)view.findViewById(R.id.fab_save);
        fab_switch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId())
                {
                    case R.id.fab_switch:
                        if(iv_mark_front_image.getVisibility() == View.VISIBLE)
                        {
                            iv_mark_front_image.setVisibility(View.INVISIBLE);
                            iv_mark_back_image.setVisibility(View.VISIBLE);
                            il_points_front.setVisibility(View.INVISIBLE);
                            il_points_back.setVisibility(View.VISIBLE);
                            surface = 1;
                        }
                        else
                        {
                            iv_mark_front_image.setVisibility(View.VISIBLE);
                            iv_mark_back_image.setVisibility(View.INVISIBLE);
                            il_points_back.setVisibility(View.INVISIBLE);
                            il_points_front.setVisibility(View.VISIBLE);
                            surface = 0;
                        }
                        break;
                }
            }
        });

        fabListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePoints();
            }
        };
//        treatmentMainActivity.fab.setOnClickListener(fabListener);
        myBackListener = new MyBackListener(preIndex);
        treatmentMainActivity.iv_back.setOnClickListener(myBackListener);
        return view;
    }

    private void addPoints()
    {
        initData();
        il_points_front.setPoints(pointSimples_front);
        il_points_back.setPoints(pointSimples_back);
        il_points_front.post(new Runnable() {
            @Override
            public void run() {
                width = il_points_front.getMeasuredWidth();
                height = il_points_front.getMeasuredHeight();
                il_points_front.setWidthHeight(il_points_front.getWidth(), il_points_front.getHeight());
                il_points_front.setImgBg(il_points_front.getWidth(), il_points_front.getHeight());
            }
        });
        markSavedInjury();
    }
    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden)
        {
            if(myBackListener != null)
            {
                myBackListener.setPreIndex(preIndex);
            }
            else
            {
                myBackListener = new MyBackListener(preIndex);
            }
            addPoints();
            treatmentMainActivity.iv_back.setOnClickListener(new MyBackListener(preIndex));
            if(fab_save != null && fab_save.getVisibility() == View.VISIBLE)
            {
                fab_save.setOnClickListener(fabListener);
            }
        }
        else
        {
            il_points_front.getPoints().clear();
            il_points_back.getPoints().clear();
            il_points_front.getLayouPoints().removeAllViews();
            il_points_back.getLayouPoints().removeAllViews();

            iv_mark_front_image.setVisibility(View.VISIBLE);
            iv_mark_back_image.setVisibility(View.INVISIBLE);
            il_points_back.setVisibility(View.INVISIBLE);
            il_points_front.setVisibility(View.VISIBLE);
            detail = null;
        }

        super.onHiddenChanged(hidden);
    }

    public static MarkPointFragment newInstance(Prioritise prioritise, int preIndex) {

        Bundle args = new Bundle();
        if(fragment == null)
        {
            fragment = new MarkPointFragment();
            fragment.setArguments(args);
        }
        fragment.prioritise = prioritise;
        fragment.preIndex = preIndex;
        return fragment;
    }
    private class MyOnLongClickListener implements View.OnLongClickListener
    {
        //长按弹出编辑信息栏
        @Override
        public boolean onLongClick(View v) {
//            Log.e("TEST", point.getX() + "---transx:" + translate_x + "---cw:" + cw);
//            Log.e("TEST", point.getY() + "---transy:" + translate_y + "---ch:" + ch);

            pointSimple = new PointSimple();
            float x_scale = (point.getX() - translate_x) / cw;
            float y_scale = (point.getY() - translate_y) / ch;
            SitePoint sitePoint = SitePoint.checkSite(x_scale, y_scale, surface);
            if(sitePoint != null)
            {
                //计算点在手机中的具体宽高比例
                pointSimple.width_scale = (sitePoint.getX_scale() * cw + translate_x) / v.getMeasuredWidth();
                pointSimple.height_scale = (sitePoint.getY_scale() * ch + translate_y) / v.getMeasuredHeight();
                List<PointSimple> pointSimples = surface == 0 ? pointSimples_front : pointSimples_back;
                Winjury winjury = new Winjury();
                winjury.setPoint(sitePoint.getName());
                winjury.setLocate(sitePoint.getX_scale() + "," + sitePoint.getY_scale());
                winjury.setSurface(surface);
                pointSimple.setWinjury(winjury);
                for(int i = 0;i < pointSimples.size();i++)
                {
                    if(pointSimples.get(i).getWinjury().getPoint().equals(pointSimple.getWinjury().getPoint()))
                    {
                        pointSimple = pointSimples.get(i);
                        break;
                    }
                }
                //这边新的点和重复点的添加操作一样，因为重复点是指向list中已有对象的
                pointSimples.add(pointSimple);
                current_index = pointSimples.indexOf(pointSimple);
                ImageLayout il_points = surface == 0 ? il_points_front : il_points_back;
                current_view = il_points.drawPoint(pointSimple);


                showZoneSelectWindow(v, pointSimple, 0);
                if(fab_save.getVisibility() == View.GONE)
                {
                    fab_save.setVisibility(View.VISIBLE);
//                    Log.e("TEST", "add listener");
                    fab_save.setOnClickListener(fabListener);
                }
            }

            return false;
        }
    }

    /**
     * 用于记录点击的点坐标
     */
    private class MyOnTouchListener implements View.OnTouchListener
    {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            point = new Point(event.getX(), event.getY());
            Log.e("TEST", event.getX() + "----" + event.getY());
            return false;
        }
    }
    /**
     * 点击zone location 行时触发此方法，用于修改zone
     * @param operator 用来判断是长按触发还是点击标记触发(0:长按, 1:点击标记)
     */
    private void showZoneSelectWindow(View parent, PointSimple pointSimple, final int operator)
    {
        final int index = surface == 0 ? pointSimples_front.indexOf(pointSimple) : pointSimples_back.indexOf(pointSimple);
        Winjury injury = pointSimple.getWinjury();
        if(popupView == null)
        {
            LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            popupView = layoutInflater.inflate(R.layout.popup_mark_point, null);
            tv_point_name = (TextView) popupView.findViewById(R.id.tv_point_name);
            et_profile = (EditText)popupView.findViewById(R.id.et_profile);
            tv_save_btn = (TextView)popupView.findViewById(R.id.tv_save_btn);
        }
        tv_save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(et_profile == null || et_profile.getText().length() == 0)
                {
                    return;
                }
                cacheMarker(index);
                cachePoints(index);
                switch (operator)
                {
                    case 0:
                        save_point_count += 1;
                        current_index = -1;
                        current_view = null;
                        break;
                    case 1:
                        break;
                }
                myPopup.dismiss();
            }
        });
        tv_point_name.setText(injury.getPoint());
        et_profile.setText(injury.getInstruction() == null ? "" : injury.getInstruction());
        if(myPopup == null)
        {
            myPopup = new PopupWindow(popupView, DensityUtil.dip2px(getContext(), 250), DensityUtil.dip2px(getContext(), 250));
            myPopup.setTouchable(true);
            myPopup.setBackgroundDrawable(new BitmapDrawable());
            myPopup.setFocusable(true);
            myPopup.setOutsideTouchable(true);
            myPopup.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    if(save_point_count == 0)
                    {
                        fab_save.setVisibility(View.GONE);
                    }
                    if(current_index != -1 || current_view != null)
                    {
                        ImageLayout il_points = surface == 0 ? il_points_front : il_points_back;
                        switch (surface)
                        {
                            case 0:
                                pointSimples_front.remove(current_index);
                                break;
                            case 1:
                                pointSimples_back.remove(current_index);
                                break;
                        }
                        il_points.getLayouPoints().removeView(current_view);
                        il_points.getLayouPoints().invalidate();
                        current_index = -1;
                        current_view = null;
                    }
                }
            });
        }
        myPopup.showAtLocation(parent, Gravity.CENTER, 0, 0);
    }
    private void initData() {
        current_index = -1;
        current_view = null;
        pointSimples_for_save = new ArrayList<>();
        pointSimples_front = new ArrayList<>();
        pointSimples_back = new ArrayList<>();
        SitePoint sitePoint = null;
        //身前的部位范围
//        SitePoint.getBack_sitePoints().clear();
//        SitePoint.getFront_sitePoints().clear();
        if(SitePoint.getBack_sitePoints().size() == 0 && SitePoint.getFront_sitePoints().size() == 0)
        {
            sitePoint = new SitePoint("Head", 0.25f, 0.0f, 0.75f, 0.15f, 0);
            sitePoint = new SitePoint("Abdomen", 0.4f, 0.28f, 0.6f, 0.44f, 0);

            sitePoint = new SitePoint("Right Shoulder", 0.34f, 0.15f, 0.4f, 0.26f, 0);
            sitePoint = new SitePoint("Right Thorax", 0.4f, 0.15f, 0.5f, 0.28f, 0);
            sitePoint = new SitePoint("Right Arm", 0.25f, 0.26f, 0.4f, 0.46f, 0);
            sitePoint = new SitePoint("Right Hand", 0.25f, 0.46f, 0.4f, 0.52f, 0);
            sitePoint = new SitePoint("Right Leg", 0.4f, 0.44f, 0.5f, 0.92f, 0);
            sitePoint = new SitePoint("Right Foot", 0.4f, 0.92f, 0.5f, 1.0f, 0);

            sitePoint = new SitePoint("Left Shoulder", 0.6f, 0.15f, 0.66f, 0.26f, 0);
            sitePoint = new SitePoint("Left Thorax", 0.5f, 0.15f, 0.6f, 0.28f, 0);
            sitePoint = new SitePoint("Left Arm", 0.6f, 0.26f, 0.75f, 0.46f, 0);
            sitePoint = new SitePoint("Left Hand", 0.6f, 0.46f, 0.75f, 0.52f, 0);
            sitePoint = new SitePoint("Left Leg", 0.5f, 0.44f, 0.6f, 0.92f, 0);
            sitePoint = new SitePoint("Left Foot", 0.5f, 0.92f, 0.6f, 1.0f, 0);

            //身后的部位范围
            sitePoint = new SitePoint("Metencephalon", 0.25f, 0.0f, 0.75f, 0.15f, 1);
            sitePoint = new SitePoint("Hip", 0.4f, 0.44f, 0.6f, 0.54f, 1);
            sitePoint = new SitePoint("Back", 0.4f, 0.15f, 0.6f, 0.44f, 1);

            sitePoint = new SitePoint("Right Shoulder", 0.34f, 0.15f, 0.4f, 0.26f, 1);
            sitePoint = new SitePoint("Right Arm", 0.25f, 0.26f, 0.4f, 0.46f, 1);
            sitePoint = new SitePoint("Right Hand", 0.25f, 0.46f, 0.4f, 0.52f, 1);
            sitePoint = new SitePoint("Right Leg", 0.4f, 0.44f, 0.5f, 0.92f, 1);

            sitePoint = new SitePoint("Left Shoulder", 0.6f, 0.15f, 0.66f, 0.26f, 1);
            sitePoint = new SitePoint("Left Arm", 0.6f, 0.26f, 0.75f, 0.46f, 1);
            sitePoint = new SitePoint("Left Hand", 0.6f, 0.46f, 0.75f, 0.52f, 1);
            sitePoint = new SitePoint("Left Leg", 0.5f, 0.44f, 0.6f, 0.92f, 1);
        }

//        Log.e("TEST", "site point length:" + SitePoint.getFront_sitePoints().size());
//        Log.e("TEST", "site point length:" + SitePoint.getBack_sitePoints().size());
    }
    //获取已有的伤处
    private void markSavedInjury()
    {
        Callback callback = new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                e.printStackTrace();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(treatmentMainActivity, "Request Failure!Please check the network.", Toast.LENGTH_SHORT).show();
                    }
                });
                Log.e("TEST", "Request Failure!Please check the network.");
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (response.isSuccessful()) {
                    ResponseBody body = response.body();
                    if (body != null) {
                        String result = body.string();
                        Log.e("TEST", result);
                        Type type = new TypeToken<List<Winjury>>() {}.getType();
                        winjuries = treatmentMainActivity.gson.fromJson(result, type);
                        if(winjuries != null)
                        {
                            treatmentMainActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Log.e("TEST", winjuries.size() + "");
                                    for(int i = 0;i < winjuries.size();i++)
                                    {
                                        initPointSimple = new PointSimple();
                                        if(winjuries.get(i).getLocate().length() > 0)
                                        {
                                            initPointSimple.width_scale = ((Double.parseDouble(winjuries.get(i).getLocate().split(",")[0])) * cw + translate_x) / il_points_front.getMeasuredWidth();
                                            initPointSimple.height_scale = ((Double.parseDouble(winjuries.get(i).getLocate().split(",")[1])) * ch + translate_y) / il_points_front.getMeasuredHeight();
                                            initPointSimple.setWinjury(winjuries.get(i));
                                            switch (winjuries.get(i).getSurface())
                                            {
                                                case 0:
                                                    Log.e("TEST", "front:" + initPointSimple.width_scale + "," + initPointSimple.height_scale);
                                                    pointSimples_front.add(initPointSimple);
                                                    il_points_front.drawPoint(initPointSimple);
                                                    break;
                                                case 1:
                                                    Log.e("TEST", "back:" + initPointSimple.width_scale + "," + initPointSimple.height_scale);
                                                    pointSimples_back.add(initPointSimple);
                                                    il_points_back.drawPoint(initPointSimple);
                                                    break;
                                            }
                                        }
                                    }
                                }
                            });
                        }
                    }

                }
                else
                {
                    Log.e("TEST", response.isSuccessful() + "---" + response.body());
                }

            }
        };
        OkHttpClientHelper.loadNetworkData(getContext(), Constants.getUrl(Constants.GET_INJURY + "/" + prioritise.getTagId()), callback);
    }
    //当图上已有标记时，点击标记显示弹框
    private class MyOnClickViewListener implements ImageLayout.OnClickViewListener
    {
        /**
         *
         * @param view 点击的imageview
         */
        @Override
        public void showPopup(View view) {
            PointSimple pointSimple = surface == 0 ? pointSimples_front.get(Integer.parseInt(view.getTag().toString()))
                    : pointSimples_back.get(Integer.parseInt(view.getTag().toString()));
            ImageView imageView = surface == 0 ? iv_mark_front_image : iv_mark_back_image;
            showZoneSelectWindow(imageView, pointSimple, 1);
        }
    }
    //缓存标记
    private void cacheMarker(int index)
    {
        switch (surface)
        {
            case 0:
                pointSimples_front.get(index).getWinjury().setInstruction(et_profile.getText().toString());
                break;
            case 1:
                pointSimples_back.get(index).getWinjury().setInstruction(et_profile.getText().toString());
                break;
        }
    }
    //缓存点到待保存的集合中
    private void cachePoints(int index)
    {
        switch (surface)
        {
            case 0:
                pointSimples_for_save.add(pointSimples_front.get(index));
                break;
            case 1:
                pointSimples_for_save.add(pointSimples_back.get(index));
                break;
        }
    }
    //保存到后台
    private boolean savePoints()
    {
        if(pointSimples_for_save != null && pointSimples_for_save.size() == 0)
        {
            return true;
        }
        //这边由于测试所以注释掉了，正常时候需要去掉注释
        if(!treatmentMainActivity.isAvailable)
        {
            Toast.makeText(getContext(), "This device has no NFC function.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(!(treatmentMainActivity.connected && treatmentMainActivity.isConnect(getContext())))
        {
            Toast.makeText(getActivity(), "The net can not connect to backend. Please check the net.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(!NfcUtils.isNfcIntent(getActivity()))
        {
            Toast.makeText(getContext(), "Please get close to NFC tag.", Toast.LENGTH_SHORT).show();
            return false;
        }
        Log.e("TEST", "----" + treatmentMainActivity + "------" + treatmentMainActivity.detail);
        if(!NfcUtils.getTagId(getActivity().getIntent()).equals(treatmentMainActivity.detail.getTagId()))
        {
            Toast.makeText(getContext(), "Please get close to correct NFC tag.", Toast.LENGTH_SHORT).show();
            return false;
        }
        treatmentMainActivity.readTag(treatmentMainActivity.getIntent(), 1);
        boolean isSuccess = false;
        List<Injury> injuries = new ArrayList<>();
        Winjury winjury = null;
        for(int i = 0;i < pointSimples_for_save.size();i++)
        {
            winjury = pointSimples_for_save.get(i).getWinjury();
            if(winjury != null)
            {
                injuries.add(new Injury(winjury.getPoint(), winjury.getInstruction()));
            }
        }
        detail = treatmentMainActivity.detail;
        if(detail.getInjuries() == null)
        {
            detail.setInjuries(new ArrayList<Injury>());
        }
        List<Injury> tag_injuries = detail.getInjuries();
        for(int i = 0;i < injuries.size();i++)
        {
            int index = -1;
            for(int j = 0;j < tag_injuries.size();j++)
            {
                if(tag_injuries.get(j).getPosition().equals(injuries.get(i).getPosition()))
                {
                    index = j;
                    break;
                }
            }
            if(index == -1)
            {
                detail.getInjuries().add(injuries.get(i));
            }
            else
            {
                detail.getInjuries().get(index).setDepict(injuries.get(i).getDepict());
            }
        }
        isSuccess = NfcUtils.writeToTag(getActivity().getIntent(), treatmentMainActivity.gson.toJson(detail));
        if(!isSuccess)
        {
            Toast.makeText(getContext(), "Please get close to the nfc tag.", Toast.LENGTH_SHORT).show();
        }
        if(isSuccess)
        {
            syncToBackEnd();
        }
        return isSuccess;
    }
//    private boolean savePoints()
//    {
//        if(pointSimples_for_save != null && pointSimples_for_save.size() == 0)
//        {
//            return true;
//        }
//        //这边由于测试所以注释掉了，正常时候需要去掉注释
//        if(!treatmentMainActivity.isAvailable)
//        {
//            Toast.makeText(getContext(), "This device has no NFC function.", Toast.LENGTH_SHORT).show();
//            return false;
//        }
//        if(!(treatmentMainActivity.connected && treatmentMainActivity.isConnect(getContext())))
//        {
//            Toast.makeText(getActivity(), "The net can not connect to backend. Please check the net.", Toast.LENGTH_SHORT).show();
//            return false;
//        }
//        if(!NfcUtils.isNfcIntent(getActivity()))
//        {
//            Toast.makeText(getContext(), "Please get close to NFC tag.", Toast.LENGTH_SHORT).show();
//            return false;
//        }
//        Log.e("TEST", "----" + treatmentMainActivity + "------" + treatmentMainActivity.detail);
//        if(!NfcUtils.getTagId(getActivity().getIntent()).equals(treatmentMainActivity.detail.getTagId()))
//        {
//            Toast.makeText(getContext(), "Please get close to correct NFC tag.", Toast.LENGTH_SHORT).show();
//            return false;
//        }
//        boolean isSuccess = false;
//        JsonArray jsonArray_points = new JsonArray();
//        JsonArray jsonArray_locates = new JsonArray();
//        JsonArray jsonArray_instructions = new JsonArray();
//        JsonArray jsonArray_surfaces = new JsonArray();
//        JsonObject obj = null;
//        Log.e("TEST", "pointSimples_for_save size is :" + pointSimples_for_save.size());
//        for(int i = 0;i < pointSimples_for_save.size();i++)
//        {
//            obj = new JsonObject();
//            obj.addProperty("point", pointSimples_for_save.get(i).getWinjury().getPoint());
//            jsonArray_points.add(obj);
//            obj = new JsonObject();
//            obj.addProperty("locate", pointSimples_for_save.get(i).getWinjury().getLocate());
//            jsonArray_locates.add(obj);
//            obj = new JsonObject();
//            obj.addProperty("instr", pointSimples_for_save.get(i).getWinjury().getInstruction());
//            jsonArray_instructions.add(obj);
//            obj = new JsonObject();
//            obj.addProperty("surface", pointSimples_for_save.get(i).getWinjury().getSurface());
//            jsonArray_surfaces.add(obj);
//        }
//        //做测试的
////        if(treatmentMainActivity.isAvailable)
////        {
////            treatmentMainActivity.readTag(treatmentMainActivity.getIntent(), 1);
////            detail = treatmentMainActivity.detail;
////        }
////        if(detail == null)
////        {
////            detail = new Detail();
////            //这边用于测试
////            detail.setTagId(prioritise.getTagId());
//////            detail.setTagId(NfcUtils.getTagId(treatmentMainActivity.getIntent()));
////        }
//        String url = Constants.getUrl(Constants.MARK_INJURY);
//        Callback callback = new Callback() {
//            @Override
//            public void onFailure(Request request, IOException e) {
//                e.printStackTrace();
//                getActivity().runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast.makeText(treatmentMainActivity, "Request Failure!Please check the network.", Toast.LENGTH_SHORT).show();
//                    }
//                });
//                Log.e("TEST", "Request Failure!Please check the network.");
//            }
//
//            @Override
//            public void onResponse(Response response) throws IOException {
//                if (response.isSuccessful()) {
//                    ResponseBody body = response.body();
//                    if (body != null) {
//                        List<Injury> injuries = new ArrayList<>();
//                        Winjury winjury = null;
//                        for(int i = 0;i < pointSimples_for_save.size();i++)
//                        {
//                            winjury = pointSimples_for_save.get(i).getWinjury();
//                            if(winjury != null)
//                            {
//                                injuries.add(new Injury(winjury.getLocate(), winjury.getInstruction()));
//                            }
//                        }
//                        if(detail.getInjuries() == null)
//                        {
//                            detail.setInjuries(new ArrayList<Injury>());
//                        }
//                        detail.getInjuries().addAll(injuries);
//                        boolean isSuccess = NfcUtils.writeToTag(getActivity().getIntent(), treatmentMainActivity.gson.toJson(detail));
//                        Log.e("TEST", "show save tag1----" + isSuccess);
//                        if(isSuccess)
//                        {
//                            saveSuccess(preIndex);
//                        }
////                        if(detail == null)
////                        {
////                            detail = new Detail();
////                            Log.e("TEST", NfcUtils.getTagId(getIntent()));
////                            detail.setTagId(NfcUtils.getTagId(getIntent()));
////                            detail.setZone(tv_loc.getText() + "");
////                        }
////                        detail.setPlv(position);
////                        String result = body.string();
////                        Log.e("TEST", result);
////                        if(result.length() > 0)
////                        {
////                            boolean isSuccess = NfcUtils.writeToTag(treatmentMainActivity.getIntent(), gson.toJson(detail));
////                            if(isSuccess)
////                            {
////                                saveSuccess();
////                            }
////                            else
////                            {
////                                //这个必须在主线程运行
////                                runOnUiThread(new Thread(){
////                                    @Override
////                                    public void run() {
////                                        showSaveTagWindow();
////                                    }
////                                });
////                            }
////                        }
//                    }
//
//                }
//                else
//                {
//                    Log.e("TEST", response.isSuccessful() + "---" + response.body());
//                }
//
//            }
//        };
//        Log.e("TEST", url);
//        Map<String, String> values = new HashMap<>();
//        Log.e("TEST", jsonArray_points.toString());
//        Log.e("TEST", jsonArray_locates.toString());
//        Log.e("TEST", jsonArray_instructions.toString());
//        Log.e("TEST", jsonArray_surfaces.toString());
//        values.put("taglibid", detail.getTagId());
//        values.put("points", jsonArray_points.toString());
//        values.put("locates", jsonArray_locates.toString());
//        values.put("instructions", jsonArray_instructions.toString());
//        values.put("surfaces", jsonArray_surfaces.toString());
//        Log.e("TEST", jsonArray_points.toString() + jsonArray_locates.toString() + jsonArray_instructions.toString() + jsonArray_surfaces.toString());
//        OkHttpClientHelper.postKeyValuePairAsync(getContext(), url, values, callback, null);
//        return isSuccess;
//    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void syncToBackEnd()
    {
        JsonArray jsonArray_points = new JsonArray();
        JsonArray jsonArray_locates = new JsonArray();
        JsonArray jsonArray_instructions = new JsonArray();
        JsonArray jsonArray_surfaces = new JsonArray();
        JsonObject obj = null;
        Log.e("TEST", "pointSimples_for_save size is :" + pointSimples_for_save.size());
        for(int i = 0;i < pointSimples_for_save.size();i++)
        {
            obj = new JsonObject();
            obj.addProperty("point", pointSimples_for_save.get(i).getWinjury().getPoint());
            jsonArray_points.add(obj);
            obj = new JsonObject();
            obj.addProperty("locate", pointSimples_for_save.get(i).getWinjury().getLocate());
            jsonArray_locates.add(obj);
            obj = new JsonObject();
            obj.addProperty("instr", pointSimples_for_save.get(i).getWinjury().getInstruction());
            jsonArray_instructions.add(obj);
            obj = new JsonObject();
            obj.addProperty("surface", pointSimples_for_save.get(i).getWinjury().getSurface());
            jsonArray_surfaces.add(obj);
        }
        String url = Constants.getUrl(Constants.MARK_INJURY);
        Callback callback = new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                e.printStackTrace();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(treatmentMainActivity, "Request Failure!Please check the network.", Toast.LENGTH_SHORT).show();
                    }
                });
                Log.e("TEST", "Request Failure!Please check the network.");
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (response.isSuccessful()) {
                    ResponseBody body = response.body();
                    if (body != null) {
                    }
                    treatmentMainActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(fab_save.getVisibility() == View.VISIBLE)
                            {
                                fab_save.setVisibility(View.GONE);
                            }
                        }
                    });

                    saveSuccess(preIndex);
                    pointSimples_for_save = new ArrayList<>();
                }
                else
                {
                    Log.e("TEST", response.isSuccessful() + "---" + response.body());
                }
            }
        };
        Log.e("TEST", url);
        Map<String, String> values = new HashMap<>();
        Log.e("TEST", jsonArray_points.toString());
        Log.e("TEST", jsonArray_locates.toString());
        Log.e("TEST", jsonArray_instructions.toString());
        Log.e("TEST", jsonArray_surfaces.toString());
        values.put("taglibid", detail.getTagId());
        values.put("points", jsonArray_points.toString());
        values.put("locates", jsonArray_locates.toString());
        values.put("instructions", jsonArray_instructions.toString());
        values.put("surfaces", jsonArray_surfaces.toString());
        Log.e("TEST", jsonArray_points.toString() + jsonArray_locates.toString() + jsonArray_instructions.toString() + jsonArray_surfaces.toString());
        OkHttpClientHelper.postKeyValuePairAsync(getContext(), url, values, callback, null);
    }
}
