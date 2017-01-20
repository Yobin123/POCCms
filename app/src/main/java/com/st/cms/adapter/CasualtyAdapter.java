package com.st.cms.adapter;

/**
 * Created by CW on 2016/8/24.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;


import com.st.cms.entity.Prioritise;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cms.st.com.poccms.R;

/**
 * point to casultyList adapter
 */
public class CasualtyAdapter extends BaseAdapter {
    private static final String TAG = "CasualtyAdapter";
    private Context context = null;
//    private List<Map<String, String>> mList = null;
    private boolean flag; //是否显示控件的状态
    public static HashMap<Integer, Boolean> checkbox_state;//保存的hashMap的状态
    public int checkedNum = 0;//选中的数量
    private OnTransferDataListener listener;

    private List<Prioritise> mList;
    public CasualtyAdapter(Context context, List<Prioritise> mList,
                           boolean flag, OnTransferDataListener listener) {
        this.context = context;
        this.mList = mList;
        this.flag = flag;
        this.listener = listener;
        checkbox_state = new HashMap<>();
        initState();
    }

    //初始化checkbox_state中的状态
    private void initState() {
        for (int i = 0; i < mList.size(); i++) {
            getIsSelected().put(i, false);
        }
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int i) {
        return mList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup viewGroup) {
        final ViewHolder mHolder;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.item_list_zone2, null, true);
            mHolder = new ViewHolder(convertView);
            convertView.setTag(mHolder);
        } else {
            mHolder = (ViewHolder) convertView.getTag();
        }
        Prioritise prioritise = mList.get(position);
        if (mList!=null){
            mHolder.tv_id_num.setText(prioritise.getTagId());
            if(prioritise.getVictim().getName().length() > 0)
            {
                mHolder.tv_name.setText(prioritise.getVictim().getName());
                mHolder.tv_name.setVisibility(View.VISIBLE);
            }

        }
//        //这是进行相应的加载
//        if (mList.get(position).get("names") != "") {
//            mHolder.tv_name.setVisibility(View.VISIBLE);
//            mHolder.tv_name.setText(mList.get(position).get("names"));
//        }else {
//            mHolder.tv_name.setVisibility(View.GONE);
//        }
//        mHolder.tv_id_num.setText(mList.get(position).get("ids"));

        //判断相应的checkbox是否是隐藏状态
        if (flag == true) {
            mHolder.rb_listview_select.setVisibility(View.VISIBLE);
            //对chekbox进行相应的点击事件
            mHolder.rb_listview_select.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    //获取相应的选中状态，checkbox的监听来改变相应的Map中的状态
                    checkbox_state.put(position, !getIsSelected().get(position));
                    int sum = 0;
                    for (int key : checkbox_state.keySet()) {
                        if (checkbox_state.get(key) == true) {
                            sum++;
                        }
                    }
                    checkedNum = sum;
                    //这里是要回调的地点，将需要回调的值传入faceFragment
                    listener.onTranseferData(checkedNum, checkbox_state);
                }
            });
            mHolder.rb_listview_select.setChecked(false);
        } else {
            mHolder.rb_listview_select.setVisibility(View.GONE);
        }

        return convertView;
    }

    public static class ViewHolder {
        public TextView tv_name;
        public TextView tv_id_num;
        public ImageView iv_avatar;
        public CheckBox rb_listview_select;

        public ViewHolder(View convertView) {
            tv_id_num = (TextView) convertView.findViewById(R.id.tv_id_num);
            tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            iv_avatar = (ImageView) convertView.findViewById(R.id.iv_avatar);
            rb_listview_select = (CheckBox) convertView.findViewById(R.id.rb_listview_select);
        }
    }

    //    进行页面的刷新
    public void isRefresh(List<Prioritise> list, boolean flag) {
        this.flag = flag;
        mList = list;
        notifyDataSetChanged();
    }

    public static HashMap<Integer, Boolean> getIsSelected() {
        return checkbox_state;
    }

    public interface OnTransferDataListener {
        void onTranseferData(int checkedNum, HashMap<Integer, Boolean> map);
    }

    public void setOnTransferDataListener() {
        new CasualtyAdapter(context, mList, flag, listener);
    }

    public void removeItems(List<Map<String, String>> _list) {
        mList.removeAll(_list);
        notifyDataSetChanged();
    }
}
