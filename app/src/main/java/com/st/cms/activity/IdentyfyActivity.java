package com.st.cms.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import cms.st.com.poccms.R;

public class IdentyfyActivity extends BaseMainActivity {
    String[] titles = new String[]{"Face Id", "Fingerprint Id", "Personal Id", "Log History"};
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    @Override
    public void initView() {
        super.initView();
        prioritise_main = findViewById(R.id.prioritise_main);
        prioritise_main = LayoutInflater.from(getApplicationContext()).inflate(R.layout.activity_zone_second, (ViewGroup)(prioritise_main.getParent()), true);
        frameLayout = (FrameLayout)prioritise_main.findViewById(R.id.layout_container_main);
        radioGroup = (RadioGroup)prioritise_main.findViewById(R.id.rg_id_zone);
        radioButton1 = (RadioButton)prioritise_main.findViewById(R.id.rb_face_zone);
        radioButton2 = (RadioButton)prioritise_main.findViewById(R.id.rb_print_zone);
        radioButton3 = (RadioButton)prioritise_main.findViewById(R.id.rb_personal_zone);
        radioButton4 = (RadioButton)prioritise_main.findViewById(R.id.rb_log_zone);
        arrRadioButton = new RadioButton[]{radioButton1, radioButton2, radioButton3, radioButton4};
        radioButton1.setChecked(true);
        toolbar.setTitle(titles[0]);
        //底部菜单栏的选择事件
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                for(int i = 0;i < arrRadioButton.length;i++)
                {
                    if(checkedId == arrRadioButton[i].getId())
                    {
                        arrRadioButton[i].setTextColor(getResources().getColor(R.color.selected));
                        arrRadioButton[i].setTextSize(14);
                        toolbar.setTitle(titles[i]);
                        switchFragment(i);

                    }
                    else
                    {
                        arrRadioButton[i].setTextSize(12);
                        arrRadioButton[i].setTextColor(getResources().getColor(R.color.non_selected));
                    }
                }
            }
        });
    }


    //此处用来重置底部菜单栏
    @Override
    protected void onResume() {
        super.onResume();
        index = 1;
        initToolBar("main");
        if(currentIndex == -1) {
            initZoneFragment();
            initView();
            currentIndex = 0;
            radioButton1.setChecked(true);
            replaceFragment(0);
        }
        //代表第三模块的fab
        setFabMargin(3);
    }

    @Override
    public void onNewIntent(Intent intent) {
//        initZoneFragment();
//        Bundle bundle = intent.getExtras();
//        if(bundle != null)
//        {
//            int index = bundle.getInt("index");
//            currentIndex = index;
//        }
        super.onNewIntent(intent);
    }


    //用来检索当前选中的radiobutton
    public int getCheckedNum()
    {
        int checkNum = 0;
        for(int i = 0;i < arrRadioButton.length;i++)
        {
            if(radioGroup.getCheckedRadioButtonId() == arrRadioButton[i].getId())
            {
                checkNum = i;
            }
        }
        return checkNum;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK )
        {
            int checkNum = getCheckedNum();
            if(currentIndex >= 4)
            {
                switchFragment(checkNum);
            }
            else
            {
                return super.onKeyDown(keyCode, event);
            }
        }
        return false;
    }

}
