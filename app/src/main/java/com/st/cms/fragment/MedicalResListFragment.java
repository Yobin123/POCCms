package com.st.cms.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;
import com.st.cms.activity.TreatmentMainActivity;
import com.st.cms.adapter.MedicalResAdapter;
import com.st.cms.entity.MedicalRes;
import com.st.cms.entity.Prioritise;
import com.st.cms.entity.Wmedical;
import com.st.cms.entity.Wperson;
import com.st.cms.utils.Constants;
import com.st.cms.utils.OkHttpClientHelper;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import cms.st.com.poccms.R;

/**
 * Created by jt on 2016/9/1.
 */
public class MedicalResListFragment extends TreatmentBaseFragment{
    private ListView lv_stockpile;
    private static MedicalResListFragment fragment;
    private String title;
    private int preIndex = -1;
    private MyBackListener myBackListener;
    private List<Wmedical> wmedicals;
    private MedicalRes res = null;
    private MedicalResAdapter medicalResAdapter;

    public void setTitle(String title) {
        this.title = title;
    }
    private String[] types = {"Blood", "Medical", "Drug"};

    //模拟数据
    private List<MedicalRes> reses = new ArrayList<>();
    private void initData()
    {
        reses.clear();
//        res = new MedicalRes("Ribavirin", 1000, 300);
//        reses.add(res);
//        res = new MedicalRes("Idoxuridine", 900, 150);
//        reses.add(res);
//        res = new MedicalRes("Lamivudine", 200, 50);
//        reses.add(res);
//        res = new MedicalRes("Quinine", 600, 100);
//        reses.add(res);
//        res = new MedicalRes("Aspirin", 1200, 350);
//        reses.add(res);
//        res = new MedicalRes("Antondin", 900, 600);
//        reses.add(res);
//        res = new MedicalRes("Ibuprofen", 500, 120);
//        reses.add(res);
//        res = new MedicalRes("Aulin", 800, 650);
//        reses.add(res);
//        res = new MedicalRes("Fentanyl", 660, 340);
//        reses.add(res);
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
                        String result = body.string();
                        Log.e("TEST", result);
                        Type type = new TypeToken<List<Wmedical>>() {}.getType();
                        wmedicals = treatmentMainActivity.gson.fromJson(result, type);
                        if(wmedicals != null)
                        {
                            treatmentMainActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Wmedical wmedical = null;
                                    for(int i = 0;i < wmedicals.size();i++)
                                    {
                                        wmedical = wmedicals.get(i);
                                        int type = getType();
                                        int w_type = compareType(wmedical.getMedical_type());
                                        if(type == w_type)
                                        {
                                            res = new MedicalRes(wmedical.getMedical_id(), wmedical.getMedical_name(),
                                                    wmedical.getQty(), type, wmedical.getUsed());
                                            reses.add(res);
                                            if(medicalResAdapter != null)
                                            {
                                                medicalResAdapter.notifyDataSetChanged();
                                            }
                                        }
                                    }
                                }
                            });
                        }
                    }
                }
                else
                {
                    Log.e("TEST", response.isSuccessful() + "---" + response.body());
                }

            }
        };
        OkHttpClientHelper.loadNetworkData(getContext(), Constants.getUrl(Constants.GET_MEDICAL), callback);
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stockpile, container, false);
        initData();
        lv_stockpile = (ListView) view.findViewById(R.id.lv_stockpile);
        medicalResAdapter = new MedicalResAdapter(getContext(), reses);
        lv_stockpile.setAdapter(medicalResAdapter);
        lv_stockpile.setOnItemClickListener(new MyOnItemClickListener());
        treatmentMainActivity = (TreatmentMainActivity)getActivity();
        treatmentMainActivity.tv_title.setText(title);
        myBackListener = new MyBackListener(preIndex);
        treatmentMainActivity.iv_back.setOnClickListener(myBackListener);
        return view;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
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
            initData();
            treatmentMainActivity.iv_back.setOnClickListener(new MyBackListener(preIndex));
            treatmentMainActivity.tv_title.setText(title);
        }
    }

    public static MedicalResListFragment newInstance(Prioritise prioritise, String title, int preIndex) {
        if(fragment == null)
        {
            fragment = new MedicalResListFragment();
        }
        fragment.prioritise = prioritise;
        fragment.title = title;
        fragment.preIndex = preIndex;
        return fragment;
    }
    private class MyOnItemClickListener implements AdapterView.OnItemClickListener
    {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            int type = getType();
            MedicalAssignFragment fragment = MedicalAssignFragment.newInstance(prioritise, (MedicalRes)parent.getAdapter().getItem(position), type);
            jumpAction(fragment);
        }
    }
    private int getType()
    {
        int type = 0;
        switch (title)
        {
            case "Medical Supplies":
                type = 1;
                break;
            case "Drug Supplies":
                type = 2;
                break;
            case "Blood Bank":
                type = 0;
                break;
        }
        return type;
    }
    private int compareType(String typeStr)
    {
        int type = 0;
        for(int i = 0;i < types.length;i++)
        {
            if(typeStr.equals(types[i]))
            {
                type = i;
                break;
            }
        }
        return type;
    }
}
