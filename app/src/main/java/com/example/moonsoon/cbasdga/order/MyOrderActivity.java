package com.example.moonsoon.cbasdga.order;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.moonsoon.cbasdga.MainActivity;
import com.example.moonsoon.cbasdga.R;
import com.example.moonsoon.cbasdga.favorite.FavoriteActivity;
import com.example.moonsoon.cbasdga.myDB;
import com.example.moonsoon.cbasdga.store.StoreActivity;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import vo.CartVO;
import vo.OrderVO;


/**
 * Created by 618 on 2015-09-04.
 */
public class MyOrderActivity extends AppCompatActivity {

    DrawerLayout dlDrawer;
    ActionBarDrawerToggle dtToggle;

    ListView lvDrawerList;
    ArrayAdapter<String> adtDrawerList;
    String[] menuItems = new String[]{"홈", "매장 목록", "즐겨찾기"};

    Toolbar toolbar;

    ListView lv_test = null;
    CustomAdapter mAdapter = null;
    Context mCtx;
    ArrayList<CartVO> m_Contents = new ArrayList<>();
    ArrayList<OrderVO> r_OrderList = null;
    myDB my;
    SQLiteDatabase sql;

    CartVO cartVO1;

    TextView o_State;
    TextView o_Wait;
    TextView o_Mileage;

    String req;
    String sResult;
    String para;
    static String rState="";
    String order_cnt;
    String phoneNumber;

