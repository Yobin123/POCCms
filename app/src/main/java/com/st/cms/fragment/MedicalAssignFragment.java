package com.st.cms.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;
import com.st.cms.activity.TreatmentMainActivity;
import com.st.cms.adapter.ResListAdapter;
import com.st.cms.entity.Detail;
import com.st.cms.entity.Injury;
import com.st.cms.entity.MedicalRes;
import com.st.cms.entity.Prioritise;
import com.st.cms.entity.Winjury;
import com.st.cms.utils.Constants;
import com.st.cms.utils.NfcUtils;
import com.st.cms.utils.OkHttpClientHelper;
import com.st.cms.widget.MedicalEditView;
import com.st.cms.widget.MedicalSelectView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cms.st.com.poccms.R;

/**
 * Created by jt on 2016/8/29.
 */
public class MedicalAssignFragment extends TreatmentBaseFragment{
    private static MedicalAssignFragment fragment;

    private MedicalSelectView spinner_medical;
    private MedicalEditView spinner_medical_qty;
    private MedicalSelectView spinner_treatment;
    private MedicalEditView spinner_treatment_qty;
    private MedicalSelectView spinner_drug;
    private MedicalEditView spinner_drug_qty;

    private ImageView iv_add_treatment;
    private ImageView iv_add_drug;

    private ListView lv_medical_list;
    private ResListAdapter medical_adapter;
    private ListView lv_drug_list;
    private ResListAdapter drug_adapter;


    //医疗物资集合
    //血型
    private MedicalRes blood_group = null;
    //treatment表示暂时的medical res缓存
    private MedicalRes treatment = null;
    private List<MedicalRes> medical_res = new ArrayList<>();
    //drug
    private MedicalRes drug = null;
    private List<MedicalRes> drug_res = new ArrayList<>();


    private int preIndex = -1;
    private MyBackListener myBackListener;
    //保存按钮的监听事件
    private MyFabOnClickListener myFabOnClickListener;
    //监听qty变化的事件
    private MedicalEditView.TextChangedActionListener textChangedActionListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_medical_assign, container, false);
        treatmentMainActivity = (TreatmentMainActivity)getActivity();
        MyAssignOnClickListener onClickListener = new MyAssignOnClickListener();
        spinner_medical = (MedicalSelectView)view.findViewById(R.id.spinner_medical);
        spinner_medical.setOnClickListener(onClickListener);
        spinner_medical_qty = (MedicalEditView) view.findViewById(R.id.spinner_medical_qty);

        spinner_treatment = (MedicalSelectView)view.findViewById(R.id.spinner_treatment);
        spinner_treatment.setOnClickListener(onClickListener);
        spinner_treatment_qty = (MedicalEditView) view.findViewById(R.id.spinner_treatment_qty);

        spinner_drug = (MedicalSelectView)view.findViewById(R.id.spinner_drug);
        spinner_drug.setOnClickListener(onClickListener);
        spinner_drug_qty = (MedicalEditView) view.findViewById(R.id.spinner_drug_qty);

        iv_add_treatment = (ImageView)view.findViewById(R.id.iv_add_treatment);
        iv_add_drug = (ImageView)view.findViewById(R.id.iv_add_drug);
        myBackListener = new MyBackListener(preIndex);
        treatmentMainActivity.iv_back.setOnClickListener(myBackListener);
        lv_medical_list = (ListView)view.findViewById(R.id.lv_medical_list);
        medical_adapter = new ResListAdapter(getContext(), medical_res);
        lv_medical_list.setAdapter(medical_adapter);
        lv_drug_list = (ListView)view.findViewById(R.id.lv_drug_list);
        drug_adapter = new ResListAdapter(getContext(), drug_res);
        lv_drug_list.setAdapter(drug_adapter);
        iv_add_treatment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if(spinner_treatment.getText().length() == 0 || spinner_treatment_qty.getText().length() == 0)
//                    return;
//                if(lv_medical_list.getVisibility() == View.GONE)
//                {
//                    lv_medical_list.setVisibility(View.VISIBLE);
//                }
//                treatment.setConsume(Integer.parseInt(spinner_treatment_qty.getText().toString()));
//                int index = checkAddRes(medical_res, treatment);
//                if(index >= 0)
//                {
//                    medical_res.get(index).setConsume(medical_res.get(index).getConsume() + treatment.getConsume());
//                }
//                else
//                {
//                    medical_res.add(treatment);
//                }
                addTreatment();
                medical_adapter.notifyDataSetChanged();
                treatment = null;
                medical_adapter.setResources(medical_res);
                spinner_treatment.setText("");
                spinner_treatment_qty.setText("");
                isShowFab();
            }
        });
        iv_add_drug.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if(spinner_drug.getText().length() == 0 || spinner_drug_qty.getText().length() == 0)
