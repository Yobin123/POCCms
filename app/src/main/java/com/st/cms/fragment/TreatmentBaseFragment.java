package com.st.cms.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;

import com.st.cms.activity.ConfirmActivity;
import com.st.cms.activity.TreatmentMainActivity;
import com.st.cms.entity.Detail;
import com.st.cms.entity.Prioritise;

import cms.st.com.poccms.R;

/**
 * Created by jt on 2016/8/29.
 */
public class TreatmentBaseFragment extends Fragment {
    public Detail detail = null;
    public TreatmentMainActivity treatmentMainActivity;
    public Prioritise prioritise;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        treatmentMainActivity = (TreatmentMainActivity)getActivity();
        treatmentMainActivity.initToolBar("content");
        treatmentMainActivity.tv_title.setText(prioritise.getTagId());
    }

    @Override
    public void onResume() {
        super.onResume();
    }
    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden)
        {
            TreatmentMainActivity treatmentMainActivity = (TreatmentMainActivity)getActivity();
            treatmentMainActivity.initToolBar("content");
            treatmentMainActivity.tv_title.setText(prioritise.getTagId());
        }
        else
        {
            if(treatmentMainActivity.fab.getVisibility() == View.VISIBLE)
            {
                treatmentMainActivity.fab.setVisibility(View.GONE);
            }
        }
        super.onHiddenChanged(hidden);
    }
    public void jumpAction(TreatmentBaseFragment fragment)
    {
        int index = 0;
        if(treatmentMainActivity.list_Fragment.contains(fragment))
        {
            index = treatmentMainActivity.list_Fragment.indexOf(fragment);
        }
        else
        {
            index = treatmentMainActivity.list_Fragment.size();
            treatmentMainActivity.list_Fragment.add(index, fragment);
        }
        //这边需要传入prioritise对象


        treatmentMainActivity.switchFragment(index);
    }
    public void showFab(Boolean hidden)
    {
        if(hidden)
        {
            treatmentMainActivity.fab.setVisibility(View.GONE);
        }
        else
        {
            treatmentMainActivity.fab.setVisibility(View.VISIBLE);
        }
    }
    public class MyBackListener implements View.OnClickListener
    {

        private int preIndex = -1;

        public void setPreIndex(int preIndex) {
            this.preIndex = preIndex;
        }

        public MyBackListener(int preIndex) {
            this.preIndex = preIndex;
        }

        @Override
        public void onClick(View v) {
            Log.e("TEST", "pre" + preIndex + "---current" + treatmentMainActivity.currentIndex);
            if(preIndex != -1)
            {
                treatmentMainActivity.changeFragment(preIndex, treatmentMainActivity.currentIndex);
            }
        }
    }
    public void saveSuccess(final int preIndex)
    {
        treatmentMainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getContext(), ConfirmActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("status", 0);
                bundle.putString("confirm", getResources().getString(R.string.ok));
                bundle.putString("info", getResources().getString(R.string.mark_info));
                intent.putExtras(bundle);
                startActivity(intent);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                treatmentMainActivity.changeFragment(preIndex, treatmentMainActivity.currentIndex);
            }
        });
    }
}
