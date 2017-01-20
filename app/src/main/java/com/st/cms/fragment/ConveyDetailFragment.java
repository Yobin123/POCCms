package com.st.cms.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;
import com.st.cms.activity.ConveyListActivity;
import com.st.cms.activity.MainActivity;
import com.st.cms.adapter.ShowMedicalAdapter;
import com.st.cms.entity.Detail;
import com.st.cms.entity.GcsScore;
import com.st.cms.entity.Injury;
import com.st.cms.entity.MedicalRes;
import com.st.cms.entity.Prioritise;
import com.st.cms.entity.Readings;
import com.st.cms.entity.VitalSign;
import com.st.cms.utils.Constants;
import com.st.cms.utils.NfcUtils;
import com.st.cms.utils.OkHttpClientHelper;
import com.st.cms.widget.ScrollBottomScrollView;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import cms.st.com.poccms.R;

/**
 * Created by jt on 2016/9/20.
 */
public class ConveyDetailFragment extends ConveyBaseFragment{
    private int preIndex = -1;
    private MyBackListener myBackListener;
    private static ConveyDetailFragment fragment;

    private String[] prioritises = new String[]{
            "P0", "P1", "P2", "P3", "UIN"
    };
    private ScrollBottomScrollView sv_detail;
    private TextView confirm_arrival;
    private MyScrollBottomListener myScrollBottomListener;
    private MyUnScrollBottomListener myUnScrollBottomListener;
    private MyOnClickListener myOnClickListener;

    private MyConfirmArrivalListener myConfirmArrivalListener;

    private ConveyListActivity.DetailInterface detailInterface;

    /*views start*/
    private TextView tag_id;
    private TextView nric;
    private TextView p_lv;
    private TextView name;
    private TextView age;
    private TextView gender;
    private TextView blood_group;
    private TextView medical_history;
    private ListView injury_points;
    private ListView treatment;
    private ListView drug;
    private TextView spo2;
    private TextView bpm;
    private TextView po_date;
    private TextView po_time;
    private TextView eye_opening;
    private TextView verbal_response;
    private TextView motor_response;
    private TextView gcs_total;
    private TextView gcs_date;
    private TextView gcs_time;
    private TextView zone_locate;
    private TextView designated_hospital;
    private TextView operating_theatre;
    private TextView ward_type;
    private TextView vehicle_plate_no;
    /*views end*/