//                    return;
//                if(lv_drug_list.getVisibility() == View.GONE)
//                {
//                    lv_drug_list.setVisibility(View.VISIBLE);
//                }
//                drug.setConsume(Integer.parseInt(spinner_drug_qty.getText().toString()));
//                int index = checkAddRes(drug_res, drug);
//                if(index >= 0)
//                {
//                    drug_res.get(index).setConsume(drug_res.get(index).getConsume() + drug.getConsume());
//                }
//                else
//                {
//                    drug_res.add(drug);
//                }
                addDrug();
                drug_adapter.notifyDataSetChanged();
                drug = null;
                drug_adapter.setResources(drug_res);
                spinner_drug.setText("");
                spinner_drug_qty.setText("");
                isShowFab();
            }
        });
        myFabOnClickListener = new MyFabOnClickListener();
        textChangedActionListener = new MedicalEditView.TextChangedActionListener() {

            @Override
            public void textChangedAction(MedicalEditView medicalEditView) {

                isShowFab();
            }
        };
        MedicalEditView.setTextChangedActionListener(textChangedActionListener);
        return view;
    }
    private void addTreatment()
    {
        if(spinner_treatment.getText().length() == 0 || spinner_treatment_qty.getText().length() == 0)
            return;
        if(lv_medical_list.getVisibility() == View.GONE)
        {
            lv_medical_list.setVisibility(View.VISIBLE);
        }
        treatment.setConsume(Integer.parseInt(spinner_treatment_qty.getText().toString()));
        int index = checkAddRes(medical_res, treatment);
        if(index >= 0)
        {
            medical_res.get(index).setConsume(medical_res.get(index).getConsume() + treatment.getConsume());
        }
        else
        {
            medical_res.add(treatment);
        }
    }
    private void addDrug()
    {
        if(spinner_drug.getText().length() == 0 || spinner_drug_qty.getText().length() == 0)
            return;
        if(lv_drug_list.getVisibility() == View.GONE)
        {
            lv_drug_list.setVisibility(View.VISIBLE);
        }
        drug.setConsume(Integer.parseInt(spinner_drug_qty.getText().toString()));
        int index = checkAddRes(drug_res, drug);
        if(index >= 0)
        {
            drug_res.get(index).setConsume(drug_res.get(index).getConsume() + drug.getConsume());
        }
        else
        {
            drug_res.add(drug);
        }
    }
    private boolean verifyConsume(MedicalEditView medicalEditView)
    {
        switch (medicalEditView.getId())
        {
            case R.id.spinner_medical_qty:
                if(Integer.parseInt(medicalEditView.getText().toString()) > blood_group.getQty())
                break;
            case R.id.spinner_treatment_qty:
                break;
            case R.id.spinner_drug_qty:
                break;
        }
        return true;
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
            treatmentMainActivity.iv_back.setOnClickListener(new MyBackListener(preIndex));
            isShowFab();
        }
        else
        {
            treatmentMainActivity.fab.setVisibility(View.GONE);
        }
        super.onHiddenChanged(hidden);
    }

    public static MedicalAssignFragment newInstance(Prioritise prioritise, int preIndex) {

        if(fragment == null)
        {
            fragment = new MedicalAssignFragment();
        }
        fragment.prioritise = prioritise;
        fragment.preIndex = preIndex;
        initView(fragment);
        return fragment;
    }

    public static void initView(MedicalAssignFragment fragment)
    {
        if(fragment.spinner_medical == null || fragment.spinner_drug == null || fragment.spinner_treatment == null)
            return;
        if(fragment.medical_adapter != null)
        {
            fragment.medical_adapter.getResources().clear();
            fragment.medical_adapter.notifyDataSetChanged();
        }
        if(fragment.drug_adapter != null)
        {
            fragment.drug_adapter.getResources().clear();
            fragment.drug_adapter.notifyDataSetChanged();
        }
        fragment.spinner_medical.setText("");
        fragment.spinner_drug.setText("");
        fragment.spinner_treatment.setText("");
        fragment.spinner_medical_qty.setText("");
        fragment.spinner_drug_qty.setText("");
        fragment.spinner_treatment_qty.setText("");
        fragment.blood_group = new MedicalRes();
        fragment.medical_res = new ArrayList<>();
        fragment.drug_res = new ArrayList<>();
    }
    /**
     *
     * @param prioritise
     * @param medicalRes
     * @param type 0:blood, 1:medical, 2:drug
     * @return
     */
    public static MedicalAssignFragment newInstance(Prioritise prioritise, MedicalRes medicalRes, int type) {

        if(fragment == null)
        {
            fragment = new MedicalAssignFragment();
        }
        fragment.prioritise = prioritise;
        switch (type)
        {
            case 0:
                fragment.blood_group = medicalRes;
                fragment.spinner_medical.setText(medicalRes.getName());
                break;
            case 1:
                if(fragment.medical_res == null)
                {
                    fragment.medical_res = new ArrayList<>();
                }
                fragment.treatment = medicalRes;
//                fragment.treatment.setQty(0);
//                fragment.medical_res.add(medicalRes);
                fragment.spinner_treatment.setText(medicalRes.getName());
                break;
            case 2:
                if(fragment.drug_res == null)
                {
                    fragment.drug_res = new ArrayList<>();
                }
//                fragment.drug_res.add(medicalRes);
                fragment.drug = medicalRes;
//                fragment.drug.setQty(0);
                fragment.spinner_drug.setText(medicalRes.getName());
                break;
            default:
                break;
        }
        return fragment;
    }
    private class MyAssignOnClickListener implements View.OnClickListener
    {

        @Override
        public void onClick(View v) {
            StockpileFragment stockpileFragment = StockpileFragment.newInstance(prioritise, treatmentMainActivity.currentIndex);
            switch (v.getId())
            {
                case R.id.spinner_medical:
                    jumpAction(stockpileFragment);
                    break;
                case R.id.spinner_drug:
                    jumpAction(stockpileFragment);
                    break;
                case R.id.spinner_treatment:
                    jumpAction(stockpileFragment);
                    break;
            }
        }
    }
    private class MyFabOnClickListener implements View.OnClickListener
    {

        @Override
        public void onClick(View v) {
            if(!judgePreSave())
            {
                Toast.makeText(treatmentMainActivity, "Please fill out the resource list.", Toast.LENGTH_SHORT).show();
                return;
            }
            saveTreatments();
        }
    }
    private boolean judgePreSave()
    {
        if((spinner_medical.getText().length() > 0 && spinner_medical_qty.getText().length() > 0) ||
                ((spinner_treatment.getText().length() > 0 && spinner_treatment_qty.getText().length() > 0) || medical_res.size() > 0) ||
                ((spinner_drug.getText().length() > 0 && spinner_drug_qty.getText().length() > 0) || drug_res.size() > 0))
        {
            return true;
        }
        return false;
    }
    private void isShowFab()
    {
        if((spinner_medical.getText().length() > 0 && spinner_medical_qty.getText().length() > 0))
        {
            showFab();
            return;
        }
        if(((spinner_treatment.getText().length() > 0 && spinner_treatment_qty.getText().length() > 0) || medical_res.size() > 0))
        {
            showFab();
            return;
        }
        if(((spinner_drug.getText().length() > 0 && spinner_drug_qty.getText().length() > 0) || drug_res.size() > 0))
        {
            showFab();
            return;
        }
        if(treatmentMainActivity.fab.getVisibility() == View.VISIBLE)
        {
            treatmentMainActivity.fab.setVisibility(View.GONE);
        }
    }
