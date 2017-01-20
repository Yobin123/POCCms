package com.st.cms.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;
import com.st.cms.entity.GcsRecord;
import com.st.cms.entity.GcsScore;
import com.st.cms.entity.Prioritise;
import com.st.cms.utils.Constants;
import com.st.cms.utils.NfcUtils;
import com.st.cms.utils.OkHttpClientHelper;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import cms.st.com.poccms.R;

/**
 * Created by jt on 2016/9/12.
 */
public class GcsFragment extends TreatmentBaseFragment{

    private GcsScore gcsRecord = null;
    private static GcsFragment fragment;

    private TextView tv_gcs_eyeopen,tv_gcs_verbal,tv_gcs_motor;
    private TextView tv_gcs_gcsscore,tv_gcs_serveritylevel;
    private Spinner spinner_gcs_eyeopen,spinner_gcs_verbal,spinner_gcs_motor ;
    private LinearLayout ll_top;
    private int eye_count;//这是对eye opening的计数
    private int verbal_response;//这是对verbal response 的计数
    private int motor_response;//这是对motor response 计数

    private int preIndex = -1;
    private MyBackListener myBackListener;
    private View.OnClickListener saveListener;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gcs, container, false);
        tv_gcs_eyeopen = (TextView) view.findViewById(R.id.tv_gcs_eyeopen);
        tv_gcs_verbal = (TextView) view.findViewById(R.id.tv_gcs_verbal);
        tv_gcs_motor = (TextView) view.findViewById(R.id.tv_gcs_motor);
        tv_gcs_gcsscore = (TextView) view.findViewById(R.id.tv_gcs_gcsscore);
        tv_gcs_serveritylevel = (TextView) view.findViewById(R.id.tv_gcs_serveritylevel);
        spinner_gcs_eyeopen = (Spinner) view.findViewById(R.id.spinner_gcs_eyeopen);
        spinner_gcs_verbal = (Spinner) view.findViewById(R.id.spinner_gcs_verbal);
        spinner_gcs_motor = (Spinner) view.findViewById(R.id.spinner_gcs_motor);
        ll_top = (LinearLayout)view.findViewById(R.id.ll_top);
        ll_top.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(treatmentMainActivity.detail != null && treatmentMainActivity.detail.getTagId().length() == 0)
                {
                    Toast.makeText(getContext(), "Please get close to nfc tag.", Toast.LENGTH_SHORT).show();
                    return;
                }
                GCSDetailFragment gcsDetailFragment = GCSDetailFragment.newInstance(prioritise, treatmentMainActivity.currentIndex);
                jumpAction(gcsDetailFragment);
            }
        });


        //对spinner上的eye openging 进行相应的监听
        spinner_gcs_eyeopen.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                eye_count = position;
                gcsRecord.setEye_opening(eye_count);
                if (position==0){
                    tv_gcs_eyeopen.setText("-");
                }
                else
                {
                    tv_gcs_eyeopen.setText(eye_count + "");
                }
                tv_gcs_gcsscore.setText((eye_count + verbal_response + motor_response)+"");
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        //对相应spinner上的verbal response进行相应的监听
        spinner_gcs_verbal.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                verbal_response = position;
                gcsRecord.setVerbal_response(position);
                if (position==0){
                    tv_gcs_verbal.setText("-");
                }else
                {
                    tv_gcs_verbal.setText((position)+"");
                }
                tv_gcs_gcsscore.setText((eye_count + verbal_response + motor_response)+"");

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //对相应spinner上的motor response 进行相应的监听
        spinner_gcs_motor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                motor_response = position;
                gcsRecord.setMotor_response(position);
                if (position==0){
                    tv_gcs_motor.setText("-");
                }else
                {
                    tv_gcs_motor.setText(motor_response + "");
                }
                tv_gcs_gcsscore.setText((eye_count + verbal_response + motor_response)+"");
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        showFab(false);
        saveListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if(gcsRecord.getEye_opening() == 0 || gcsRecord.getVerbal_response() == 0 || gcsRecord.getMotor_response() == 0)
            {
                Toast.makeText(getContext(), "Please select the status score.", Toast.LENGTH_SHORT).show();
                return;
            }
            saveStatus();
            }
        };
        treatmentMainActivity.iv_back.setOnClickListener(new MyBackListener(preIndex));
        treatmentMainActivity.fab.setOnClickListener(saveListener);
        gcsRecord = new GcsScore();
        return view;
    }

    @Override
    public void onResume() {
//        gcsRecord = new GcsRecord();
//        showFab(false);
        super.onResume();
//        myBackListener = new MyBackListener(preIndex);
//        treatmentMainActivity.iv_back.setOnClickListener(myBackListener);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(!hidden)
        {
            gcsRecord = new GcsScore();
            showFab(hidden);
            Log.e("TEST", "show fab");
            tv_gcs_gcsscore.setText(0 + "");
            tv_gcs_eyeopen.setText("-");
            tv_gcs_verbal.setText("-");
            tv_gcs_motor.setText("-");
            spinner_gcs_eyeopen.setSelection(0);
            spinner_gcs_verbal.setSelection(0);
            spinner_gcs_motor.setSelection(0);
            treatmentMainActivity.tv_title.setText(prioritise.getTagId());
            if(myBackListener != null)
            {
                myBackListener.setPreIndex(preIndex);
            }
            else
            {
                myBackListener = new MyBackListener(preIndex);
            }
            treatmentMainActivity.iv_back.setOnClickListener(new MyBackListener(preIndex));
            treatmentMainActivity.fab.setOnClickListener(saveListener);
        }
        else
        {
            detail = null;
        }
    }

    public static GcsFragment newInstance(Prioritise prioritise, int preIndex) {

        if(fragment == null)
        {
            fragment = new GcsFragment();
        }
        fragment.prioritise = prioritise;
        fragment.preIndex = preIndex;
        return fragment;
    }
    private boolean saveStatus()
    {
        //这边由于测试所以注释掉了，正常时候需要去掉注释
        if(!treatmentMainActivity.isAvailable)
        {
            Toast.makeText(getContext(), "This device has no NFC function.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(!(treatmentMainActivity.connected && treatmentMainActivity.isConnect(getContext())))
        {
            Toast.makeText(getActivity(), "The net can not connect to backend. Please check the net.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(!NfcUtils.isNfcIntent(getActivity()))
        {
            Toast.makeText(getContext(), "Please get close to NFC tag.", Toast.LENGTH_SHORT).show();
            return false;
        }
        Log.e("TEST", "----" + treatmentMainActivity + "------" + treatmentMainActivity.detail);
        if(!NfcUtils.getTagId(getActivity().getIntent()).equals(treatmentMainActivity.detail.getTagId()))
        {
            Toast.makeText(getContext(), "Please get close to correct NFC tag.", Toast.LENGTH_SHORT).show();
            return false;
        }
//        treatmentMainActivity.readTag(treatmentMainActivity.getIntent(), 1);
        boolean isSuccess = false;
        detail = treatmentMainActivity.detail;
        gcsRecord.setTimestamp(new Date().getTime());
        detail.setGcsScore(gcsRecord);
        isSuccess = NfcUtils.writeToTag(getActivity().getIntent(), treatmentMainActivity.gson.toJson(detail));
//        if(!isSuccess)
//        {
//            Toast.makeText(getContext(), "Please get close to the nfc tag.", Toast.LENGTH_SHORT).show();
//        }
        if(isSuccess)
        {
            syncToBackEnd();
        }
        return isSuccess;
    }
    private void syncToBackEnd()
    {
        JsonObject jsonObject = null;
        JsonArray jsonArray_type = new JsonArray();
        JsonArray jsonArray_status = new JsonArray();

        jsonObject = new JsonObject();
        jsonObject.addProperty("type", 0);
        jsonArray_type.add(jsonObject);
        jsonObject = new JsonObject();
        jsonObject.addProperty("type", 1);
        jsonArray_type.add(jsonObject);
        jsonObject = new JsonObject();
        jsonObject.addProperty("type", 2);
        jsonArray_type.add(jsonObject);

        jsonObject = new JsonObject();
        jsonObject.addProperty("status", gcsRecord.getEye_opening());
        jsonArray_status.add(jsonObject);
        jsonObject = new JsonObject();
        jsonObject.addProperty("status", gcsRecord.getVerbal_response());
        jsonArray_status.add(jsonObject);
        jsonObject = new JsonObject();
        jsonObject.addProperty("status", gcsRecord.getMotor_response());
        jsonArray_status.add(jsonObject);

        String url = Constants.getUrl(Constants.GCS);
        Callback callback = new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                e.printStackTrace();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(treatmentMainActivity, "Request Failure!Please check the network.", Toast.LENGTH_SHORT).show();
                    }
                });
                Log.e("TEST", "Request Failure!Please check the network.");
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (response.isSuccessful()) {
                    ResponseBody body = response.body();
                    if (body != null) {
                    }
                    saveSuccess(preIndex);
                }
                else
                {
                    Log.e("TEST", response.isSuccessful() + "---" + response.body());
                }
            }
        };
        Log.e("TEST", url);
        Map<String, String> values = new HashMap<>();
        Log.e("TEST", jsonArray_type.toString());
        Log.e("TEST", jsonArray_status.toString());
        values.put("taglibid", detail.getTagId());
        values.put("types", jsonArray_type.toString());
        values.put("status", jsonArray_status.toString());
        Log.e("TEST", jsonArray_type.toString() + jsonArray_status.toString());
        OkHttpClientHelper.postKeyValuePairAsync(getContext(), url, values, callback, null);
    }
}
