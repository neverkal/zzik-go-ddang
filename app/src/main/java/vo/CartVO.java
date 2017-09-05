package vo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by 618 on 2015-09-21.
 */
public class CartVO implements Parcelable{
    private String menu_name;
    private int menu_price;
    private int menu_amount;
    private String store_name;
    private int store_num;

    public CartVO(){

    }

    public CartVO(Parcel in){
        readFromParcel(in);
    }

    public String getMenu_name() {
        return menu_name;
    }

    public void setMenu_name(String menu_name) {
        this.menu_name = menu_name;
    }

    public int getMenu_price() {
        return menu_price;
    }

    public void setMenu_price(int menu_price) {
        this.menu_price = menu_price;
    }

    public int getMenu_amount() {
        return menu_amount;
    }

    public void setMenu_amount(int menu_amount) {
        this.menu_amount = menu_amount;
    }

    public String getStore_name() {
        return store_name;
    }

    public void setStore_name(String store_name) {
        this.store_name = store_name;
    }

    public int getStore_num() {
        return store_num;
    }

    public void setStore_num(int store_num) {
        this.store_num = store_num;
    }

    @Override
    public String toString() {
        return "CartVO{" +
                "menu_name='" + menu_name + '\'' +
                ", menu_price=" + menu_price +
                ", menu_amount=" + menu_amount +
                ", store_name='" + store_name + '\'' +
                ", store_num=" + store_num +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(menu_name);
        dest.writeInt(menu_price);
        dest.writeInt(menu_amount);
        dest.writeString(store_name);
        dest.writeInt(store_num);
    }

    private void readFromParcel(Parcel in){
        menu_name = in.readString();
        menu_price = in.readInt();
        menu_amount = in.readInt();
        store_name = in.readString();
        store_num = in.readInt();
    }

    public static final Creator CREATOR = new Creator() {
        @Override
        public CartVO createFromParcel(Parcel in) {
            return new CartVO(in);
        }

        @Override
        public Object[] newArray(int size) {
            return new CartVO[size];
        }
    };
}