//    private void isShowFab()
//    {
//        if((spinner_medical.getText().length() > 0 && spinner_medical_qty.getText().length() > 0))
//        {
//            showFab();
//            return;
//        }
//        if(((spinner_treatment.getText().length() > 0 && spinner_treatment_qty.getText().length() > 0) || medical_res.size() > 0))
//        {
//            showFab();
//            return;
//        }
//        if(((spinner_drug.getText().length() > 0 && spinner_drug_qty.getText().length() > 0) || drug_res.size() > 0))
//        {
//            showFab();
//            return;
//        }
//        if(treatmentMainActivity.fab.getVisibility() == View.VISIBLE)
//        {
//            treatmentMainActivity.fab.setVisibility(View.GONE);
//        }
//    }
    private void showFab()
    {
        if(treatmentMainActivity.fab.getVisibility() == View.GONE)
        {
            treatmentMainActivity.fab.setVisibility(View.VISIBLE);
            treatmentMainActivity.fab.setOnClickListener(myFabOnClickListener);
        }
    }
    private boolean saveTreatments()
    {
        //这边由于测试所以注释掉了，正常时候需要去掉注释
        if(!treatmentMainActivity.isAvailable)
        {
            Toast.makeText(getContext(), "This device has no NFC function.", Toast.LENGTH_SHORT).show();
            return false;
        }
//        if(!(treatmentMainActivity.connected && treatmentMainActivity.isConnect(getContext())))
//        {
//            Toast.makeText(getActivity(), "The net can not connect to backend. Please check the net.", Toast.LENGTH_SHORT).show();
//            return false;
//        }
        if(!NfcUtils.isNfcIntent(getActivity()))
        {
            Toast.makeText(getContext(), "Please get close to NFC tag.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(!NfcUtils.getTagId(getActivity().getIntent()).equals(treatmentMainActivity.detail.getTagId()))
        {
            Toast.makeText(getContext(), "Please get close to correct NFC tag.", Toast.LENGTH_SHORT).show();
            return false;
        }
        treatmentMainActivity.readTag(treatmentMainActivity.getIntent(), 1);
        detail = treatmentMainActivity.detail;
//        boolean isSuccess = false;
//        JsonArray jsonArray_medicalIds = new JsonArray();
//        JsonArray jsonArray_consumes = new JsonArray();
        //添加血型
//        transformData(blood_group, jsonArray_medicalIds, jsonArray_consumes);
//        //添加treatment
//        transformData(medical_res, jsonArray_medicalIds, jsonArray_consumes);
//        if(treatment != null && spinner_treatment_qty.getText().length() > 0)
//        {
//            treatment.setConsume(Integer.parseInt(spinner_treatment_qty.getText().toString()));
//            transformData(treatment, jsonArray_medicalIds, jsonArray_consumes);
//        }
//        //添加drug
//        transformData(drug_res, jsonArray_medicalIds, jsonArray_consumes);
//        if(drug != null && spinner_drug_qty.getText().length() > 0)
//        {
//            drug.setConsume(Integer.parseInt(spinner_drug_qty.getText().toString()));
//            transformData(drug, jsonArray_medicalIds, jsonArray_consumes);
//        }
        //做测试的
//        if(treatmentMainActivity.isAvailable)
//        {
//            treatmentMainActivity.readTag(treatmentMainActivity.getIntent(), 1);
//            detail = treatmentMainActivity.detail;
//        }
//        if(detail == null)
//        {
//            detail = new Detail();
//            //这边用于测试
//            detail.setTagId(prioritise.getTagId());
////            detail.setTagId(NfcUtils.getTagId(treatmentMainActivity.getIntent()));
//        }
//        List<MedicalRes> treatments = new ArrayList<>();
//        if(detail.getTreatments() == null)
//        {
//            detail.setTreatments(new ArrayList<MedicalRes>());
//        }
//        treatments.addAll(medical_res);
//        if(treatment != null && spinner_treatment_qty.getText().length() > 0)
//        {
//            treatments.add(treatment);
//        }
//        detail.setTreatments(treatments);
//        List<MedicalRes> drugs = new ArrayList<>();
//        if(detail.getDrugs() == null)
//        {
//            detail.setDrugs(new ArrayList<MedicalRes>());
//        }
//        drugs.addAll(drug_res);
//        if(drugs != null && spinner_drug_qty.getText().length() > 0)
//        {
//            drugs.add(drug);
//        }
//        detail.setDrugs(drugs);
        verifyBlood();
        verifyTreatment();
        verifyDrug();
        boolean isSuccess = NfcUtils.writeToTag(getActivity().getIntent(), treatmentMainActivity.gson.toJson(detail));
        saveSuccess(preIndex);
        detail = null;
        blood_group = null;
        treatment = null;
        drug = null;
        medical_res = new ArrayList<>();
        drug_res = new ArrayList<>();
        return isSuccess;
    }
    private void syncToBackend(JsonArray jsonArray_medicalIds, JsonArray jsonArray_consumes)
    {
        String url = Constants.getUrl(Constants.TREAT);
        Log.e("TEST", "----" + url);
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
                        Log.e("TEST", "save to tag success");
                    }
                }
                else
                {
                    Log.e("TEST", response.isSuccessful() + "---" + response.body());
                }
            }
        };
        Map<String, String> values = new HashMap<>();
        values.put("taglibid", detail.getTagId());
        values.put("hospitalid", "1");
        values.put("medicalIds", jsonArray_medicalIds.toString());
        values.put("consumes", jsonArray_consumes.toString());
        OkHttpClientHelper.postKeyValuePairAsync(getContext(), url, values, callback, null);
    }

    private boolean verifyBlood()
    {
        if(blood_group != null && spinner_medical_qty.getText().toString().length() > 0)
        {
            blood_group.setConsume(Integer.parseInt(spinner_medical_qty.getText().toString()));
        }
        if(detail != null)
        {
            if(detail.getBloodGroup() != null)
            {
                if(detail.getBloodGroup().getName().equals(blood_group.getName()))
                {
                    detail.getBloodGroup().setConsume(detail.getBloodGroup().getConsume() + blood_group.getConsume());
                    return true;
                }
                else
                {
                    detail.setBloodGroup(blood_group);
                    return true;
                }
            }
            else
            {
                detail.setBloodGroup(blood_group);
                return true;
            }
        }
        return false;
    }
    private boolean verifyTreatment()
    {
        if(treatment != null && spinner_treatment_qty.getText().length() > 0)
        {
            treatment.setConsume(Integer.parseInt(spinner_treatment_qty.getText().toString()));
        }
        List<MedicalRes> treatments_saved = detail.getTreatments();
        if(treatments_saved == null)
        {
            treatments_saved = new ArrayList<>();
        }
        if(medical_res != null && medical_res.size() > 0)
        {
            for(int i = 0;i < medical_res.size();i++)
            {
                int index = checkAddRes(treatments_saved, medical_res.get(i));
                if(index > 0)
                {
                    treatments_saved.get(index).setConsume(treatments_saved.get(index).getConsume() + medical_res.get(i).getConsume());
                }
                else
                {
                    treatments_saved.add(medical_res.get(i));
                }
            }
        }
        if(treatment != null && treatment.getConsume() > 0)
        {
            int index = checkAddRes(treatments_saved, treatment);
            if(index > 0)
            {
                treatments_saved.get(index).setConsume(treatments_saved.get(index).getConsume() + treatment.getConsume());
            }
            else
            {
                treatments_saved.add(treatment);
            }
        }

//        for(MedicalRes res : treatments_saved)
//        {
//            Log.e("TEST", res.toString());
//        }
        detail.setTreatments(treatments_saved);
        return false;
    }
    private boolean verifyDrug()
    {
        if(drug != null && spinner_drug_qty.getText().length() > 0)
        {
            drug.setConsume(Integer.parseInt(spinner_drug_qty.getText().toString()));
        }
        List<MedicalRes> drugs_saved = detail.getDrugs();
        if(drugs_saved == null)
        {
            drugs_saved = new ArrayList<>();
        }
        if(drug_res != null && drug_res.size() > 0)
        {
            for(int i = 0;i < drug_res.size();i++)
            {
                int index = checkAddRes(drugs_saved, drug_res.get(i));
                if(index > 0)
                {
                    drugs_saved.get(index).setConsume(drugs_saved.get(index).getConsume() + drug_res.get(i).getConsume());
                }
                else
                {
                    drugs_saved.add(drug_res.get(i));
                }
            }
        }
        if(drug != null && drug.getConsume() > 0)
        {
            int index = checkAddRes(drugs_saved, drug);
            if(index > 0)
            {
                drugs_saved.get(index).setConsume(drugs_saved.get(index).getConsume() + drug.getConsume());
            }
            else
            {
                drugs_saved.add(drug);
            }
        }
        for(MedicalRes res : drugs_saved)
        {
            Log.e("TEST", res.toString());
        }
        detail.setDrugs(drugs_saved);
        return false;
    }
    private int checkAddRes(List<MedicalRes> reses, MedicalRes res)
    {
        for(int i = 0;i < reses.size();i++)
        {
            if(reses.get(i).getName().equals(res.getName()))
            {
                return i;
            }
        }
        return -1;
    }
}
