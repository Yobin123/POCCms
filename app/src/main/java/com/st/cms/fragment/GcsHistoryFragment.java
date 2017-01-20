package com.st.cms.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.st.cms.adapter.GcsHistoryAdapter;
import com.st.cms.entity.Prioritise;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cms.st.com.poccms.R;

/**
 * Created by jt on 2016/9/12.
 */
public class GcsHistoryFragment extends TreatmentBaseFragment{

    private static GcsHistoryFragment fragment;
    private SwipeRefreshLayout mSwipeLayout;
    private RecyclerView mRecyclerView;
    private List<Map<String, String>> totalList = new ArrayList<>();
    private GcsHistoryAdapter adapter;
    private int preIndex = -1;
    private MyBackListener myBackListener;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gcslog_history, container, false);
        initData();
        initView(view);
        return view;
    }
    //进行数据的初始化
    private void initData() {
        totalList.clear();
        for (int i = 0; i < 3; i++) {
            Map<String,String> map = new HashMap();
            map.put("eyeopen",2+i+"");
            map.put("verbal",2+2*i+"");
            map.put("motor",4+i+"");
            map.put("time","12-20:"+(i+1)*20+"pm");
            totalList.add(map);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        myBackListener = new MyBackListener(preIndex);
        treatmentMainActivity.iv_back.setOnClickListener(myBackListener);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden)
        {
            treatmentMainActivity.tv_title.setText(prioritise.getTagId());
            if(myBackListener != null)
            {
                myBackListener.setPreIndex(preIndex);
            }
            else
            {
                myBackListener = new MyBackListener(preIndex);
            }
            treatmentMainActivity.iv_back.setOnClickListener(new MyBackListener(preIndex));
        }
        super.onHiddenChanged(hidden);
    }
    private void initView(View view) {
        mSwipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipelayout_gcs_history);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerview_gcs_history);

        //设置item具有固定的的size，可以提升加载效率。
        // recyclerView_main.setHasFixedSize(true);
        //设置布局管理器（必须设置）
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(linearLayoutManager);
//        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext,2);
        //添加item之间的分割线，可以不需要
        //设置item的动画效果
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        //设置适配器
        adapter = new GcsHistoryAdapter(getContext(),totalList);
        mRecyclerView.setAdapter(adapter);


        //设置swipelay  Color.parseColor("#ff000)
        mSwipeLayout.setColorSchemeColors(Color.RED, Color.BLUE, Color.GREEN);
        //设置相应的swiperefreshlayout的监听器，实现刷新动作
        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                totalList.clear();
                initData();
                adapter.notifyDataSetChanged();
                //让刷新消失
                mSwipeLayout.setRefreshing(false);
            }
        });
    }

    public static GcsHistoryFragment newInstance(Prioritise prioritise, int preIndex) {
        if(fragment == null)
        {
            fragment = new GcsHistoryFragment();
        }
        fragment.prioritise = prioritise;
        fragment.preIndex = preIndex;
        return fragment;
    }
}
