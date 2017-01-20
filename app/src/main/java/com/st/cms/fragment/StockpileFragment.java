package com.st.cms.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.st.cms.activity.TreatmentMainActivity;
import com.st.cms.adapter.MedicalResAdapter;
import com.st.cms.adapter.StockPileAdapter;
import com.st.cms.entity.Prioritise;

import cms.st.com.poccms.R;

/**
 * Created by jt on 2016/8/31.
 */
public class StockpileFragment extends TreatmentBaseFragment{

    private static StockpileFragment fragment = null;
    private static String title = "";
    private ListView lv_stockpile;
    private String[] resNames = new String[]{"Blood Bank", "Medical Supplies", "Drug Supplies"};
    private int preIndex = -1;
    private MyBackListener myBackListener;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stockpile, container, false);
        treatmentMainActivity = (TreatmentMainActivity)getActivity();
        lv_stockpile = (ListView)view.findViewById(R.id.lv_stockpile);
        lv_stockpile.setAdapter(new StockPileAdapter(getContext(), resNames));
        lv_stockpile.setOnItemClickListener(new MyStockpileOnItemClickListener());
        treatmentMainActivity.tv_title.setText("Stockpile");
        myBackListener = new MyBackListener(preIndex);
        treatmentMainActivity.iv_back.setOnClickListener(myBackListener);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
//        treatmentMainActivity.tv_title.setText("Stockpile");
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
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
            treatmentMainActivity.tv_title.setText("Stockpile");
        }
    }

    public static StockpileFragment newInstance(Prioritise prioritise, int preIndex) {


        if(fragment == null)
        {
            fragment = new StockpileFragment();
        }
        fragment.prioritise = prioritise;
        fragment.preIndex = preIndex;
        return fragment;
    }
    private class MyStockpileOnItemClickListener implements AdapterView.OnItemClickListener
    {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            jumpAction(position);
        }
    }
    private void jumpAction(int position)
    {
        int index = 0;
        MedicalResListFragment medicalResListFragment = MedicalResListFragment.newInstance(prioritise, resNames[position], treatmentMainActivity.currentIndex);
        if(treatmentMainActivity.list_Fragment.contains(medicalResListFragment))
        {
            medicalResListFragment.setTitle(resNames[position]);
            index = treatmentMainActivity.list_Fragment.indexOf(medicalResListFragment);
        }
        else
        {
            index = treatmentMainActivity.list_Fragment.size();
            treatmentMainActivity.list_Fragment.add(index, MedicalResListFragment.newInstance(prioritise, resNames[position], treatmentMainActivity.currentIndex));
        }
        //这边需要传入prioritise对象


        treatmentMainActivity.switchFragment(index);
    }
}
