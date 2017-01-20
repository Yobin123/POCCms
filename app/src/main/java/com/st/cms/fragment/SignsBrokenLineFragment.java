package com.st.cms.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.st.cms.activity.TreatmentMainActivity;
import com.st.cms.entity.Prioritise;
import com.st.cms.entity.VitalSign;
import com.st.cms.widget.BPMView;
import com.st.cms.widget.Spo2View;

import java.util.List;

import cms.st.com.poccms.R;

/**
 * Created by jt on 2016/9/5.
 */
public class SignsBrokenLineFragment extends TreatmentBaseFragment{

    private static SignsBrokenLineFragment fragment;
    private static String title = "";
    private BPMView bpm_view;
    private Spo2View spo2_view;
    private List<VitalSign> signs;
    private int preIndex = -1;
    private MyBackListener myBackListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_broken_line, container, false);
        bpm_view = (BPMView)view.findViewById(R.id.bpm_view);
        spo2_view = (Spo2View)view.findViewById(R.id.spo2_view);
        treatmentMainActivity = (TreatmentMainActivity)getActivity();
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogHistoryFragment fragment = LogHistoryFragment.newInstance(prioritise, "LogHistory", signs, treatmentMainActivity.currentIndex);
                jumpAction(fragment);
            }
        });
        bpm_view.setSigns(signs);
        spo2_view.setSigns(signs);
        bpm_view.invalidate();
        spo2_view.invalidate();
        treatmentMainActivity.tv_title.setText(prioritise.getTagId());
        myBackListener = new MyBackListener(preIndex);
        treatmentMainActivity.iv_back.setOnClickListener(myBackListener);
        return view;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if(!hidden)
        {
            bpm_view.setSigns(signs);
            spo2_view.setSigns(signs);
            bpm_view.invalidate();
            spo2_view.invalidate();
            if(myBackListener != null)
            {
                myBackListener.setPreIndex(preIndex);
            }
            else
            {
                myBackListener = new MyBackListener(preIndex);
            }
            treatmentMainActivity.iv_back.setOnClickListener(new MyBackListener(preIndex));
            bpm_view.setSigns(signs);
            spo2_view.setSigns(signs);
            bpm_view.invalidate();
            spo2_view.invalidate();
            treatmentMainActivity.tv_title.setText(prioritise.getTagId());
        }
//        else if(hidden)
//        {
//            signs = null;
//        }
        super.onHiddenChanged(hidden);
    }

    public static SignsBrokenLineFragment newInstance(Prioritise prioritise, List<VitalSign> signs, int preIndex) {

        if(fragment == null)
        {
            fragment = new SignsBrokenLineFragment();
        }
        fragment.prioritise = prioritise;
//        fragment.title = title;
        fragment.signs = signs;
        fragment.preIndex = preIndex;
        return fragment;
    }
}
