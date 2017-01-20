package com.st.cms.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.st.cms.activity.ConveyListActivity;
import com.st.cms.adapter.VictimListAdapter;
import com.st.cms.application.PocApplication;
import com.st.cms.entity.Prioritise;

import cms.st.com.poccms.R;

/**
 * Created by jt on 2016/9/7.
 */
public class ConveyListFragment extends ConveyBaseFragment{
    private ListView victim_list;

    private TextView tv_btn;
    private VictimListAdapter victimListAdapter = null;
    private static ConveyListFragment fragment;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_fragment, container, false);
        tv_btn = (TextView)view.findViewById(R.id.tv_btn);
        victim_list = (ListView)view.findViewById(R.id.victim_list);

        victimListAdapter = new VictimListAdapter(getContext(), PocApplication.getPocApplication().getPersons());
        victim_list.setAdapter(victimListAdapter);
        victim_list.setOnItemClickListener(new MyOnItemClickListener());

        conveyListActivity.initToolBar("main");
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if(!hidden)
        {
            conveyListActivity.initToolBar("main");
        }
        super.onHiddenChanged(hidden);
    }

    private class MyOnItemClickListener implements AdapterView.OnItemClickListener
    {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ConveyMapFragment fragment = ConveyMapFragment.newInstance((Prioritise)parent.getAdapter().getItem(position), conveyListActivity.currentIndex);
            jumpAction(fragment);
        }
    }
}
