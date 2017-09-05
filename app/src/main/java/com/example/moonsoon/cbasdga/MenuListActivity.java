package com.example.moonsoon.cbasdga;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

/**
 * Created by Administrator on 2015-10-09.
 */
public class MenuListActivity extends AppCompatActivity {

    private TextView s_Name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_list);

        Intent change = getIntent();
        // 넘어온 change 데이터 (storeName,storeNum) 받기

        String storeName;
        int storeNum = change.getIntExtra("storeNum", 0);

        s_Name = (TextView)findViewById(R.id.s_Name);
        s_Name.setText(change.getStringExtra("storeName") + storeNum);
    }
}
