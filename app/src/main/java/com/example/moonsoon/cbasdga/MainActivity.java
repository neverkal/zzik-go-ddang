package com.example.moonsoon.cbasdga;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;

import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.moonsoon.cbasdga.favorite.FavoriteActivity;
import com.example.moonsoon.cbasdga.gcm.GCMInfo;
import com.example.moonsoon.cbasdga.order.CartActivity;
import com.example.moonsoon.cbasdga.order.MyOrderActivity;
import com.example.moonsoon.cbasdga.store.StoreActivity;
import com.example.moonsoon.cbasdga.store.StoreDetailActivity;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import vo.CartVO;
import vo.PushVO;


public class MainActivity extends AppCompatActivity{
    DrawerLayout dlDrawer;
    ActionBarDrawerToggle dtToggle;

    ListView lvDrawerList;
    ArrayAdapter<String> adtDrawerList;
    String[] menuItems = new String[]{"홈", "매장 목록", "즐겨찾기"};

    Toolbar toolbar;

    //push
    public static final String TAG = "MainActivity";

    private Random random;

    ArrayList<String> idList = new ArrayList<String>();

    ArrayList<PushVO> m_Contents = new ArrayList<PushVO>();

    Context mCtx;
    String regId;
    String phoneNumber;
    GPSTracker gps;
    static double latitude;
    static double longitude;
    static String my_addr;

    public static int order_num;
    public static ArrayList<CartVO> m_aContents;
    public static String order_cnt;

    TextView temp_mileage;
    PushVO pvo = null;
    public static int mileage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gps = new GPSTracker(MainActivity.this);
        latitude = gps.getLatitude();
        longitude = gps.getLongitude();

        lvDrawerList = (ListView)findViewById(R.id.lv_activity_list);
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

        dlDrawer = (DrawerLayout) findViewById(R.id.dl_activity_main);
        dtToggle = new ActionBarDrawerToggle(this, dlDrawer, toolbar,  R.string.app_name, R.string.app_name);
        dlDrawer.setDrawerListener(dtToggle);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mCtx = this;
        // 인텐트를 전달받는 경우
        Intent intent = getIntent();

//        try{
//            Log.d("ordertry진입", "네");
//            choice = intent.getExtras().getInt("choice");
//            if(1 == choice){
//                order_num = intent.getExtras().getInt("order_num");
//                Log.d("mainorder진입", String.valueOf(order_num));
//            }
//        }catch(NullPointerException e){
//            e.printStackTrace();
//        }

        if (intent != null) {
            processIntent(intent);
        }


    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            insertGCMinfo();
            getAddrByLoc();
            getMileage();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void getAddrByLoc(){

        getAddrByLocTask getAddrByLocTask = new getAddrByLocTask();
        getAddrByLocTask.execute();

    }

    private void insertGCMinfo(){

        InsertGCMinfoTask insertGCMinfoTask = new InsertGCMinfoTask();
        insertGCMinfoTask.execute();

    }

    private class getAddrByLocTask extends AsyncTask<Void, Void, Boolean> {