    private ShowMedicalAdapter treatment_adapter;
    private ShowMedicalAdapter drug_adapter;
    private ShowMedicalAdapter injury_adapter;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_convey_detail, container, false);
        initView(view);
        sv_detail = (ScrollBottomScrollView)view.findViewById(R.id.sv_detail);
        myScrollBottomListener = new MyScrollBottomListener();
        myUnScrollBottomListener = new MyUnScrollBottomListener();
        myOnClickListener = new MyOnClickListener();
        sv_detail.setScrollBottomListener(myScrollBottomListener);
        sv_detail.setUnScrollBottomListener(myUnScrollBottomListener);
        conveyListActivity.arrow_up.setOnClickListener(myOnClickListener);
        confirm_arrival = (TextView) view.findViewById(R.id.confirm_arrival);
        myConfirmArrivalListener = new MyConfirmArrivalListener();
        confirm_arrival.setOnClickListener(myConfirmArrivalListener);
        conveyListActivity.arrow_up.setVisibility(View.GONE);
        conveyListActivity.initToolBar("content");
        conveyListActivity.tv_title.setText(prioritise.getTagId());
        conveyListActivity.iv_back.setOnClickListener(new MyBackListener(preIndex));
        initDetailInfo();
        initViewData();
        return view;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if(!hidden)
        {
            if(myBackListener != null)
            {
                myBackListener.setPreIndex(preIndex);
            }
            else
            {
                myBackListener = new MyBackListener(preIndex);
            }
            if(myScrollBottomListener == null)
            {
                myScrollBottomListener = new MyScrollBottomListener();
            }
            if(myUnScrollBottomListener == null)
            {
                myUnScrollBottomListener = new MyUnScrollBottomListener();
            }
            if(myOnClickListener == null)
            {
                myOnClickListener = new MyOnClickListener();
            }
            sv_detail.setScrollBottomListener(myScrollBottomListener);
            sv_detail.setUnScrollBottomListener(myUnScrollBottomListener);
            conveyListActivity.arrow_up.setOnClickListener(myOnClickListener);
            conveyListActivity.iv_back.setOnClickListener(new MyBackListener(preIndex));
            conveyListActivity.initToolBar("content");
            conveyListActivity.tv_title.setText(prioritise.getTagId());
            clearView();
            initDetailInfo();
        }
        else
        {
            if(conveyListActivity.arrow_up.getVisibility() == View.VISIBLE)
            {
                conveyListActivity.arrow_up.setVisibility(View.GONE);
            }
            detail = null;
        }
        super.onHiddenChanged(hidden);
    }

    @Override
    public void onPause() {
        if(conveyListActivity.arrow_up.getVisibility() == View.VISIBLE)
        {
            conveyListActivity.arrow_up.setVisibility(View.GONE);
        }
        super.onPause();
    }

    public static ConveyDetailFragment newInstance(Prioritise prioritise, int preIndex) {
        if(fragment == null)
        {
            fragment= new ConveyDetailFragment();
        }
        fragment.prioritise = prioritise;
        fragment.preIndex = preIndex;
        return fragment;
    }
    private class MyScrollBottomListener implements ScrollBottomScrollView.ScrollBottomListener
    {

        @Override
        public void scrollBottom() {
            if(conveyListActivity.arrow_up.getVisibility() == View.GONE)
            {
                conveyListActivity.arrow_up.setVisibility(View.VISIBLE);
                confirm_arrival.setVisibility(View.VISIBLE);
            }
        }
    }
    private class MyUnScrollBottomListener implements ScrollBottomScrollView.UnScrollBottomListener
    {

        @Override
        public void unScrollBottom() {
            if(conveyListActivity.arrow_up.getVisibility() == View.VISIBLE)
            {
                conveyListActivity.arrow_up.setVisibility(View.GONE);
                confirm_arrival.setVisibility(View.GONE);
            }
        }
    }
    private class MyOnClickListener implements View.OnClickListener
    {

        @Override
        public void onClick(View v) {
            Log.e("TEST", "click up");
            conveyListActivity.arrow_up.setVisibility(View.GONE);
            confirm_arrival.setVisibility(View.GONE);
            sv_detail.setScrollY(0);
        }
    }
    private void initView(View view)
    {
        tag_id = (TextView)view.findViewById(R.id.tag_id);
        nric = (TextView)view.findViewById(R.id.nric);
        p_lv = (TextView)view.findViewById(R.id.p_lv);
        name = (TextView)view.findViewById(R.id.name);
        age = (TextView)view.findViewById(R.id.age);
        gender = (TextView)view.findViewById(R.id.gender);
        blood_group = (TextView)view.findViewById(R.id.blood_group);
        medical_history = (TextView)view.findViewById(R.id.medical_history);
        injury_points = (ListView) view.findViewById(R.id.injury_points);
        treatment = (ListView) view.findViewById(R.id.treatment);
        drug = (ListView) view.findViewById(R.id.drug);
        spo2 = (TextView)view.findViewById(R.id.spo2);
        bpm = (TextView)view.findViewById(R.id.bpm);
        po_date = (TextView)view.findViewById(R.id.po_date);
        po_time = (TextView)view.findViewById(R.id.po_time);
        eye_opening = (TextView)view.findViewById(R.id.eye_opening);
        verbal_response = (TextView)view.findViewById(R.id.verbal_response);
        motor_response = (TextView)view.findViewById(R.id.motor_response);
        gcs_total = (TextView)view.findViewById(R.id.gcs_total);
        gcs_date = (TextView)view.findViewById(R.id.gcs_date);
        gcs_time = (TextView)view.findViewById(R.id.gcs_time);
        zone_locate = (TextView)view.findViewById(R.id.zone_locate);
        designated_hospital = (TextView)view.findViewById(R.id.designated_hospital);
        operating_theatre = (TextView)view.findViewById(R.id.operating_theatre);
        ward_type = (TextView)view.findViewById(R.id.ward_type);
        vehicle_plate_no = (TextView)view.findViewById(R.id.vehicle_plate_no);
    }
    private void clearView()
    {
        tag_id.setText("-");
        nric.setText("-");
        p_lv.setText("-");
        name.setText("-");
        age.setText("-");
        gender.setText("-");
        blood_group.setText("-");
        medical_history.setText("-");

        Log.e("TEST", injury_points + "---" + ((ShowMedicalAdapter)injury_points.getAdapter()));
        if(injury_points != null && ((ShowMedicalAdapter)injury_points.getAdapter()) != null)
        {
            ((ShowMedicalAdapter)injury_points.getAdapter()).setResources(new ArrayList<Object>());
            ((ShowMedicalAdapter)injury_points.getAdapter()).notifyDataSetChanged();
        }
        if(treatment != null && ((ShowMedicalAdapter)treatment.getAdapter()) != null)
        {
            ((ShowMedicalAdapter)treatment.getAdapter()).setResources(new ArrayList<Object>());
            ((ShowMedicalAdapter)treatment.getAdapter()).notifyDataSetChanged();
        }
        if(drug != null && ((ShowMedicalAdapter)drug.getAdapter()) != null)
        {
            ((ShowMedicalAdapter)drug.getAdapter()).setResources(new ArrayList<Object>());
            ((ShowMedicalAdapter)drug.getAdapter()).notifyDataSetChanged();
        }
        spo2.setText("-");
        bpm.setText("-");
        po_date.setText("-");
        po_time.setText("-");
        eye_opening.setText("-");
        verbal_response.setText("-");
        motor_response.setText("-");
        gcs_total.setText("-");
        gcs_date.setText("-");
        gcs_time.setText("-");
        zone_locate.setText("-");
        designated_hospital.setText("-");
        operating_theatre.setText("-");
        ward_type.setText("-");
        vehicle_plate_no.setText("-");
    }
    private void initViewData() {
        Log.e("TEST", "aaaaaaa---------------");
        detail = conveyListActivity.detail;
        if(detail == null)
        {
            Toast.makeText(getContext(), "Please get close to nfc tag.", Toast.LENGTH_SHORT).show();
            return;
        }
        //假数据测试
//        if (detail == null) {
//            detail = new Detail();
//            detail.setTagId(prioritise.getTagId());
//            detail.setAge(25);
//            detail.setGender(0);
//            detail.setDesignated_hospital("Tan Tock Seng");
//            detail.setName("Zeus");
//            detail.setNric("0s221365");
//            detail.setZone("Hospital");
//            detail.setWardType("C5");
//            detail.setPlv(3);
//            detail.setVehicle_plate_no("s333312");
//            detail.setOperating_theatre("Theatre 1");
//            detail.setProfile("It is for test.");
//            List<Injury> injuries = new ArrayList<Injury>();
//            injuries.add(new Injury("Right Arm", "Bleeding"));
//            injuries.add(new Injury("Right Leg", "Fractured"));
//            detail.setInjuries(injuries);
//            detail.setVitalSign(new VitalSign(98, 99, 1476255684769l));
//            detail.setGcsScore(new GcsScore(2, 3, 3, 1476255684769l));
//            detail.setBloodGroup(new MedicalRes("7", "O-", 150, 0, 10));
//            List<MedicalRes> drugs = new ArrayList<>();
//            drugs.add(new MedicalRes("4", "Azithromycin", 260, 2, 10));
//            drugs.add(new MedicalRes("4", "Azithromycin", 260, 2, 10));
//            detail.setDrugs(drugs);
//            List<MedicalRes> treatments = new ArrayList<>();
//            treatments.add(new MedicalRes("1", "Strrile Water", 310, 1, 10));
//            treatments.add(new MedicalRes("1", "Strrile Water", 310, 1, 10));
//            detail.setTreatments(treatments);
//        }
        SimpleDateFormat sdf_date = new SimpleDateFormat("yy/MM/DD");
        SimpleDateFormat sdf_time = new SimpleDateFormat("hh:mm:ss a", Locale.US);

        tag_id.setText(detail.getTagId());
        nric.setText(detail.getNric());
        p_lv.setText(prioritises[detail.getPlv() == -1 ? 4 : detail.getPlv()]);
        name.setText(detail.getName());
        age.setText(detail.getAge() + "");
        gender.setText(detail.getGender() == 0 ? "M" : "F");

        if(detail.getBloodGroup() != null)
        {
            blood_group.setText(detail.getBloodGroup().getName());
        }
        medical_history.setText(detail.getProfile());
        if(detail.getTreatments() != null)
        {
            treatment_adapter = new ShowMedicalAdapter(getContext(), detail.getTreatments());
            treatment.setAdapter(treatment_adapter);
            setListViewHeightBasedOnChildren(treatment);
        }
        if(detail.getDrugs() != null)
        {
            drug_adapter = new ShowMedicalAdapter(getContext(), detail.getDrugs());
            drug.setAdapter(drug_adapter);
            setListViewHeightBasedOnChildren(drug);
        }
        if(detail.getInjuries() != null)
        {
            injury_adapter = new ShowMedicalAdapter(getContext(), detail.getInjuries());
            injury_points.setAdapter(injury_adapter);
            setListViewHeightBasedOnChildren(injury_points);
        }

        if (detail.getVitalSign() != null) {
            spo2.setText(detail.getVitalSign().getSpo2() + "");
            bpm.setText(detail.getVitalSign().getBpm() + "");
            Date date = new Date(detail.getVitalSign().getTimestamp());
            po_date.setText(sdf_date.format(date));
            po_time.setText(sdf_time.format(date));
        }

        if (detail.getGcsScore() != null)
        {
            eye_opening.setText(detail.getGcsScore().getEye_opening() + "");
            verbal_response.setText(detail.getGcsScore().getVerbal_response() + "");
            motor_response.setText(detail.getGcsScore().getMotor_response() + "");
            gcs_total.setText((detail.getGcsScore().getMotor_response() + detail.getGcsScore().getEye_opening() + detail.getGcsScore().getVerbal_response()) + "");

            Date date_gcs = new Date(detail.getGcsScore().getTimestamp());
            gcs_date.setText(sdf_date.format(date_gcs));
            gcs_time.setText(sdf_time.format(date_gcs));
        }

        zone_locate.setText(detail.getZone());
        designated_hospital.setText(detail.getDesignated_hospital());
        operating_theatre.setText(detail.getOperating_theatre());
        ward_type.setText(detail.getWardType());
        vehicle_plate_no.setText(detail.getVehicle_plate_no());
    }

    /**
     *  计算listview的高度
     * @param listView
     */
    public void setListViewHeightBasedOnChildren(ListView listView) {
        // 获取ListView对应的Adapter
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) {
            // listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, listView);
            // 计算子项View 的宽高
            listItem.measure(0, 0);
            // 统计所有子项的总高度
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        // listView.getDividerHeight()获取子项间分隔符占用的高度
        // params.height最后得到整个ListView完整显示需要的高度
        listView.setLayoutParams(params);
    }
    private class MyConfirmArrivalListener implements View.OnClickListener
    {
        @Override
        public void onClick(View v) {
            if(v.getId() == R.id.confirm_arrival)
            {
                saveArrivalState();
//                if(detail != null && !detail.isArrived())
//                {
//                    if(saveArrivalState())
//                    {
//                        Toast.makeText(getActivity(), "Save success.", Toast.LENGTH_SHORT).show();
//                    }
//                }
//                else
//                {
//                    Toast.makeText(getActivity(), "The victim has been sent to hospital.", Toast.LENGTH_SHORT).show();
//                }
            }
        }
    }

    private void initDetailInfo()
    {
        if(detailInterface == null)
        {
            detailInterface = new ConveyListActivity.DetailInterface() {
                @Override
                public void getDetailFromTag() {
                    detail = conveyListActivity.detail;
                    if(detail != null)
                    {
                        if(detail.getTagId().equals(NfcUtils.getTagId(getActivity().getIntent())))
                        {
                            initViewData();
                        }
                    }
                }
            };
        }
        conveyListActivity.setDetailInterface(detailInterface);
    }
    private boolean saveArrivalState()
    {

//这边由于测试所以注释掉了，正常时候需要去掉注释
        if(!conveyListActivity.isAvailable)
        {
            Toast.makeText(getContext(), "This device has no NFC function.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(!(conveyListActivity.connected && conveyListActivity.isConnect(getContext())))
        {
            Toast.makeText(getActivity(), "The net can not connect to backend. Please check the net.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(!NfcUtils.isNfcIntent(getActivity()))
        {
            Toast.makeText(getContext(), "Please get close to NFC tag.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(!NfcUtils.getTagId(getActivity().getIntent()).equals(detail.getTagId()))
        {
            Toast.makeText(getContext(), "Please get close to correct NFC tag.", Toast.LENGTH_SHORT).show();
            return false;
        }
//        if(detail == null)
//        {
//            Toast.makeText(getContext(), "Please get close to NFC tag.", Toast.LENGTH_SHORT).show();
//            return false;
//        }

//        if(detail == null)
//        {
//            detail = new Detail();
//            detail.setTagId(prioritise.getTagId());
//        }
        //假数据
//        detail.setHospital_id(1);
//        detail.setAmbulance_id(7);
        boolean isSuccess = false;
        Callback callback = new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                e.printStackTrace();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(conveyListActivity, "Request Failure!Please check the network.", Toast.LENGTH_SHORT).show();
                    }
                });
                Log.e("TEST", "Request Failure!Please check the network.");
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (response.isSuccessful()) {
                    ResponseBody body = response.body();
                    if (body != null) {
                        Log.e("TEST", body.string());
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getContext(), "The victim has been sent to hospital.", Toast.LENGTH_SHORT).show();
                        }
                    });
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                TimeUnit.SECONDS.sleep(2);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            Intent intent = new Intent(getActivity(), MainActivity.class);
                            startActivity(intent);
                            getActivity().finish();
                        }
                    }).start();
                }
                else
                {
                    Log.e("TEST", response.isSuccessful() + "---" + response.body());
                }

            }
        };
        detail = conveyListActivity.detail;
        detail.setArrived(true);
        isSuccess = NfcUtils.writeToTag(getActivity().getIntent(), conveyListActivity.gson.toJson(detail));
        if(!isSuccess)
        {
            Toast.makeText(getContext(), "Save failure!Please get close to NFC tag.", Toast.LENGTH_SHORT).show();
            return false;
        }
        List<MedicalRes> data_for_save = new ArrayList<>();
        if(detail.getBloodGroup() != null)
        {
            data_for_save.add(detail.getBloodGroup());
        }
        if(detail.getTreatments() != null && detail.getTreatments().size() > 0)
        {
            data_for_save.addAll(detail.getTreatments());
        }
        if(detail.getDrugs() != null && detail.getDrugs().size() > 0)
        {
            data_for_save.addAll(detail.getDrugs());
        }
        JsonArray jsonArray_medicalIds = new JsonArray();
        JsonArray jsonArray_consumes = new JsonArray();
        transformData(data_for_save, jsonArray_medicalIds, jsonArray_consumes);
        Map<String, String> values = new HashMap<>();
        values.put("taglibid", detail.getTagId());
        values.put("hospitalid", detail.getHospital_id() + "");
        values.put("ambulanceId", detail.getAmbulance_id() + "");
        Log.e("TEST", "----" + jsonArray_medicalIds.toString());
        Log.e("TEST", "----" + jsonArray_consumes.toString());
        values.put("medicalIds", jsonArray_medicalIds.toString());
        values.put("consumes", jsonArray_consumes.toString());
        Log.e("TEST", Constants.getUrl(Constants.TREAT2 ));
        OkHttpClientHelper.postKeyValuePairAsync(getContext(), Constants.getUrl(Constants.TREAT2),values, callback, null);
        return isSuccess;
    }
    //预留方法，有可能需要保存到后台，进行数据格式转换
    private void transformData(Object datas, JsonArray jsonArray_medicalIds, JsonArray jsonArray_consumes)
    {
        JsonObject obj = null;
        if(datas instanceof MedicalRes)
        {
            MedicalRes medicalRes = (MedicalRes)datas;
            obj = new JsonObject();
            obj.addProperty("id", medicalRes.getId());
            jsonArray_medicalIds.add(obj);
            obj = new JsonObject();
            obj.addProperty("consumes", medicalRes.getConsume());
            jsonArray_consumes.add(obj);
        }
        if(datas instanceof ArrayList<?>)
        {
            if(((ArrayList) datas).size() > 0)
            {
                if(((ArrayList)datas).get(0) instanceof MedicalRes)
                {
                    List<MedicalRes> resources = (List<MedicalRes>)datas;
                    for(int i = 0;i < resources.size();i++)
                    {
                        transformData(resources.get(i), jsonArray_medicalIds, jsonArray_consumes);
                    }
                }
            }
        }
    }
}
