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
import android.widget.Toast;

import com.st.cms.utils.NfcUtils;

import cms.st.com.poccms.R;

/**
 * Created by jt on 2016/8/22.
 */
public class TreatmentMainActivity extends BaseMainActivity{


    String[] titles = new String[]{"Marker Injury Point", "Treatment", "Vital Signs", "GCS"};
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
        initFragment();
        replaceFragment(0);


        prioritise_main = findViewById(R.id.prioritise_main);
        prioritise_main = LayoutInflater.from(getApplicationContext()).inflate(R.layout.treat_main_list, (ViewGroup)(prioritise_main.getParent()), true);
        frameLayout = (FrameLayout)prioritise_main.findViewById(R.id.layout_container_main);
        radioGroup = (RadioGroup)prioritise_main.findViewById(R.id.rg_tab_bar);
        radioButton1 = (RadioButton)prioritise_main.findViewById(R.id.treat_tab_marker);
        radioButton1.setChecked(true);
        radioButton2 = (RadioButton)prioritise_main.findViewById(R.id.treat_tab_treatment);
        radioButton3 = (RadioButton)prioritise_main.findViewById(R.id.treat_tab_signs);
        radioButton4 = (RadioButton)prioritise_main.findViewById(R.id.treat_tab_gcs);
        arrRadioButton = new RadioButton[]{radioButton1, radioButton2, radioButton3, radioButton4};
        radioButton1.setChecked(true);
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
        index = 2;
        //代表第三模块的fab
        setFabMargin(3);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if(NfcUtils.isNfcIntent(this))
        {
            readTag(intent, 0);
        }
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
}
