package com.st.cms.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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
 * Created by yobin_he on 2017/1/18.
 */
public class TakePictureActivity extends BaseMainActivity implements View.OnClickListener {
    public static final int TAKE_PHOTO = 1;
    public static final int CROP_PHOTO = 2;
    public final static String IMAGE_PATH = "image_path";

    //同步次数，如果超过3次则取消同步
    private int sync_count = 0;

    private ImageView mImageView;
    private Button btn_capture;
    private Button btn_retake;
    private ImageView iv_save;

    private File outputImage;
    private boolean flag = true;//用于判断是否显示retake的按钮
    private Bitmap newBitmap;
    private String nric = null;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    Bitmap bitmap = (Bitmap) msg.obj;
                    if (bitmap!= null) {
                        mImageView.setImageBitmap(bitmap);
                    }
                    if (flag) {
//                            flag = false;
                        btn_capture.setVisibility(View.GONE);
                        btn_retake.setVisibility(View.VISIBLE);
                        iv_save.setVisibility(View.VISIBLE);
                    }
//                    if(progressDialog.isShowing()){
//                        progressDialog.cancel();
//                    }
                    break;
            }
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        nric = "S5452543H";
        initView();
    }


    @Override
    public void initView() {
        initToolBar("main");
        super.initView();
        prioritise_main = findViewById(R.id.prioritise_main);
        prioritise_main = LayoutInflater.from(getApplicationContext()).inflate(R.layout.takepic_content_main, (ViewGroup) (prioritise_main.getParent()), true);

        mImageView = (ImageView) prioritise_main.findViewById(R.id.iv_check);
        btn_capture = (Button) prioritise_main.findViewById(R.id.btn_capture);
        btn_retake = (Button) prioritise_main.findViewById(R.id.btn_retake);
        iv_save = (ImageView) prioritise_main.findViewById(R.id.iv_save);

        btn_capture.setOnClickListener(this);
        btn_retake.setOnClickListener(this);
        iv_save.setOnClickListener(this);

//        int isSuspect = detail.getIsSuspect();
//        Log.i("----->>>>>", "isppct" + isSuspect);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_capture:
                creatFileToSave();//创建保存照片
                takePic();
//                syncToBackEnd1();
                break;
            case R.id.btn_retake:
                creatFileToSave();//创建保存照片
                takePic();
                break;
            case R.id.iv_save:
                //这里建立保存上传

                if(!progressDialog.isShowing()){
                    progressDialog.show();
                }
                syncToBackEnd1();
                break;
        }

    }


    private void creatFileToSave() {
        //创建File对象用于存储拍照后的图片
        if(nric!=null){
            outputImage = new File(Environment.getExternalStorageDirectory(),nric+ ".jpg");
        }else {
            nric = "iconImag";
            outputImage = new File(Environment.getExternalStorageDirectory(),nric+ ".jpg");
        }
        saveDataToPicName(getApplicationContext());

        try {
            if (outputImage.exists()) {
                outputImage.delete();
            }
            outputImage.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void takePic() {

        Intent intent = null;
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    1);
        }
//        if(Build.VERSION.SDK_INT<=Build.VERSION_CODES.KITKAT){
//            intent = new Intent("android.media.action.IMAGE_CAPTURE");
//            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
//        }
//        else
//        {
//            if(outputImage==null){
//                outputImage=new File(getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "tupian.png");
//            }
        intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//            Uri imageURI=Uri.fromFile(outputImage);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(outputImage));
//        }
        if (intent != null) {
//            intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
            startActivityForResult(intent, TAKE_PHOTO); // 启动相机程序
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    Intent intent = new Intent("com.android.camera.action.CROP");
                    intent.setDataAndType(Uri.fromFile(outputImage), "image/*");
                    intent.putExtra("scale", true);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(outputImage));
                    startActivityForResult(intent, CROP_PHOTO); // 启动裁剪程序
                }
                break;
            case CROP_PHOTO:
                if (resultCode == RESULT_OK) {
//                    progressDialog = new ProgressDialog(TakePictureActivity.this);
//                    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//                    progressDialog.setMessage("please wait a moment");
//                    if(!progressDialog.isShowing()){
//                        progressDialog.show();
//                    }
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
//                                newBitmap = ImageUtils.getCompressImage(outputImage.getPath());
                                newBitmap = ImageUtils.readBitmapByPath(outputImage.getPath());
                                 Message message = new Message();
                                 message.what = 1;
                                 message.obj = newBitmap;
                                 handler.sendMessage(message);
//                                ImageUtils.setCompressImage(outputImage.getPath());
                            }
                        }).start();
                    }
                break;
            default:
                break;
        }

    }

    private void syncToBackEnd1() {

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
//                        if(progressDialog.isShowing()){
//                            progressDialog.cancel();
//                            progressDialog = null;
//                        }
                    }
                });
                if (sync_count <= 3) {
                    try {
                        syncToBackEnd1();
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
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
//                                progressDialog.cancel();
                                detail = null;
                                if(progressDialog.isShowing()){
                                    progressDialog.cancel();
                                    progressDialog = null;
                                }
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
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
        String id = sp.getString("taglibid", "");
        String zone = sp.getString("zone", "");
        String area = sp.getString("area", "");
        String plv = sp.getString("plv", "");
        String bagId = sp.getString("bagId", "");
        String suspect = sp.getString("suspect", "");

        Map<String,String> values1 = new HashMap<>();
        values1.put("taglibid", id);
        values1.put("zone", zone);
        values1.put("area", area);
        values1.put("plv",plv);
        values1.put("bagId",bagId);
        values1.put("suspect",suspect);
        values1.put("nric",nric);

        Log.i("----->>>", "--->>id" + id);
        Log.i("----->>>", "--->>zone" + zone);
        Log.i("----->>>", "--->>area" + area);
        Log.i("----->>>", "--->>plv" + plv);
        Log.i("----->>>", "--->>bagId" + bagId);
        Log.i("----->>>", "--->>suspect" + suspect);


        Log.i("---->>>", "---->>>" + url);
//        Map<String, String> values = new HashMap<>();
//        values.put("taglibid", detail.getTagId());
//        values.put("taglibid", 1111 + "");
//        values.put("zone", "A");
//        values.put("area", "A");
//        values.put("plv", 111 + "");
//        values.put("bagId", 1111 + "");
//        values.put("suspect", 1 + "");
//        values.put("nric", "S5452543H");

//       String newFilePath = Environment.getExternalStorageDirectory()+File.separator+"iconImage.jpg";
//        String filePath = Environment.getExternalStorageDirectory()+File.separator+"avatar1.jpg";


//        values.put("imgString",Arrays.toString(b));
//        boolean flag = getFile(Arrays.toString(b), newFilePath, "newFile.jpg");
//        Log.i("---->>>", "syncToBackEnd1: "+ flag) ;

//        Log.i("----->>>>", "-->>"+getBytes(filePath));
//        File file = new File(filePath);
        File files[] = new File[]{outputImage};
        String name = values1.get("nric");
        String[] fileNames = null;
        if(name!=null) {
            fileNames= new String[]{name +".jpg"};
        }else {
            fileNames= new String[]{"tempImage.jpg"};
        }


//        Log.i("----->>>", "outpuimage" + outputImage.getAbsolutePath());
//        Log.i("----->>", "syncToBackEnd: " + values.toString());
//        OkHttpClientHelper.postKeyValuePairAsync(getApplicationContext(), url, values1, callback, null);
        try {
            Log.i("----->>>>", "syncToBackEnd1: " + 111111);
            OkHttpClientHelper.postUploadFilesAsync(getApplicationContext(), url, values1, files, fileNames, callback, "tag");
        } catch (IOException e) {
            Log.i("----->>>>", "syncToBackEnd1: " + 33333333);
            e.printStackTrace();
        }
    }

    private void saveDataToPicName(Context context){
        SharedPreferences sp = context.getSharedPreferences("config", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("nric",nric);
        editor.commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OkHttpClientHelper.cancelCall("tag");
    }


    @Override
    public void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);
    }


}
