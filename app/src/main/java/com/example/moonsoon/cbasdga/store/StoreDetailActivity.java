package com.example.moonsoon.cbasdga.store;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.moonsoon.cbasdga.MainActivity;
import com.example.moonsoon.cbasdga.R;
import com.example.moonsoon.cbasdga.menu.MenuListFragment;
import com.example.moonsoon.cbasdga.order.CartActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import vo.StoreVO;

import static com.google.android.gms.internal.zzid.runOnUiThread;

/**
 * Created by moonsoon on 2015-09-15.
 */
public class StoreDetailActivity extends ActionBarActivity {
    StoreLocFragment slf = new StoreLocFragment();
    public MenuListFragment mlf = new MenuListFragment();
    FragmentManager fm;
    FragmentTransaction ft;

    TextView sd_addr;
    TextView sd_tel;
    TextView sd_time;

    int choice;

    String store_name;
    String store_tel;
    String store_addr;
    String store_time;
    int store_num;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.store_info);



        fm = getSupportFragmentManager();
        ft = fm.beginTransaction();

        ft.replace(R.id.menu_list, mlf).commit();

        sd_addr = (TextView)findViewById(R.id.s_Addr);
        sd_tel = (TextView)findViewById(R.id.s_Tel);
        sd_time = (TextView)findViewById(R.id.s_Time);

        Button btn1 = (Button)findViewById(R.id.btn1);
        Button btn2 = (Button)findViewById(R.id.btn2);

        Intent intent = getIntent();
        choice = intent.getExtras().getInt("choice");

        if(choice == 1){
            store_name = intent.getExtras().getString("store_name");
            store_num = intent.getExtras().getInt("store_num");
            store_tel = intent.getExtras().getString("store_tel");
            store_addr = intent.getExtras().getString("store_addr");
            store_time = intent.getExtras().getString("store_time");

            Bundle bundle = new Bundle();
            mlf.setArguments(bundle);
            bundle.putString("store_name", store_name);
            bundle.putString("store_tel", store_tel);
            bundle.putString("store_addr", store_addr);
            bundle.putString("store_time", store_time);
            bundle.putInt("store_num", store_num);
            bundle.putInt("choice", 1);
            mlf.setArguments(bundle);

            sd_addr.setText(store_addr);
            sd_tel.setText(store_tel);
            sd_time.setText(store_time);

        }else if(choice == 2){
            store_num = intent.getExtras().getInt("store_num");

            StoreTask storeTask = new StoreTask();
            storeTask.execute();

            Bundle bundle = new Bundle();
            mlf.setArguments(bundle);
            bundle.putInt("store_num", store_num);
            bundle.putString("store_name", store_name);
            bundle.putString("store_tel", store_tel);
            bundle.putString("store_addr", store_addr);
            bundle.putString("store_time", store_time);
            bundle.putInt("choice", 2);
            mlf.setArguments(bundle);
        }

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(getApplicationContext(), "btn1", Toast.LENGTH_SHORT).show();
                getSupportFragmentManager().beginTransaction().remove(slf).commit();
                fm = getSupportFragmentManager();
                ft = fm.beginTransaction();
                ft.replace(R.id.menu_list, mlf);
                ft.commit();
            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(getApplicationContext(), "btn2", Toast.LENGTH_SHORT).show();
                getSupportFragmentManager().beginTransaction().remove(mlf).commit();
                Bundle bundle = new Bundle();
                slf.setArguments(bundle);
                bundle.putString("store_addr", store_addr);
                Log.d("store주소", store_addr);
                slf.setArguments(bundle);
                fm = getSupportFragmentManager();
                ft = fm.beginTransaction();
                ft.replace(R.id.menu_list, slf);
                ft.commit();
            }
        });
    }

    //actionbar button start
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_menulist, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_cart:
                choice = 2;
                Intent intent = new Intent(this, CartActivity.class);
                intent.putExtra("choice", choice);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(choice == 2){
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
        }
    }

    private class StoreTask extends AsyncTask<Void, Void, Boolean> {

        String sResult;

        @Override
        protected Boolean doInBackground(Void... params) {
            JSONObject json = null;

            Log.d("NFCStore =", "진입");

            try {
                String body = "store_num="+store_num;
                URL u = new URL("http://192.168.0.3/proj_1/android/storelistNFC.jsp");
                HttpURLConnection huc = (HttpURLConnection) u.openConnection();
                huc.setReadTimeout(4000);
                huc.setConnectTimeout(4000);
                huc.setRequestMethod("POST");
                huc.setDoInput(true);
                huc.setDoOutput(true);
                huc.setRequestProperty("utf-8",
                        "application/x-www-form-urlencoded");
                OutputStream os = huc.getOutputStream();
                os.write(body.getBytes("utf-8"));
                os.flush();
                os.close();

                BufferedReader is = new BufferedReader(new InputStreamReader(
                        huc.getInputStream(), "UTF-8"));
                int ch;
                StringBuffer sb = new StringBuffer();
                while ((ch = is.read()) != -1) {
                    sb.append((char) ch);
                }
                if (is != null) {
                    is.close();
                }

                sResult = sb.toString();

                json = new JSONObject(sResult);
                Log.d("detailsResult = ", sResult);
                JSONArray jArr = json.getJSONArray("categorylist");

                for (int i = 0; i < jArr.length(); i++) {
                    StoreVO storeVO = new StoreVO();
                    json = jArr.getJSONObject(i);
                    storeVO.setOwner_id(json.getString("owner_id"));
                    storeVO.setStore_name(json.getString("store_name"));
                    storeVO.setStore_tel(json.getString("store_tel"));
                    storeVO.setStore_addr(json.getString("store_addr"));
                    storeVO.setStore_time(json.getString("store_time"));
                    storeVO.setStore_category(json.getString("store_category"));
                    storeVO.setSearchType(json.getString("searchType"));
                    storeVO.setSearchValue(json.getString("searchValue"));
                    storeVO.setStore_post(json.getString("store_post"));
                    storeVO.setStore_num(json.getInt("store_num"));

                    store_addr = storeVO.getStore_addr();
                    store_tel = storeVO.getStore_tel();
                    store_time = storeVO.getStore_time();
                    store_name = storeVO.getStore_name();

                    Log.d("store_addr", store_addr);
                    Log.d("store_tel", store_tel);
                    Log.d("store_time", store_time);
                    Log.d("task_store_namee", store_name);

                }

            } catch (Exception e) {
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            if(aBoolean){
//                Toast.makeText(getApplicationContext(), "데이터 로드 성공", Toast.LENGTH_SHORT).show();
                reFresh();
            }else {
                //실패 햇을 경우
//                Toast.makeText(getApplicationContext(), "데이터 로드 실패", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void reFresh(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                sd_addr.setText(store_addr);
                sd_tel.setText(store_tel);
                sd_time.setText(store_time);
            }
        });
    }

    //actionbar button end
}
