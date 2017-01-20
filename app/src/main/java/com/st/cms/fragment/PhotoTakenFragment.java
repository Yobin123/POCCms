package com.st.cms.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.st.cms.activity.CameraActivity;
import com.st.cms.activity.IdentyfyActivity;
import com.st.cms.entity.Prioritise;
import com.st.cms.utils.IntentGoTo;

import cms.st.com.poccms.R;


public class PhotoTakenFragment extends IdentifyBaseFragment implements View.OnClickListener{
    private static final String TAG = "PhotoTakenFragment";
    private TextView tv_fingerprint_connect;
    private Button bt_fingerprint_scan;
    private TextView tv_fingerprint_explain;
    private static PhotoTakenFragment fragment;

    private int preIndex = -1;
    private MyZoneBackListener myBackListener;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fingerprintconnect, container, false);
        tv_fingerprint_connect = (TextView) view.findViewById(R.id.tv_fingerprint_connect);
        bt_fingerprint_scan = (Button) view.findViewById(R.id.bt_fingerprint_scan);
        tv_fingerprint_explain = (TextView) view.findViewById(R.id.tv_fingerprint_explain);
        identyfyActivity = (IdentyfyActivity) getActivity();
        return view;
    }

    //这是交换了相应标志位
    @Override
    public void onResume() {
        super.onResume();
        myBackListener = new MyZoneBackListener(preIndex);
        identyfyActivity.iv_back.setOnClickListener(myBackListener);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        /**
         * 由于与连接指纹读取使用同一个layout，故删除相应的连接提示;
         */
        tv_fingerprint_explain.setVisibility(View.GONE);
        tv_fingerprint_connect.setVisibility(View.GONE);
        bt_fingerprint_scan.setText("Take face photo");
        bt_fingerprint_scan.setOnClickListener(this);

    }

    public static PhotoTakenFragment newInstance(Prioritise prioritise,int preIndex) {

        Bundle args = new Bundle();
        if(fragment == null)
        {
            fragment = new PhotoTakenFragment();
            fragment.setArguments(args);
        }

        fragment. prioritise= prioritise;
        fragment.preIndex = preIndex;
        return fragment;
    }
//    public static PhotoTakenFragment newInstance() {
//
//        if (fragment==null){
//            fragment= new PhotoTakenFragment();
//        }
//
//        return fragment;
//    }

    @Override
    public void onClick(View view) {
        //点击跳转到相应的拍照阶段
        Toast.makeText(getContext(), "此处应该跳到拍照的activity", Toast.LENGTH_SHORT).show();
        IntentGoTo.intentActivity(getContext(), CameraActivity.class,prioritise.getTagId());
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden)
        {
            identyfyActivity.tv_title.setText(prioritise.getTagId());
            if (myBackListener != null){
                myBackListener.setPreIndex(preIndex);
            }else {
                myBackListener = new MyZoneBackListener(preIndex);
            }
            identyfyActivity.iv_back.setOnClickListener(new MyZoneBackListener(preIndex));
        }
        super.onHiddenChanged(hidden);
    }


}
