package com.st.cms.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;
import com.st.cms.utils.Constants;
import com.st.cms.utils.OkHttpClientHelper;
import com.st.cms.utils.TagIdEventExpress;

import java.io.IOException;

import cms.st.com.poccms.R;
import de.greenrobot.event.EventBus;

public class FaceDetectActivity extends AppCompatActivity {
    private static final String TAG ="FaceDetectActivity" ;
    private Context mContext = this;
    private String pic_path;
    private OkHttpClientHelper okHttpClientHelper ;
    private String face_url;
    private String tagId;
//    private Map<String,byte[]> map = null;
   private String result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_detect);
        EventBus.getDefault().registerSticky(mContext);//注册
        initData();
    }

    private void initData() {
//        进行相应的联网上传操作
        okHttpClientHelper = OkHttpClientHelper.getOkHttpClientUtils(mContext);
        final Callback callback = new Callback() {
            @Override
            public void onFailure(Request request, final IOException e) {
                e.printStackTrace();
                Log.e("TEST", "Request Failure!Please check the network.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mContext,"The network is bad", Toast.LENGTH_SHORT).show();
                    }
                });

//                result = body.string();
                Intent intent = new Intent();
                intent.setClass(mContext, MatchesFoundActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("result",result);
                intent.putExtras(bundle);
                startActivity(intent);
                finish();

            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (response.isSuccessful()) {
                    ResponseBody body = response.body();
                    if (body != null) {
                        result = body.string();
                        Intent intent = new Intent();
                        intent.setClass(mContext, MatchesFoundActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("result",result);
                        intent.putExtras(bundle);
                        startActivity(intent);
                        finish();
                    }
                }
//                        else {
//                            Log.e("TEST", response.isSuccessful() + "---" + response.body());
//                        }

            }
        };
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                    okHttpClientHelper.getDataAsync(mContext, Constants.getIdentifyUrl("S0000001H","xxxx"), callback, "tag");

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();






//        map = new ArrayMap<>();
//        okHttpClientHelper = OkHttpClientHelper.getOkHttpClientUtils(mContext);
//        pic_path = getIntent().getExtras().getString("path");//这是文件路径
//        int position = pic_path.lastIndexOf("/")+1;//获取文件的名称
//        String fileName = pic_path.substring(position,pic_path.length());
//        String[] fileNames = new String[]{fileName};
//        //获取File[] 参数
//        File file = new File(pic_path);
//        File[] files = new File[]{file};//
//        Map<String, String> mapid = new HashMap<>();
//        //获取map的值
//        mapid.put("taglibid", tagId);
////        map.put(tagId,params);
//
//        Callback callback = new Callback() {
//            @Override
//            public void onFailure(Request request, IOException e) {
//                e.printStackTrace();
//                Log.e("TEST", "Request Failure!Please check the network.");
//            }
//
//            @Override
//            public void onResponse(Response response) throws IOException {
//                if (response.isSuccessful()) {
//                    ResponseBody body = response.body();
//                    if (body != null) {
//                        String result = body.string();
//                        Log.e("TEST","--->>" +result);
////                        Toast.makeText(mContext, result+"", Toast.LENGTH_SHORT).show();
//                    }
//
//                }
//                else
//                {
//                    Log.e("TEST", response.isSuccessful() + "---" + response.body());
//                }
//
//            }
//        };
////        String respose_data = okHttpClientHelper.postKeyValuePairByte(mContext,"http://112.4.27.9/mall-back/if_user/store_list", map, "tag");
//        try {
//            okHttpClientHelper.postUploadFilesAsync(mContext,Constants.getUrl(Constants.FACE),mapid,files,fileNames,callback, "tag");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        okHttpClientHelper.cancelCall("tag");//解除网络
        EventBus.getDefault().unregister(mContext);//解除注册
    }

    /**
     *用事件总线获取数据
     * @param event
     */
    public void onEventMainThread(TagIdEventExpress event){
        tagId = event.getmTagId();
    }
//
//    /**
//     * 将文件转化为流的形式
//     * @param file
//     * @return
//     */
//    public byte[] fileToByteData(File file){
//        byte[] data=new byte[1048576];
//
//        FileInputStream fis;
//        try {
//            fis = new FileInputStream(file);
//            BufferedInputStream bis = new BufferedInputStream(fis);
//            bis.read(data);
//        } catch (Exception e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//        return data;
//    }
}
