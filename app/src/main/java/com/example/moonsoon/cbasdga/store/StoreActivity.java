package com.example.moonsoon.cbasdga.store;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.moonsoon.cbasdga.R;
import com.example.moonsoon.cbasdga.order.CartActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;

import vo.StoreVO;

/**
 * Created by moonsoon on 2015-09-14.
 */
public class StoreActivity extends AppCompatActivity implements ActionBar.TabListener{
    SectionsPagerAdapter mSectionsPagerAdapter;
    ViewPager mViewPager = null;
    PlaceholderFragment pFragment = null;

    int choice;
    int getint;
    public static int fav_cnt;

    Context mCtx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.store_main);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();

        Log.d("position = ", String.valueOf(getint));


        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mCtx = this;

        mViewPager.setAdapter(mSectionsPagerAdapter);

        try{
            getint = intent.getExtras().getInt("position");
            Log.d("getint = ", String.valueOf(getint));
        }catch (NullPointerException e){
            e.printStackTrace();
        }

        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                    actionBar.setSelectedNavigationItem(position);
            }
        });

        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++){
            actionBar.addTab(
                    actionBar.newTab().setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }

           mViewPager.setCurrentItem(getint);
    }


    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavUtils.navigateUpFromSameTask(this);
        return true;
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }
    //actionbar button start
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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
    //actionbar button end
    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            Log.d("position = ", String.valueOf(position));

            switch (position) {
                case 0:
                    return PlaceholderFragment.newInstance(position + 1);
                case 1:
                    return PlaceholderFragment.newInstance(position + 1);
                case 2:
                    return PlaceholderFragment.newInstance(position + 1);
                case 3:
                    return PlaceholderFragment.newInstance(position + 1);
                case 4:
                    return PlaceholderFragment.newInstance(position + 1);
                case 5:
                    return PlaceholderFragment.newInstance(position + 1);
                case 6:
                    return PlaceholderFragment.newInstance(position + 1);
                case 7:
                    return PlaceholderFragment.newInstance(position + 1);
                case 8:
                    return PlaceholderFragment.newInstance(position + 1);
                default:
                    return PlaceholderFragment.newInstance(position + 1);
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 9;
        }

        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
                case 2:
                    return getString(R.string.title_section3).toUpperCase(l);
                case 3:
                    return getString(R.string.title_section4).toUpperCase(l);
                case 4:
                    return getString(R.string.title_section5).toUpperCase(l);
                case 5:
                    return getString(R.string.title_section6).toUpperCase(l);
                case 6:
                    return getString(R.string.title_section7).toUpperCase(l);
                case 7:
                    return getString(R.string.title_section8).toUpperCase(l);
                case 8:
                    return getString(R.string.title_section9).toUpperCase(l);
            }
            return null;
        }
    }

    public static class PlaceholderFragment extends Fragment {
        private static final String ARG_SECTION_NUMBER = "section_number";

        ListView lv_category;
        Context mCtx = null;
        CustomAdapter mStoreAdapter = null;
        ArrayList<StoreVO> mStoreList = null;

        String getCatename;
        int getNumber;

        String store_name;

        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            mCtx = getActivity();
            mStoreList = new ArrayList<StoreVO>();
            mStoreAdapter = new CustomAdapter(mStoreList);

            getNumber = getArguments().getInt(ARG_SECTION_NUMBER);
            getCatename = getCategory(getNumber);
        }

        @Override
        public void onAttach(Context context) {
            super.onAttach(context);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.activity_category_result, container, false);

            lv_category = (ListView)rootView.findViewById(R.id.lv_category);
            lv_category.setAdapter(mStoreAdapter);

            lv_category.setOnItemClickListener(new ListView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    store_name = mStoreList.get(position).getStore_name().toString();
                    Intent intent = new Intent(mCtx.getApplicationContext(), StoreDetailActivity.class);
                    intent.putExtra("choice", 1);
                    intent.putExtra("store_name", store_name);
                    intent.putExtra("store_num", mStoreList.get(position).getStore_num());
                    intent.putExtra("store_tel", mStoreList.get(position).getStore_tel());
                    intent.putExtra("store_addr", mStoreList.get(position).getStore_addr());
                    intent.putExtra("store_time", mStoreList.get(position).getStore_time());
                    startActivity(intent);
                }
            });

            mStoreAdapter.notifyDataSetChanged();

            return rootView;
        }

        @Override
        public void onStart() {
            super.onStart();
            CategoryTask categoryTask = new CategoryTask();
            categoryTask.execute();
        }

        @Override
        public void onPause() {
            super.onPause();
            mStoreList.clear();
        }

        private class CustomAdapter extends BaseAdapter{

            ArrayList<StoreVO> mItems;

            public CustomAdapter(ArrayList<StoreVO> mContents){
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

                if(convertView == null){
                    holder = new ViewHolder();
                    LayoutInflater li = (LayoutInflater) mCtx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    v = li.inflate(R.layout.category_items, null, true);

                    holder.txt_name = (TextView)v.findViewById(R.id.c_name);
                    holder.txt_location = (TextView)v.findViewById(R.id.location);
//                    holder.txt_count = (TextView)v.findViewById(R.id.c_cnt);
                    holder.ratingBar = (RatingBar)v.findViewById(R.id.rating);

                    v.setTag(holder);

                }else{
                    holder = (ViewHolder) v.getTag();
                }

                holder.txt_name.setText(mItems.get(position).getStore_name());
                holder.txt_location.setText(mItems.get(position).getStore_addr());

                fav_cnt = mItems.get(position).getFav_cnt();

                Log.d("fav_cnt", ""+fav_cnt);

                if( (0<fav_cnt) && (fav_cnt<=10) ){
                    holder.ratingBar.setRating((float)1);
                }else if( (11<=fav_cnt) && (fav_cnt<=20) ){
                    holder.ratingBar.setRating((float)2);
                }else if( (21<=fav_cnt) && (fav_cnt<=30) ) {
                    holder.ratingBar.setRating((float) 3);
                }else if( (31<=fav_cnt) && (fav_cnt<=40) ) {
                    holder.ratingBar.setRating((float) 4);
                }else if( (41<=fav_cnt) ) {
                    holder.ratingBar.setRating((float) 5);
                }

                holder.ratingBar.setIsIndicator(true);
//                holder.txt_count.setText(""+mItems.get(position).getFav_cnt());

                return v;
            }

            private class ViewHolder{
                TextView txt_name;
                TextView txt_location;
//                TextView txt_count;
                RatingBar ratingBar;
            }
        }

        private class CategoryTask extends AsyncTask<Void, Void, Boolean> {

            String sResult;

            @Override
            protected Boolean doInBackground(Void... params) {
                JSONObject json = null;

                Log.d("Category =", "진입");

                try {
                    String body = "category="+getCatename;
                    URL u = new URL("http://192.168.0.3/proj_1/android/storelist.jsp");
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
                        storeVO.setFav_cnt(json.getInt("fav_cnt"));

                        Log.d("Cstorenumber", String.valueOf(storeVO.getFav_cnt()));

                        mStoreList.add(storeVO);
                    }

                } catch (Exception e) {
                }

                return true;
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);

                if(aBoolean){
//                    Toast.makeText(mCtx, "데이터 로드 성공", Toast.LENGTH_SHORT).show();
                    mStoreAdapter.notifyDataSetChanged();

                }else {
                    //실패 햇을 경우
//                    Toast.makeText(mCtx, "데이터 로드 실패", Toast.LENGTH_SHORT).show();
                }
            }
        }

        public String getCategory(int position){

            switch (position){
                case 1:
                    return "카페";
                case 2:
                    return "패스트푸드";
                case 3:
                    return "고기";
                case 4:
                    return "피자";
                case 5:
                    return "파스타";
                case 6:
                    return "치킨";
                case 7:
                    return "한식";
                case 8:
                    return "중식";
                case 9:
                    return "일식";
                default:
                    return "카페";
            }
        }

    }

    public void im_OnClick(View v){
        Intent intent = new Intent(this, StoreDetailActivity.class);
        startActivity(intent);
    }
}

