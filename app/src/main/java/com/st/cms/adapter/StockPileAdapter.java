package com.st.cms.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import cms.st.com.poccms.R;

/**
 * Created by jt on 2016/8/31.
 */
public class StockPileAdapter extends BaseAdapter{

    private String[] resNames = null;

    private Context context;
    public StockPileAdapter(Context context, String[] resNames) {
        this.context = context;
        this.resNames = resNames;
    }

    @Override
    public int getCount() {
        return resNames.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null)
        {
            convertView = LayoutInflater.from(context).inflate(R.layout.adapter_stockpile, parent, false);
        }
        if (resNames == null)
        {
            return null;
        }
        ViewHolder holder = new ViewHolder();
        holder.tv_res_name = (TextView)convertView.findViewById(R.id.tv_res_name);
        holder.tv_res_name.setText(resNames[position]);
        return convertView;
    }
    private class ViewHolder
    {
        public TextView tv_res_name;
        public ImageView iv_detail;
    }
}
