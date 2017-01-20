package com.st.cms.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.st.cms.entity.Injury;
import com.st.cms.entity.MedicalRes;

import java.util.ArrayList;
import java.util.List;

import cms.st.com.poccms.R;

/**
 * Created by jt on 2016/10/12.
 */
public class ShowMedicalAdapter extends BaseAdapter{

    private List<?> resources = null;
    private TextView tv_medical_detail = null;
    private Context context;
    public ShowMedicalAdapter(Context context, List<?> resources) {
        this.context = context;
        this.resources = resources;
        if(this.resources == null)
        {
            this.resources = new ArrayList<>();
        }
    }
    public void setResources(List<?> resources)
    {
        this.resources = resources;
    }
    @Override
    public int getCount() {
        return resources.size();
    }

    @Override
    public Object getItem(int position) {
        return resources.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null)
        {
            convertView = LayoutInflater.from(context).inflate(R.layout.adapter_show_medical, parent, false);
        }
        tv_medical_detail = (TextView) convertView.findViewById(R.id.tv_medical_detail);
        if(resources.get(position) instanceof  MedicalRes)
        {
            MedicalRes medicalRes = (MedicalRes)resources.get(position);
            if(medicalRes != null)
            {
                tv_medical_detail.setText(medicalRes.getName() + "(×" + medicalRes.getConsume() + ")");
                return convertView;
            }
        }
        if(resources.get(position) instanceof Injury)
        {
            Injury injury = (Injury)resources.get(position);
            if(injury != null)
            {
                tv_medical_detail.setText(injury.getPosition() + "(" + injury.getDepict() + ")");
                return convertView;
            }
        }
        return null;
    }
}
