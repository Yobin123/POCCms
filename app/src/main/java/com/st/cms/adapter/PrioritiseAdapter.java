package com.st.cms.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import cms.st.com.poccms.R;

/**
 * Created by jt on 2016/8/18.
 */
public class PrioritiseAdapter extends BaseAdapter{

    private Context context;
    private View.OnClickListener onClickListener;
    //级别
    private String[] prioritises = null;
    //背景色
    private int[] colors = new int[]{
            R.color.black, R.color.tangerine, R.color.yellow, R.color.green, R.color.lime
    };
    //字体颜色
    private int[] text_colors = new int[]{
            R.color.white, R.color.white, R.color.black, R.color.black, R.color.black
    };
    private EditTextListener listener;
    public PrioritiseAdapter(Context context)
    {
        this.context = context;
    }

    public PrioritiseAdapter(Context context, String[] prioritises) {
        this.context = context;
        this.prioritises = prioritises;
    }

    @Override
    public int getCount() {
        return prioritises.length;
    }

    @Override
    public Object getItem(int position) {
        return prioritises[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
//        if(position == prioritises.length -1){
//            if(convertView == null){
//                convertView = LayoutInflater.from(context).inflate(R.layout.dialog_ll_showbagid,null);
//            }
//            EditText editText = (EditText) convertView.findViewById(R.id.editText);
//            Log.i("----->>", "getView: " + editText.getText().toString());
//            listener.getEditTextListener(editText);
//        }else {
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.adapter_prioritise, null);
            }
            TextView tv_lv = (TextView)convertView.findViewById(R.id.tv_lv);
            int color = context
                    .getResources().getColor(text_colors[position]);
            tv_lv.setText(prioritises[position]);
            tv_lv.setTextColor(color);
            tv_lv.setBackgroundResource(colors[position]);
//        }

        return convertView;
    }
    public interface EditTextListener{
         void getEditTextListener(View view);
    }
}
