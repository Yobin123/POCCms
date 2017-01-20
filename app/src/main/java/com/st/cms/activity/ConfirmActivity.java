package com.st.cms.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.View;

import cms.st.com.poccms.R;

/**
 * Created by jt on 2016/8/18.
 */
public class ConfirmActivity extends BaseInfoActivity {

    private boolean flag = true;
    public static final int RESULT_TAKE_PIC = 1001;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm);
        initView();
    }

    @Override
    public void initView() {
        super.initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        tv_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        tv_confirm.setText(confirm);
        tv_prioritise_complete.setText(info);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

//    @Override
//    public void finish() {
//        super.finish();
//        overridePendingTransition(R.anim.save_hide_anim,0);
//    }




}

