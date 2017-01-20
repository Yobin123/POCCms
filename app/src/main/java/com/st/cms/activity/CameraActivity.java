package com.st.cms.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.linj.FileOperateUtil;
import com.linj.album.view.FilterImageView;
import com.linj.camera.view.CameraContainer;
import com.linj.camera.view.CameraView;
import com.st.cms.utils.IntentGoTo;

import java.io.File;
import java.util.List;

import cms.st.com.poccms.R;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

public class CameraActivity extends SwipeBackActivity implements View.OnClickListener, CameraContainer.TakePictureListener {
    private static final String TAG = "CameraAty";
    private Context context = this;
    private boolean mIsRecordMode = false;//判断照相还是摄像的标志位
    private String mSaveRoot;
    private CameraContainer mContainer;
    private FilterImageView mThumbView;
    private ImageButton mCameraShutterButton;
    private ImageButton mRecordShutterButton;
    private ImageView mFlashView;
    private ImageButton mSwitchModeButton;
    private ImageView mSwitchCameraView;
//    private ImageView mSettingView;
    private ImageView mVideoIconView;
    private View mHeaderBar;
    private boolean isRecording;//判断是否在记录

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //去掉title
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().
        setContentView(R.layout.camera);

        initView();
        initThumbnail();
    }


    private void initView() {
        mHeaderBar = findViewById(R.id.camera_header_bar);
        mContainer = (CameraContainer) findViewById(R.id.container);
        mThumbView = (FilterImageView) findViewById(R.id.btn_thumbnail);//生成缩略图的控件
        mVideoIconView = (ImageView) findViewById(R.id.videoicon);//与btn_thumbnail重叠在一起，生成缩略图的效果
        mCameraShutterButton = (ImageButton) findViewById(R.id.btn_shutter_camera);
        mRecordShutterButton = (ImageButton) findViewById(R.id.btn_shutter_record);//拍照，录像功能
        mSwitchModeButton = (ImageButton) findViewById(R.id.btn_switch_mode);

        mSwitchCameraView = (ImageView) findViewById(R.id.btn_switch_camera);
        mFlashView = (ImageView) findViewById(R.id.btn_flash_mode);



        mThumbView.setOnClickListener(this);
        mCameraShutterButton.setOnClickListener(this);
        mRecordShutterButton.setOnClickListener(this);
        mFlashView.setOnClickListener(this);
        mSwitchModeButton.setOnClickListener(this);
        mSwitchCameraView.setOnClickListener(this);
//        mSettingView.setOnClickListener(this);

        mSaveRoot = "test";
        mContainer.setRootPath(mSaveRoot);
    }

    /**
     * 生成相应的缩略图,如果有list中有图片，并且是在第1个位置，就将该缩略图放在相应的位置，否则就用固定的图标来代替
     */
    private void initThumbnail() {
        String thumbFolder = FileOperateUtil.getFolderPath(context, FileOperateUtil.TYPE_THUMBNAIL, mSaveRoot);
        List<File> files = FileOperateUtil.listFiles(thumbFolder, ".jpg");//这是产生jpg文件
        if (files != null && files.size() > 0) {
            Bitmap thumbBitmap = BitmapFactory.decodeFile(files.get(0).getAbsolutePath());//获取第一个图片的缩略图
            if (thumbBitmap != null) {
                mThumbView.setImageBitmap(thumbBitmap);
                if (files.get(0).getAbsolutePath().contains("video")) {
                    mVideoIconView.setVisibility(View.VISIBLE);
                } else {
                    mVideoIconView.setVisibility(View.GONE);
                }
            }
        } else {
            mThumbView.setImageBitmap(null);
            mVideoIconView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_shutter_camera:
                mRecordShutterButton.setVisibility(View.GONE);
                mRecordShutterButton.setClickable(false);
                mContainer.takePicture();//进行拍照的功能
                break;

            case R.id.btn_thumbnail:
                //进行跳转到图片展示页面
                IntentGoTo.intentActivity(this,AlbumActivity.class);
                break;

            case R.id.btn_flash_mode:
                //进行拍摄模式的选择
                if (mContainer.getFlashMode() == CameraView.FlashMode.ON) {
                    mContainer.setFlashMode(CameraView.FlashMode.OFF);
                    mFlashView.setImageResource(R.drawable.btn_flash_off);
                } else if (mContainer.getFlashMode() == CameraView.FlashMode.OFF) {
                    mContainer.setFlashMode(CameraView.FlashMode.AUTO);
                    mFlashView.setImageResource(R.drawable.btn_flash_auto);
                } else if (mContainer.getFlashMode() == CameraView.FlashMode.AUTO) {
                    mContainer.setFlashMode(CameraView.FlashMode.TORCH);
                    mFlashView.setImageResource(R.drawable.btn_flash_torch);
                } else if (mContainer.getFlashMode() == CameraView.FlashMode.TORCH) {
                    mContainer.setFlashMode(CameraView.FlashMode.ON);
                    mFlashView.setImageResource(R.drawable.btn_flash_on);
                }
                break;

            //照相与录影模式的设定；
            case R.id.btn_switch_mode:
                if (mIsRecordMode) {
                    mSwitchModeButton.setImageResource(R.mipmap.ic_switch_camera);
                    mCameraShutterButton.setVisibility(View.VISIBLE);
                    mCameraShutterButton.setClickable(true);
                    mRecordShutterButton.setVisibility(View.GONE);
                    mHeaderBar.setVisibility(View.VISIBLE);
                    mIsRecordMode = false;
                    mContainer.switchMode(0);
                    //进行相应的录像功能：
                    stopRecord();
                } else {
                    mSwitchModeButton.setImageResource(R.mipmap.ic_switch_video);
                    mCameraShutterButton.setVisibility(View.GONE);//拍照按钮消失
                    mRecordShutterButton.setVisibility(View.VISIBLE);//录像按钮出现
                    mRecordShutterButton.setClickable(true);//录像按钮可以被点击
                    mHeaderBar.setVisibility(View.GONE);//切换模式图标消失;

                    mIsRecordMode = true;
                    mContainer.switchMode(5);
                }
                break;

            //进行相应的录像功能
            case R.id.btn_shutter_record:
                mCameraShutterButton.setVisibility(View.GONE);
                mCameraShutterButton.setClickable(false);
                if (!isRecording) {
                    //开始进行录像记录
                    isRecording = mContainer.startRecord();
                    mSwitchModeButton.setVisibility(View.GONE);
                    mSwitchModeButton.setClickable(false);
                    if (isRecording) {
                        mRecordShutterButton.setBackgroundResource(R.drawable.btn_shutter_recording);
                    }
                } else {
                    stopRecord();
                    mSwitchModeButton.setVisibility(View.VISIBLE);
                    mSwitchModeButton.setClickable(true);

                }
                break;

            case R.id.btn_switch_camera:
                mContainer.switchCamera();
                break;

//            //进行相应的水印,到时候不需要
//            case R.id.btn_other_setting:
//                mContainer.setWaterMark();
//                break;
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // 这是takepicture方法中重写的两个方法
    ///////////////////////////////////////////////////////////////////////////
    @Override
    public void onTakePictureEnd(Bitmap bm) {

        mCameraShutterButton.setClickable(true);
    }

    @Override
    public void onAnimtionEnd(Bitmap bm, boolean isVideo) {
        if (bm != null) {
            Bitmap thumbnail = ThumbnailUtils.extractThumbnail(bm, 213, 213);
            mThumbView.setImageBitmap(thumbnail);
            if (isVideo) {
                mVideoIconView.setVisibility(View.VISIBLE);
            } else {
                mVideoIconView.setVisibility(View.GONE);
            }
        }

    }

    private void stopRecord() {
        mContainer.stopRecord(this);
        isRecording = false;
        mRecordShutterButton.setBackgroundResource(R.drawable.btn_shutter_record);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mContainer.stopRecord();
        mContainer = null;
    }


}
