package com.st.cms.fragment;

import android.Manifest;
import android.app.Dialog;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SupportErrorDialogFragment;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;
import com.st.cms.activity.ConveyListActivity;
import com.st.cms.entity.Detail;
import com.st.cms.entity.Prioritise;
import com.st.cms.entity.Wperson;
import com.st.cms.utils.Constants;
import com.st.cms.utils.NfcUtils;
import com.st.cms.utils.OkHttpClientHelper;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;

import cms.st.com.poccms.R;

/**
 * Created by jt on 2016/9/7.
 */
public class ConveyMapFragment extends ConveyBaseFragment implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    private static ConveyMapFragment fragment;

    private SupportMapFragment mapFragment;
    private GoogleMap map;
    private GoogleApiClient mGoogleApiClient;
    private Spinner spinner_hospital, spinner_vehicle;
    private ArrayAdapter arrayAdapter, vehicleAdapter;
    private AdapterView.OnItemSelectedListener myOnItemSelectedListener, vehicleOnItemSelectedListener;
    private int currentHospitalId = 1;
    private int currentVehicle = 1;

    private TextView tv_confirm;
    private RelativeLayout rl_dashboard;
    private RelativeLayout rl_info;
    private int preIndex = -1;
    private MyBackListener myBackListener;
    private ConveyListActivity.DetailInterface detailInterface;
    private String[][] ambulanceIds = {{"7", "8"}, {"9" , "10"}, {"11", "12"}};
    /*
	 * Define a request code to send to Google Play services This code is
	 * returned in Activity.onActivityResult
	 */
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_convey_map, container, false);
        mapFragment = (SupportMapFragment)getChildFragmentManager().findFragmentById(R.id.map_view);
        spinner_hospital = (Spinner)view.findViewById(R.id.spinner_hospital);
        spinner_vehicle = (Spinner)view.findViewById(R.id.spinner_vehicle);
        arrayAdapter = ArrayAdapter.createFromResource(getContext(), R.array.hospitals, R.layout.widget_spinner_style);
        vehicleAdapter = ArrayAdapter.createFromResource(getContext(), R.array.hospital1, R.layout.widget_spinner_style);
        spinner_vehicle.setAdapter(vehicleAdapter);
        myOnItemSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position)
                {
                    case 0:
                        vehicleAdapter = ArrayAdapter.createFromResource(getContext(), R.array.hospital1, R.layout.widget_spinner_style);
                        spinner_vehicle.setAdapter(vehicleAdapter);
                        currentHospitalId = 1;
                        break;
                    case 1:
                        vehicleAdapter = ArrayAdapter.createFromResource(getContext(), R.array.hospital2, R.layout.widget_spinner_style);
                        spinner_vehicle.setAdapter(vehicleAdapter);
                        currentHospitalId = 2;
                        break;
                    case 2:
                        vehicleAdapter = ArrayAdapter.createFromResource(getContext(), R.array.hospital3, R.layout.widget_spinner_style);
                        spinner_vehicle.setAdapter(vehicleAdapter);
                        currentHospitalId = 3;
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };
        vehicleOnItemSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentVehicle = position + 1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };
        spinner_vehicle.setOnItemSelectedListener(vehicleOnItemSelectedListener);
        spinner_hospital.setAdapter(arrayAdapter);
        spinner_hospital.setOnItemSelectedListener(myOnItemSelectedListener);
        rl_dashboard = (RelativeLayout)view.findViewById(R.id.rl_dashboard);
        rl_info = (RelativeLayout)view.findViewById(R.id.rl_info);
        tv_confirm = (TextView)view.findViewById(R.id.tv_confirm);
        tv_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!saveHospital())
                {
                    return;
                }
                rl_dashboard.setVisibility(View.GONE);
                rl_info.setVisibility(View.VISIBLE);
                tv_confirm.setVisibility(View.GONE);
            }
        });
        connectClient();
        conveyListActivity.initToolBar("content");
        conveyListActivity.tv_title.setText(prioritise.getTagId());
        conveyListActivity.iv_back.setOnClickListener(new MyBackListener(preIndex));
        Log.e("TEST", mapFragment + "---");
        if(mapFragment != null)
        {
            map = mapFragment.getMap();
        }
        if(map != null)
        {
            LatLng latLng = new LatLng(1.34460, 103.79376);
            addMarker(latLng);
        }
        rl_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConveyDetailFragment fragment = ConveyDetailFragment.newInstance(prioritise, conveyListActivity.currentIndex);
                jumpAction(fragment);
            }
        });
        initDetailInfo();
        return view;
    }

    public static ConveyMapFragment newInstance(Prioritise prioritise, int preIndex) {
        if (fragment == null) {
            fragment = new ConveyMapFragment();
        }
        fragment.prioritise = prioritise;
        fragment.preIndex = preIndex;
        initView();
        return fragment;
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
            if(myOnItemSelectedListener != null && spinner_hospital != null)
            {
                spinner_hospital.setOnItemSelectedListener(myOnItemSelectedListener);
            }
            if(vehicleOnItemSelectedListener != null && spinner_vehicle != null)
            {
                spinner_vehicle.setOnItemSelectedListener(vehicleOnItemSelectedListener);
            }
            conveyListActivity.iv_back.setOnClickListener(new MyBackListener(preIndex));
            connectClient();
            conveyListActivity.initToolBar("content");
            conveyListActivity.tv_title.setText(prioritise.getTagId());
            initDetailInfo();
            arrayAdapter = ArrayAdapter.createFromResource(getContext(), R.array.hospitals, R.layout.widget_spinner_style);
            vehicleAdapter = ArrayAdapter.createFromResource(getContext(), R.array.hospital1, R.layout.widget_spinner_style);
            spinner_vehicle.setAdapter(vehicleAdapter);
            spinner_hospital.setAdapter(arrayAdapter);
        }
        else
        {
            if (mGoogleApiClient != null) {
                mGoogleApiClient.disconnect();
            }
        }
        super.onHiddenChanged(hidden);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    protected void connectClient() {
        // Connect the client.
        if (isGooglePlayServicesAvailable() && mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }
    @Override
    public void onConnected(@Nullable Bundle bundle) {
    }

    @Override
    public void onConnectionSuspended(int i) {
        if (i == CAUSE_SERVICE_DISCONNECTED) {
            Toast.makeText(getActivity(), "Disconnected. Please re-connect.", Toast.LENGTH_SHORT).show();
        } else if (i == CAUSE_NETWORK_LOST) {
            Toast.makeText(getActivity(), "Network lost. Please re-connect.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPause() {
        // Disconnecting the client invalidates it.
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        super.onPause();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
/*
		 * Google Play services can resolve some errors it detects. If the error
		 * has a resolution, try sending an Intent to start a Google Play
		 * services activity that can resolve error.
		 */
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(getActivity(),
                        CONNECTION_FAILURE_RESOLUTION_REQUEST);
				/*
				 * Thrown if Google Play services canceled the original
				 * PendingIntent
				 */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getActivity(),
                    "Sorry. Location services not available to you", Toast.LENGTH_LONG).show();
        }
    }
    private boolean isGooglePlayServicesAvailable() {
        // Check that Google Play services is available
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getContext());
        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {
            // In debug mode, log the status
            Log.d("Location Updates", "Google Play services is available.");
            return true;
        } else {
            // Get the error dialog from Google Play services
            Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(resultCode, getActivity(),
                    CONNECTION_FAILURE_RESOLUTION_REQUEST);

            // If Google Play services can provide an error dialog
            if (errorDialog != null) {
                // Create a new DialogFragment for the error dialog
                SupportErrorDialogFragment errorFragment = new SupportErrorDialogFragment();
                errorFragment.setupDialog(errorDialog, SupportErrorDialogFragment.STYLE_NORMAL);
                errorFragment.show(getFragmentManager(), "Location Updates");
            }

            return false;
        }
    }
    private void addMarker(LatLng latLng)
    {
        if(map != null)
        {
            map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            map.animateCamera(CameraUpdateFactory.zoomTo(15));
            MarkerOptions markerOptions = new MarkerOptions().position(latLng);
            BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.mipmap.location);
            markerOptions.icon(bitmap);
            map.addMarker(markerOptions);
        }
    }
    private void initDetailInfo()
    {
        if(detailInterface == null)
        {
            detailInterface = new ConveyListActivity.DetailInterface() {
                @Override
                public void getDetailFromTag() {
//                    if(detail.getTagId().equals(prioritise.getTagId()))
//                    {
                        detail = conveyListActivity.detail;
                        if(detail != null)
                        {
                            if(detail.getDesignated_hospital() != null && detail.getDesignated_hospital().length() > 0)
                            {
                                rl_dashboard.setVisibility(View.GONE);
                                rl_info.setVisibility(View.VISIBLE);
                                tv_confirm.setVisibility(View.GONE);
                            }
                        }
//                    }
                }
            };
        }
        conveyListActivity.setDetailInterface(detailInterface);
    }
    private static void initView()
    {
        if(fragment.rl_dashboard == null || fragment.rl_info == null || fragment.tv_confirm == null)
            return;
        fragment.rl_dashboard.setVisibility(View.VISIBLE);
        fragment.rl_info.setVisibility(View.GONE);
        fragment.tv_confirm.setVisibility(View.VISIBLE);
    }
    private boolean saveHospital()
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

        //假数据
        detail.setHospital_id(currentHospitalId);
        detail.setAmbulance_id(Integer.parseInt(ambulanceIds[currentHospitalId - 1][currentVehicle - 1]));
        int vehicle_id = 0;
        switch (currentHospitalId)
        {
            case 1:
                vehicle_id = R.array.hospital1;
                break;
            case 2:
                vehicle_id = R.array.hospital2;
                break;
            case 3:
                vehicle_id = R.array.hospital3;
                break;
        }
        detail.setDesignated_hospital(getResources().getStringArray(R.array.hospitals)[currentHospitalId - 1]);
        detail.setVehicle_plate_no(getResources().getStringArray(vehicle_id)[currentVehicle - 1]);
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
                }
                else
                {
                    Log.e("TEST", response.isSuccessful() + "---" + response.body());
                }

            }
        };
        Log.e("TEST", Constants.getUrl(Constants.SET_HOSPITAL + "/" + detail.getTagId() + "/" + detail.getHospital_id() + "/" + detail.getAmbulance_id()));
        OkHttpClientHelper.loadNetworkData(getContext(), Constants.getUrl(Constants.SET_HOSPITAL + "/" + detail.getTagId() + "/" + detail.getHospital_id() + "/" + detail.getAmbulance_id()), callback);
        isSuccess = NfcUtils.writeToTag(getActivity().getIntent(), conveyListActivity.gson.toJson(detail));
        return isSuccess;
    }
}
