package com.st.cms.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;
import com.st.cms.utils.Constants;
import com.st.cms.utils.ImageUtils;
import com.st.cms.utils.OkHttpClientHelper;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import cms.st.com.poccms.R;

/**
 * Created by yobin_he on 2017/1/19.
 */

public class ConfirmActivityForMain extends  BaseMainActivity{
    private TextView tv_id;
    private TextView tv_loc;
    private TextView tv_zone;

    private LinearLayout ll_zone;
    private TextView tv_confirm;
    private TextView tv_level;
    private int  sync_count;
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
    private Uri imageUri;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }
    @Override
    public void initView() {
        initToolBar("main");
        super.initView();
        prioritise_main = findViewById(R.id.prioritise_main);
        prioritise_main = LayoutInflater.from(getApplicationContext()).inflate(R.layout.layout_confirm_content, (ViewGroup) (prioritise_main.getParent()), true);

        tv_id = (TextView) prioritise_main.findViewById(R.id.tv_id);
        tv_loc = (TextView) prioritise_main.findViewById(R.id.tv_loc);
        tv_zone = (TextView) prioritise_main.findViewById(R.id.tv_zone);
        tv_confirm = (TextView) prioritise_main.findViewById(R.id.tv_confirm);
        tv_level = (TextView) prioritise_main.findViewById(R.id.tv_level);

        mImage = (ImageView) prioritise_main.findViewById(R.id.iv_main_victim);

        SharedPreferences sp = getApplicationContext().getSharedPreferences("config", MODE_PRIVATE);

        String id =sp.getString("taglibid","");
        String zone= sp.getString("zone","");
        String area=sp.getString("area","");
        String plv = sp.getString("plv","");

        tv_id.setText(id);
        tv_zone.setText(zone);
        tv_level.setText(plv);
        Log.i("--->>>", "----->>>"+tv_level);
        changeToPic();

        tv_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                syncToBackEnd1();
                Intent intent = new Intent(getApplicationContext(), TakePictureActivity.class);
                startActivity(intent);
                finish();

//                if (module == 1) {
//                    module = 0;
//                    Intent intent = new Intent(getApplicationContext(),TakePictureActivity.class);
//                    startActivity(intent);
//                    finish();
//                }


            }
        });
    }



    @Override
    protected void onResume() {
        super.onResume();
        changeToPic();
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









    private  void  syncToBackEnd1()  {
        Log.i("----->>", "----->>"+121212121);
        sync_count += 1;
        String url = Constants.getUrl(Constants.SAVE_TAG);
        Callback callback = new Callback() {
            @Override
            public void onFailure(Request request, final IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        progressDialog.cancel();
                        if (e.getMessage().length() > 0) {
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Request Failure!Please check the network.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                if (sync_count <= 3) {
                    try {
                        syncToBackEnd1();
                    } catch (Exception e1) {
                        Log.i("----->>>>", "syncToBackEnd1: "+22222);
                        e1.printStackTrace();
                    }
                }
                Log.e("TEST", "Request Failure!Please check the network.");
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
//                                progressDialog.cancel();
                                detail = null;
                                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });
//                        saveSuccess();
                    }

                } else {
                    Log.e("TEST", response.isSuccessful() + "---" + response.body());
                }

            }
        };




        SharedPreferences sp = getApplicationContext().getSharedPreferences("config", MODE_PRIVATE);
        String id =sp.getString("taglibid","");
        String zone= sp.getString("zone","");
        String area=sp.getString("area","");
        String plv = sp.getString("plv","");
        String bagId = sp.getString("bagId","");
        String suspect = sp.getString("suspect","");

        Map<String,String> values1 = new HashMap<>();
        values1.put("taglibid", id);
        values1.put("zone", zone);
        values1.put("area", area);
        values1.put("plv",plv);
        values1.put("bagId",bagId);
        values1.put("suspect",suspect);

//        I/----->>: ----->>121212121
//        01-19 14:27:24.882 20065-20065/cms.st.com.poccms I/----->>>: --->>id71ECC7D4
//        01-19 14:27:24.882 20065-20065/cms.st.com.poccms I/----->>>: --->>zoneTriage
//        01-19 14:27:24.882 20065-20065/cms.st.com.poccms I/----->>>: --->>areaA
//        01-19 14:27:24.882 20065-20065/cms.st.com.poccms I/----->>>: --->>plv2
//        01-19 14:27:24.882 20065-20065/cms.st.com.poccms I/----->>>>: syncToBackEnd1: 111111
//
////        if(bagId != null ){
//
//        }else {
//            values1.put("bagId",1111+"");
//        }
//        if(suspect!=null){
//            values1.put("suspect",suspect);
//        }else {
//            values1.put("suspect",1+ "");
//        }
        values1.put("nric","S5452543H");

        Log.i("----->>>", "--->>id"+id);
        Log.i("----->>>", "--->>zone"+zone);
        Log.i("----->>>", "--->>area"+area);
        Log.i("----->>>", "--->>plv"+plv);
        Log.i("----->>>", "--->>bagId"+bagId);
        Log.i("----->>>", "--->>suspect"+suspect);


//        Log.i("---->>>", "---->>>"+url);
//        Map<String, String> values = new HashMap<>();
////        values.put("taglibid", detail.getTagId());
//        values.put("taglibid", 1111+"");
//        values.put("zone", "A");
//        values.put("area", "A");
//        values.put("plv",111+"");
//        values.put("bagId",1111+"");
//        values.put("suspect",1+ "");
//        values.put("nric","S5452543H");

//       String newFilePath = Environment.getExternalStorageDirectory()+File.separator+"iconImage.jpg";
//        String filePath = Environment.getExternalStorageDirectory()+File.separator+"avatar1.jpg";

//        StreamReader sr = new StreamReader(getBytes(filePath));
//        string s = sr.ReadToEnd();

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//        byte[] b = getBytes(filePath);
//        Log.i("----->>>", "syncToBackEnd1: " + b.toString());
//        String portraitFile = new String(b);
//        try {
//            String sendString=new String(b);
//            values.put("imgString",sendString);
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }

//        values.put("imgString",Arrays.toString(b));
//        boolean flag = getFile(Arrays.toString(b), newFilePath, "newFile.jpg");
//        Log.i("---->>>", "syncToBackEnd1: "+ flag) ;

//        Log.i("----->>>>", "-->>"+getBytes(filePath));
//        File file = new File("");
//        File files[] = new File[]{file};
//-
//        String fileNames[] = new String[]{"temp.jpg"};

//        Log.i("----->>>", "outpuimage" + outputImage.getAbsolutePath());
//        Log.i("----->>", "syncToBackEnd: " + values.toString());
//        OkHttpClientHelper.postKeyValuePairAsync(getApplicationContext(), url, values1, callback, null);
        try {
            Log.i("----->>>>", "syncToBackEnd1: "+111111);
            OkHttpClientHelper.postUploadFilesAsync(getApplicationContext(),url,values1,null,null,callback,"tag");
        } catch (IOException e) {
            Log.i("----->>>>", "syncToBackEnd1: "+33333333);
            e.printStackTrace();
        }


//        try {
//        } catch (Exception e) {
//            Toast.makeText(getApplicationContext(), "Please open the Network.", Toast.LENGTH_SHORT).show();
//            e.printStackTrace();
//        }
    }
    private String getIconName(Context context){
        SharedPreferences sp = context.getSharedPreferences("config", MODE_PRIVATE);
        return sp.getString("nric","");
    }
}
