package com.st.cms.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.st.cms.activity.TreatmentMainActivity;
import com.st.cms.adapter.SignsLogAdapter;
import com.st.cms.entity.Prioritise;
import com.st.cms.entity.VitalSign;

import java.util.List;

import cms.st.com.poccms.R;

/**
 * Created by jt on 2016/9/6.
 */
public class LogHistoryFragment extends TreatmentBaseFragment{

    private static LogHistoryFragment fragment;
    public List<VitalSign> signs;
    private String title;
    private ListView log_list;
    private int preIndex = -1;
    private MyBackListener myBackListener;
    public void setSigns(List<VitalSign> signs) {
        this.signs = signs;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_loghistory, container, false);
        log_list = (ListView)view.findViewById(R.id.log_list);
        log_list.setAdapter(new SignsLogAdapter(getContext(), signs));
        treatmentMainActivity = (TreatmentMainActivity)getActivity();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        signs = null;
        treatmentMainActivity.tv_title.setText(title);
        myBackListener = new MyBackListener(preIndex);
        treatmentMainActivity.iv_back.setOnClickListener(myBackListener);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden)
        {
            treatmentMainActivity.tv_title.setText(prioritise.getTagId());
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
    public static LogHistoryFragment newInstance(Prioritise prioritise, String title, List<VitalSign> signs, int preIndex) {

        if(fragment == null)
        {
            fragment = new LogHistoryFragment();
        }
        fragment.prioritise = prioritise;
        fragment.title = title;
        fragment.signs = signs;
        fragment.preIndex = preIndex;
        return fragment;
    }
}
