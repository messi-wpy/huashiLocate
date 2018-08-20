package com.example.messi_lp.huashidemo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.muxistudio.cardbanner.CardBanner;
import com.muxistudio.cardbanner.ViewHolder;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by yue on 2018/8/19.
 */

public class PointDetails extends Activity{

    public CardBanner mBanner;
    public TextView mDetails;
    public PointData mPointData;
    private List<ViewHolder<Integer>> mViewHolders = new ArrayList<>();

    //  测试用图
    private Integer[] resIds = {R.drawable.a,R.drawable.b,R.drawable.c};

    private List<Integer> resIdList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_point_detail);


        Intent intent = getIntent();
        double latitude = intent.getDoubleExtra("latitude",0.0);
        double longitude = intent.getDoubleExtra("longitude",0.0);
        initView(latitude,longitude);
    }



    public void initView(double latitude,double longitude){
        mBanner = findViewById(R.id.map_detail_banner);

    }
}
