package com.example.moonsoon.cbasdga.favorite;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.moonsoon.cbasdga.R;
import com.example.moonsoon.cbasdga.order.OrderDetailActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.StringTokenizer;

import vo.OrderVO;

/**
 * Created by moonsoon on 2015-09-16.
 */
public class Favorite_OrderedFragment extends Fragment {

    ListView ordered_list;

    CustomAdapter oAdapter = null;
    ArrayList<OrderVO> o_Contents = new ArrayList<OrderVO>();
    Context oCtx;
    TextView o_s_name;
    TextView o_s_tel;
    TextView o_s_addr;
    TextView o_s_date;
    TextView o_s_price;

    String phoneNumber;
    String getTime;
    String oriTime;

    private ArrayList<OrderVO> getJson;

    public static Favorite_OrderedFragment newInstance() {
        Favorite_OrderedFragment fragment = new Favorite_OrderedFragment();
        return fragment;
    }

    public Favorite_OrderedFragment() {

    // Required empty public constructor
    }

    public interface OnSelectedLinstener{
        public void onDialogSelected();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        phoneNumber = getPhoneNumber();

        oCtx = getActivity();

        o_Contents = new ArrayList<OrderVO>();
        oAdapter = new CustomAdapter(o_Contents);
    }
    public String getPhoneNumber(){
        TelephonyManager mgr = (TelephonyManager)getActivity().getSystemService(Context.TELEPHONY_SERVICE);
        return mgr.getLine1Number();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.favorite_ordered_fragment, container, false);
        ordered_list = (ListView)view.findViewById(R.id.f_o_list);
        o_s_name = (TextView)view.findViewById(R.id.o_s_name);
        o_s_tel = (TextView)view.findViewById(R.id.o_s_tel);
        o_s_addr = (TextView)view.findViewById(R.id.o_s_addr);
        o_s_date = (TextView)view.findViewById(R.id.o_s_date);

        ordered_list.setAdapter(oAdapter);

        ordered_list.setOnItemClickListener(new ListView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                                                    //OrderDetailActivity.class
                Intent intent = new Intent(getActivity().getBaseContext().getApplicationContext(), OrderDetailActivity.class);
                intent.putExtra("orderDate", o_Contents.get(position).getOrder_date());
                intent.putExtra("orderId", phoneNumber);
                startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        OrderTask ot = new OrderTask();
        ot.execute();
    }

    @Override
    public void onPause() {
        super.onPause();

        o_Contents.clear();
    }

    private class CustomAdapter extends BaseAdapter{
        ArrayList<OrderVO> oItems;

        public CustomAdapter(ArrayList<OrderVO> oItems) {
            this.oItems = oItems;
        }

        @Override
        public int getCount() {
            return oItems.size();
        }

        @Override
        public Object getItem(int position) {
            return oItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;

            ViewHolder holder = null;
            LayoutInflater oInflater = ((AppCompatActivity)oCtx).getLayoutInflater();

            if(convertView == null){
                holder = new ViewHolder();
                v = (View)oInflater.inflate(R.layout.favorite_ordered_item, null);

                holder.txt_o_s_name = (TextView)v.findViewById(R.id.o_s_name);
                holder.txt_o_s_tel = (TextView)v.findViewById(R.id.o_s_tel);
                holder.txt_o_s_addr = (TextView)v.findViewById(R.id.o_s_addr);
                holder.txt_o_s_date = (TextView)v.findViewById(R.id.o_s_date);

                v.setTag(holder);
            }else{
                holder = (ViewHolder)v.getTag();
            }

            holder.txt_o_s_name.setText(oItems.get(position).getStore_name());
            holder.txt_o_s_tel.setText(oItems.get(position).getStore_tel());
            holder.txt_o_s_addr.setText(oItems.get(position).getStore_addr());

            StringTokenizer stz = new StringTokenizer(oItems.get(position).getOrder_date(), "-");
            String token = "";
            while(stz.hasMoreTokens()){
                token = stz.nextToken();
                token = stz.nextToken() + "월 ";
                token += stz.nextToken() + "일 ";
                token += stz.nextToken() + "시 ";
                token += stz.nextToken() + "분";
                stz.nextToken();
            }

            holder.txt_o_s_date.setText(token);

            return v;
        }

        private class ViewHolder{
            TextView txt_o_s_name;
            TextView txt_o_s_tel;
            TextView txt_o_s_addr;
            TextView txt_o_s_date;
        }
    }

    private class OrderTask extends AsyncTask<Void, Void, Boolean> {
        String sResult;

        @Override
        protected Boolean doInBackground(Void... params) {
            String respond;
            int count = 0;
            JSONObject json = null;

            try {
                String body = "mylist="+phoneNumber;
                URL u = new URL("http://192.168.0.3/proj_1/android/myorderlist.jsp");
                HttpURLConnection huc = (HttpURLConnection)u.openConnection();
                huc.setReadTimeout(4000);
                huc.setConnectTimeout(4000);
                huc.setRequestMethod("POST");
                huc.setDoInput(true);
                huc.setDoOutput(true);
                huc.setRequestProperty("utf-8", "application/x-www-form-urlencoded");
                OutputStream os = huc.getOutputStream();
                os.write(body.getBytes("utf-8"));
                os.flush();
                os.close();

                BufferedReader is = new BufferedReader(new InputStreamReader(huc.getInputStream(), "UTF-8"));
                int ch;
                StringBuffer sb = new StringBuffer();
                while((ch = is.read()) != -1){
                    sb.append((char) ch);
                }
                if(is != null){
                    is.close();
                }

                sResult = sb.toString();
                Log.d("fResult = ", sResult);

                json = new JSONObject(sResult);
                JSONArray jArr = json.getJSONArray("myorderlist");
                count = jArr.length();


                for(int i = 0; i < jArr.length(); i++){
                    OrderVO orderVO = new OrderVO();
                    json = jArr.getJSONObject(i);
                    orderVO.setStore_name(json.getString("store_name"));
                    orderVO.setStore_addr(json.getString("store_addr"));
                    orderVO.setStore_tel(json.getString("store_tel"));
                    orderVO.setOrder_price(json.getInt("order_price"));
                    orderVO.setStore_num(json.getInt("store_num"));

                    oriTime = json.getString("order_date");

                    orderVO.setOrder_date(oriTime);

                    o_Contents.add(orderVO);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            return true;
        }
        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            if(aBoolean){
//                Toast.makeText(getView().getContext(), "데이터 로드 성공", Toast.LENGTH_SHORT).show();
                oAdapter.notifyDataSetChanged();

            }else {
                //실패 햇을 경우
//                Toast.makeText(getView().getContext(), "데이터 로드 실패", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
