package com.example.moonsoon.cbasdga.favorite;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.moonsoon.cbasdga.R;
import com.example.moonsoon.cbasdga.store.StoreDetailActivity;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import vo.FavoriteVO;

/**
 * Created by moonsoon on 2015-09-17.
 */
public class Favorite_MainFragment extends Fragment {

    ListView favorite_list;

    ArrayList<FavoriteVO> f_Contents = new ArrayList<FavoriteVO>();
    CustomAdapter fAdapter  = null;

    Context fCtx;
    TextView f_s_name;
    TextView f_s_tel;
    TextView f_s_addr;
    TextView f_s_count;
    TextView f_s_time;

    String phoneNumber;
    String store_name;
    String del_store_name;
    int store_num;

    private ArrayList<FavoriteVO> getJson;

    public static Favorite_MainFragment newInstance() {
        Favorite_MainFragment fragment = new Favorite_MainFragment();
        return fragment;
    }

    public Favorite_MainFragment() {
        // Required empty public constructor
    }

    public interface OnSelectedLinstener{
        public void onDialogSelected();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        phoneNumber = getPhoneNumber();
        fCtx = getActivity();

        f_Contents = new ArrayList<FavoriteVO>();
        fAdapter = new CustomAdapter(f_Contents);
    }

    public String getPhoneNumber(){
        TelephonyManager mgr = (TelephonyManager)getActivity().getSystemService(Context.TELEPHONY_SERVICE);
        return mgr.getLine1Number();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.favorite_favorite_fragment, container, false);
        favorite_list = (ListView)view.findViewById(R.id.f_f_list);
        f_s_name = (TextView)view.findViewById(R.id.f_s_name);
        f_s_tel = (TextView)view.findViewById(R.id.f_s_tel);
        f_s_addr = (TextView)view.findViewById(R.id.f_s_addr);
        f_s_count = (TextView)view.findViewById(R.id.f_s_count);
        f_s_time = (TextView)view.findViewById(R.id.f_s_time);

        favorite_list.setAdapter(fAdapter);

        //setOnItemClickListener

        favorite_list.setOnItemClickListener(new ListView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("리스너", "");
                store_name = f_Contents.get(position).getStore_name().toString();
                Intent intent = new Intent(fCtx.getApplicationContext(), StoreDetailActivity.class);
                intent.putExtra("store_name", store_name);
                intent.putExtra("store_num", f_Contents.get(position).getStore_num());
                intent.putExtra("store_tel", f_Contents.get(position).getStore_tel());
                intent.putExtra("store_addr", f_Contents.get(position).getStore_addr());
                intent.putExtra("store_time", f_Contents.get(position).getStore_time());
                intent.putExtra("choice", 1);
                startActivity(intent);
            }
        });

