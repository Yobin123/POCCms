package com.st.cms.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.st.cms.entity.VitalSign;

import java.util.List;

import cms.st.com.poccms.R;

/**
 * Created by jt on 2016/9/2.
 */
public class SignsAdapter extends BaseAdapter{

    private Context context;
    private List<List<VitalSign>> signsList = null;
    public SignsAdapter(Context context, List<List<VitalSign>> signsList) {
        this.context = context;
        this.signsList = signsList;
    }

    @Override
    public int getCount() {
        return signsList.size();
    }

    @Override
    public Object getItem(int position) {
        return signsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null)
        {
            convertView = LayoutInflater.from(context).inflate(R.layout.adapter_oximeter, parent, false);
        }
        ViewHolder holder = new ViewHolder();
        if(signsList.get(position) != null)
        {
            holder.oximeter_record = (TextView)convertView.findViewById(R.id.oximeter_record);
            holder.oximeter_record.setText(("Oximeter " + (position + 1)));
            return convertView;
        }
        return null;
    }
    private class ViewHolder
    {
        public TextView oximeter_record;
    }
}
