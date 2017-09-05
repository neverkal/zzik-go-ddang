package com.example.moonsoon.cbasdga.order;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.moonsoon.cbasdga.R;
import com.example.moonsoon.cbasdga.store.StoreDetailActivity;

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
import java.util.StringTokenizer;

import vo.OrderVO;

public class OrderDetailActivity extends AppCompatActivity {

    ListView lv_OInfo = null;
    Context mCtx;
    CustomAdapter mAdapter = null;
    ArrayList<OrderVO> m_Contents;

    TextView oinfo_sumPrice;
    TextView oinfo_date;
    TextView oinfo_req;

    String getTime;
    String getPhone;

    int sumPrice;
    int store_num;

    String imgUrl = "http://192.168.0.3/proj_1/upload/";
    URL url = null;
    Bitmap bmimg;

    Button storeView_btn;
    Button buy_btn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favorite_ordered_detail);

        oinfo_sumPrice = (TextView)findViewById(R.id.c_SumPrice);
        oinfo_date = (TextView)findViewById(R.id.o_time);
        oinfo_req = (TextView)findViewById(R.id.oinfo_req);
        storeView_btn = (Button)findViewById(R.id.c_addmenu);
        buy_btn = (Button)findViewById(R.id.c_order);

        lv_OInfo = (ListView) findViewById(R.id.lv_orderinfo);
        mCtx = this;
        m_Contents = new ArrayList<OrderVO>();

        mAdapter = new CustomAdapter(m_Contents);
        lv_OInfo.setAdapter(mAdapter);

        final Intent intent = getIntent();
        getTime = intent.getExtras().getString("orderDate");
        getPhone = intent.getExtras().getString("orderId");
        Log.d("getTime", getTime);
        Log.d("getPhone", getPhone);

        storeView_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addInt = new Intent(getBaseContext().getApplicationContext(), StoreDetailActivity.class);
                addInt.putExtra("store_num", store_num);
                addInt.putExtra("choice", 2);
                startActivity(addInt);
            }
        });

        buy_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(getBaseContext().getApplicationContext(), MyOrderActivity.class);
                intent1.putExtra("test", 4);
                intent1.putParcelableArrayListExtra("array", m_Contents);
                for(int i=0; i<m_Contents.size(); i++){
                    Log.d("detail", m_Contents.get(i).getOrder_menu());
                }
                startActivity(intent1);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        OInfoTask oInfoTask = new OInfoTask();
        oInfoTask.execute();
    }

    @Override
    protected void onPause() {
        super.onPause();
        m_Contents.clear();
        sumPrice = 0;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private class CustomAdapter extends BaseAdapter {

        ArrayList<OrderVO> mItems;

        public CustomAdapter(ArrayList<OrderVO> mContents) {
            this.mItems = mContents;
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
            LayoutInflater mInflater = ((AppCompatActivity) mCtx).getLayoutInflater();

            if (convertView == null) {
                holder = new ViewHolder();
                v = (View) mInflater.inflate(R.layout.favorite_ordered_detail_item, null);

                holder.txt_name = (TextView) v.findViewById(R.id.odi_name);
                holder.txt_pay = (TextView) v.findViewById(R.id.odi_price);
                holder.txt_amount = (TextView)v.findViewById(R.id.odi_amount);
                holder.img_photo = (ImageView) v.findViewById(R.id.odi_photo);

                v.setTag(holder);
            } else {
                holder = (ViewHolder) v.getTag();
            }

            holder.txt_name.setText(mItems.get(position).getOrder_menu());
            holder.txt_pay.setText("" + mItems.get(position).getOrder_price());
            holder.txt_amount.setText(""+mItems.get(position).getOrder_amount());
            holder.img_photo.setImageBitmap(mItems.get(position).getMenu_jpg());

            return v;
        }
    }

    private class ViewHolder {
        TextView txt_name;
        TextView txt_pay;
        TextView txt_amount;
        ImageView img_photo;
    }

    private class OInfoTask extends AsyncTask<Void, Void, Boolean> {

        String sResult;

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                String body = "orderid="+getPhone+"&ordertime="+getTime;
                Log.d("body =", body);
                URL u = new URL("http://192.168.0.3/proj_1/android/myorder.jsp");
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

                BufferedReader is = new BufferedReader(new InputStreamReader(huc.getInputStream(), "UTF-8"));
                int ch;
                StringBuffer sb = new StringBuffer();
                while ((ch = is.read()) != -1) {
                    sb.append((char) ch);
                }
                if (is != null) {
                    is.close();
                }

                sResult = sb.toString();

                Log.d("ordersResult = ", sResult);

                Object jobj = JSONValue.parse(sResult);
                JSONObject json = (JSONObject) jobj;
                JSONArray jarr = (JSONArray) json.get("myorder");

                for(int i=0; i<jarr.size(); i++){
                    JSONObject jj =(JSONObject)jarr.get(i);

                    OrderVO order_manVO = new OrderVO();
                    order_manVO.setOrder_menu(jj.get("order_menu").toString());
                    order_manVO.setOrder_date(jj.get("order_date").toString());
                    order_manVO.setOrder_price(Integer.parseInt(jj.get("order_price").toString()));
                    order_manVO.setMenu_photo(jj.get("menu_photo").toString());
                    order_manVO.setOrder_req(jj.get("order_req").toString());
                    order_manVO.setOrder_amount(Integer.parseInt(jj.get("order_amount").toString()));
                    order_manVO.setOrder_id(jj.get("order_id").toString());
                    order_manVO.setStore_num(Integer.parseInt(jj.get("store_num").toString()));

                    Log.d("order_manVO", order_manVO.toString());

                    try{
                        url = new URL(imgUrl+order_manVO.getMenu_photo());
                        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                        conn.setDoInput(true);
                        conn.connect();

                        InputStream input = conn.getInputStream();

                        bmimg = BitmapFactory.decodeStream(input);

                    }catch (IOException e){
                        e.printStackTrace();
                    }

                    order_manVO.setMenu_jpg(getCroppedBitmap(bmimg));

                    sumPrice += Integer.parseInt(jj.get("order_price").toString()) * Integer.parseInt(jj.get("order_amount").toString());

                    m_Contents.add(order_manVO);

                    Log.d("컨텐가게번호", String.valueOf(m_Contents.get(i).getStore_num()));
                    Log.d("컨텐가격", String.valueOf(m_Contents.get(i).getOrder_price()));

                }


            } catch (IOException e) {
                e.printStackTrace();
            }

            return true;

        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            if (aBoolean) {
//                Toast.makeText(mCtx, "데이터 로드 성공", Toast.LENGTH_SHORT).show();
                mAdapter.notifyDataSetChanged();
                reFresh();
            } else {
                //실패 햇을 경우
//                Toast.makeText(mCtx, "데이터 로드 실패", Toast.LENGTH_SHORT).show();
            }
        }

    }

    public void reFresh(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                StringTokenizer stz = new StringTokenizer(m_Contents.get(0).getOrder_date(), "-");
                String token = "";
                while(stz.hasMoreTokens()){
                    token = stz.nextToken();
                    token = stz.nextToken() + "월 ";
                    token += stz.nextToken() + "일 ";
                    token += stz.nextToken() + "시 ";
                    token += stz.nextToken() + "분";
                    stz.nextToken();
                }
                oinfo_req.setText(m_Contents.get(0).getOrder_req());
                oinfo_date.setText(token);
                oinfo_sumPrice.setText("" + sumPrice);
                store_num = m_Contents.get(0).getStore_num();
            }
        });
    }

    public Bitmap getCroppedBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        //Bitmap _bmp = Bitmap.createScaledBitmap(output, 60, 60, false);
        //return _bmp;
        return output;
    }

}
