package com.st.cms.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.st.cms.entity.MedicalRes;

import java.util.List;

import cms.st.com.poccms.R;

/**
 * Created by jt on 2016/9/1.
 */
public class MedicalResAdapter extends BaseAdapter{
    private Context context;
    private List<MedicalRes> reses = null;
    public MedicalResAdapter(Context context, List<MedicalRes> reses) {
        this.context = context;
        this.reses = reses;
    }

    @Override
    public int getCount() {
        return reses.size();
    }

    @Override
    public Object getItem(int position) {
        return reses.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null)
        {
            convertView = LayoutInflater.from(context).inflate(R.layout.adapter_medicalres, parent, false);
        }
        MedicalRes res = reses.get(position);
        if(res != null)
        {
            ViewHolder holder = new ViewHolder();
            holder.tv_medical_name = (TextView)convertView.findViewById(R.id.tv_medical_name);
            holder.tv_medical_name.setText(res.getName());

            holder.tv_status = (TextView)convertView.findViewById(R.id.tv_status);

            holder.tv_medical_qty = (TextView)convertView.findViewById(R.id.tv_medical_qty);
            holder.tv_medical_qty.setText("" + (res.getQty() - res.getConsume()));

            holder.tv_original_qty = (TextView)convertView.findViewById(R.id.tv_original_qty);
            holder.tv_original_qty.setText("" + res.getQty());

            holder.tv_assist_res = (TextView)convertView.findViewById(R.id.tv_assist_res);

            holder.tv_used_res = (TextView)convertView.findViewById(R.id.tv_used_res);
            holder.tv_used_res.setText("" + res.getConsume());

        }
        return convertView;
    }
    private class ViewHolder
    {
        public TextView tv_medical_name;
        public TextView tv_status;
        public TextView tv_medical_qty;
        public TextView tv_original_qty;
        public TextView tv_assist_res;
        public TextView tv_used_res;

    }
}
