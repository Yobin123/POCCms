package com.st.cms.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.st.cms.entity.Detail;
import com.st.cms.entity.MedicalRes;
import com.st.cms.utils.Constants;
import com.st.cms.utils.NfcUtils;
import com.st.cms.utils.OkHttpClientHelper;
import com.st.cms.utils.TagIdEventExpress;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import cms.st.com.poccms.R;
import de.greenrobot.event.EventBus;


public class DetailActivity extends AppCompatActivity {
    public static final String TAG = "DetailActivity";
    private Context mContext = this;
    private LinearLayout mInformaion;
    private boolean flag = false;
    private EditText et_detail_nric;
    private EditText et_detail_name;
    private EditText et_detail_gender;
    private EditText et_detail_blood;
    private EditText et_detail_history;
    private FloatingActionButton fab_save;
    private TextView tv_title;
    private Toolbar toolbar_content;
    private ImageView iv_back;
    private String person_id;
    @Override
    public void onStateNotSaved() {
        super.onStateNotSaved();
    }

    private Detail detail  ;
    private Gson gson = null;
    //保存弹出框
    private View saveView = null;
    private PopupWindow mySavePopup = null;

    private boolean isAvailable = false;
    private boolean connected = false;
    private boolean isEdit = false;
    private String jsonString = null;
    private String tagId;
    //同步次数，如果超过3次则取消同步
    private int sync_count = 0;
    private OkHttpClientHelper okHttpClientHelper;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        detail = new Detail();
        gson = new Gson();
        isConnected();
        initView();
        EventBus.getDefault().registerSticky(mContext);//注册



    }

    private void initView() {
        toolbar_content = (Toolbar) findViewById(R.id.toolbar_detail);
        mInformaion = (LinearLayout) findViewById(R.id.llayout_personInformation);
        et_detail_nric = (EditText) findViewById(R.id.et_detail_nric);
        et_detail_name = (EditText) findViewById(R.id.et_detail_name);
        et_detail_gender = (EditText) findViewById(R.id.et_detail_gender);
        et_detail_blood = (EditText) findViewById(R.id.et_detail_blood);
        et_detail_history = (EditText) findViewById(R.id.et_detail_history);
        fab_save = (FloatingActionButton) findViewById(R.id.fab_save);
        tv_title = (TextView) findViewById(R.id.tv_title);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        setSupportActionBar(toolbar_content);

        jsonString = getIntent().getExtras().getString("result");
        Log.e("TEST", jsonString);
        try {
            if (jsonString!=null){
                JSONObject son_json = new JSONObject(jsonString);
                et_detail_nric.setText(son_json.getString("nric"));
                et_detail_name.setText(son_json.getString("name"));
                int gender = son_json.getInt("gender");
                if (gender==1){
                    et_detail_gender.setText("M");
                }else {
                    et_detail_gender.setText("F");

                }
                Log.e("TEST", son_json.get("bloodBroup").toString());
//                if(son_json.get("bloodBroup").toString().length() > 0)
//                {
//                    et_detail_blood.setText(new JSONObject((son_json.get("bloodBroup").toString()).substring(son_json.get("bloodBroup").toString().indexOf("{"))).get("name").toString());
//                }
                et_detail_history.setText(son_json.get("profile").toString());
                person_id = son_json.get("id").toString();
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }


        //进行长按可以进行相应的编辑
        mInformaion.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                et_detail_nric.setEnabled(true);
                et_detail_name.setEnabled(true);
                et_detail_gender.setEnabled(true);
                et_detail_blood.setEnabled(true);
                et_detail_history.setEnabled(true);
                isEdit = true;
                return true;
            }
        });

        // 进行返回
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
//      将相应的信息保存到tagId
        fab_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(mContext, "you have click save button", Toast.LENGTH_SHORT).show();