//        favorite_list.setOnItemLongClickListener(new ListView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
//
//                AlertDialog.Builder builder = new AlertDialog.Builder(fCtx);
//                builder.setTitle("즐겨찾기를 삭제")
//                        .setMessage("즐겨찾기를 삭제하시겠습니까?")
//                        .setCancelable(false)
//                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                store_num = f_Contents.get(position).getStore_num();
//                                new DeleteNearFavinfoTask().execute();
//                                f_Contents.remove(position);
//                                fAdapter.notifyDataSetChanged();
//                            }
//                        })
//                        .setNegativeButton("취소", new DialogInterface.OnClickListener(){
//                            // 취소 버튼 클릭시 설정
//                            public void onClick(DialogInterface dialog, int whichButton){
//                                dialog.cancel();
//                            }
//                        });
//                return false;
//            }
//        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        FavoriteTask ft = new FavoriteTask();
        ft.execute();
    }

    @Override
    public void onPause() {
        super.onPause();
        f_Contents.clear();
    }

    private class CustomAdapter extends BaseAdapter{
        ArrayList<FavoriteVO> fItems;

        public CustomAdapter(ArrayList<FavoriteVO> fItems){
            this.fItems = fItems;
        }

        @Override
        public int getCount() {
            return fItems.size();
        }

        @Override
        public Object getItem(int position) {
            return fItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View v = convertView;

            ViewHolder holder = null;

            LayoutInflater fInflater = ((AppCompatActivity)fCtx).getLayoutInflater();

            if(convertView == null){
                holder = new ViewHolder();
                v = (View)fInflater.inflate(R.layout.favorite_favorite_item, null);

                holder.txt_f_s_addr = (TextView)v.findViewById(R.id.f_s_addr);
                holder.txt_f_s_tel = (TextView)v.findViewById(R.id.f_s_tel);
                holder.txt_f_s_name = (TextView)v.findViewById(R.id.f_s_name);
                holder.txt_f_s_count = (TextView)v.findViewById(R.id.f_s_count);
                holder.txt_f_s_time = (TextView)v.findViewById(R.id.f_s_time);
                holder.btn_del = (Button)v.findViewById(R.id.btn_del);

                v.setTag(holder);
            }else{
                holder = (ViewHolder)v.getTag();
            }

            holder.txt_f_s_addr.setText(fItems.get(position).getStore_addr());
            holder.txt_f_s_tel.setText(fItems.get(position).getStore_tel());
            holder.txt_f_s_name.setText(fItems.get(position).getStore_name());
            holder.txt_f_s_count.setText(""+fItems.get(position).getStore_count());
            holder.txt_f_s_time.setText(fItems.get(position).getStore_time());
            holder.btn_del.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    store_num = f_Contents.get(position).getStore_num();
                    new DeleteNearFavinfoTask().execute();
                    f_Contents.remove(position);
                    notifyDataSetChanged();
                }
            });
            holder.btn_del.setFocusable(false);

            return v;
        }

        private class ViewHolder{
            TextView txt_f_s_name;
            TextView txt_f_s_tel;
            TextView txt_f_s_addr;
            TextView txt_f_s_count;
            TextView txt_f_s_time;
            Button btn_del;
        }
    }

    private class FavoriteTask extends AsyncTask<Void, Void, Boolean>{
        String fResult;

        @Override
        protected Boolean doInBackground(Void... params) {
            String respond;
            int count = 0;
            JSONObject json = null;

            try{
                String body = "cus_id="+phoneNumber;
                URL u = new URL("http://192.168.0.3/proj_1/android/favList.jsp");
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

                fResult = sb.toString();
                Log.d("fmResult = ", fResult);

                json = new JSONObject(fResult);
                JSONArray jArr = json.getJSONArray("fav_list");
                count = jArr.length();

                for(int i = 0; i < jArr.length(); i++){
                    FavoriteVO favoriteVO = new FavoriteVO();

                    json = jArr.getJSONObject(i);
                    favoriteVO.setStore_name(json.getString("store_name"));
                    favoriteVO.setStore_count(json.getInt("count"));
                    favoriteVO.setStore_addr(json.getString("store_addr"));
                    favoriteVO.setStore_tel(json.getString("store_tel"));
                    favoriteVO.setStore_time(json.getString("store_time"));
                    favoriteVO.setStore_num(json.getInt("store_num"));
                    f_Contents.add(favoriteVO);
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
                fAdapter.notifyDataSetChanged();

            }else {
                //실패 햇을 경우
//                Toast.makeText(getView().getContext(), "데이터 로드 실패", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class DeleteNearFavinfoTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                String requestURL = "http://192.168.0.3/proj_1/android/deleteNearFavList.jsp";
                InputStream is = requestdeleteNearFav(requestURL);
                if (is == null) {
                    Log.i("xxx", "is is null");
                }

                return true;
            }catch(Exception ex){
                Log.i("err", ex.getMessage());
                return false;
            }
        }
    }

    public InputStream requestdeleteNearFav(String requestURL) {

        Log.i("xxx", "requestGet start");
        try {

            //1.
            HttpClient client = new DefaultHttpClient();
            Log.i("xxx", "111111111111111111111111111");
            Log.i("requestURL", requestURL);

            List dataList = new ArrayList();
            dataList.add(new BasicNameValuePair("del_store_num", String.valueOf(store_num)));
            dataList.add(new BasicNameValuePair("del_phone_num", phoneNumber));
            requestURL = requestURL + "?" + URLEncodedUtils.format(dataList, "UTF-8");
            Log.i("requestURL222", requestURL);
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
}
