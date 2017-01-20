package com.st.cms.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.st.cms.entity.GcsRecord;
import com.st.cms.entity.GcsScore;
import com.st.cms.entity.Prioritise;
import com.st.cms.utils.DensityUtil;
import com.st.cms.widget.StatesView;

import java.util.Arrays;

import cms.st.com.poccms.R;


public class GCSDetailFragment extends TreatmentBaseFragment implements View.OnClickListener{
    private GcsScore gcsRecord;
    private static GCSDetailFragment fragment;
    private StatesView mStatesView;
    private RadioGroup mRadiogroup;
    private int[] a = new int[]{1, 2, 4};
    private int[] b = new int[]{3, 4, 6};
    private int[] c = new int[]{4, 8, 15};
    private String[] duration=new String[]{"0-0:20am","0:20-0:40am","0:40-1am"} ;
    private RadioButton[] arrRadiobutton;
    private String[] textViewData;
    private ImageView expand_icon_left, expand_icon_right;
    private int radioIndex = 0;
    private HorizontalScrollView hsv_time;
    private int preIndex = -1;
    private MyBackListener myBackListener;

    public void setGcsRecord(GcsScore gcsRecord) {
        this.gcsRecord = gcsRecord;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gcs_histogram, container, false);
        initView(view);
        return view;
    }
    private void initData()
    {
        int param1 = 0, param2 = 0, param3 = 0;
        for(int i = 0;i < 3;i++)
        {
            param1 = (int)(Math.random() * 3) + 1;
            param2 = (int)(Math.random() * 4) + 1;
            param3 = (int)(Math.random() * 5) + 1;
            a[i] = param1;
            b[i] = param1 + param2;
            c[i] = param1 + param2 + param3;

        }
//        a = new int[]{((int)(Math.random() * 3) + 1), ((int)(Math.random() * 3) + 1), ((int)(Math.random() * 3) + 1)};
//        b = new int[]{((int)(Math.random() * 4) + 1), ((int)(Math.random() * 4) + 1), ((int)(Math.random() * 4) + 1)};
//        c = new int[]{((int)(Math.random() * 5) + 1), ((int)(Math.random() * 5) + 1), ((int)(Math.random() * 5) + 1)};
        Log.e("TEST", Arrays.toString(a) + "----");
        Log.e("TEST", Arrays.toString(b) + "----");
        Log.e("TEST", Arrays.toString(c) + "----");
    }
    public void initView(View view)
    {
        expand_icon_left = (ImageView) view.findViewById(R.id.expand_icon_left);
        expand_icon_right = (ImageView) view.findViewById(R.id.expand_icon_right);
        expand_icon_left.setOnClickListener(this);
        expand_icon_right.setOnClickListener(this);
        mStatesView = (StatesView) view.findViewById(R.id.statesview);
        mRadiogroup = (RadioGroup) view.findViewById(R.id.rg_gcs_duration);
        textViewData = getResources().getStringArray(R.array.gcs_tested_time);
        hsv_time = (HorizontalScrollView)view.findViewById(R.id.hsv_time);
        arrRadiobutton = new RadioButton[textViewData.length];
//        进行相应的for循环动态生成相应的radiobutton
        for (int i = 0; i < arrRadiobutton.length; i++) {
            RadioButton radioButton = new RadioButton(getContext());
            radioButton.setText(textViewData[i]);//给相应的radiobutton赋予值
            radioButton.setGravity(Gravity.CENTER);//文字居中
            //默认圆点被替换
            radioButton.setButtonDrawable(android.R.drawable.screen_background_dark_transparent);
            //获取屏幕的宽度计算出平分的宽度应该占得数值
            int screeWidth = DensityUtil.dip2px(getContext(),210);
            int eachWidth = screeWidth/3;
            //设置相应radiobutton layout_width layout_height
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(eachWidth, ViewGroup.LayoutParams.MATCH_PARENT);
            radioButton.setLayoutParams(params);

            arrRadiobutton[i] = radioButton;
            mRadiogroup.addView(arrRadiobutton[i]);
        }
        //设置第一个radiobutton为选中状态,到时候以测量时获取相应的系统时间为选中状态
        arrRadiobutton[0].setChecked(true);
        arrRadiobutton[0].setBackgroundColor(Color.BLACK);
        arrRadiobutton[0].setTextColor(Color.WHITE);
        initData();
        mStatesView.updateOpenData(a, duration);
        mStatesView.updateverbalData(b, duration);
        mStatesView.updateMotorData(c, duration);
        //对radiogroup进行相应的监听
        mRadiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                for (int i = 0; i < arrRadiobutton.length ; i++) {
                    if (arrRadiobutton[i].getId()==checkedId){
                        arrRadiobutton[i].setBackgroundColor(Color.BLACK);
                        arrRadiobutton[i].setTextColor(Color.WHITE);
                        duration = selectedSection(textViewData[i]);
                        Log.i("TEST", "---->>>"+duration[0]+":"+duration[1]+":"+duration[2]);
                        initData();
                        mStatesView.updateOpenData(a, duration);
                        mStatesView.updateverbalData(b, duration);
                        mStatesView.updateMotorData(c, duration);
                        radioIndex = i;
                    }else {
                        arrRadiobutton[i].setBackgroundColor(getResources().getColor(R.color.rb_gcs_bg));
                        arrRadiobutton[i].setTextColor(Color.BLACK);
                    }
                }
            }
        });

        mStatesView.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        myBackListener = new MyBackListener(preIndex);
        treatmentMainActivity.iv_back.setOnClickListener(myBackListener);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if(!hidden)
        {
            if(myBackListener != null)
            {
                myBackListener.setPreIndex(preIndex);
            }
            else
            {
                myBackListener = new MyBackListener(preIndex);
            }
            treatmentMainActivity.iv_back.setOnClickListener(new MyBackListener(preIndex));
        }
        super.onHiddenChanged(hidden);
    }

    public String[] selectedSection(String s){
        String[] str  ;
        int index = s.indexOf("-");
        int fChars_index = 0;
        String startNum = s.substring(0, index);
        for (int i = 0; i < s.length(); i++) {
            String item = String.valueOf(s.charAt(i));
            if (item.matches("[a-z]")==true){
                fChars_index=i;
                break;
            }
        }
        String endNum = s.substring(index+1,fChars_index);
        String time_indicate = s.substring(fChars_index,s.length());
        str = new String[]{startNum+"-"+startNum+":20"+time_indicate,
                startNum+":20"+"-"+startNum+":40"+time_indicate,
                startNum+":40"+"-"+endNum+time_indicate};
        return str;
    }


    public static GCSDetailFragment newInstance(Prioritise prioritise, int preIndex) {
        if(fragment == null)
        {
            fragment = new GCSDetailFragment();
        }
        fragment.prioritise = prioritise;
        fragment.preIndex = preIndex;
        return fragment;
    }
    //实现相应的跳转功能
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.expand_icon_left:
                if(radioIndex == 0)
                {
                    return;
                }
                arrRadiobutton[radioIndex].setBackgroundColor(getResources().getColor(R.color.rb_gcs_bg));
                arrRadiobutton[radioIndex].setTextColor(Color.BLACK);
                radioIndex -= 1;
                arrRadiobutton[radioIndex].setBackgroundColor(Color.BLACK);
                arrRadiobutton[radioIndex].setTextColor(Color.WHITE);
                duration = selectedSection(textViewData[radioIndex]);
                if(radioIndex > 0)
                {
                    hsv_time.setScrollX(DensityUtil.dip2px(getContext(),210) / 3 * (radioIndex - 1));
                    initData();
                    mStatesView.updateOpenData(a, duration);
                    mStatesView.updateverbalData(b, duration);
                    mStatesView.updateMotorData(c, duration);
                }

                break;
            case R.id.expand_icon_right:
                if(radioIndex == (textViewData.length - 1))
                {
                    return;
                }
                arrRadiobutton[radioIndex].setBackgroundColor(getResources().getColor(R.color.rb_gcs_bg));
                arrRadiobutton[radioIndex].setTextColor(Color.BLACK);
                radioIndex += 1;
                arrRadiobutton[radioIndex].setBackgroundColor(Color.BLACK);
                arrRadiobutton[radioIndex].setTextColor(Color.WHITE);
                duration = selectedSection(textViewData[radioIndex]);
                if(radioIndex < (textViewData.length - 1))
                {
                    hsv_time.setScrollX(DensityUtil.dip2px(getContext(),210) / 3 * (radioIndex - 1));
                    initData();
                    mStatesView.updateOpenData(a, duration);
                    mStatesView.updateverbalData(b, duration);
                    mStatesView.updateMotorData(c, duration);
                }
                break;
            case R.id.statesview:
                GcsHistoryFragment gcsHistoryFragment = GcsHistoryFragment.newInstance(prioritise, treatmentMainActivity.currentIndex);
                jumpAction(gcsHistoryFragment);
                break;
            default:
                break;
        }
//        intent.setClass(getContext(),GCSLogHistoryActivity.class);
//        startActivity(intent);
    }
}
