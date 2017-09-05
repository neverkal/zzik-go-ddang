package com.example.moonsoon.cbasdga.menu;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.moonsoon.cbasdga.R;
import com.example.moonsoon.cbasdga.store.StoreDetailActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import vo.MenuVO;
import vo.StoreVO;

import static com.google.android.gms.internal.zzid.runOnUiThread;

/**
 * Created by moonsoon on 2015-09-15.
 */
public class MenuListFragment extends Fragment {

    String imgUrl = "http://192.168.0.3/proj_1/upload/";
    URL url = null;
    Bitmap bmimg;

    ListView lv_test;
    CustomAdapter mAdapter = null;
    ArrayList<MenuVO> m_contents = new ArrayList<MenuVO>();
    Context mCtx;
    TextView m_name;
    ImageView m_photo;
    TextView m_price;

    //String store_name;
    int store_num;
    String store_name;
    String store_tel;
    String store_addr;
    String store_time;


    private ArrayList<MenuVO> getJson;

    public static MenuListFragment newInstance(){
        MenuListFragment fragment = new MenuListFragment();
        return fragment;
    }
    public MenuListFragment(){

    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("Tag", "MenuListFragment");
    }


    @Override
    public void onResume() {
        super.onResume();
        m_contents.clear();
    }

    public interface OnSelecttedListener{
        public void onDialogSelected();
    }

    @Override
    public void onDestroyView() {
        m_contents.clear();
        super.onDestroy();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);;

        mCtx = getActivity();

        m_contents = new ArrayList<MenuVO>();
        mAdapter = new CustomAdapter(m_contents);

        Bundle ext = getArguments();

        if(ext.getInt("choice") == 1){
            store_num = ext.getInt("store_num");
            store_name = ext.getString("store_name");

            MenuTask mt = new MenuTask();
            mt.execute();
        }else if(ext.getInt("choice") == 2){
           store_num = ext.getInt("store_num");

            MenuTask mt = new MenuTask();
            mt.execute();

            StoreTask storeTask = new StoreTask();
            storeTask.execute();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.menu_list_frag, container,false);
        lv_test = (ListView)view.findViewById(R.id.lv_test);
        m_name = (TextView)view.findViewById(R.id.m_name);
        m_price = (TextView)view.findViewById(R.id.m_price);
        m_photo = (ImageView)view.findViewById(R.id.m_photo);

        lv_test.setAdapter(mAdapter);

        lv_test.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(mCtx.getApplicationContext(), MenuInfoActivity.class);
                MenuVO menuVO = new MenuVO();
                menuVO = m_contents.get(position);
                Log.d("menovo = ", menuVO.toString());
                Log.d("storename = ", store_name);
                intent.putExtra("menuVO", menuVO);
                intent.putExtra("store_name", store_name);
                intent.putExtra("store_num", store_num);
                getActivity().startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        m_contents.clear();
    }

    private class CustomAdapter extends BaseAdapter{

        ArrayList<MenuVO> mItems;

//        public void refreshAdapter(ArrayList<MenuVO> mItems){
//            this.mItems.clear();
//            this.mItems.addAll(mItems);
//            notifyDataSetChanged();
//        }

        public CustomAdapter(ArrayList<MenuVO> mContents){
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
        public View getView(final int position, View convertView, ViewGroup parent) {

            View v = convertView;

            ViewHolder holder = null;
            LayoutInflater mInflater = ((AppCompatActivity)mCtx).getLayoutInflater();

            if(convertView == null){
                holder = new ViewHolder();
                v = (View)mInflater.inflate(R.layout.menu_list_item, null);

                holder.img_photo = (ImageView)v.findViewById(R.id.m_photo);
                holder.txt_name = (TextView)v.findViewById(R.id.m_name);
                holder.txt_price = (TextView)v.findViewById(R.id.m_price);

                v.setTag(holder);

            }else{
                holder = (ViewHolder)v.getTag();
            }

            holder.img_photo.setImageBitmap(mItems.get(position).getMenu_jpg());
            holder.txt_name.setText(mItems.get(position).getMenu_name());
            holder.txt_price.setText("" + mItems.get(position).getMenu_price());

            return v;
        }

        private class ViewHolder{
            ImageView img_photo;
            TextView txt_name;
            TextView txt_price;
        }
    }

    private class MenuTask extends AsyncTask<Void, Void, Boolean>{

        String sResult;
        ArrayList<MenuVO> tempContents = new ArrayList<MenuVO>();

        @Override
        protected Boolean doInBackground(Void... params) {
            String respond;
            int count = 0 ;
            JSONObject json = null;

            try{
                String body = "store_num="+String.valueOf(store_num);
                URL u = new URL("http://192.168.0.3/proj_1/android/menulist.jsp");
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

                json = new JSONObject(sResult);
                JSONArray jArr = json.getJSONArray("menulist");
                count = jArr.length();

                tempContents.clear();

                for(int i=0; i<jArr.length(); i++){
                    MenuVO menuVO = new MenuVO();
                    json = jArr.getJSONObject(i);
                    menuVO.setMenu_name(json.getString("menu_name"));
                    menuVO.setMenu_price(json.getInt("menu_price"));
                    menuVO.setStore_num(json.getInt("store_num"));
                    menuVO.setMenu_photo(json.getString("menu_photo"));
                    menuVO.setMenu_info(json.getString("menu_info"));

                    Log.d("menuVO", menuVO.toString());

                    try{
                        url = new URL(imgUrl+menuVO.getMenu_photo());
                        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                        conn.setDoInput(true);
                        conn.connect();

                        InputStream input = conn.getInputStream();

                        bmimg = BitmapFactory.decodeStream(input);

                    }catch (IOException e){
                        e.printStackTrace();
                    }

                    menuVO.setMenu_jpg(getCroppedBitmap(bmimg));

                    tempContents.add(menuVO);
                }

            } catch (Exception e) {
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            if(aBoolean){
                //Toast.makeText(getActivity().getApplicationContext(), "데이터 로드 성공", Toast.LENGTH_SHORT).show();
//                mAdapter.notifyDataSetChanged();
//                mAdapter.refreshAdapter(m_contents);
                m_contents.addAll(tempContents);
                reFresh();
            }else {
                //실패 햇을 경우
                //Toast.makeText(getActivity().getApplicationContext(), "데이터 로드 실패", Toast.LENGTH_SHORT).show();
            }
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

                    store_name = storeVO.getStore_name();

                    Log.d("mtask_store_namee", store_name);

                }

            } catch (Exception e) {
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            if(aBoolean){
//                Toast.makeText(getView().getContext(), "데이터 로드 성공", Toast.LENGTH_SHORT).show();
//                mAdapter.refreshAdapter(m_contents);
                reFresh();
            }else {
                //실패 햇을 경우Toast.makeText(getView().getContext(), "데이터 로드 실패", Toast.LENGTH_SHORT).show();
//
            }
        }
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

    public void reFresh(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                mAdapter.notifyDataSetChanged();
            }
        });
    }
}
