package com.st.cms.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.linj.FileOperateUtil;
import com.linj.album.view.AlbumViewPager;
import com.linj.album.view.MatrixImageView;
import com.linj.video.view.VideoPlayerContainer;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cms.st.com.poccms.R;

public class AlbumItemActivity extends Activity implements View.OnClickListener ,MatrixImageView.OnSingleTapListener,AlbumViewPager.OnPlayVideoListener {
    private static final String TAG ="AlbumItemActivity" ;
    private Context mContext = this;
    private AlbumViewPager mViewPager;
    private ImageView mBackView;
    private ImageView mCameraView;
    private VideoPlayerContainer mContainer;
    private TextView mCountView;
    private View mHeaderBar, mBottomBar;
    private Button mDeleteButton;
    private Button mEditButton;
    private String mSaveRoot;
    private int index;
    private int currentItem = 0;
    private boolean data_flag = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.albumitem);
        mViewPager = (AlbumViewPager) findViewById(R.id.albumviewpager);
        mContainer=(VideoPlayerContainer)findViewById(R.id.videoview);
        mBackView = (ImageView) findViewById(R.id.header_bar_photo_back);
        mCameraView = (ImageView) findViewById(R.id.header_bar_photo_to_camera);
        mCountView = (TextView) findViewById(R.id.header_bar_photo_count);
        mHeaderBar = findViewById(R.id.album_item_header_bar);
        mBottomBar = findViewById(R.id.album_item_bottom_bar);
        mDeleteButton = (Button) findViewById(R.id.delete);
        mEditButton = (Button) findViewById(R.id.edit);

        mBackView.setOnClickListener(this);
        mCameraView.setOnClickListener(this);
        mCountView.setOnClickListener(this);
        mDeleteButton.setOnClickListener(this);
        mEditButton.setOnClickListener(this);
        mViewPager.setOnClickListener(this);
        mViewPager.setOnPlayVideoListener(this);

        mSaveRoot = "test";
        mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if (mViewPager.getAdapter() != null) {
                    String text = (position + 1) + "/" + mViewPager.getAdapter().getCount();
                    mCountView.setText(text);
                } else {
                    mCountView.setText("0/0");
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
                // TODO Auto-generated method stub

            }
        });

         String currentFileName = null;
        if (getIntent().getExtras() != null)
            currentFileName = getIntent().getExtras().getString("id");
            index = getIntent().getExtras().getInt("index");
        if (currentFileName != null) {
            File file = new File(currentFileName);
            currentFileName = file.getName();
            if (currentFileName.indexOf(".") > 0)
                currentFileName = currentFileName.substring(0, currentFileName.lastIndexOf("."));
        }

        loadAlbum(mSaveRoot, currentFileName,index);

    }

    @Override
    public void onClick(View view) {
        // TODO Auto-generated method stub
        switch (view.getId()) {
            case R.id.header_bar_photo_back:
                finish();
                break;
            case R.id.header_bar_photo_to_camera:
                startActivity(new Intent(this, CameraActivity.class));
                break;
            case R.id.delete:
                showDeleteDialog();
                break;
            case R.id.edit:
                //脸部识别阶段,这里可以上传该照片
                String path = mViewPager.detectCurrentPath();
                Intent intent = new Intent();
                intent.setClass(this,FaceDetectActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("path",path);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            default:
                break;


        }
    }

    //进行相应的加载数据，应该判断相应的数据是否video
    public void loadAlbum(String rootPath, final String fileName, int index) {
        String folder = FileOperateUtil.getFolderPath(this, FileOperateUtil.TYPE_IMAGE, rootPath);
        String thumbnailFolder = FileOperateUtil.getFolderPath(this, FileOperateUtil.TYPE_THUMBNAIL, rootPath);
        List<File> imageList = FileOperateUtil.listFiles(folder, ".jpg");
        List<File> videoList = FileOperateUtil.listFiles(thumbnailFolder, ".jpg", "video");
        final List<File> files = new ArrayList<File>();

        switch (index){
            case 0:
                if (imageList != null && imageList.size() > 0) {
                    files.addAll(imageList);
                }
                break;
            case 1:
               if (videoList != null && videoList.size() > 0) {
                files.addAll(videoList);
            }
                mBottomBar.setVisibility(View.GONE);
                mHeaderBar.setVisibility(View.GONE);
                break;
        }

        FileOperateUtil.sortList(files, false);
        if (files.size() > 0) {
            List<String> paths = new ArrayList<String>();
            for (File file : files) {
                if (fileName != null && file.getName().contains(fileName))
                    currentItem = files.indexOf(file);
                paths.add(file.getAbsolutePath());
            }
            mViewPager.setAdapter(mViewPager.new ViewPagerAdapter(paths));
            mViewPager.setCurrentItem(currentItem);
            mCountView.setText((currentItem + 1) + "/" + paths.size());
        } else {
            mCountView.setText("0/0");
        }
    }

    //滑动动画
    @Override
    public void onSingleTap() {
        if(mHeaderBar.getVisibility()== View.VISIBLE){
            AlphaAnimation animation=new AlphaAnimation(1, 0);
            animation.setDuration(500);
            mHeaderBar.startAnimation(animation);
            mBottomBar.startAnimation(animation);
            mHeaderBar.setVisibility(View.GONE);
            mBottomBar.setVisibility(View.GONE);
        }else {
            AlphaAnimation animation=new AlphaAnimation(0, 1);
            animation.setDuration(500);
            mHeaderBar.startAnimation(animation);
            mBottomBar.startAnimation(animation);
            mHeaderBar.setVisibility(View.VISIBLE);
            mBottomBar.setVisibility(View.VISIBLE);
        }
    }
    private void showDeleteDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage("Do you delete the items which is selected")
                .setPositiveButton("Sure", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String result = mViewPager.deleteCurrentPath();
                        if (result != null)
                            mCountView.setText(result);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }



    @Override
    public void onBackPressed() {
        if(mContainer.getVisibility()== View.VISIBLE){
            mContainer.stopPlay();
            mContainer.setVisibility(View.GONE);
        }
        else {
            super.onBackPressed();
        }
    }
    @Override
    protected void onStop() {
        if(mContainer.getVisibility()== View.VISIBLE){
            mContainer.stopPlay();
            mContainer.setVisibility(View.GONE);
        }


        super.onStop();
    }

    //进行相应的播放视频的监听操作
    @Override
    public void onPlay(String path) {
        // TODO Auto-generated method stub
        try{
            mContainer.playVideo(path);
        }
        catch(Exception e){
            e.printStackTrace();
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }
}
