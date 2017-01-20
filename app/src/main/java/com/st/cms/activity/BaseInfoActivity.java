package com.st.cms.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cms.st.com.poccms.R;

/**
 * Created by jt on 2016/8/24.
 */
public class BaseInfoActivity extends Activity{
    private ActionBar actionBar;
    public TextView tv_confirm;
    public RelativeLayout rl_back;
    public ImageView iv_sign;
    public TextView tv_prioritise_complete;


    public int status = 0;
    public String confirm = "";
    public String info = "";
    public int module = 0;
    public boolean ok_flag ;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //取消标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        actionBar = getActionBar();
        if(actionBar != null)
        {
            actionBar.hide();
        }
        //取消状态栏
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    protected void onResume() {
        Bundle bundle = getIntent().getExtras();
        status = bundle.getInt("status");
        confirm = bundle.getString("confirm");
        info = bundle.getString("info");
        module = bundle.getInt("module");
        ok_flag = bundle.getBoolean("okFlag");
        super.onResume();
    }

    public void initView() {
        rl_back = (RelativeLayout)findViewById(R.id.rl_back);
        tv_confirm = (TextView)findViewById(R.id.tv_confirm);
        iv_sign = (ImageView)findViewById(R.id.iv_sign);
        tv_prioritise_complete = (TextView)findViewById(R.id.tv_prioritise_complete);
    }
}
