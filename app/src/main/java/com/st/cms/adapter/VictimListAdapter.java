package com.st.cms.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.st.cms.entity.Prioritise;
import com.st.cms.entity.Victim;
import com.st.cms.entity.Wperson;

import java.util.ArrayList;
import java.util.List;

import cms.st.com.poccms.R;

/**
 * Created by jt on 2016/8/24.
 */
public class VictimListAdapter extends BaseAdapter {

    private Context context;
    //模拟数据
    private List<Prioritise> prioritises;
    private List<Wperson> persons = null;
//    String[] names = new String[]{"Lion", "Licha", "John", "Kim", "Amy", "Sunny"};
//    String[] names = new String[]{"", "", "John", "", "Amy", ""};
//    String[] ids = new String[]{"123456s", "754623a", "562143w", "789654z", "999888f", "989655j"};
//    String[] nirs = new String[]{"123456s", "754623a", "562143w", "789654z", "999888f", "989655j"};
    public VictimListAdapter(Context context, List<Wperson> persons) {
        this.context = context;
        prioritises = new ArrayList<>();
        this.persons = persons;
        Prioritise prioritise = null;
        if(persons != null)
        {
            for(int i = 0;i < persons.size();i++)
            {
                Wperson person = persons.get(i);
                prioritise = new Prioritise(person.getTag_id(), person.getTaglib_id(), new Victim(person.getName(), person.getNric()));
                prioritises.add(prioritise);
            }
        }
//        if(prioritises.size() == 0)
//        {
//            prioritises.add(new Prioritise(29, "ST0000000001", new Victim("Reuben Low", "aaaa")));
//        }
//        Log.e("TEST", "" + prioritises.size());
    }

    @Override
    public int getCount() {
        return prioritises.size();
    }

    @Override
    public Object getItem(int position) {
        return prioritises.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null)
        {
            convertView = LayoutInflater.from(context).inflate(R.layout.victim_list, null);
        }

        Prioritise prioritise = prioritises.get(position);
        if(prioritise != null)
        {
            ViewHolder holder = new ViewHolder();
            holder.tv_id_num = (TextView) convertView.findViewById(R.id.tv_id_num);
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            holder.iv_avatar = (ImageView)convertView.findViewById(R.id.iv_avatar);

            holder.tv_id_num.setText(prioritise.getTagId());
            if(prioritise.getVictim().getName().length() > 0)
            {
                holder.tv_name.setText(prioritise.getVictim().getName());
                holder.tv_name.setVisibility(View.VISIBLE);
            }
            return convertView;
        }
        return null;
    }
    private class ViewHolder
    {
        public TextView tv_name;
        public TextView tv_id_num;
        public ImageView iv_avatar;
    }
}
