package com.st.cms.fragment;

import android.content.Context;
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

import com.st.cms.activity.TreatmentMainActivity;
import com.st.cms.adapter.VictimListAdapter;
import com.st.cms.application.PocApplication;
import com.st.cms.entity.Prioritise;

import cms.st.com.poccms.R;

/**
 * Created by jt on 2016/8/24.
 */
public class TreatmentMainFragment extends Fragment implements View.OnClickListener{

    private ListView victim_list;
    private String type;
    private TextView tv_btn;
    private Prioritise prioritise = null;
    private VictimListAdapter victimListAdapter = null;
    private TreatmentMainActivity treatmentMainActivity;

    public String getType() {
        return type;
    }

    @Override
    public void setArguments(Bundle args) {
        type = args.getString("type");
        super.setArguments(args);
    }

    @Override
    public Context getContext() {
        return super.getContext();
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_fragment, container, false);
        tv_btn = (TextView)view.findViewById(R.id.tv_btn);
        tv_btn.setOnClickListener(this);
        victim_list = (ListView)view.findViewById(R.id.victim_list);
        if(PocApplication.getPocApplication().getPersons() != null)
        {
            victimListAdapter = new VictimListAdapter(getContext(), PocApplication.getPocApplication().getPersons());
            victim_list.setAdapter(victimListAdapter);
            victim_list.setOnItemLongClickListener(new MyListOnItemLongClickListener());
        }
        treatmentMainActivity = (TreatmentMainActivity)getActivity();
        treatmentMainActivity.initToolBar("main");
        victim_list.setOnItemClickListener(new MyListOnItemClickListener(treatmentMainActivity));
        treatmentMainActivity.fab.setVisibility(View.GONE);
        return view;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden)
        {
            treatmentMainActivity = (TreatmentMainActivity)getActivity();
            Log.e("TEST", hidden + "hiddenchange");
            treatmentMainActivity.initToolBar("main");
            victim_list.setOnItemClickListener(new MyListOnItemClickListener(treatmentMainActivity));
        }
        if(treatmentMainActivity.fab.getVisibility() == View.VISIBLE)
        {
            treatmentMainActivity.fab.setVisibility(View.GONE);
        }
        super.onHiddenChanged(hidden);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.tv_btn:
                //判断当前是哪个fragment
                switch (type)
                {
                    case "mark":
                        break;
                    case "treat":
                        break;
                    case "sign":
                        break;
                    case "gcs":
                        break;
                }
                break;
            default:
                break;
        }
    }

    private class MyListOnItemLongClickListener implements AdapterView.OnItemLongClickListener
    {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            if(prioritise == (Prioritise) victimListAdapter.getItem(position))
            {
                tv_btn.setVisibility(View.GONE);
                prioritise = null;
            }
            else
            {
                prioritise = (Prioritise) victimListAdapter.getItem(position);
                tv_btn.setVisibility(View.VISIBLE);
            }
            return true;
        }
    }
    private class MyListOnItemClickListener implements AdapterView.OnItemClickListener
    {
        private TreatmentMainActivity treatmentMainActivity;

        public MyListOnItemClickListener(TreatmentMainActivity treatmentMainActivity) {
            this.treatmentMainActivity = treatmentMainActivity;
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //判断当前的radiobutton
            int checkNum = treatmentMainActivity.getCheckedNum();
            Prioritise prioritise = null;
            prioritise = (Prioritise)victimListAdapter.getItem(position);
            int index = 0;
            switch (checkNum)
            {
                case 0:

                    MarkPointFragment markPointFragment = MarkPointFragment.newInstance(prioritise, checkNum);
                    if(treatmentMainActivity.list_Fragment.contains(markPointFragment))
                    {
                        index = treatmentMainActivity.list_Fragment.indexOf(markPointFragment);
                    }
                    else
                    {
                        index = treatmentMainActivity.list_Fragment.size();
                        treatmentMainActivity.list_Fragment.add(index, MarkPointFragment.newInstance(prioritise, checkNum));
                    }
                    //这边需要传入prioritise对象


                    tv_btn.setVisibility(View.GONE);
                    treatmentMainActivity.switchFragment(index);
                    break;
                case 1:
                    MedicalAssignFragment medicalAssignFragment = MedicalAssignFragment.newInstance(prioritise, checkNum);
                    if(treatmentMainActivity.list_Fragment.contains(medicalAssignFragment))
                    {
                        index = treatmentMainActivity.list_Fragment.indexOf(medicalAssignFragment);
                    }
                    else
                    {
                        index = treatmentMainActivity.list_Fragment.size();
                        treatmentMainActivity.list_Fragment.add(index, MedicalAssignFragment.newInstance(prioritise, checkNum));
                    }
                    //这边需要传入prioritise对象


                    tv_btn.setVisibility(View.GONE);
                    treatmentMainActivity.switchFragment(index);
                    break;
                case 2:
                    VitalSignsFragment vitalSignsFragment = VitalSignsFragment.newInstance(prioritise, checkNum);
                    if(treatmentMainActivity.list_Fragment.contains(vitalSignsFragment))
                    {
                        index = treatmentMainActivity.list_Fragment.indexOf(vitalSignsFragment);
                    }
                    else
                    {
                        index = treatmentMainActivity.list_Fragment.size();
                        treatmentMainActivity.list_Fragment.add(index, VitalSignsFragment.newInstance(prioritise, checkNum));
                    }
                    //这边需要传入prioritise对象


                    tv_btn.setVisibility(View.GONE);
                    treatmentMainActivity.switchFragment(index);
                    break;
                case 3:
                    GcsFragment gcsFragment = GcsFragment.newInstance(prioritise, checkNum);
                    if(treatmentMainActivity.list_Fragment.contains(gcsFragment))
                    {
                        index = treatmentMainActivity.list_Fragment.indexOf(gcsFragment);
                    }
                    else
                    {
                        index = treatmentMainActivity.list_Fragment.size();
                        treatmentMainActivity.list_Fragment.add(index, GcsFragment.newInstance(prioritise, checkNum));
                    }
                    //这边需要传入prioritise对象


                    tv_btn.setVisibility(View.GONE);
                    treatmentMainActivity.switchFragment(index);
                    break;
                default:
                    break;
            }
        }
    }
}
