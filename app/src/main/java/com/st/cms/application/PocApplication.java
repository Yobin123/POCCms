package com.st.cms.application;

import android.app.Application;

import com.st.cms.db.DataBaseHelper;
import com.st.cms.entity.Wperson;

import java.util.List;

/**
 * Created by jt on 2016/8/18.
 */
public class PocApplication extends Application{
    private static PocApplication pocApplication;
    private DataBaseHelper dbHelper;

    private List<Wperson> persons;
    private String result = null;
//    private Gson gson;

    @Override
    public void onCreate() {
        super.onCreate();
        pocApplication = this;
        dbHelper=new DataBaseHelper(this, "poccms.db", null, 1);
        //创建私有目录

//        gson = new Gson();

//        Map<String, String> values = new HashMap<>();
//        values.put("name", "jt");
//        String url = Constants.getUrl(Constants.HELLO);
//        callback = new Callback() {
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
//                        Log.e("TEST", result);
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
//        OkHttpClientHelper.postKeyValuePairAsync(getApplicationContext(), url, values, callback, null);
    }

    public static PocApplication getPocApplication()
    {
        if(pocApplication == null)
        {
            pocApplication = new PocApplication();
        }
        return pocApplication;
    }

    public DataBaseHelper getDbHelper() {
        if(dbHelper!=null){
            return dbHelper;
        }
        return new DataBaseHelper(this);
    }
    public List<Wperson> getPersons() {
        return persons;
    }

    public void setPersons(List<Wperson> persons) {
        this.persons = persons;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
