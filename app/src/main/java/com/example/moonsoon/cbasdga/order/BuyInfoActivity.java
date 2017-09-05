package com.example.moonsoon.cbasdga.order;

/**
 * Created by 618 on 2015-10-01.
 */

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.moonsoon.cbasdga.MainActivity;
import com.example.moonsoon.cbasdga.R;
import com.example.moonsoon.cbasdga.myDB;
import com.google.android.gms.wallet.Cart;

import java.util.ArrayList;

import vo.CartVO;
import vo.MenuVO;

public class BuyInfoActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    ListView lv_test = null;
    CustomAdapter mAdapter = null;
    MenuVO m_MenuVO;
    ArrayList<CartVO> m_Contents = new ArrayList<>();
    Context mCtx;
    myDB my;
    SQLiteDatabase sql;
    CartVO t_Cart;

    ArrayList<String> spinlist;
    Button payButton;

    Intent intent2=null;

    private TextView buy_Price;
    private TextView buy_Name;
    private TextView buy_SumPrice;
    private EditText buy_Req;

    int sumPrice = 0;
    int choice = 0;
    int store_num;
    String phoneNumber;
    String store_name;
    String req;

    private int initialvalues;

    CartVO cartVO1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.buyinfo_list);

        buy_Price = (TextView) findViewById(R.id.buy_price);
        buy_Name = (TextView) findViewById(R.id.buy_name);
        buy_SumPrice = (TextView) findViewById(R.id.buy_sumprice);
        buy_Req = (EditText) findViewById(R.id.buy_req);

        lv_test = (ListView) findViewById(R.id.lv_buy);
        payButton = (Button) findViewById(R.id.buy_button);

        final Intent intent = getIntent();
        choice = intent.getExtras().getInt("choice");
        if(choice == 1 || choice == 2){
            m_MenuVO = (MenuVO)intent.getExtras().getParcelable("menuVO2");
            store_name = intent.getExtras().getString("store_name");
            initialvalues = intent.getExtras().getInt("initvalue");
            store_num = intent.getExtras().getInt("store_num");
        }else if(choice == 3){
            t_Cart = intent.getExtras().getParcelable("cartvo");

            store_num = t_Cart.getStore_num();
            store_name = t_Cart.getStore_name();
        }

        spinlist = new ArrayList<String>();
        spinlist.add("현금");
        spinlist.add("카드");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, spinlist);
        Spinner sp = (Spinner)this.findViewById(R.id.buy_spin);
        sp.setPrompt("결제선택");
        sp.setAdapter(adapter);
        sp.setOnItemSelectedListener(this);

        mCtx = this;

        //ArrayList가 인자로 들어가게 해 놓음
        mAdapter = new CustomAdapter(m_Contents);

        lv_test.setAdapter(mAdapter);

        my = new myDB(this);

        BuyTask buyTask = new BuyTask();
        buyTask.execute();

        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(mCtx, "결제는 준비중...", Toast.LENGTH_SHORT).show();
                req = buy_Req.getText().toString();
                intent2 = new Intent(getBaseContext().getApplicationContext(), MyOrderActivity.class);

                if(choice==1){
                    intent2.putExtra("cartvo1", cartVO1);
                }

                intent2.putExtra("require", req);
                intent2.putExtra("sumPrice", sumPrice);
                intent2.putExtra("store_num", store_num);
                intent2.putExtra("initvalue", initialvalues);
                intent2.putExtra("test", choice);

                if(choice == 3){
                    intent2.putExtra("cartvo", t_Cart);
                }
                Log.d("req =", req);
                startActivity(intent2);
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//        Toast.makeText(this, spinlist.get(position), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private class CustomAdapter extends BaseAdapter{

        ArrayList<CartVO> mItems;

        public CustomAdapter(ArrayList<CartVO> mContents){
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

        private class ViewHolder{
            TextView txt_name;
            TextView txt_price;
            TextView txt_amount;
        }
    }


    private class CustomOnClickListener implements View.OnClickListener{

        CartVO mCartVo;

        public CustomOnClickListener(CartVO arg){
            this.mCartVo = arg;
        }

        @Override
        public void onClick(View v) {
//            Toast.makeText(mCtx, "onClickTest", Toast.LENGTH_SHORT).show();
        }
    }

    private class BuyTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {

            try{
                if(choice == 1){
                    Log.d("초이스1", "진입");
                    cartVO1 = new CartVO();
                    cartVO1.setMenu_name(m_MenuVO.getMenu_name());
                    cartVO1.setMenu_price(m_MenuVO.getMenu_price());
                    cartVO1.setStore_name(store_name);
                    cartVO1.setStore_num(store_num);
                    cartVO1.setMenu_amount(initialvalues);

                    Log.d("menuname = ", m_MenuVO.getMenu_name());
                    Log.d("menuprice = ", String.valueOf(m_MenuVO.getMenu_price()));
                    Log.d("storename = ", store_name);
                    Log.d("init = ", String.valueOf(initialvalues));
                    Log.d("store_num = ", String.valueOf(store_num));


                    cartVO1.toString();

                    sumPrice += cartVO1.getMenu_price();

                    m_Contents.add(cartVO1);

//                    sql = my.getWritableDatabase();
//                    sql.delete("CART", null, null);
//
//                    sql = my.getWritableDatabase();
//                    sql.execSQL("INSERT INTO cart VALUES('" + m_MenuVO.getMenu_name() + "' ," + m_MenuVO.getMenu_price() + "," + initialvalues + ", '" + store_name + "', " + store_num + ");");
//
//                    sql.close();

                }else if(choice==2){
                    sql = my.getReadableDatabase();
                    Cursor cursor;
                    cursor = sql.rawQuery("SELECT * FROM CART;", null);

                    while (cursor.moveToNext()) {
                        CartVO cartVO = new CartVO();
                        cartVO.setMenu_name(cursor.getString(0));
                        cartVO.setMenu_price(cursor.getInt(1));
                        cartVO.setMenu_amount(cursor.getInt(2));
                        cartVO.setStore_name(cursor.getString(3));
                        cartVO.setStore_num(cursor.getInt(4));

                        sumPrice += cartVO.getMenu_price() * cartVO.getMenu_amount();
                        Log.d("sumPrice = ", String.valueOf(sumPrice));

                        m_Contents.add(cartVO);
                    }

                    Log.d("store_name =", store_name);

                    cursor.close();
                    sql.close();
                }else if(choice==3){
                    initialvalues = t_Cart.getMenu_amount();
                    sumPrice += t_Cart.getMenu_price() * t_Cart.getMenu_amount();

                    m_Contents.add(t_Cart);
                }

            } catch (Exception e) {
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            mAdapter.notifyDataSetChanged();
            if(choice == 1){
                sumPrice = sumPrice*initialvalues;
                buy_SumPrice.setText(""+sumPrice);
            }else if(choice == 2){
                buy_SumPrice.setText(""+sumPrice);
            }else if(choice == 3){
                buy_SumPrice.setText(""+sumPrice);
            }
        }

        public String getPhoneNumber(){
            TelephonyManager mgr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
            return  mgr.getLine1Number();
        }


    }

}
