package com.example.moonsoon.cbasdga.order;

/**
 * Created by 618 on 2015-09-21.
 */

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.moonsoon.cbasdga.MainActivity;
import com.example.moonsoon.cbasdga.R;
import com.example.moonsoon.cbasdga.favorite.FavoriteActivity;
import com.example.moonsoon.cbasdga.myDB;
import com.example.moonsoon.cbasdga.store.StoreActivity;

import org.w3c.dom.Text;

import java.util.ArrayList;

import vo.CartVO;

public class CartActivity extends AppCompatActivity {

    DrawerLayout dlDrawer;
    ActionBarDrawerToggle dtToggle;

    ListView lvDrawerList;
    ArrayAdapter<String> adtDrawerList;
    String[] menuItems = new String[]{"홈", "매장 목록", "즐겨찾기"};

    Toolbar toolbar;


    ListView lv_cartlist = null;
    CustomAdapter mAdapter = null;
    ArrayList<CartVO> m_Contents = new ArrayList<CartVO>();
    ArrayList<Integer> rowid = new ArrayList<>();
    Context mCtx;
    myDB my;
    SQLiteDatabase sql;

    int sumPrice;
    int store_num;
    String store_name;

    String menu_name;
    int menu_price;
    int menu_amount;

    private TextView m_Name;
    private TextView m_price;
    private TextView m_SumPrice;

    int choice;
    int temp1 = 0;
    int temp2 = 0;
    int initvalue;
    boolean flag = false;
    boolean cFlag = false;

    Button menuPlus_btn;
    Button buy_btn;

