package vo;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by moonsoon on 2015-09-23.
 */
public class OrderVO implements Parcelable {

    private String store_name, store_addr, order_id, order_date, order_menu, order_req, store_tel, store_time, menu_photo;
    private int store_num, order_price, order_amount;
    private Bitmap menu_jpg;

    public OrderVO(){}

    public OrderVO(Parcel in){readFromParcel(in);}

    public OrderVO(String store_name, String store_addr, String order_id, String order_date, String order_menu, String order_req, String store_tel, String store_time, String menu_photo, int store_num,
                   int order_price, int order_amount, Bitmap menu_jpg) {

        this.store_name = store_name;
        this.store_addr = store_addr;
        this.order_id = order_id;
        this.order_date = order_date;
        this.order_menu = order_menu;
        this.order_req = order_req;
        this.store_tel = store_tel;
        this.store_time = store_time;
        this.menu_photo = menu_photo;
        this.store_num = store_num;
        this.order_price = order_price;
        this.order_amount = order_amount;
        this.menu_jpg = menu_jpg;
    }

    public String getStore_name() {
        return store_name;
    }

    public void setStore_name(String store_name) {
        this.store_name = store_name;
    }

    public String getStore_addr() {
        return store_addr;
    }

    public void setStore_addr(String store_addr) {
        this.store_addr = store_addr;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getOrder_date() {
        return order_date;
    }

    public void setOrder_date(String order_date) {
        this.order_date = order_date;
    }

    public String getOrder_menu() {
        return order_menu;
    }

    public void setOrder_menu(String order_menu) {
        this.order_menu = order_menu;
    }

    public String getOrder_req() {
        return order_req;
    }

    public void setOrder_req(String order_req) {
        this.order_req = order_req;
    }

    public String getStore_tel() {
        return store_tel;
    }

    public void setStore_tel(String store_tel) {
        this.store_tel = store_tel;
    }

    public String getStore_time() {
        return store_time;
    }

    public void setStore_time(String store_time) {
        this.store_time = store_time;
    }

    public String getMenu_photo() {
        return menu_photo;
    }

    public void setMenu_photo(String menu_photo) {
        this.menu_photo = menu_photo;
    }

    public int getStore_num() {
        return store_num;
    }

    public void setStore_num(int store_num) {
        this.store_num = store_num;
    }

    public int getOrder_price() {
        return order_price;
    }

    public void setOrder_price(int order_price) {
        this.order_price = order_price;
    }

    public int getOrder_amount() {
        return order_amount;
    }

    public void setOrder_amount(int order_amount) {
        this.order_amount = order_amount;
    }

    public Bitmap getMenu_jpg() {
        return menu_jpg;
    }

    public void setMenu_jpg(Bitmap menu_jpg) {
        this.menu_jpg = menu_jpg;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(store_name);
        dest.writeString(store_addr);
        dest.writeString(order_id);
        dest.writeString(order_date);
        dest.writeString(order_menu);
        dest.writeString(order_req);
        dest.writeString(store_tel);
        dest.writeString(store_time);
        dest.writeString(menu_photo);
        dest.writeInt(store_num);
        dest.writeInt(order_price);
        dest.writeInt(order_amount);
    }

    @Override
    public String toString() {
        return "OrderVO{" +
                "store_name='" + store_name + '\'' +
                ", store_addr='" + store_addr + '\'' +
                ", order_id='" + order_id + '\'' +
                ", order_date='" + order_date + '\'' +
                ", order_menu='" + order_menu + '\'' +
                ", order_req='" + order_req + '\'' +
                ", store_tel='" + store_tel + '\'' +
                ", store_time='" + store_time + '\'' +
                ", menu_photo='" + menu_photo + '\'' +
                ", store_num=" + store_num +
                ", order_price=" + order_price +
                ", order_amount=" + order_amount +
                ", menu_jpg=" + menu_jpg +
                '}';
    }

    private void readFromParcel(Parcel in){
        store_name = in.readString();
        store_addr = in.readString();
        order_id = in.readString();
        order_date = in.readString();
        order_menu = in.readString();
        order_req = in.readString();
        store_tel = in.readString();
        store_time = in.readString();
        menu_photo = in.readString();
        store_num = in.readInt();
        order_price = in.readInt();
        order_amount = in.readInt();
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public OrderVO createFromParcel(Parcel in) {
            return new OrderVO(in);
        }
        public OrderVO[] newArray(int size) {
            return new OrderVO[size];
        }
    };
}
