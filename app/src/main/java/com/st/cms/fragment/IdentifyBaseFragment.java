package com.st.cms.fragment;

import android.support.v4.app.Fragment;
import android.view.View;

import com.st.cms.activity.IdentyfyActivity;
import com.st.cms.entity.Prioritise;

/**
 * Created by CW on 2016/9/10.
 */
public class IdentifyBaseFragment extends Fragment {
    public IdentyfyActivity identyfyActivity;
    public Prioritise prioritise;
    @Override
    public void onResume() {
        identyfyActivity = (IdentyfyActivity)getActivity();
        identyfyActivity.initToolBar("content");
        if(prioritise != null)
        {
            identyfyActivity.tv_title.setText(prioritise.getTagId());
        }
        super.onResume();
    }
    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden)
        {
            IdentyfyActivity identyfyActivity = (IdentyfyActivity)getActivity();
            identyfyActivity.initToolBar("content");
        }
        super.onHiddenChanged(hidden);
    }
    public void jumpAction(IdentifyBaseFragment fragment)
    {
        int index = 0;
        if(identyfyActivity.list_Fragment.contains(fragment))
        {
            index = identyfyActivity.list_Fragment.indexOf(fragment);
        }
        else
        {
            index = identyfyActivity.list_Fragment.size();
            identyfyActivity.list_Fragment.add(index, fragment);
        }
        //这边需要传入prioritise对象


        identyfyActivity.switchFragment(index);
    }
    public void showFab(Boolean hidden)
    {
        if(hidden)
        {
            identyfyActivity.fab.setVisibility(View.GONE);
        }
        else
        {
            identyfyActivity.fab.setVisibility(View.VISIBLE);
        }
    }
    public class MyZoneBackListener implements View.OnClickListener
    {

        private int preIndex = -1;

        public void setPreIndex(int preIndex) {
            this.preIndex = preIndex;
        }

        public MyZoneBackListener(int preIndex) {
            this.preIndex = preIndex;
        }

        @Override
        public void onClick(View v) {

            if(preIndex != -1)
            {
                identyfyActivity.changeFragment(preIndex, identyfyActivity.currentIndex);
            }
        }
    }
}
