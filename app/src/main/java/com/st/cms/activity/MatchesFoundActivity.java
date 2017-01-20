package com.st.cms.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cms.st.com.poccms.R;


public class MatchesFoundActivity extends AppCompatActivity {
    private Context mContext = this;
    private ListView mListview;
    private TextView mTVFound;
    private TextView mTVNoMatch;
    private List<Map<String,String>> match_list = new ArrayList<>();
    String result;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matches_found);
        initData();
        initView();
    }

    private void initData()  {
//        result = getIntent().getExtras().getString("result");
//        if (result!=null){
//            try {
//                JSONObject jsonObject = new JSONObject(result);
//                String nric = jsonObject.getString("nric");
//                Map<String,String>map = new HashMap<>();
//                map.put("nric",nric);
//                match_list.add(map);
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }

    }

    private void initView() {
        mListview = (ListView) findViewById(R.id.lv_matches_persons);
        mTVFound = (TextView) findViewById(R.id.tv_matches_number);
        mTVNoMatch = (TextView) findViewById(R.id.tv_matches_empty);

        SimpleAdapter adapter = new SimpleAdapter(mContext,match_list,R.layout.victim_list,new String[]{"nric"},new int[]{R.id.tv_id_num});
        mListview.setAdapter(adapter);
        mListview.setEmptyView(mTVNoMatch);
        mTVFound.setText(match_list.size()+"match founded");

        mListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.setClass(mContext, DetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("result",result);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

//        mTVFound.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {

//            }
//        });
    }
}
