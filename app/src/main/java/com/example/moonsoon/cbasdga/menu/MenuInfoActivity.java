package com.example.moonsoon.cbasdga.menu;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.moonsoon.cbasdga.R;
import com.example.moonsoon.cbasdga.myDB;
import com.example.moonsoon.cbasdga.order.BuyInfoActivity;
import com.example.moonsoon.cbasdga.order.CartActivity;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import vo.MenuVO;

/**
 * Created by 618 on 2015-08-27.
 */
public class MenuInfoActivity extends AppCompatActivity {

    private TextView m_Name;
    private TextView m_Price;
    private TextView m_Info;
    private ImageView m_Jpg;
    private MenuVO m_MenuVO;

    Button cartButton;
    Button buyButton;

    private EditText m_amount;
    private Button m_amountUpBtn;
    private Button m_amountDownBtn;

    String store_name;
    int store_num;
    int choice;

    private int max_range = 10;
    private int min_range = 1;
    private int initialvalues = 1;

    private int pSum;
    private int flag = 0;

    myDB my;
    SQLiteDatabase sql;

    //이미지 관리 필요 변수
    String imgUrl = "http://192.168.0.3/proj_1/upload/";
    URL url = null;
    Bitmap bmimg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_info);

        m_Name = (TextView) findViewById(R.id.m_name);
        m_Price = (TextView) findViewById(R.id.m_price);
        m_Info = (TextView) findViewById(R.id.m_info);
        m_Jpg = (ImageView) findViewById(R.id.m_photo);

        cartButton = (Button) findViewById(R.id.cart_btn);
        buyButton = (Button) findViewById(R.id.buy_btn);

        m_amount = (EditText) findViewById(R.id.mi_amount);
        m_amountUpBtn = (Button) findViewById(R.id.mi_amountUp);
        m_amountDownBtn = (Button) findViewById(R.id.mi_amountDown);

        final Intent intent = getIntent();
        m_MenuVO = (MenuVO) intent.getExtras().getParcelable("menuVO");
        store_name = intent.getExtras().getString("store_name");
        store_num = intent.getExtras().getInt("store_num");

        pSum = m_MenuVO.getMenu_price();

        Log.d("MenuVO = ", m_MenuVO.toString());
        Log.d("minfo_storename = ", store_name);
        Log.d("minfo num = ", String.valueOf(store_num));

        InfoTask Info = new InfoTask();
        Info.execute();

        my = new myDB(this);

        m_amountUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (initialvalues >= min_range && initialvalues <= max_range){
                    if(pSum < (m_MenuVO.getMenu_price()*10)){
                        initialvalues++;
                        m_amount.setText(""+initialvalues);
                        pSum += m_MenuVO.getMenu_price();
                        m_Price.setText(pSum+"원");
                    }
                }else if (initialvalues > max_range){
                    initialvalues = max_range;
                    m_amount.setText("" + initialvalues);
                }

//                if(flag == 0){
//                    if(initialvalues <= 10){
//                        pSum += m_MenuVO.getMenu_price();
//                        m_Price.setText(String.valueOf(pSum)+"원");
//                        if(initialvalues == 10){
//                            flag = 1;
//                        }else if(initialvalues >= 10){
//                            flag = 0;
//                        }
//                    }
//                }

            }
        });

        m_amountDownBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (initialvalues >= min_range && initialvalues <= max_range) {
                    if(pSum > m_MenuVO.getMenu_price()){
                        initialvalues--;
                        m_amount.setText(""+initialvalues);
                        pSum -= m_MenuVO.getMenu_price();
                        m_Price.setText(pSum+"원");
                    }
                }else if (initialvalues < min_range) {
                    initialvalues = min_range;
                    m_amount.setText(""+initialvalues);
                }

//                m_amount.setText(initialvalues + "");
//
//                if( initialvalues >=1 && initialvalues <= 10){
//                    pSum -= m_MenuVO.getMenu_price();
//                    m_Price.setText(String.valueOf(pSum)+"원");
//                    Log.d("마이너스진입", "진입");
//                }else if(initialvalues == 1){
//                    m_Price.setText(""+m_MenuVO.getMenu_price()+"원");
//                }
            }
        });

        buyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(getBaseContext().getApplicationContext(), BuyInfoActivity.class);
                intent2.putExtra("menuVO2", m_MenuVO);
                intent2.putExtra("choice", 1);
                intent2.putExtra("store_name", store_name);
                intent2.putExtra("store_num", store_num);
                intent2.putExtra("initvalue", initialvalues);
                startActivity(intent2);

                //cartvo로 넘기기
            }
        });

        cartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choice = 2;

                my.insertCart(sql, m_MenuVO.getMenu_name(), m_MenuVO.getMenu_price(), Integer.parseInt(m_amount.getText().toString()), store_name, store_num);
                Intent intent1 = new Intent(getBaseContext().getApplicationContext(), CartActivity.class);
                intent1.putExtra("menuVO", m_MenuVO);
                intent1.putExtra("store_name", store_name);
                intent1.putExtra("store_num", store_num);
                intent1.putExtra("choice", choice);
                startActivity(intent1);
                            }
        });
    }



    private class InfoTask extends AsyncTask<Void, Void, Boolean> {

        String sResult;

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                url = new URL(imgUrl + m_MenuVO.getMenu_photo());
                Log.d("URL =", imgUrl + m_MenuVO.getMenu_photo().toString());
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true);
                conn.connect();

                InputStream input = conn.getInputStream();

                bmimg = BitmapFactory.decodeStream(input);

            } catch (IOException e) {
                e.printStackTrace();
            }

            m_MenuVO.setMenu_jpg(getCroppedBitmap(bmimg));

            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            m_Name.setText(m_MenuVO.getMenu_name());
            m_Info.setText(m_MenuVO.getMenu_info());
            m_Price.setText(String.valueOf(m_MenuVO.getMenu_price())+"원");
            m_Jpg.setImageBitmap(m_MenuVO.getMenu_jpg());
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
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

