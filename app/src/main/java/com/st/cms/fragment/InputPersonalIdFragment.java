package com.st.cms.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;
import com.st.cms.activity.DetailActivity;
import com.st.cms.activity.IdentyfyActivity;
import com.st.cms.entity.Detail;
import com.st.cms.entity.MedicalRes;
import com.st.cms.entity.Prioritise;
import com.st.cms.utils.Constants;
import com.st.cms.utils.OkHttpClientHelper;

import org.json.JSONObject;

import java.io.IOException;

import cms.st.com.poccms.R;


public class InputPersonalIdFragment extends IdentifyBaseFragment {
    private EditText et_personal_nric;
    private EditText et_personal_no;
    private Button bt_personal_retrieve;
    private int preIndex = -1;
    private MyZoneBackListener myBackListener;
    private OkHttpClientHelper okHttpClientHelper;
    private static InputPersonalIdFragment fragment = null;
    private JSONObject son_json;
    private Detail detail = new Detail();
    private MedicalRes medicalRes = new MedicalRes();
    private String result;
    public static InputPersonalIdFragment newInstance() {
        InputPersonalIdFragment fragment = new InputPersonalIdFragment();
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_input_personal, container, false);
        et_personal_nric = (EditText) view.findViewById(R.id.et_personal_nric);
        et_personal_no = (EditText) view.findViewById(R.id.et_personal_no);
        bt_personal_retrieve = (Button) view.findViewById(R.id.bt_personal_retrieve);
        identyfyActivity = (IdentyfyActivity) getActivity();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        okHttpClientHelper = OkHttpClientHelper.getOkHttpClientUtils(getContext());


        //点击实现相应的跳转，这里还需请求数据，获取相应的身份信息
        bt_personal_retrieve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "你点击了这个button", Toast.LENGTH_SHORT).show();
                //这里还需要从相应的数据中获取需要的信息去检验数据，最终跳转的相应到loghistory阶段
//        Map<String,String> map = new HashMap<>();

//        进行相应的上传数据信息

//        if (nric!=null&&no!=null){
//            map.put("nric",nric);
//            map.put("no",no);
//        }else {
//            Toast.makeText(getContext(), "Please Input Both Of Them", Toast.LENGTH_SHORT).show();
//        }

                final String nric = et_personal_nric.getText().toString();
                final String no = et_personal_no.getText().toString();



                Callback callback = new Callback() {
                    @Override
                    public void onFailure(Request request, final IOException e) {
                        e.printStackTrace();
                        Log.e("TEST", "Request Failure!Please check the network.");
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getContext(),"The network is bad", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onResponse(Response response) throws IOException {
                        if (response.isSuccessful()) {
                            ResponseBody body = response.body();
                            if (body != null) {
                               result = body.string();
                                Intent intent = new Intent();
                                intent.setClass(getContext(), DetailActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putString("result",result);
                                intent.putExtras(bundle);
                                getActivity().startActivity(intent);
                        }
                        }
//                        else {
//                            Log.e("TEST", response.isSuccessful() + "---" + response.body());
//                        }

                    }
                };
                if (nric.equals("") && no.equals("")){
                    Toast.makeText(getContext(), "The Input shouldn't be empty", Toast.LENGTH_SHORT).show();
                    return;
                }else {
                    if (!nric.equals("") && no.equals("")){
                        okHttpClientHelper.getDataAsync(getContext(), Constants.getIdentifyUrl(nric,"xxxx"), callback, "tag");

                    }else if (nric.equals("") && no.equals("")){
                        okHttpClientHelper.getDataAsync(getContext(),Constants.getIdentifyUrl("xxxx",no),callback,"tag");
                    }else {
                        okHttpClientHelper.getDataAsync(getContext(),Constants.getIdentifyUrl(nric,no),callback,"tag");
                    }
                }



            }
        });
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden) {
            identyfyActivity.tv_title.setText(prioritise.getTagId());
            if (myBackListener != null) {
                myBackListener.setPreIndex(preIndex);
            } else {
                myBackListener = new MyZoneBackListener(preIndex);
            }
            identyfyActivity.iv_back.setOnClickListener(new MyZoneBackListener(preIndex));
            et_personal_nric.setText("");
            et_personal_no.setText("");
        }
        super.onHiddenChanged(hidden);
    }


    public static InputPersonalIdFragment newInstance(Prioritise prioritise, int preIndex) {

        Bundle args = new Bundle();
        if (fragment == null) {
            fragment = new InputPersonalIdFragment();
            fragment.setArguments(args);
        } else {
            fragment.prioritise = prioritise;
            fragment.preIndex = preIndex;
        }

        return fragment;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        okHttpClientHelper.cancelCall("tag");//解除网络
    }
}
