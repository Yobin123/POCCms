package com.st.cms.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;
import com.st.cms.adapter.PrioritiseAdapter;
import com.st.cms.entity.Detail;
import com.st.cms.utils.Constants;
import com.st.cms.utils.DensityUtil;
import com.st.cms.utils.ImageUtils;
import com.st.cms.utils.NfcUtils;
import com.st.cms.utils.OkHttpClientHelper;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import cms.st.com.poccms.R;

/**
 *
 */
public class MainActivity extends BaseMainActivity{

    private TextView tv_id;
    private TextView tv_loc;
    private ListView lv_prioritises;
    private TextView tv_lv;

    private LinearLayout ll_zone;
    //保存弹出框
    private View saveView = null;
    private PopupWindow mySavePopup = null;
    private Button btn_save_tag = null;

    //弹出框
    private PopupWindow myPopup;
    private View popupView;
    private ListView zone_array;
    //级别
    private String[] prioritises = new String[]{
            "P0", "P1", "P2", "P3", "UIN"
    };
    //同步次数，如果超过3次则取消同步
    private int sync_count = 0;
   private PrioritiseAdapter prioritiseAdapter;
    ///////////////////////////////////////////////////////////////////////////
    // 这是新添加控件
    ///////////////////////////////////////////////////////////////////////////

//    肖像
    private ImageView mImage;
    //是否为可疑人物
    private CheckBox mChecBox;
    //可以人物标志位
    private int isSuspect = 0;
    private String bagId = null;
    //用于跳转到ok页面时；判断是否跳到照相界面
    private boolean ok_flag = true;
    private ImageView iv_main_victim;
    private EditText mEditText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    @Override
    public void initView() {
        initToolBar("main");
        super.initView();
        prioritise_main = findViewById(R.id.prioritise_main);
        prioritise_main = LayoutInflater.from(getApplicationContext()).inflate(R.layout.content_main, (ViewGroup) (prioritise_main.getParent()), true);
        tv_id = (TextView) prioritise_main.findViewById(R.id.tv_id);
        tv_loc = (TextView) prioritise_main.findViewById(R.id.tv_loc);
        tv_lv = (TextView) prioritise_main.findViewById(R.id.tv_lv);

        mEditText = (EditText)prioritise_main.findViewById(R.id.editText);


        lv_prioritises = (ListView) prioritise_main.findViewById(R.id.lv_prioritises);
        ll_zone = (LinearLayout) prioritise_main.findViewById(R.id.ll_zone);
        ll_zone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                showSaveTagWindow();
                showZoneSelectWindow();
            }
        });


        //肖像和可疑人物
        mImage = (ImageView) prioritise_main.findViewById(R.id.iv_main_victim);
        mChecBox = (CheckBox) prioritise_main.findViewById(R.id.cb_main_suspect);
//        mChecBox.setChecked(false);
        changeToPic();

       //监听相应的checkbox,写入到相应的Tag对象
        mChecBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                  if( isChecked){
                      isSuspect = 1;

                  }else {
                      isSuspect = 0;
                  }
                Log.i("isSuspect", "----->>"+isSuspect );
            }
        });



        //初始化prioritises列表
        prioritiseAdapter= new PrioritiseAdapter(getApplicationContext(), prioritises);
        lv_prioritises.setAdapter(prioritiseAdapter);
        lv_prioritises.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position != prioritises.length -1){
                    onPrioritise(position);
                }