////              获取editText中的数据，并将其中的数据设置在detail中
//                    detail.setNric(et_detail_nric.getText().toString());
//                    detail.setName(et_detail_name.getText().toString());
//
//                    if (et_detail_gender.getText().toString().equals("M")||et_detail_gender.getText().toString().equals("m")){
//                        detail.setGender(1);
//                    }else {
//                        detail.setGender(2);
//                    }
//
//                    MedicalRes medicalRes = new MedicalRes();
//                    medicalRes.setName( et_detail_blood.getText().toString());
//                    detail.setBloodGroup(medicalRes);
//                    detail.setProfile(et_detail_history.getText().toString());
//                    detail.setTagId("91C9C5D4");
                onPrioritise();
            }
        });
    }

    @Override
    protected void onResume() {
        Log.e("TEST", "bind nfc" + this.getClass());
        isAvailable = NfcUtils.bindAdapter(getApplicationContext(), this.getClass(), this);
        checkConnection();
        super.onResume();
    }

    @Override
    public void onCreateSupportNavigateUpTaskStack(@NonNull TaskStackBuilder builder) {
        super.onCreateSupportNavigateUpTaskStack(builder);
    }

    @Override
    protected void onNewIntent(Intent intent) {
//        super.onNewIntent(intent);
        setIntent(intent);
        try{
            if((NfcAdapter.ACTION_TECH_DISCOVERED.equals(getIntent().getAction())
                    || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())
                    || NfcAdapter.ACTION_TAG_DISCOVERED.equals(getIntent().getAction()))){
                readTag(intent,0);
            }
        }catch (Exception e){

        }
    }

    private void readTag(Intent intent, int num) {
        String content = NfcUtils.readFromTag(intent);
        if(isGoodJson(content))
        {
            try{
                detail = gson.fromJson(content, Detail.class);
                Log.i(TAG, "----->>>> detail--->>>>" + detail.getTagId() );
                if (num==0){
                    Toast.makeText(mContext, "Read successful!", Toast.LENGTH_SHORT).show();
                }
            }catch (Exception e){

            }
        }
    }

