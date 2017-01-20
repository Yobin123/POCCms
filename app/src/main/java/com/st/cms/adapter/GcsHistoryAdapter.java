package com.st.cms.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import cms.st.com.poccms.R;

/**
 * Created by CW on 2016/9/7.
 */
public class GcsHistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context = null;
    private List<Map<String, String>> list = null;
    private LayoutInflater inflater = null;

    public GcsHistoryAdapter(Context context, List<Map<String, String>> list) {
        this.list = list;
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_recyclerview_gcshistory, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((ViewHolder)holder).tv_gcshistory_eyeopen.setText(list.get(position).get("eyeopen"));
        ((ViewHolder)holder).tv_gcshistory_verbal.setText(list.get(position).get("verbal"));
        ((ViewHolder)holder).tv_gcshistory_motor.setText(list.get(position).get("motor"));
        int eyeopen_count = Integer.parseInt(list.get(position).get("eyeopen"));
        int verbal_count = Integer.parseInt(list.get(position).get("verbal"));
        int motor_count = Integer.parseInt(list.get(position).get("motor"));
        int total = eyeopen_count+verbal_count+motor_count;
        ((ViewHolder)holder).tv_gcshistory_total.setText("total:"+total);
        ((ViewHolder)holder).tv_gcshistory_date.setText(getCanlender());
        ((ViewHolder)holder).tv_gcshistory_time.setText(list.get(position).get("time"));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_gcshistory_eyeopen, tv_gcshistory_verbal, tv_gcshistory_motor,
                tv_gcshistory_total, tv_gcshistory_date, tv_gcshistory_time;

        public ViewHolder(View convertView) {
            super(convertView);
            tv_gcshistory_eyeopen = (TextView) convertView.findViewById(R.id.tv_gcshistory_eyeopen);
            tv_gcshistory_verbal = (TextView) convertView.findViewById(R.id.tv_gcshistory_verbal);
            tv_gcshistory_motor = (TextView) convertView.findViewById(R.id.tv_gcshistory_motor);
            tv_gcshistory_total = (TextView) convertView.findViewById(R.id.tv_gcshistory_total);
            tv_gcshistory_date = (TextView) convertView.findViewById(R.id.tv_gcshistory_date);
            tv_gcshistory_time = (TextView) convertView.findViewById(R.id.tv_gcshistory_time);
        }
    }
    public String getCanlender(){
        String date;
        Calendar calendar = Calendar.getInstance();
        String year = String.valueOf(calendar.get(Calendar.YEAR));
        String month = String.valueOf(calendar.get(Calendar.MONTH)+1);
        String day = String.valueOf(calendar.get(Calendar.DATE));
        date = day+"/"+month+"/"+year;
        return date;
    }

}