//                mEditText = (EditText) view.findViewById(R.id.editText);
//                jumpToTakePicActivity();
//             testBack(position);
            }
        });
    }

    private void jumpToTakePicActivity() {
        Intent intent = new Intent(getApplicationContext(),TakePictureActivity.class);
        startActivity(intent);

    }

    private void changeToPic() {
        String icon = getIconName(getApplicationContext());
        if(icon!=null){
            File outputImage = new File(Environment.getExternalStorageDirectory(), icon +".jpg");
            if(outputImage.exists() && outputImage.length() > 0){
                try {
                    Bitmap bitmap =ImageUtils.readBitmapByPath(outputImage.getPath());
                    Bitmap bitmap1= ImageUtils.drawCircleView01(bitmap);
                    mImage.setImageBitmap(bitmap1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void testBack(int position) {
        alertOkPage();

    }

    private void alertOkPage() {

        Intent intent = new Intent();
        intent.setClass(getApplicationContext(), ConfirmActivityForMain.class);
        Bundle bundle = new Bundle();
        bundle.putInt("status", 0);
        bundle.putString("confirm", getResources().getString(R.string.ok));
        bundle.putString("info", getResources().getString(R.string.prioritise_completed));
        bundle.putBoolean("okFlag",ok_flag);
        intent.putExtras(bundle);
        startActivity(intent);
//        overridePendingTransition(R.anim.ok_show_anim,R.anim.ok_hide_anim);
//        overridePendingTransition(R.anim.save_show_anim,0);
//         startActivity(intent);//此处的requestCode应与下面结果处理函中调用的requestCode一致
        //Activity

    }


    @Override
    protected void onResume() {
        super.onResume();
        setFabMargin(1);
        index = 0;
        if (!isAvailable) {
            Toast.makeText(getApplicationContext(), "This device has no NFC function.", Toast.LENGTH_SHORT).show();
        }
//        rl_btn.setVisibility(View.VISIBLE);
//        rl_content.setVisibility(View.GONE);
//        mChecBox.setChecked(false);
//        changeToPic();
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        try {
            if (isAvailable) {
                if (!(NfcAdapter.ACTION_TECH_DISCOVERED.equals(getIntent().getAction())
                        || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())
                        || NfcAdapter.ACTION_TAG_DISCOVERED.equals(getIntent().getAction()))) {
                    tv_id.setText(getResources().getString(R.string.none_text));
                    tv_loc.setText(getResources().getTextArray(R.array.zone_loc)[0]);
                    tv_lv.setText("-");
                    return;
                }
                readTag(intent, 0);
                tv_id.setText(NfcUtils.getTagId(intent));
                if (detail != null) {
                    tv_loc.setText(detail.getZone());
                    tv_lv.setText(prioritises[detail.getPlv() == -1 ? 4 : detail.getPlv()]);

                    Log.i("----->>>>", "----->>>detail"+detail.getIsSuspect());
                    Log.i("----->>>>", "----->>>detail"+(detail.getIsSuspect() == 1));

                    if(detail.getIsSuspect() == 1){
                        mChecBox.setChecked(true);
                    }else {
                        mChecBox.setChecked(false);
                    }

                    if(detail != null && detail.getBagId() != null)
                    {
                        mEditText.setText(detail.getBagId());
                    }
                } else {
                    tv_loc.setText(getResources().getTextArray(R.array.zone_loc)[0]);
                    tv_lv.setText(prioritises[4]);
                    mChecBox.setChecked(false);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 设定prioritise的逻辑代码，并且成功跳转成功界面
     */
    private void onPrioritise(int position) {
        if (!isAvailable) {
            Toast.makeText(getApplicationContext(), "This device has no NFC function.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!(connected && isConnect(getApplicationContext()))) {
            Toast.makeText(getApplicationContext(), "The net can not connect to backend. Please check the net.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!NfcUtils.isNfcIntent(this)) {
            Toast.makeText(getApplicationContext(), "Please get close to NFC tag.", Toast.LENGTH_SHORT).show();
            return;
        }
        if(detail != null)
        {
            if (!NfcUtils.getTagId(getIntent()).equals(detail.getTagId())) {
                Toast.makeText(getApplicationContext(), "Please get close to correct NFC tag.", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        if (tv_loc.getText().length() < 2) {
            Toast.makeText(MainActivity.this, "Please define the zone location.", Toast.LENGTH_SHORT).show();
            return;
        }
        readTag(getIntent(), 1);
        if(mEditText != null && mEditText.getText() != null)
        {
            detail.setBagId(mEditText.getText().toString().trim());
            bagId = mEditText.getText().toString().trim();
        }
        if(isSuspect == 1){

            mChecBox.setChecked(true);
            detail.setIsSuspect(isSuspect);
        }else {
            mChecBox.setChecked(false);
            detail.setIsSuspect(isSuspect);
        }
        if (position == 4) {
            position = -1;
        }
        saveTag(position);
    }

    /**
     * 点击zone location 行时触发此方法，用于修改zone
     */
    private void showZoneSelectWindow() {
        if (popupView == null) {
            popupView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.popup_zone, null);
            zone_array = (ListView) popupView.findViewById(R.id.zone_array);
            zone_array.setAdapter(new ArrayAdapter<String>(getApplicationContext(), R.layout.array_zone, getResources().getStringArray(R.array.zone_loc)));
            zone_array.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    tv_loc.setText(getResources().getStringArray(R.array.zone_loc)[position]);
                    myPopup.dismiss();
                }
            });
        }
        if (myPopup == null) {
            myPopup = new PopupWindow(popupView, DensityUtil.dip2px(getApplicationContext(), 200), LinearLayout.LayoutParams.WRAP_CONTENT);
            myPopup.setFocusable(true);
            myPopup.setOutsideTouchable(true);
            myPopup.setBackgroundDrawable(new BitmapDrawable());
        }
        myPopup.showAtLocation(ll_zone, Gravity.CENTER, 0, 0);
    }

    /**
     * 通过网络保存数据到后台
     */
    private void saveTag(final int position) {

        if (detail == null) {
            detail = new Detail();
            detail.setTagId(NfcUtils.getTagId(getIntent()));
        }
        detail.setPlv(position);
        detail.setZone(tv_loc.getText() + "");

        detail.setIsSuspect(isSuspect); //设置是否为相应的嫌疑人

        if(bagId != null){
            detail.setBagId(bagId);
        }else {
            detail.setBagId("");
        }

        boolean isSuccess = NfcUtils.writeToTag(getIntent(), gson.toJson(detail));
        if (!isSuccess) {
            Toast.makeText(MainActivity.this, "Please get close to the nfc tag.", Toast.LENGTH_SHORT).show();
        }
        if (isSuccess) {
//            saveSuccess();
            saveData(getApplicationContext());
            alertOkPage();
            Toast.makeText(MainActivity.this, "Success to save info to tag. Prepare to Sync to backend now.", Toast.LENGTH_SHORT).show();
            sync_count = 0;
//            syncToBackEnd(position);
            //保存到preference里面

//            SharedPreferences sp = getApplicationContext().getSharedPreferences("config", MODE_PRIVATE);
//           String id= sp.getString("taglibid","");
//            Log.i("----->>>", "save "+ id);
            //跳转页面
//            saveSuccess();


        }
    }


    private void syncToBackEnd(final int position) {
        sync_count += 1;
        String url = Constants.getUrl(Constants.SAVE_TAG);
        Callback callback = new Callback() {
            @Override
            public void onFailure(Request request, final IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (e.getMessage().length() > 0) {
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Request Failure!Please check the network.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                if (sync_count <= 3) {
                    syncToBackEnd(position);
                }
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (response.isSuccessful()) {
                    ResponseBody body = response.body();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Success to sync to backend", Toast.LENGTH_SHORT).show();
                        }
                    });
                    if (body != null) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                detail = null;
                                tv_id.setText("-");
                                tv_loc.setText("-");
                                tv_lv.setText("-");
                            }
                        });
                        saveSuccess();
                    }

                } else {
                    Log.e("TEST", response.isSuccessful() + "---" + response.body());
                }

            }
        };
        Log.e("TEST", url);
        Map<String, String> values = new HashMap<>();
//        values.put("taglibid", detail.getTagId());
        values.put("taglibid", 1111+"");
        values.put("zone", detail.getZone());
        values.put("area", "A");
        values.put("plv", position + "");
        Log.e("TEST", "----plv:  " + position);
        values.put("bagId",1111+"");
        values.put("suspect",1+ "");
        Log.i("----->>", "syncToBackEnd: " + values.toString());
        OkHttpClientHelper.postKeyValuePairAsync(getApplicationContext(), url, values, callback, null);
//        try {
//        } catch (Exception e) {
//            Toast.makeText(getApplicationContext(), "Please open the Network.", Toast.LENGTH_SHORT).show();
//            e.printStackTrace();
//        }
    }

    private void showSaveTagWindow() {
        if (saveView == null) {
            saveView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.popup_save_tag, null);
            btn_save_tag = (Button) saveView.findViewById(R.id.btn_save_tag);
            btn_save_tag.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!(NfcAdapter.ACTION_TECH_DISCOVERED.equals(getIntent().getAction())
                            || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())
                            || NfcAdapter.ACTION_TAG_DISCOVERED.equals(getIntent().getAction()))) {
                        Toast.makeText(getApplicationContext(), "Please get close to NFC tag.", Toast.LENGTH_SHORT).show();
                    }
                    Log.e("TEST", NfcUtils.getTagId(getIntent()) + "----" + detail.getTagId());
                    if (NfcUtils.getTagId(getIntent()).equals(detail.getTagId())) {
                        boolean isSuccess = NfcUtils.writeToTag(getIntent(), gson.toJson(detail));
                        Log.e("TEST", "show save tag1----" + isSuccess);
                        if (isSuccess) {
                            mySavePopup.dismiss();
                            saveSuccess();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Please get close to correct NFC tag.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        if (mySavePopup == null) {
            mySavePopup = new PopupWindow(saveView, DensityUtil.dip2px(getApplicationContext(), 200), LinearLayout.LayoutParams.WRAP_CONTENT);
            mySavePopup.setFocusable(true);
            mySavePopup.setOutsideTouchable(true);
        }
        mySavePopup.showAtLocation(ll_zone, Gravity.CENTER, 0, 0);
    }



    private void saveData(Context context){
        SharedPreferences sp = context.getSharedPreferences("config", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        editor.putString("taglibid",detail.getTagId());
        editor.putString("zone",detail.getZone());
        editor.putString("area","A");
        editor.putString("plv",detail.getPlv()+"");
        editor.putString("bagId",detail.getBagId());
        editor.putString("suspect",detail.getIsSuspect()+"");
        editor.commit();
    }
    private String getIconName(Context context){
        SharedPreferences sp = context.getSharedPreferences("config", MODE_PRIVATE);
        return sp.getString("nric","");
    }


}
