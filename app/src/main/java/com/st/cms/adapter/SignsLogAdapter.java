package com.st.cms.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.st.cms.entity.VitalSign;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import cms.st.com.poccms.R;

/**
 * Created by jt on 2016/9/6.
 */
public class SignsLogAdapter extends BaseAdapter{

    private Context context;
    private List<VitalSign> signs;
    private SimpleDateFormat sdf_date;
    private SimpleDateFormat sdf_time;

    public SignsLogAdapter(Context context, List<VitalSign> signs) {
        this.context = context;
        this.signs = signs;
        sdf_date = new SimpleDateFormat("MM/dd/yy");
        sdf_time = new SimpleDateFormat("hh:mm:ss a", Locale.US);
    }

    @Override
    public int getCount() {
        return signs.size();
    }

    @Override
    public Object getItem(int position) {
        return signs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null)
        {
            convertView = LayoutInflater.from(context).inflate(R.layout.adapter_signslog, parent, false);
        }
        VitalSign sign = signs.get(position);
        if(sign != null)
        {
            ViewHolder holder = new ViewHolder();
            holder.tv_log_spo2 = (TextView)convertView.findViewById(R.id.tv_log_spo2);
            holder.tv_log_bpm = (TextView)convertView.findViewById(R.id.tv_log_bpm);
            holder.tv_log_date = (TextView)convertView.findViewById(R.id.tv_log_date);
            holder.tv_log_time = (TextView)convertView.findViewById(R.id.tv_log_time);
            holder.tv_log_spo2.setText(sign.getSpo2() + " %SpO2");
            holder.tv_log_bpm.setText(sign.getBpm() + " BPM");
            holder.tv_log_date.setText(sdf_date.format(sign.getTimestamp()) + "");
            holder.tv_log_time.setText(sdf_time.format(sign.getTimestamp()) + "");
            return convertView;
        }
        return null;
    }
    private class ViewHolder
    {
        public TextView tv_log_spo2;
        public TextView tv_log_bpm;
        public TextView tv_log_date;
        public TextView tv_log_time;
    }
}
