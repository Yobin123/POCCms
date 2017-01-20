package com.st.cms.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;
import com.st.cms.activity.DetailActivity;
import com.st.cms.activity.IdentyfyActivity;
import com.st.cms.activity.MatchesFoundActivity;
import com.st.cms.adapter.CasualtyAdapter;
import com.st.cms.entity.Prioritise;
import com.st.cms.entity.Victim;
import com.st.cms.utils.Constants;
import com.st.cms.utils.OkHttpClientHelper;
import com.st.cms.utils.TagIdEventExpress;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cms.st.com.poccms.R;
import de.greenrobot.event.EventBus;

public class ZoneSecondFragment extends Fragment implements SwipeMenuListView.OnMenuItemClickListener {
    private static final String TAG = "FaceFragment";
    private static ZoneSecondFragment fragment;
    private List<Map<String, String>> list_tagId = new ArrayList<>();
    private SwipeMenuListView lv_id_zone2;
    private CasualtyAdapter adapter;
    private TextView tv_edit;
    private Button bt_delete_zone2;
    private boolean flag = false;
    private int checkedNums;//记录选中的条目数量
    private HashMap<Integer, Boolean> map_state = new HashMap<>();
    private List<Map<String, String>> delete_list = new ArrayList<>();
    private boolean checkFlag = false;
    private int surface = 0;
    public int preIndex = -1;
    private OkHttpClientHelper okHttpClientHelper ;

    private IdentyfyActivity identyfyActivity;
    private List<Prioritise> list_victim = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String[] names = new String[]{"", "", "John", "", "Amy", ""};
        String[] ids = new String[]{"123456s", "754623a", "562143w", "789654z", "999888f", "989655j"};
        String[] nirs = new String[]{"123456s", "754623a", "562143w", "789654z", "999888f", "989655j"};

        Prioritise prioritise = null;
        for (int i = 0; i < 6; i++) {
            prioritise = new Prioritise(new Victim(names[i], nirs[i]), ids[i]);
            list_victim.add(prioritise);
        }


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_face, container, false);
        lv_id_zone2 = (SwipeMenuListView) view.findViewById(R.id.lv_id_zone2);
        tv_edit = (TextView) view.findViewById(R.id.tv_edit);
        bt_delete_zone2 = (Button) view.findViewById(R.id.bt_delete_zone2);
        initView();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    public void initView() {
        adapter = new CasualtyAdapter(getContext(), list_victim, flag, new CasualtyAdapter.OnTransferDataListener() {
            @Override
            public void onTranseferData(int checkedNum, HashMap<Integer, Boolean> map) {
                checkedNums = checkedNum;
                map_state = map;
                //获取选中的个数
                bt_delete_zone2.setText("Delete Selected(" + checkedNums + ")");
                //要删除的list
                for (int i = 0; i < list_tagId.size(); i++) {
                    for (int key : map_state.keySet()) {
                        if (key == i && map.get(key) == true) {
                            delete_list.add(list_tagId.get(i));
                        }
                    }
                }
            }
        });
        lv_id_zone2.setAdapter(adapter);

//       动态创建相应的滑动菜单
        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem deleteItem = new SwipeMenuItem(getActivity());
                deleteItem.setBackground(new ColorDrawable(Color.RED));
                deleteItem.setWidth(dp2px(80));
                deleteItem.setIcon(R.mipmap.delete);
                menu.addMenuItem(deleteItem);
            }
        };
        lv_id_zone2.setMenuCreator(creator);

        //进行相应的item监听，从而进行跳转操作