        GpsToAddress gps;
        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            Log.i("xxx", "getAddrByLocTask2222222222");
            TextView gpstxt = (TextView)findViewById(R.id.gps_address);
            gpstxt.setText(my_addr);

//            if(my_addr.equals(null)){
//                gpstxt.setText("현재 위치를 표시할 수 없습니다.");
//            }

        }
        @Override
        protected Boolean doInBackground(Void... params) {
            try{
                Log.i("xxx", "getAddrByLocTask111111111");
                gps = new GpsToAddress(latitude,longitude);
                my_addr = gps.getAddress();
                Log.d("11111111111111", gps.getAddress());
            }
            catch (Exception e){
                e.printStackTrace();
            }

            return  true;
        }
    }

    private class InsertGCMinfoTask extends AsyncTask<Void, Void, Boolean> {


        @Override
        protected Boolean doInBackground(Void... params) {
            GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
            try{
                regId = gcm.register(GCMInfo.PROJECT_ID);}
            catch(IOException e){e.printStackTrace();}
//                regId="APA91bHGUu4RQFA8ZTUy19UvImZshG0glWqIm57-2v_1DERddMtQ4D3OfzJTuEzBs-Y02x2z3hPE7XO5-YSRUhLIY57-1gwHwqgmTgRlg5KAyUi0RTsQw5Z_qjbH-J-gaNqjmRWLiuId";
            // 등록 ID 리스트에 추가 (현재는 1개만)
            idList.clear();
            idList.add(regId);
            //휴대폰 번호 가져오기 - 임시로 막아 놓음
            //유심칩이 없어서 막아놓고 밑에 번호를 적어 두었어요!
            TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            String phoneNum = tm.getLine1Number();
            Log.d(TAG, phoneNum);


//            phoneNum ="8212345678";
            //디비 저장-임시로 막아 놓음
            String requestURL = "http://192.168.0.3/proj_1/android/justConnToApp.jsp";
            Log.i("regId1111111111", regId);
            Log.i("phoneNum1111111111", phoneNum);
            InputStream is = requestGCMGet(requestURL, regId, phoneNum);
            Log.i("xxx", "requestGet finish");
            //finish();
            return true;
        }
    }

    public InputStream requestGCMGet(String requestURL, String regId, String phoneNum) {

        Log.i("xxx", "requestGet start");
        try {

            //1.
            HttpClient client = new DefaultHttpClient();
            Log.i("xxx", "111111111111111111111111111");
            Log.i("requestURL", requestURL);
            Log.i("regId", regId);
            Log.i("phoneNum", phoneNum);
            //폼데이터 저장
            List dataList = new ArrayList();
            dataList.add(new BasicNameValuePair("regId", regId));
            dataList.add(new BasicNameValuePair("phoneNum", phoneNum));
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
    }//end requestGet()

    @Override
    protected void onNewIntent(Intent intent) {
        Log.d(TAG, "onNewIntent() called.");

        processIntent(intent);

        super.onNewIntent(intent);
    }

    private void processIntent(Intent intent) {
        String from = intent.getStringExtra("from");
        if (from == null) {
            Log.d(TAG, "from is null.");
            return;
        }

        String command = intent.getStringExtra("command");
        String type = intent.getStringExtra("type");
        String data = intent.getStringExtra("data");

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
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(dtToggle.onOptionsItemSelected(item)){
            return true;
        }

        switch (item.getItemId()){
            case R.id.action_map:
                int choice = 3;
                Intent intent = new Intent(this, FavoriteActivity.class);
                intent.putExtra("choice", choice);
                startActivity(intent);
                return true;
            case R.id.action_myorder:
                Intent intent2 = new Intent(this, MyOrderActivity.class);
                intent2.putExtra("test", 5);
                Log.d("putorder_num", String.valueOf(order_num));
                intent2.putExtra("order_num", order_num);
                intent2.putExtra("order_cnt", order_cnt);
                intent2.putParcelableArrayListExtra("list", m_aContents);
                startActivity(intent2);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void getMileage() {

        GetMileageTask getMileageTask = new GetMileageTask();
        getMileageTask.execute();

    }

    private class GetMileageTask extends AsyncTask<Void, Void, Boolean> {
        ArrayList<PushVO> pushList;
        PushVO vo;

        @Override
        protected void onPostExecute(Boolean result) {
            temp_mileage = (TextView) findViewById(R.id.lv_activity_mileage);
            temp_mileage.setText("마일리지:" + mileage);

        }

        @Override
        protected Boolean doInBackground(Void... params) {
            String requestURL = "http://192.168.0.3/proj_1/android/getmileageInfo.jsp";
            InputStream is = requestMileageInfo(requestURL);
            getMileageInfoByParsingXML(is);
            return true;
        }
    }

    public void getMileageInfoByParsingXML(InputStream is) {
        //ArrayList<PushVO> list = new ArrayList<PushVO>();

        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(is, "UTF-8");
            int eventType = parser.getEventType();
            a:
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String startTag = parser.getName();
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if (startTag.equals("record")) {
                            pvo = new PushVO();
                        }
                        if (pvo != null) {
                            if (startTag.equals("mileage")) {
                                pvo.setMileage(Integer.parseInt(parser.nextText()));
                                Log.i("mileage", "" + pvo.getMileage());
                                mileage = pvo.getMileage();
                            }
                        } else {
                            Log.i("null", "pvo = null");
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        String endTag = parser.getName();
                        if (endTag.equals("record")) {
                            Log.i("gmParsing", "success");
                        } else {
                            break a;
                        }
                        break;
                    default:
                        Log.i("default", "default");
                        break;
                }
                eventType = parser.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException ie) {
            ie.printStackTrace();
        }

    }

    public InputStream requestMileageInfo(String requestURL) {
        Log.i("xxx", "requestMileageInfo start");
        try {
            phoneNumber = this.getPhoneNumber();
            //1.
            HttpClient client = new DefaultHttpClient();
            Log.i("xxx", "111111111111111111111111111");
            Log.i("requestMileageInfo", requestURL);
            Log.i("phoneNum", phoneNumber);
            //폼데이터 저장
            List dataList = new ArrayList();
            dataList.add(new BasicNameValuePair("phoneNumber", phoneNumber));
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

    public String getPhoneNumber() {

        TelephonyManager mgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        phoneNumber = mgr.getLine1Number();
        return phoneNumber;
    }

    //main에서 버튼 선택시 Activity전환
    public void nv_OnClick1(View v){
        Intent intent = new Intent(this, MainActivity.class);
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
    public void order_OnClick(View v){
        Intent intent = new Intent(this, FavoriteActivity.class);
        intent.putExtra("choice", 2);
        startActivity(intent);
    }


    //카테고리별로 이동
    public void cafeBtn(View v){
        Intent intent = new Intent(this, StoreActivity.class);
        intent.putExtra("position", 0);
        startActivity(intent);
    }

    public void fastBtn(View v){
        Intent intent = new Intent(this, StoreActivity.class);
        intent.putExtra("position", 1);
        startActivity(intent);
    }

    public void meatBtn(View v){
        Intent intent = new Intent(this, StoreActivity.class);
        intent.putExtra("position", 2);
        startActivity(intent);
    }

    public void pizzBtn(View v){
        Intent intent = new Intent(this, StoreActivity.class);
        intent.putExtra("position", 3);
        startActivity(intent);
    }

    public void pastaBtn(View v){
        Intent intent = new Intent(this, StoreActivity.class);
        intent.putExtra("position", 4);
        startActivity(intent);
    }

    public void chkBtn(View v){
        Intent intent = new Intent(this, StoreActivity.class);
        intent.putExtra("position", 5);
        startActivity(intent);
    }

    public void krBtn(View v){
        Intent intent = new Intent(this, StoreActivity.class);
        intent.putExtra("position", 6);
        startActivity(intent);
    }

    public void cnBtn(View v){
        Intent intent = new Intent(this, StoreActivity.class);
        intent.putExtra("position", 7);
        startActivity(intent);
    }

    public void jpBtn(View v){
        Intent intent = new Intent(this, StoreActivity.class);
        intent.putExtra("position", 8);
        startActivity(intent);
    }



    //장바구니로 이동
    public void cartBtn(View v){
        int choice = 1;
        Intent intent = new Intent(this, CartActivity.class);
        intent.putExtra("choice", choice);
        startActivity(intent);
    }


}