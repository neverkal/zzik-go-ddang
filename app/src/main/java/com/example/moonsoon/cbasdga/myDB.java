package com.example.moonsoon.cbasdga;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class myDB extends SQLiteOpenHelper {
    public myDB(Context context) {
        super(context, "human", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table cart " +
                "(menu_name char(20), menu_price Integer, menu_amount Integer, store_name char(30), store_num Integer)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS cart");
        onCreate(db);
    }

    public void insertCart(SQLiteDatabase db, String m_name, int m_price, int m_amount, String store_name, int store_num) {
        db = this.getWritableDatabase();
        db.execSQL("INSERT INTO cart VALUES('" + m_name + "', " + m_price + ", " + m_amount + ", '" + store_name + "', " + store_num + ");");
        db.close();
    }

    public void updateCart(SQLiteDatabase db, int m_amount, String m_name) {
        db = this.getWritableDatabase();
        db.execSQL("UPDATE CART SET menu_amount = " + m_amount + "WHERE menu_name ='" + m_name + "'");
        db.close();
    }

    public void deleteCart(SQLiteDatabase db, int m_name) {
        Log.d("m_name:", String.valueOf(m_name));
        db = this.getWritableDatabase();
        db.delete("CART", "rowid" + "=" + m_name, null);
        db.close();
    }

    public void truncateCart(SQLiteDatabase db, int store_num) {
        db = this.getWritableDatabase();
        db.execSQL("DELETE FROM CART WHERE store_num = '" + store_num + "'");
//        db.execSQL("TRUNCATE TABLE CART");
        db.close();
    }
}
