package com.st.cms.activity;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.st.cms.fragment.AlbumFragment;
import com.st.cms.utils.IntentGoTo;

import cms.st.com.poccms.R;


public class AlbumActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "AlbumActivity";
    private Context mContext = this;
    private ImageView header_bar_back;
    private RelativeLayout header_bar_navi;
    private FrameLayout fl_container_album;

    private RadioButton rbt_album_picture;
    private RadioButton rbt_album_video;
    private RadioButton[] arrRadioBtn;
    private RadioGroup rg_album;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.album);

        initView();

    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void initView() {
        fl_container_album = (FrameLayout) findViewById(R.id.fl_container_album);
        header_bar_navi = (RelativeLayout) findViewById(R.id.header_bar_navi);
        header_bar_back = (ImageView) findViewById(R.id.header_bar_back);

        rg_album = (RadioGroup) findViewById(R.id.rg_album);
        rbt_album_picture = (RadioButton) findViewById(R.id.rbt_album_picture);
        rbt_album_video = (RadioButton) findViewById(R.id.rbt_album_video);
        arrRadioBtn = new RadioButton[]{rbt_album_picture, rbt_album_video};

        //设定第一个button为相应的默认值
        arrRadioBtn[0].setChecked(true);
        rbt_album_picture.setBackground(getResources().getDrawable(R.drawable.btn_album_left));
        rbt_album_picture.setTextColor(getResources().getColor(R.color.album_item_bg));
        replaceFragment(0);


        //对相应的radiogroup进行相应的监听，从而可以确定相应的选中状态
        rg_album.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                if (checkedId == arrRadioBtn[0].getId()) {
                    rbt_album_picture.setBackground(getResources().getDrawable(R.drawable.btn_album_left));
                    rbt_album_picture.setTextColor(getResources().getColor(R.color.album_item_bg));
                    rbt_album_video.setBackground(getResources().getDrawable(R.color.album_item_bg));
                    rbt_album_video.setTextColor(getResources().getColor(R.color.selected));
                    replaceFragment(0);

                } else if (checkedId == arrRadioBtn[1].getId()) {
                    rbt_album_video.setBackground(getResources().getDrawable(R.drawable.btn_album_right));
                    rbt_album_video.setTextColor(getResources().getColor(R.color.album_item_bg));
                    rbt_album_picture.setBackground(getResources().getDrawable(R.color.album_item_bg));
                    rbt_album_picture.setTextColor(getResources().getColor(R.color.selected));
                    replaceFragment(1);

                }
            }
        });
        header_bar_back.setOnClickListener(this);

    }


    //默认的填充的碎片
    private void replaceFragment(int i) {
        AlbumFragment album_fragment = AlbumFragment.newInstance();
        Bundle bundle = new Bundle();
        bundle.putInt("index", i);
        album_fragment.setArguments(bundle);
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.fl_container_album, album_fragment);
        transaction.commit();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.header_bar_back:
                IntentGoTo.intentActivity(mContext, CameraActivity.class);
                finish();
                break;

        }
    }
}