//    private String resetNFCData()
//    {
//        detail = new Detail();
//        detail.setTagId(NfcUtils.getTagId(getIntent()));
//        detail.setZone("Triage");
//        Log.e("TEST", "save:" + detail.toString());
//        String content = gson.toJson(detail);
//        NfcUtils.writeToTag(this.getIntent(), content);
//        return content;
//    }

    //判断是否是一个json字符串
    public boolean isGoodJson(String json) {

        try {
            new JsonParser().parse(json);
            return true;
        } catch (JsonParseException e) {
            System.out.println("bad json: " + json);
            return false;
        }
    }


    private void onPrioritise(){
        if(!isAvailable)
        {
            Toast.makeText(getApplicationContext(), "This device has no NFC function.", Toast.LENGTH_SHORT).show();
            return;
        }

        //判断是否连接后台
        if (!(connected&&isConnect(mContext))){
            Toast.makeText(mContext, "The net can not connect to backend. Please check the net.", Toast.LENGTH_SHORT).show();
            return;
        }
        //判断是否靠近tag
        if (!NfcUtils.isNfcIntent(this)){
            Toast.makeText(mContext, "Please get close to NFC tag.", Toast.LENGTH_SHORT).show();
            return;
        }
        //判断是否是原来的tagId
        if (!NfcUtils.getTagId(getIntent()).equals(detail.getTagId())){
            Toast.makeText(mContext, "Please get close to correct NFC tag.", Toast.LENGTH_SHORT).show();
            return;
        }
        readTag(getIntent(),1);
        saveTag();

    }



    private void saveTag() {
        if(detail != null)
        {
//            获取editText中的数据，并将其中的数据设置在detail中
            detail.setNric(et_detail_nric.getText().toString());
            detail.setName(et_detail_name.getText().toString());

            if (et_detail_gender.getText().toString().equals("M")||et_detail_gender.getText().toString().equals("m")){
                detail.setGender(1);
            }else {
                detail.setGender(2);
            }

            MedicalRes medicalRes = new MedicalRes();
            medicalRes.setName( et_detail_blood.getText().toString());
            detail.setBloodGroup(medicalRes);
            detail.setProfile(et_detail_history.getText().toString());

        }

//        Log.e("TEST","----->>>>++++"+ gson.toJson(detail));

        boolean isSuccess = NfcUtils.writeToTag(getIntent(),gson.toJson(detail));
        Log.i(TAG, "------>>>>tagid"+detail.getTagId()+"---zone"+detail.getZone());
        if (!isSuccess){
            Toast.makeText(mContext, "Please get close to the nfc tag.", Toast.LENGTH_SHORT).show();
        }
        if (isSuccess){

            Log.i(TAG, "----->>>>>");

            Toast.makeText(mContext, " Success to save info to tag. Prepare to Sync to backend now.", Toast.LENGTH_SHORT).show();
//            Toast.makeText(mContext,gson.toJson(detail) , Toast.LENGTH_LONG).show();
            sync_count = 0;
            synToBackEnd();
//            saveSuccess();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        NfcUtils.unbindAdapter(this);
    }

    private void synToBackEnd(){
      sync_count +=1;
      String url = Constants.getPersonInformation();
      Callback callback = new Callback() {
          @Override
          public void onFailure(Request request, final IOException e) {
              e.printStackTrace();
              runOnUiThread(new Runnable() {
                  @Override
                  public void run() {
                      if(e.getMessage().length() > 0)
                      {
                          Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                      }
                      else
                      {
                          Toast.makeText(getApplicationContext(), "Request Failure!Please check the network.", Toast.LENGTH_SHORT).show();
                      }
                  }
              });
              if(sync_count <= 3)
              {
                  synToBackEnd();
              }
              Log.e("TEST", "Request Failure!Please check the network.");
          }

          @Override
          public void onResponse(Response response) throws IOException {
                if (response.isSuccessful()){
//                    ResponseBody body = response.body();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(mContext, "Success to sync to backend", Toast.LENGTH_SHORT).show();
                        }
                    });
                    saveSuccess();

//                    if (body!=null){
//                    }
                }
                else {
                    Log.e("TEST", response.isSuccessful() + "---" + response.body());
                }
          }
      };

      Map<String,String> values = new HashMap<>();
      values.put("taglibid",detail.getTagId());
      values.put("nric",detail.getNric());
      values.put("name",detail.getName());
      values.put("gender",detail.getGender() + "" );
      values.put("blood",detail.getBloodGroup() + "");
      values.put("medhis",detail.getProfile());
      values.put("personid",person_id == null ? "2" : person_id);
      try{
          okHttpClientHelper.postKeyValuePairAsync(mContext,url,values,callback,"tag");
      }catch (Exception e){
          Toast.makeText(getApplicationContext(), "Please open the Network.", Toast.LENGTH_SHORT).show();
          e.printStackTrace();
      }
  }



    private void saveSuccess()
    {
        Intent intent = new Intent(getApplicationContext(), ConfirmActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("status", 0);
        bundle.putString("confirm", getResources().getString(R.string.ok));
        bundle.putString("info", getResources().getString(R.string.mark_info));
        bundle.putInt("module", 1);
        intent.putExtras(bundle);
        startActivity(intent);
    }

//    //读取nfc tag 并转换成detail对象
//    public void readTag(Intent intent,int num){
//        detail = null;
//
//
//    }




    private static boolean isConnect(Context context){
        //获取手机所有连接管理对象(包括wi-fi,net的连接的管理)
        try{
            ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity!=null){
                //获取网络连接管理对象
                NetworkInfo info = connectivity.getActiveNetworkInfo();
                if (info!=null&&info.isConnected()){
                    //判断当前网络是否已经连接
                    if(info.getState()==NetworkInfo.State.CONNECTED){
                        return true;
                    }
                }
            }

        }catch (Exception e){
            Log.v("error",e.toString());
        }
        return false;
    }


    /**
     * 传递tagid
     * @param event
     */
    public void onEventMainThread(TagIdEventExpress event){
       tagId = event.getmTagId();
        Toast.makeText(DetailActivity.this, tagId, Toast.LENGTH_SHORT).show();
        tv_title.setText(tagId);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(mContext);//解除注册
        okHttpClientHelper.cancelCall("tag");//解除网络
    }


    /**
     * 测试与服务器连通性
     */
    public void isConnected()
    {
        Log.e("TEST", "test connect");
        new Thread(new Runnable() {
            @Override
            public void run() {
                int timeOut = 100000;
                try {
                    InetAddress inet = InetAddress.getByName(Constants.IP);
                    connected = isReachable(inet, Constants.PORT, timeOut);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static boolean isReachable(InetAddress netAddr, int port, int timeout) {
        boolean isReachable = false;
        Socket socket = null;
        try {
            socket = new Socket();
            // Creates a socket address from an IP address and a port number
            InetSocketAddress endpointSocketAddr = new InetSocketAddress(netAddr, port);
            socket.connect(endpointSocketAddr, timeout);
            Log.e("TEST", "SUCCESS - remote: " + netAddr.getHostAddress() + " port " + port);
            isReachable = true;
        } catch (IOException e) {
            Log.e("TEST", "FAILRE - remote: " + netAddr.getHostAddress() + " port " + port);
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    Log.e("TEST", "Error occurred while closing socket..");
                }
            }
        }
        return isReachable;
    }

    public void checkConnection()
    {
        if (isConnect(this)==false)
        {
            new AlertDialog.Builder(this)
                    .setTitle("网络错误")
                    .setMessage("网络连接失败，请确认网络连接")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            Intent intent=new Intent();
                            intent.setClassName("com.android.settings", "com.android.settings.Settings");
                            startActivity(intent);
//							android.os.Process.killProcess(android.os.Process.myPid());
//							System.exit(0);
                        }
                    }).show();
        }
    }

}


