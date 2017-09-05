package vo;

/**
 * Created by 618 on 2015-09-25.
 */
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by 618 on 2015-09-04.
 */
public class StoreVO implements Parcelable{

    private String owner_id,store_name, store_tel, store_addr, store_time, store_category,searchType,searchValue,store_post;
    private int store_num, fav_cnt;

    public StoreVO (){

    }

    public StoreVO(Parcel in){
        readFromParcel(in);
    }

    public StoreVO(String owner_id, String store_name, String store_tel, String store_addr, String store_time, String store_category, String searchType,
                   String searchValue, String store_post, int store_num){
        this.owner_id = owner_id;
        this.store_name = store_name;
        this.store_tel = store_tel;
        this.store_addr = store_addr;
        this.store_time = store_time;
        this.store_category = store_category;
        this.searchType = searchType;
        this.searchValue = searchValue;
        this.store_post = store_post;
        this.store_num =store_num;
    }

    public String getOwner_id() {
        return owner_id;
    }

    public void setOwner_id(String owner_id) {
        this.owner_id = owner_id;
    }

    public String getStore_name() {
        return store_name;
    }

    public void setStore_name(String store_name) {
        this.store_name = store_name;
    }

    public String getStore_tel() {
        return store_tel;
    }

    public void setStore_tel(String store_tel) {
        this.store_tel = store_tel;
    }

    public String getStore_addr() {
        return store_addr;
    }

    public void setStore_addr(String store_addr) {
        this.store_addr = store_addr;
    }

    public String getStore_time() {
        return store_time;
    }

    public void setStore_time(String store_time) {
        this.store_time = store_time;
    }

    public String getStore_category() {
        return store_category;
    }

    public void setStore_category(String store_category) {
        this.store_category = store_category;
    }

    public String getSearchType() {
        return searchType;
    }

    public void setSearchType(String searchType) {
        this.searchType = searchType;
    }

    public String getSearchValue() {
        return searchValue;
    }

    public void setSearchValue(String searchValue) {
        this.searchValue = searchValue;
    }

    public String getStore_post() {
        return store_post;
    }

    public void setStore_post(String store_post) {
        this.store_post = store_post;
    }

    public int getStore_num() {
        return store_num;
    }

    public void setStore_num(int store_num) {
        this.store_num = store_num;
    }

    public int getFav_cnt() {
        return fav_cnt;
    }

    public void setFav_cnt(int fav_cnt) {
        this.fav_cnt = fav_cnt;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(owner_id);
        dest.writeString(store_name);
        dest.writeString(store_tel);
        dest.writeString(store_addr);
        dest.writeString(store_time);
        dest.writeString(store_category);
        dest.writeString(searchType);
        dest.writeString(searchValue);
        dest.writeInt(store_num);
        dest.writeInt(fav_cnt);
    }

    private void readFromParcel(Parcel in){
        owner_id = in.readString();
        store_name = in.readString();
        store_tel = in.readString();
        store_addr = in.readString();
        store_time = in.readString();
        store_category = in.readString();
        searchType = in.readString();
        searchValue = in.readString();
        store_num = in.readInt();
        fav_cnt = in.readInt();
    }

    public static final Creator CREATOR = new Creator(){
        public StoreVO createFromParcel(Parcel in){
            return new StoreVO(in);
        }

        public  StoreVO[] newArray(int size){
            return new StoreVO[size];
        }
    };

}