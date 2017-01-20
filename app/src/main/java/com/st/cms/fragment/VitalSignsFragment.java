package com.st.cms.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.st.cms.activity.TreatmentMainActivity;
import com.st.cms.adapter.SignsAdapter;
import com.st.cms.entity.Prioritise;
import com.st.cms.entity.VitalSign;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import cms.st.com.poccms.R;

/**
 * Created by jt on 2016/9/2.
 */
public class VitalSignsFragment extends TreatmentBaseFragment{

    public static VitalSignsFragment fragment;
    private ListView lv_signs_log;
    private TextView tv_read_button;

    //模拟数据
    private List<List<VitalSign>> signsList = null;
    private List<VitalSign> signs = null;
    private int preIndex = -1;
    private MyBackListener myBackListener;
    //当前的sign记录


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signs, container, false);
        initData();
        lv_signs_log = (ListView)view.findViewById(R.id.lv_signs_log);
        SignsAdapter signsAdapter = new SignsAdapter(getContext(), signsList);
        lv_signs_log.setAdapter(signsAdapter);
        lv_signs_log.setOnItemClickListener(new MyOnItemClickListener());
        tv_read_button = (TextView)view.findViewById(R.id.tv_read_button);
//        treatmentMainActivity = (TreatmentMainActivity)getActivity();
        View.OnClickListener readClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(signs == null)
                {
                    return;
                }
                SignsBrokenLineFragment signsBrokenLineFragment = SignsBrokenLineFragment.newInstance(prioritise, signs, treatmentMainActivity.currentIndex);
                jumpAction(signsBrokenLineFragment);
                signs = null;
                tv_read_button.setBackgroundColor(getResources().getColor(R.color.light_blue));
            }
        };
        tv_read_button.setOnClickListener(readClickListener);
        signs = null;
        tv_read_button.setBackgroundColor(getResources().getColor(R.color.light_blue));
        myBackListener = new MyBackListener(preIndex);
        treatmentMainActivity.iv_back.setOnClickListener(myBackListener);
        return view;
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
            signs = null;
            tv_read_button.setBackgroundColor(getResources().getColor(R.color.light_blue));
        }
        super.onHiddenChanged(hidden);
    }

    private void initData()
    {
        signsList = new ArrayList<>();
        VitalSign sign = null;
        Math.random();
        long base_time = 1472803910244l;
        for(int j = 0;j < 3;j++)
        {
            signs = new ArrayList<>();
            for(int i = 0;i < 10;i++)
            {
                sign = new VitalSign((75 + (int)(Math.random() * 25)), (50 + (int)(Math.random() * 25)), base_time);
                signs.add(sign);
                base_time = base_time + (int)(Math.random() * 9000) + 1000;
                Log.e("TEST", new SimpleDateFormat("dd:mm:ss sss").format(base_time));
            }
            signsList.add(signs);
        }
        for(int i = 0;i < signsList.get(0).size();i++)
        {
            Log.e("TEST", signsList.get(0).get(i).getSpo2() + "---" + signsList.get(0).get(i).getBpm() + "----" + new SimpleDateFormat("dd:mm:ss sss").format(signsList.get(0).get(i).getTimestamp()));
        }

//        sign = new VitalSign(97, 69, 1472804154918l);
//        signs.add(sign);
//        sign = new VitalSign(99, 71, 1472804191443l);
//        signs.add(sign);
//        signsList.add(signs);

//        signs = new ArrayList<>();
//        sign = new VitalSign(99, 70, 1472803910244l);
//        signs.add(sign);
//        sign = new VitalSign(97, 70, 1472804154918l);
//        signs.add(sign);
//        sign = new VitalSign(96, 71, 1472804191443l);
//        signs.add(sign);
//        signsList.add(signs);
//
//        signs = new ArrayList<>();
//        sign = new VitalSign(97, 70, 1472803910244l);
//        signs.add(sign);
//        sign = new VitalSign(97, 69, 1472804154918l);
//        signs.add(sign);
//        sign = new VitalSign(96, 70, 1472804191443l);
//        signs.add(sign);
//        signsList.add(signs);


        //重置signs为null，该对象是表示被选中的记录的
        signs = null;
    }

    public static VitalSignsFragment newInstance(Prioritise prioritise, int preIndex) {
        
        if(fragment == null)
        {
            fragment = new VitalSignsFragment();
        }
        fragment.prioritise = prioritise;
        fragment.preIndex = preIndex;
        return fragment;
    }
    private class MyOnItemClickListener implements AdapterView.OnItemClickListener
    {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if(signs == signsList.get(position))
            {
                signs = null;
                tv_read_button.setBackgroundColor(getResources().getColor(R.color.light_blue));
            }
            else
            {
                signs = signsList.get(position);
                tv_read_button.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            }
        }
    }
}
