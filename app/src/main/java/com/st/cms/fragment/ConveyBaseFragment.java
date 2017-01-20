package com.st.cms.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.st.cms.activity.ConveyListActivity;
import com.st.cms.entity.Detail;
import com.st.cms.entity.Prioritise;

/**
 * Created by jt on 2016/9/7.
 */
public class ConveyBaseFragment extends Fragment{
    public ConveyListActivity conveyListActivity;
    public Prioritise prioritise;
    public Detail detail;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        conveyListActivity = (ConveyListActivity)getActivity();
    }

    public void jumpAction(ConveyBaseFragment fragment)
    {
        int index = 0;
        if(conveyListActivity.list_Fragment.contains(fragment))
        {
            index = conveyListActivity.list_Fragment.indexOf(fragment);
        }
        else
        {
            index = conveyListActivity.list_Fragment.size();
            conveyListActivity.list_Fragment.add(index, fragment);
        }
        //这边需要传入prioritise对象


        conveyListActivity.switchFragment(index);
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
            Log.e("TEST", "pre" + preIndex + "---current" + conveyListActivity.currentIndex);
            if(preIndex != -1)
            {
                conveyListActivity.changeFragment(preIndex, conveyListActivity.currentIndex);
            }
        }
    }
}
