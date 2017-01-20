package com.st.cms.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.st.cms.activity.IdentyfyActivity;
import com.st.cms.entity.Prioritise;

import cms.st.com.poccms.R;


public class PersonInformationFragment extends IdentifyBaseFragment {

    private static PersonInformationFragment fragment;
    private LinearLayout mInformaion;
    private boolean flag = false;
    private EditText et_detail_nric;
    private EditText et_detail_name;
    private EditText et_detail_gender;
    private EditText et_detail_blood;
    private EditText et_detail_history;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_person_information, container, false);
        mInformaion = (LinearLayout) view.findViewById(R.id.llayout_personInformation);
        et_detail_nric = (EditText) view.findViewById(R.id.et_detail_nric);
        et_detail_name = (EditText) view.findViewById(R.id.et_detail_name);
        et_detail_gender = (EditText) view.findViewById(R.id.et_detail_gender);
        et_detail_blood = (EditText) view.findViewById(R.id.et_detail_blood);
        et_detail_history = (EditText) view.findViewById(R.id.et_detail_history);
        identyfyActivity = (IdentyfyActivity) getActivity();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //进行长按可以进行相应的编辑
        mInformaion.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                et_detail_nric.setEnabled(true);
                et_detail_name.setEnabled(true);
                et_detail_gender.setEnabled(true);
                et_detail_blood.setEnabled(true);
                et_detail_history.setEnabled(true);
                return true;
            }
        });
    }


    //进行相应的单例模式
    public static PersonInformationFragment newInstance() {
        PersonInformationFragment fragment = new PersonInformationFragment();
        return fragment;
    }

    public static PersonInformationFragment newInstance(Prioritise prioritise) {

        if (fragment == null) {
            fragment = new PersonInformationFragment();
        }
        fragment.prioritise = prioritise;
        return fragment;
    }

}
