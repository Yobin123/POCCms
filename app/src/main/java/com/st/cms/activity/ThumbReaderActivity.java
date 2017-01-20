package com.st.cms.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.st.cms.utils.IntentGoTo;

import cms.st.com.poccms.R;


public class ThumbReaderActivity extends AppCompatActivity {
    private Context context = this;
    private Handler handler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thumb_reader);

//        这是进行指纹扫描用的，现在用相应的睡眠来模拟读取指纹的过程
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            IntentGoTo.intentActivity(context,MatchesFoundActivity.class);
                        }
                    });
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }
}