//        lv_id_zone2.setOnItemClickListener(getContext());
        //进行相应得menu监听，从而可以进行滑动监听
        lv_id_zone2.setOnMenuItemClickListener(this);

        //对edit进行相应的监听
        tv_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (flag == false) {
                    // 让相应的radiobutton和button显示出来，从而进行相应的删除操作
                    bt_delete_zone2.setVisibility(View.VISIBLE);
                    //是相应的edit变为done,且不能被点击
                    tv_edit.setText("Done");
                    flag = true;
                    //进行相应的页面显示
                    adapter.isRefresh(list_victim, flag);
                    //对删除确定按钮进行进行监听
                    bt_delete_zone2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            adapter.removeItems(delete_list);
                            bt_delete_zone2.setVisibility(View.GONE);
                            tv_edit.setText("Edit");
                            flag = false;
                            adapter.isRefresh(list_victim, flag);
                            Toast.makeText(getContext(), "你点击了确定删除", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    flag = false;
                    adapter.isRefresh(list_victim, flag);
                    bt_delete_zone2.setVisibility(View.GONE);
                    tv_edit.setText("Edit");
                }

            }
        });
    }

    //    碎片进行相应的单例化
    public static ZoneSecondFragment newInstance() {
        if (fragment == null) {
            fragment = new ZoneSecondFragment();
        }
        return fragment;
    }

    @Override
    public void onResume() {
        identyfyActivity = (IdentyfyActivity) getActivity();
        identyfyActivity.initToolBar("main");
        lv_id_zone2.setOnItemClickListener(new MyListOnItemClickListener(identyfyActivity));
        identyfyActivity.fab.setVisibility(View.GONE);


        super.onResume();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden) {
            identyfyActivity = (IdentyfyActivity) getActivity();
            identyfyActivity.initToolBar("main");
            lv_id_zone2.setOnItemClickListener(new MyListOnItemClickListener(identyfyActivity));
            identyfyActivity.fab.setVisibility(View.GONE);
        }
        super.onHiddenChanged(hidden);
    }

    private class MyListOnItemClickListener implements AdapterView.OnItemClickListener {
        private IdentyfyActivity identyfyActivity;

        public MyListOnItemClickListener(IdentyfyActivity identyfyActivity) {
            this.identyfyActivity = identyfyActivity;
        }

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
            Toast.makeText(getContext(), "你点了第" + position + "个item", Toast.LENGTH_SHORT).show();
            //通过相应的activity给予的index,进行相应的加载
//        currentIndex = arg;
//        switchFragment(index);




            //进行传递数据作用
            EventBus.getDefault().postSticky(new TagIdEventExpress(list_victim.get(position).getTagId()));

            //判断当前的radiobutton
            int checkNum = identyfyActivity.getCheckedNum();
            Prioritise prioritise = null;
            prioritise = (Prioritise) adapter.getItem(position);
            int index = 0;
            switch (checkNum) {
                case 0:

                    PhotoTakenFragment photoTakenFragment = PhotoTakenFragment.newInstance(prioritise, checkNum);
                    if (identyfyActivity.list_Fragment.contains(photoTakenFragment)) {
                        index = identyfyActivity.list_Fragment.indexOf(photoTakenFragment);
                    } else {
                        index = identyfyActivity.list_Fragment.size();
                        identyfyActivity.list_Fragment.add(index, PhotoTakenFragment.newInstance(prioritise, checkNum));
                    }

                    //这边需要传入prioritise对象


//                tv_btn.setVisibility(View.GONE);
                    identyfyActivity.switchFragment(index);
                    break;
                case 1:
                    FingerprintconnectFragment fingerprintconnectFragment = FingerprintconnectFragment.newInstance(prioritise, checkNum);
                    if (identyfyActivity.list_Fragment.contains(fingerprintconnectFragment)) {
                        index = identyfyActivity.list_Fragment.indexOf(fingerprintconnectFragment);
                    } else {
                        index = identyfyActivity.list_Fragment.size();
                        identyfyActivity.list_Fragment.add(index, FingerprintconnectFragment.newInstance(prioritise, checkNum));
                    }
                    //这边需要传入prioritise对象


//                tv_btn.setVisibility(View.GONE);
                    identyfyActivity.switchFragment(index);
                    break;
                case 2:
                    InputPersonalIdFragment inputPersonalIdFragment = InputPersonalIdFragment.newInstance(prioritise, checkNum);
                    if (identyfyActivity.list_Fragment.contains(inputPersonalIdFragment)) {
                        index = identyfyActivity.list_Fragment.indexOf(inputPersonalIdFragment);
                    } else {
                        index = identyfyActivity.list_Fragment.size();
                        identyfyActivity.list_Fragment.add(index, InputPersonalIdFragment.newInstance(prioritise, checkNum));
                    }
                    //这边需要传入prioritise对象


//                tv_btn.setVisibility(View.GONE);
                    identyfyActivity.switchFragment(index);
                    break;
                case 3:
                    initData();
//                    Intent intent = new Intent();
//                    intent.setClass(getContext(), DetailActivity.class);
//                    Bundle bundle = new Bundle();
//                    bundle.putString("tagId", list_victim.get(position).getTagId());
//                    intent.putExtras(bundle);
//                    getActivity().startActivity(intent);
                    break;
                default:
                    break;
            }

        }
    }
    private void initData() {
//        进行相应的联网上传操作
        okHttpClientHelper = OkHttpClientHelper.getOkHttpClientUtils(getContext());
        final Callback callback = new Callback() {
            @Override
            public void onFailure(Request request, final IOException e) {
                e.printStackTrace();
                Log.e("TEST", "Request Failure!Please check the network.");
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(), "The network is bad", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (response.isSuccessful()) {
                    ResponseBody body = response.body();
                    if (body != null) {
                        String result = body.string();
//                        Intent intent = new Intent();
//                        intent.setClass(getContext(), MatchesFoundActivity.class);
//                        Bundle bundle = new Bundle();
//                        intent.putExtras(bundle);
//                        startActivity(intent);
                        Intent intent = new Intent();
                        intent.setClass(getContext(), DetailActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("result", result);
//                        bundle.putString("tagId", list_victim.get(position).getTagId());
                        intent.putExtras(bundle);
                        getActivity().startActivity(intent);
                    }
                }
//                        else {
//                            Log.e("TEST", response.isSuccessful() + "---" + response.body());
//                        }

            }
        };
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                    okHttpClientHelper.getDataAsync(getContext(), Constants.getIdentifyUrl("S0000001H", "xxxx"), callback, "tag");

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    /**
     * 尺寸的换算
     *
     * @param dipValue
     * @return
     */
    public int dp2px(float dipValue) {
        final float scale = this.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    //接口回调
    @Override
    public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
        switch (index) {
            case 0:
                list_victim.remove(position);
                adapter.isRefresh(list_victim, flag);
                break;
        }
        return false;
    }

}
