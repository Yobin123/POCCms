package com.st.cms.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.st.cms.activity.IdentyfyActivity;
import com.st.cms.activity.ThumbReaderActivity;
import com.st.cms.entity.Prioritise;
import com.st.cms.utils.IntentGoTo;

import cms.st.com.poccms.R;


public class FingerprintconnectFragment extends IdentifyBaseFragment implements View.OnClickListener {
    private TextView tv_fingerprint_connect;
    private Button bt_fingerprint_scan;
    private static FingerprintconnectFragment fragment;
    private int preIndex = -1;
    private MyZoneBackListener myBackListener;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //进行相应的控件绑定
        View view = inflater.inflate(R.layout.fragment_fingerprintconnect, container, false);
        tv_fingerprint_connect = (TextView) view.findViewById(R.id.tv_fingerprint_connect);
        bt_fingerprint_scan = (Button) view.findViewById(R.id.bt_fingerprint_scan);
        identyfyActivity = (IdentyfyActivity) getActivity();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        bt_fingerprint_scan.setText("Start Fingerprint Scan");
        bt_fingerprint_scan.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        //跳转到相应的指纹识别阶段
        IntentGoTo.intentActivity(getContext(), ThumbReaderActivity.class);
    }
//    //指纹连接的单例模式
//    public static FingerprintconnectFragment newInstance() {
//        FingerprintconnectFragment fragment = new FingerprintconnectFragment();
//        return fragment;
//    }
    public static FingerprintconnectFragment newInstance(Prioritise prioritise,int preIndex) {

        Bundle args = new Bundle();
        if(fragment == null)
        {
            fragment = new FingerprintconnectFragment();
            fragment.setArguments(args);
        }

        fragment.prioritise = prioritise;
        fragment.preIndex = preIndex;
        return fragment;
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
