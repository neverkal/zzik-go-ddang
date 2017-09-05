package vo;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by 618 on 2015-08-22.
 */
public class MenuVO implements Parcelable{
    private int menuResource = 0;

    private String menu_name, menu_info, menu_photo;
    private int store_num, menu_price;
    private Bitmap menu_jpg;

    public MenuVO (){

    }

    public MenuVO(Parcel in){
        readFromParcel(in);
    }

    public MenuVO(String menu_name, String menu_info, String menu_photo, int store_num, int menu_price){
        this.menu_name = menu_name;
        this.menu_info = menu_info;
        this.menu_photo = menu_photo;
        this.store_num = store_num;
        this.menu_price = menu_price;
    }

    public String getMenu_name() {
        return menu_name;
    }

    public void setMenu_name(String menu_name) {
        this.menu_name = menu_name;
    }

    public String getMenu_info() {
        return menu_info;
    }

    public void setMenu_info(String menu_info) {
        this.menu_info = menu_info;
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

    public int getMenu_price() {
        return menu_price;
    }

    public void setMenu_price(int menu_price) {
        this.menu_price = menu_price;
    }

    public Bitmap getMenu_jpg() {
        return menu_jpg;
    }

    public void setMenu_jpg(Bitmap menu_jpg) {
        this.menu_jpg = menu_jpg;
    }

    @Override
    public String toString() {
        return "MenuVO{" +
                "menu_name='" + menu_name + '\'' +
                ", menu_info='" + menu_info + '\'' +
                ", menu_photo='" + menu_photo + '\'' +
                ", store_num=" + store_num +
                ", menu_price=" + menu_price +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(menu_name);
        dest.writeString(menu_info);
        dest.writeString(menu_photo);
        dest.writeInt(store_num);
        dest.writeInt(menu_price);
    }

    private void readFromParcel(Parcel in){
        menu_name = in.readString();
        menu_info = in.readString();
        menu_photo = in.readString();
        store_num = in.readInt();
        menu_price = in.readInt();
    }

    public static final Creator CREATOR = new Creator() {
        public MenuVO createFromParcel(Parcel in) {
            return new MenuVO(in);
        }

        public MenuVO[] newArray(int size) {
            return new MenuVO[size];
        }
    };
}
