package com.st.cms.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.st.cms.entity.MedicalRes;

import java.util.ArrayList;
import java.util.List;

import cms.st.com.poccms.R;

/**
 * Created by jt on 2016/9/26.
 */
public class ResListAdapter extends BaseAdapter {
    private List<MedicalRes> resources = null;
    private Context context;
    private ResListAdapter resListAdapter;

    public ResListAdapter(Context context, List<MedicalRes> resources) {
        this.context = context;
        this.resources = (resources == null ? new ArrayList<MedicalRes>() : resources);
        resListAdapter = this;
    }

    public void setResources(List<MedicalRes> resources) {
        this.resources = resources;
    }

    public List<MedicalRes> getResources() {
        return resources;
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        if(convertView == null)
        {
            convertView = LayoutInflater.from(context).inflate(R.layout.adapter_res_list, parent, false);
        }
        MedicalRes medicalRes = resources.get(position);
        if(medicalRes == null)
        {
            return null;
        }
        ViewHolder viewHolder = new ViewHolder();
        viewHolder.tv_res_name = (TextView)convertView.findViewById(R.id.tv_res_name);
        viewHolder.tv_res_qty = (TextView)convertView.findViewById(R.id.tv_res_qty);
        viewHolder.iv_delete = (ImageView) convertView.findViewById(R.id.iv_delete);
        viewHolder.tv_res_name.setText(medicalRes.getName());
        viewHolder.tv_res_qty.setText(medicalRes.getConsume() + "");
        viewHolder.iv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resources.remove(position);
                //通知listview进行重绘
                notifyDataSetChanged();
            }
        });
        return convertView;
    }
    private class ViewHolder
    {
        public TextView tv_res_name;
        public TextView tv_res_qty;
        public ImageView iv_delete;
    }
}
