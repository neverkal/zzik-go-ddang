package com.example.moonsoon.cbasdga.order;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.moonsoon.cbasdga.MainActivity;
import com.example.moonsoon.cbasdga.MenuListActivity;
import com.example.moonsoon.cbasdga.R;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class AddFavorite extends AppCompatActivity {

    public static final int REQUEST_CODE_FAVLIST = 1001;

    private int storeNum;
    private String cus_id;
    Intent returnMyOrder;
    Intent returnMenulist;
    Intent addFavorite;
    private boolean flag;       //  어느 액티비티에서 넘어온건지 확인하는 flag
				                                //  true : 주문완료popup,  false : 메뉴판 - 즐겨찾기추가버튼

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent addFavorite = getIntent();
        storeNum = addFavorite.getExtras().getInt("storeNum");
        cus_id = addFavorite.getExtras().getString("cusID");
        flag = addFavorite.getExtras().getBoolean("flag");

        try {	
            insertFavoriteToDB();
            if (flag) {                 // 즐겨 찾기 추가 후 주문 상태로 다시 돌아감
                returnMyOrder = new Intent(this, MyOrderActivity.class);
                returnMyOrder.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(returnMyOrder);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    private void insertFavoriteToDB() {
        InsertFavorite();
    }

    private void InsertFavorite() {
        InsertFavoriteTask insertFavoriteObj = new InsertFavoriteTask();
        insertFavoriteObj.execute();
    }

    public InputStream requestFavGet(String requestURL, int store_num, String cus_id) {

        try {

            HttpClient client = new DefaultHttpClient();
            //폼데이터 저장
            List dataList = new ArrayList();
            dataList.add(new BasicNameValuePair("store_num", storeNum + ""));
            dataList.add(new BasicNameValuePair("cus_id", cus_id));

            requestURL = requestURL + "?" + URLEncodedUtils.format(dataList, "UTF-8");
            HttpGet get = new HttpGet(requestURL);
            HttpResponse response = client.execute(get);
            HttpEntity entity = response.getEntity();
            InputStream is = entity.getContent();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String str = "";
            try {
                while ((str = br.readLine()) != null) {
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return is;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private class InsertFavoriteTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {

            // 넘어온 매장번호, 고객아이디, flag 받기

            String requestURL = "http://192.168.0.3/android/insertFavToWeb.jsp";
            InputStream fis = requestFavGet(requestURL, storeNum, cus_id);

            return true;
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        processIntent(intent);
    }

    private void processIntent(Intent intent) {
        String from = intent.getStringExtra("from");
        if (from == null) {
            return;
        }
        String command = intent.getStringExtra("command");
        String type = intent.getStringExtra("type");
        String data = intent.getStringExtra("data");
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

