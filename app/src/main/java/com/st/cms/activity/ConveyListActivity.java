package com.st.cms.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.st.cms.fragment.ConveyListFragment;
import com.st.cms.utils.NfcUtils;

import cms.st.com.poccms.R;

/**
 * Created by jt on 2016/9/7.
 */
public class ConveyListActivity extends BaseMainActivity{
    private DetailInterface detailInterface;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }
    @Override
    public void initView() {
        initToolBar("main");
        initFragment();
        replaceFragment(0);
        super.initView();
        prioritise_main = findViewById(R.id.prioritise_main);
        prioritise_main = LayoutInflater.from(getApplicationContext()).inflate(R.layout.activity_convey_list, (ViewGroup)(prioritise_main.getParent()), true);
        frameLayout = (FrameLayout)prioritise_main.findViewById(R.id.layout_container_main);
    }

    @Override
    protected void onResume() {
//        initFragment();
        super.onResume();
//        replaceFragment(0);
        index = 3;
        setFabMargin(4);
    }

    @Override
    protected void initFragment() {
        list_Fragment.clear();
        ConveyListFragment fragment = new ConveyListFragment();
        list_Fragment.add(fragment);
        currentIndex = 0;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if(isAvailable)
        {
            if(NfcUtils.isNfcIntent(this))
            {
                if(detailInterface != null)
                {
                    readTag(getIntent(), 0);
                    detailInterface.getDetailFromTag();
                }
            }
        }
    }
    public void setDetailInterface(DetailInterface detailInterface) {
        this.detailInterface = detailInterface;
    }

    public interface DetailInterface
    {
        public void getDetailFromTag();
    }
}