    CartTask cartTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.cart_list);

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

        dlDrawer = (DrawerLayout) findViewById(R.id.dl_activity_cart);
        dtToggle = new ActionBarDrawerToggle(this, dlDrawer, toolbar,  R.string.app_name, R.string.app_name);
        dlDrawer.setDrawerListener(dtToggle);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        m_Name = (TextView) findViewById(R.id.cMenu_name);
        m_price = (TextView) findViewById(R.id.cMenu_price);
        m_SumPrice = (TextView) findViewById(R.id.c_SumPrice);
        lv_cartlist = (ListView) findViewById(R.id.lv_cart);

        menuPlus_btn = (Button) findViewById(R.id.c_addmenu);
        buy_btn = (Button) findViewById(R.id.c_order);

        final Intent cart = getIntent();
        choice = cart.getExtras().getInt("choice");

        if(choice == 2){
            store_name = cart.getExtras().getString("store_name");
            store_num = cart.getExtras().getInt("store_num");
        }

        mCtx = this;

        //ArrayList가 인자로 들어가게 해 놓음
        mAdapter = new CustomAdapter(m_Contents);
        lv_cartlist.setAdapter(mAdapter);

        my = new myDB(this);

        cartTask = new CartTask();
        cartTask.execute();

            menuPlus_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        Intent intent = new Intent(getBaseContext(), StoreActivity.class);
                        startActivity(intent);
                }
            });

            buy_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(m_Contents.size()==1){
                        flag = false;
                    }

                    if(sumPrice == 0){
                        Toast.makeText(getApplicationContext(), "주문에 대한 메뉴가 없습니다.", Toast.LENGTH_SHORT).show();
                    }else{
                        if(flag == true){
                            Toast.makeText(getApplicationContext(), "다른 매장의 메뉴를 동시에 주문하실 수 없습니다.", Toast.LENGTH_SHORT).show();
                        }else if(flag == false){
                            Intent intent = new Intent(getBaseContext(), BuyInfoActivity.class);
                            intent.putExtra("choice", 2);
                            intent.putExtra("store_name", store_name);
                            startActivity(intent);
                        }
                    }
                }
            });
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
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onStart() {

        super.onStart();
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mAdapter.notifyDataSetChanged();
    }

    private class CustomAdapter extends BaseAdapter {

        ArrayList<CartVO> mItems;

        public CustomAdapter(ArrayList<CartVO> mContents) {

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
            LayoutInflater mInflater = ((AppCompatActivity) mCtx).getLayoutInflater();

            if (convertView == null) {
                holder = new ViewHolder();
                v = (View) mInflater.inflate(R.layout.cart_list_items, null);

                holder.txt_storename = (TextView) v.findViewById(R.id.cStore_name);
                holder.txt_name = (TextView) v.findViewById(R.id.cMenu_name);
                holder.txt_price = (TextView) v.findViewById(R.id.cMenu_price);
                holder.txt_amount = (TextView) v.findViewById(R.id.c_amount);
                holder.btn_delete = (Button) v.findViewById(R.id.c_delmenu);
                holder.btn_ordermenu = (Button) v.findViewById(R.id.c_ordermenu);
                v.setTag(holder);

            } else {
                holder = (ViewHolder) v.getTag();
            }

            holder.txt_name.setText(mItems.get(position).getMenu_name());
            holder.txt_price.setText("" + (mItems.get(position).getMenu_price() * mItems.get(position).getMenu_amount()));
            holder.txt_amount.setText("" + mItems.get(position).getMenu_amount());
            holder.txt_storename.setText(mItems.get(position).getStore_name());
            holder.btn_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sumPrice -= mItems.get(position).getMenu_price() * mItems.get(position).getMenu_amount();
                    m_SumPrice.setText("" + sumPrice);
                    mItems.remove(position);
                    my.deleteCart(sql, rowid.get(position));
                    sql.close();
                    mAdapter.notifyDataSetChanged();
                    if(m_Contents.size() == 0){
                        flag = false;
                    }
                }
            });
            holder.btn_ordermenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getBaseContext(), BuyInfoActivity.class);
                    intent.putExtra("choice", 3);
                    intent.putExtra("cartvo", m_Contents.get(position));
                    startActivity(intent);
                }
            });

            return v;
        }

        private class ViewHolder {
            TextView txt_storename;
            TextView txt_name;
            TextView txt_price;
            TextView txt_amount;
            Button btn_delete;
            Button btn_ordermenu;
        }
    }


    private class CustomOnClickListener implements View.OnClickListener {

        CartVO mCartVo;

        public CustomOnClickListener(CartVO arg) {
            this.mCartVo = arg;
        }

        @Override
        public void onClick(View v) {
//            Toast.makeText(mCtx, "onClickTest", Toast.LENGTH_SHORT).show();
        }
    }

    private class CartTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                sql = my.getReadableDatabase();
                Cursor cursor;
                cursor = sql.rawQuery("SELECT rowid, menu_name, menu_price, menu_amount, store_name, store_num FROM CART;", null);

                while (cursor.moveToNext()) {

                    int cnt = 0;

                    CartVO cartVO = new CartVO();
                    cartVO.setMenu_name(cursor.getString(1));
                    cartVO.setMenu_price(cursor.getInt(2));
                    cartVO.setMenu_amount(cursor.getInt(3));
                    cartVO.setStore_name(cursor.getString(4));
                    cartVO.setStore_num(cursor.getInt(5));

                    Log.d("cartAmount = ", String.valueOf(cartVO.getMenu_amount()));
                    Log.d("cartPrice = ", String.valueOf(cartVO.getMenu_price()));

                    rowid.add(cursor.getInt(0));
                    Log.d("rowid = ", String.valueOf(cursor.getInt(0)));
                    initvalue = cartVO.getMenu_amount();

                    sumPrice += cartVO.getMenu_price()*initvalue;

                    Log.d("cartsumPrice = ", String.valueOf(sumPrice));

                    m_Contents.add(cartVO);

                    temp1 = cartVO.getStore_num();

                    if(temp2 == 0){
                        temp2 = cartVO.getStore_num();
                    }

                    if(flag == false){
                        if(temp1 == temp2){
                            temp2 = temp1;
                        }else if(temp1 != temp2){
                            flag = true;
                        }
                    }

                }
                Log.d("store_name =", store_name);

                cursor.close();
                sql.close();

            } catch (Exception e) {
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            mAdapter.notifyDataSetChanged();
            m_SumPrice.setText("" + sumPrice);
        }
    }

    public void nv_OnClick1(View v){
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
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
}