    int choice;
    int test;
    static int sumPrice;
    static int store_num;
    int order_num;
    private int initialvalues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myorder_list);

        o_State = (TextView) findViewById(R.id.order_state);
        o_Wait = (TextView) findViewById(R.id.order_wait);
        o_Mileage = (TextView) findViewById(R.id.lv_myorder_mileage);

        lv_test = (ListView) findViewById(R.id.lv_order);

        lvDrawerList = (ListView)findViewById(R.id.lv_activity_myorderlist);
        adtDrawerList = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, menuItems);
        lvDrawerList.setAdapter(adtDrawerList);

        lvDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                switch (position) {
                    case 0:
                        nv_OnClick1(view);
                        break;
                    case 1:
                        nv_OnClick2(view);
                        break;
                    case 2:
                        fav_OnClick(view);
                        break;

                }
                dlDrawer.closeDrawer(GravityCompat.START);
            }
        });

        dlDrawer = (DrawerLayout) findViewById(R.id.dl_activity_myorder);
        dtToggle = new ActionBarDrawerToggle(this, dlDrawer, toolbar,  R.string.app_name, R.string.app_name);
        dlDrawer.setDrawerListener(dtToggle);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mCtx = this;

        mAdapter = new CustomAdapter(m_Contents);
        lv_test.setAdapter(mAdapter);

        o_Mileage.setText(""+MainActivity.mileage);

        Intent intent = getIntent();

        test = intent.getExtras().getInt("test");
        Log.d("test", String.valueOf(test));
        if(test==1){
            cartVO1 = (CartVO) intent.getExtras().getParcelable("cartvo1");
            store_num = intent.getExtras().getInt("store_num");
            req = intent.getExtras().getString("require");
            initialvalues = intent.getExtras().getInt("initvalue");
            sumPrice = intent.getExtras().getInt("sumPrice");
            Log.d("mainsum", String.valueOf(sumPrice));
        }
        else if(test==2 || test==3){
            store_num = intent.getExtras().getInt("store_num");
            req = intent.getExtras().getString("require");
            initialvalues = intent.getExtras().getInt("initvalue");
            sumPrice = intent.getExtras().getInt("sumPrice");
            Log.d("mainsum", String.valueOf(sumPrice));
        }

        if(test == 3){
            cartVO1 = intent.getExtras().getParcelable("cartvo");
        }else if(test==4){
            r_OrderList = intent.getParcelableArrayListExtra("array");
            for(int i=0; i<r_OrderList.size(); i++){
                Log.d("rstorenum", String.valueOf(r_OrderList.get(i).getStore_num()));
                Log.d("rorderprice", String.valueOf(r_OrderList.get(i).getOrder_price()));
                Log.d("ramount", String.valueOf(r_OrderList.get(i).getOrder_amount()));
            }

        }else if(test==5){
            Log.d("test5진입", "네");
            try{
                order_num = intent.getExtras().getInt("order_num");
                order_cnt = intent.getExtras().getString("order_cnt");
                m_Contents.addAll(intent.<CartVO>getParcelableArrayListExtra("list"));
                for(int i=0; i<m_Contents.size(); i++){
                    Log.d("엠컨텐", m_Contents.get(i).getMenu_name());
                }
                para = "order_number="+String.valueOf(order_num);
                Log.d("orderPara", para);

                StateTask stateTask = new StateTask();
                stateTask.execute();
            }catch (NullPointerException e){
                e.printStackTrace();
            }
        }

        if(test!=5){
            my = new myDB(this);

            OrderTask orderTask = new OrderTask();
            orderTask.execute();
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState){
        super.onPostCreate(savedInstanceState);
        dtToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        dtToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_menulist, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(dtToggle.onOptionsItemSelected(item)){
            return true;
        }

        switch (item.getItemId()){
            case R.id.action_cart:
                choice = 2;
                Intent intent = new Intent(this, CartActivity.class);
                intent.putExtra("choice", 2);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private class CustomAdapter extends BaseAdapter{

        ArrayList<CartVO> mItems;

        public CustomAdapter(ArrayList<CartVO> mContents){
            this.mItems = mContents;
            for(int i=0; i<mContents.size(); i++){
                Log.d("엠컨텐2", mContents.get(i).getMenu_name());
            }
        }


        @Override
        public int getCount() {
            return mItems.size();
        }

        @Override
        public Object getItem(int position) {
            return mItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View v = convertView;

            ViewHolder holder = null;
            LayoutInflater mInflater = ((AppCompatActivity)mCtx).getLayoutInflater();

            if(convertView == null){
                holder = new ViewHolder();
                v = (View)mInflater.inflate(R.layout.buyinfo_list_item, null);

                holder.txt_name = (TextView)v.findViewById(R.id.buy_name);
                holder.txt_price = (TextView)v.findViewById(R.id.buy_price);
                holder.txt_amount = (TextView)v.findViewById(R.id.buy_amount);

                v.setTag(holder);
            }else{
                holder = (ViewHolder)v.getTag();
            }

            holder.txt_name.setText(mItems.get(position).getMenu_name());
            holder.txt_price.setText(""+mItems.get(position).getMenu_price());
            holder.txt_amount.setText(""+mItems.get(position).getMenu_amount());

            return v;
        }
    }

    private class ViewHolder{
        TextView txt_name;
        TextView txt_price;
        TextView txt_amount;
    }

    private class CustomOnClickListener implements View.OnClickListener{
        CartVO mCartVO;

        public CustomOnClickListener(CartVO arg){
            this.mCartVO = arg;
        }

        @Override
        public void onClick(View v) {
//            Toast.makeText(mCtx, "ClickTest", Toast.LENGTH_SHORT).show();
        }
    }

    private class OrderTask extends AsyncTask<Void, Void, Boolean>{

        @Override
        protected Boolean doInBackground(Void... params) {

            Log.d("OrderTast=", "성공");
            JSONArray array = new JSONArray();

            int i=0;

            if(test == 1){
                Log.d("cartvotest1", "진입");

                    JSONObject jobj = new JSONObject();

                    jobj.put("order_id", getPhoneNumber());
                    jobj.put("order_menu", cartVO1.getMenu_name());
                    jobj.put("order_price",cartVO1.getMenu_price());
                    jobj.put("store_num", cartVO1.getStore_num());
                    jobj.put("order_req", req);
                    jobj.put("order_amount", cartVO1.getMenu_amount());

                    Log.d("jobj", jobj.toJSONString());

                    array.add(jobj);

                    m_Contents.add(cartVO1);

            } else if(test == 2){
                sql = my.getReadableDatabase();
                Cursor cursor;
                cursor = sql.rawQuery("SELECT * FROM CART;", null);

                while (cursor.moveToNext()) {
                    CartVO cartVO = new CartVO();
                    cartVO.setMenu_name(cursor.getString(0));
                    cartVO.setMenu_price(cursor.getInt(1));
                    cartVO.setStore_num(cursor.getInt(4));
                    cartVO.setMenu_amount(cursor.getInt(2));

                    OrderVO order_manVO = new OrderVO();
                    order_manVO.setOrder_id(getPhoneNumber());
                    order_manVO.setOrder_menu(cartVO.getMenu_name());
                    order_manVO.setOrder_price(cartVO.getMenu_price());
                    order_manVO.setStore_num(cartVO.getStore_num());
                    order_manVO.setOrder_req(req);
                    order_manVO.setOrder_amount(cartVO.getMenu_amount());

                    JSONObject jobj = new JSONObject();
                    jobj.put("order_id", order_manVO.getOrder_id());
                    jobj.put("order_menu", order_manVO.getOrder_menu());
                    jobj.put("order_price", order_manVO.getOrder_price());
                    jobj.put("store_num", order_manVO.getStore_num());
                    jobj.put("order_req", order_manVO.getOrder_req());
                    jobj.put("order_amount", order_manVO.getOrder_amount());

                    Log.d("jobj=", jobj.toJSONString());

                    array.add(jobj);

                    m_Contents.add(cartVO);

                    i++;
                }

                cursor.close();
                sql.close();

            }else if(test == 3){
                OrderVO orderVO = new OrderVO();
                orderVO.setOrder_id(getPhoneNumber());
                orderVO.setOrder_menu(cartVO1.getMenu_name());
                orderVO.setOrder_price(cartVO1.getMenu_price());
                orderVO.setStore_num(store_num);
                orderVO.setOrder_req(req);
                orderVO.setOrder_amount(initialvalues);

                JSONObject jobj = new JSONObject();
                jobj.put("order_id", orderVO.getOrder_id());
                jobj.put("order_menu", orderVO.getOrder_menu());
                jobj.put("order_price", orderVO.getOrder_price());
                jobj.put("store_num", orderVO.getStore_num());
                jobj.put("order_req", orderVO.getOrder_req());
                jobj.put("order_amount", orderVO.getOrder_amount());

                Log.d("jobj=", jobj.toJSONString());

                array.add(jobj);

                m_Contents.add(cartVO1);

            }else if(test==4){

                Log.d("test4진입", "123");

                for(int j=0; j<r_OrderList.size(); j++){

                    JSONObject jobj = new JSONObject();

                    jobj.put("order_id", r_OrderList.get(j).getOrder_id());
                    jobj.put("order_menu", r_OrderList.get(j).getOrder_menu());
                    jobj.put("order_price", r_OrderList.get(j).getOrder_price());
                    jobj.put("store_num", r_OrderList.get(j).getStore_num());
                    jobj.put("order_req", r_OrderList.get(j).getOrder_req());
                    jobj.put("order_amount", r_OrderList.get(j).getOrder_amount());

                    Log.d("jobj", jobj.toJSONString());

                    array.add(jobj);

                    CartVO cartVO = new CartVO();
                    cartVO.setMenu_name(r_OrderList.get(j).getOrder_menu());
                    cartVO.setMenu_price(r_OrderList.get(j).getOrder_price());
                    cartVO.setMenu_amount(r_OrderList.get(j).getOrder_amount());

                    m_Contents.add(cartVO);
                }

            }

            Log.d("jarr=", array.toJSONString());

            Map<String, Object> map = new HashMap<String, Object>();
            map.put("orderlist", array);

            JSONObject jsonobj = new JSONObject();
            jsonobj.putAll(map);

            Log.d("json테스트", jsonobj.toJSONString());

                try{
                    String body = "obj="+jsonobj.toJSONString();
                    URL u = new URL("http://192.168.0.3/proj_1/android/menuinsert.jsp");
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
                    Log.d("sResult = ", sResult);

                    Object rObj = JSONValue.parse(sResult);
                    JSONObject rJson = (JSONObject)rObj;

                    para = "order_number="+rJson.get("number").toString();
                    order_num = Integer.parseInt(rJson.get("number").toString());

                    Log.d("para =", para);

                }catch(IOException e){
                    e.printStackTrace();
                }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if(aBoolean){
//                Toast.makeText(mCtx, "데이터 로드 성공", Toast.LENGTH_SHORT).show();
                mAdapter.notifyDataSetChanged();
                choice = 1;

                MainActivity.order_num = order_num;
                MainActivity.m_aContents = m_Contents;
                MainActivity.order_cnt = order_cnt;
            }else {
                //실패 햇을 경우
//                Toast.makeText(mCtx, "데이터 로드 실패", Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        if(choice == 1){
            StateTask stateTask = new StateTask();
            stateTask.execute();
        }
        else{
            o_State.setText("승인 대기");
        }

        Log.d("onResume =", "진입");
    }

    private class StateTask extends AsyncTask<Void, Void, String>{
        String  Oresult="";

        @Override
        protected String doInBackground(Void... params) {

            try{
                Log.d("taskPara =", para);
                URL u = new URL("http://192.168.0.3/proj_1/android/state.jsp");
                HttpURLConnection huc = (HttpURLConnection) u.openConnection();
                huc.setReadTimeout(4000);
                huc.setConnectTimeout(4000);
                huc.setRequestMethod("POST");
                huc.setDoInput(true);
                huc.setDoOutput(true);
                huc.setRequestProperty("utf-8",
                        "application/x-www-form-urlencoded");
                OutputStream os = huc.getOutputStream();
                os.write(para.getBytes("utf-8"));
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
                Log.d("myorderresult=", sResult);

                Object why = JSONValue.parse(sResult);
                JSONObject whwhy = (JSONObject)why;
                rState = whwhy.get("state").toString();
                order_cnt = whwhy.get("orderCount").toString();

                if(rState.equals("y")){
                    Oresult = "결제 완료";
                }else{
                    Oresult = "식사 후 결제";
                }
                switch(rState){
                    case "w" : Oresult = "승인 대기";
                        break;
                    case "n" : Oresult = "주문 거부";
                        break;
                    case "s" : Oresult = "조리 시작";
                        break;
                    case "e" : Oresult = "식사 중";
                        break;
                    case "cs" : Oresult = "조리 대기";;
                        break;
                    case "oc" : Oresult = "조리 완료";
                        break;
                    case "d" : Oresult = "삭제";
                        break;
                }

            }catch(IOException e){
                e.printStackTrace();
            }
            return Oresult;
        }

        @Override
        protected void onPostExecute(String aString) {
//            Toast.makeText(mCtx, "네트워크 로드 성공", Toast.LENGTH_SHORT).show();
            Log.d("aString = ", aString);
            o_State.setText(aString);
            o_Wait.setText(order_cnt);
            if(rState.equals("d")){
                SetMileageTask setMileageTask = new SetMileageTask();
                setMileageTask.execute();
            }
            mAdapter.notifyDataSetChanged();
            favoriteAddAlert();

            MainActivity.order_num = order_num;
            MainActivity.m_aContents = m_Contents;
            MainActivity.order_cnt = order_cnt;
        }
    }

    public String getPhoneNumber(){
        String phoneNumber;
        TelephonyManager mgr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        phoneNumber = mgr.getLine1Number();
        return phoneNumber;
    }

    public void nv_OnClick1(View v){
        Intent intent = new Intent(this, MainActivity.class);
        MainActivity.order_num = order_num;
        MainActivity.m_aContents = m_Contents;
        MainActivity.order_cnt = order_cnt;
        Log.d("mainOrder_num", String.valueOf(order_num));
//        intent.putExtra("choice", 1);
//        intent.putExtra("order_num", order_num);
        startActivity(intent);
    }
    public void nv_OnClick2(View v){
        Intent intent = new Intent(this, StoreActivity.class);
        startActivity(intent);
    }
    public void fav_OnClick(View v){
        Intent intent = new Intent(this, FavoriteActivity.class);
        intent.putExtra("choice", 1);
        startActivity(intent);
    }

    public void favoriteAddAlert() {
        //주문 상태가 주문 완료일때만 popup
        Log.d("얼럿진입", "네");
        Log.d("gettext", o_State.getText().toString());

        if (((o_State.getText()).toString()).equals("삭제")) {
            AlertDialog.Builder alert = new AlertDialog.Builder(MyOrderActivity.this);
            alert.setTitle("이용해 주셔서 감사합니다.");
            alert.setMessage("해당 매장을 즐겨찾기로 등록하시겠습니까?");
            alert.setPositiveButton("예", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    InsertFavoriteTask insertFavoriteTask = new InsertFavoriteTask();
                    insertFavoriteTask.execute();
                    Toast.makeText(mCtx, "즐겨찾기에 추가 되었습니다.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    MainActivity.order_num = 0;
                    MainActivity.m_aContents = null;
                    MainActivity.order_cnt = null;
                    startActivity(intent);
                }
            });
            alert.setNegativeButton("아니오", null);
            alert.show();
        }
    }

    public InputStream requestFavGet(String requestURL, int store_num, String cus_id) {

        try {

            HttpClient client = new DefaultHttpClient();
            //폼데이터 저장
            List dataList = new ArrayList();
            dataList.add(new BasicNameValuePair("store_num", store_num + ""));
            dataList.add(new BasicNameValuePair("cus_id", cus_id));

            requestURL = requestURL + "?" + URLEncodedUtils.format(dataList, "UTF-8");
            Log.d("requestURL", requestURL);
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

            String requestURL = "http://192.168.0.3/proj_1/android/insertFavToWeb.jsp";
            InputStream fis = requestFavGet(requestURL, store_num, getPhoneNumber());

            return true;
        }
    }

    private class SetMileageTask extends AsyncTask<Void, Void, Boolean> {


        @Override
        protected Boolean doInBackground(Void... params) {

            //디비 저장-임시로 막아 놓음
            String requestURL = "http://192.168.0.3/proj_1/android/mileageInfoToWeb.jsp";
            InputStream is = requestMileageInfoGet(requestURL);
            Log.i("xxx", "requestGet finish");
            return true;
        }
    }

    public InputStream requestMileageInfoGet(String requestURL) {

        Log.i("xxx", "requestGet start");
        try {
            phoneNumber = this.getPhoneNumber();
            //1.
            HttpClient client = new DefaultHttpClient();
            Log.i("xxx", "111111111111111111111111111");
            Log.i("requestLogin", requestURL);
            Log.i("phoneNum", phoneNumber);
            Log.i("sumPrice", ""+sumPrice);
            //폼데이터 저장
            List dataList = new ArrayList();
            dataList.add(new BasicNameValuePair("phoneNumber", phoneNumber));
            dataList.add(new BasicNameValuePair("sumPrice", Integer.toString(sumPrice)));
            requestURL = requestURL + "?" + URLEncodedUtils.format(dataList, "UTF-8");
            Log.i("requestURL22222222", requestURL);
            HttpGet get = new HttpGet(requestURL);
            Log.i("xxx", "222222222222222222222222222222");
            //2. 요청
            HttpResponse response = client.execute(get);
            Log.i("xxx", "333333333333333333333333333333333");
            //3. 결과 받기
            HttpEntity entity = response.getEntity();
            InputStream is = entity.getContent();
            Log.i("xxx", "4444444444444444444444444");
            return is;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
