package com.st.cms.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.linj.FileOperateUtil;
import com.linj.album.view.AlbumGridView;
import com.st.cms.activity.AlbumItemActivity;
import com.st.cms.utils.IntentGoTo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import cms.st.com.poccms.R;

import static com.linj.album.view.AlbumGridView.OnCheckedChangeListener;
import static com.linj.album.view.AlbumGridView.OnItemClickListener;


public class AlbumFragment extends Fragment implements View.OnClickListener, OnCheckedChangeListener {
    private static final String TAG = "AlbumFragment";
    private AlbumGridView albumview;
    private int index;
    private String mSaveRoot;
    private int itemCount = 0;
    private LinearLayout album_bottom_bar;
    private Button btn_delete;
    public AlbumGridView.AlbumViewAdapter adapter;
    private TextView tv_empty;
    private String tagId;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        index = getArguments().getInt("index");
        tagId = getArguments().getString("id");//没有传过去；
        mSaveRoot = "test";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_album, container, false);
        albumview = (AlbumGridView) view.findViewById(R.id.albumview);
        btn_delete = (Button) view.findViewById(R.id.btn_delete);
        tv_empty = (TextView) view.findViewById(R.id.tv_empty);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //根据相应的值来确定相应的数据源
        loadAlbum(mSaveRoot, ".jpg", index);

        //短按监听
        albumview.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (albumview.getEditable())
                    return;
                IntentGoTo.intentActivity(getContext(), AlbumItemActivity.class, view.getTag().toString(), index);
            }
        });
        //长按删除监听
        albumview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (albumview.getEditable())
                    return true;
                albumview.setEditable(true);
                btn_delete.setVisibility(View.VISIBLE);
                return true;
            }
        });

        btn_delete.setOnClickListener(this);
    }

    /**
     * 根据相应的index,来确定相应的数据源，其中数据源是路径
     *
     * @param mSaveRoot
     * @param format
     * @param index
     * @return
     */
    private void loadAlbum(String mSaveRoot, String format, int index) {
        List<String> list_pic = new ArrayList<>();
        List<String> list_video = new ArrayList<>();
        String thumbFolder = FileOperateUtil.getFolderPath(getContext(), FileOperateUtil.TYPE_THUMBNAIL, mSaveRoot);
        List<File> files = FileOperateUtil.listFiles(thumbFolder, format);

        if (files != null && files.size() > 0) {
            for (int i = 0; i < files.size(); i++) {
                if (files.get(i).getAbsolutePath().contains("video")) {
                    list_video.add(files.get(i).getAbsolutePath());
                } else {
                    list_pic.add(files.get(i).getAbsolutePath());
                }
            }
        }
        switch (index) {
            case 0:
                albumview.setAdapter(albumview.new AlbumViewAdapter(list_pic));
                albumview.setEmptyView(tv_empty);
                break;
            case 1:
                albumview.setAdapter(albumview.new AlbumViewAdapter(list_video));
                albumview.setEmptyView(tv_empty);
                break;

        }
    }

    //进行相应的单例模式
    public static AlbumFragment newInstance() {
        AlbumFragment fragment = new AlbumFragment();
        return fragment;
    }


    private void enterEdit() {
        albumview.setEditable(true);
    }

    private void leaveEdit() {
        albumview.setEditable(false);//这是取消按钮
    }

    /**
     * 删除时进行相应的提示
     */
    private void showDeleteDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Do you delete the items which is selected")
                .setPositiveButton("Sure", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Set<String> items = albumview.getSelectedItems();
                        for (String path : items) {
                            boolean flag = FileOperateUtil.deleteThumbFile(path, getContext());
                            if (!flag) Log.i(TAG, path);
                        }
                        loadAlbum(mSaveRoot, ".jpg", index);//这是重新获取数据源并加载数据
                        albumview.setEditable(false);//这是取消按钮
                        btn_delete.setVisibility(View.GONE);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        btn_delete.setVisibility(View.GONE);
                        albumview.setEditable(false);
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_delete:
                showDeleteDialog();
                break;
        }
    }

    @Override
    public void onCheckedChanged(Set<String> set) {
        itemCount = set.size();
//        btn_delete.setText("Delete("+ String.valueOf(set.size())+")");

    }


}
