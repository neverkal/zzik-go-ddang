package vo;

public class FavoriteVO {
	int store_num,store_count;
	double latitude,longitude;
	String store_name,store_addr,cus_id, store_tel, store_time;

	public int getStore_num() {
		return store_num;
	}
	public void setStore_num(int store_num) {
		this.store_num = store_num;
	}
	public String getStore_time() {
		return store_time;
	}
	public void setStore_time(String store_time) {
		this.store_time = store_time;
	}
	public int getStore_count() {
		return store_count;
	}
	public void setStore_count(int store_count) {
		this.store_count = store_count;
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
	public String getCus_id() {
		return cus_id;
	}
	public void setCus_id(String cus_id) {
		this.cus_id = cus_id;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public void setStore_tel(String store_tel){
		this.store_tel = store_tel;
	}
	public String getStore_tel(){
		return store_tel;
	}
}
