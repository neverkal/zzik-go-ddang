package vo;

public class PushVO {
	String phonenum, regid;
	int all_order_price, mileage;

	public String getPhonenum() {
		return phonenum;
	}

	public void setPhonenum(String phonenum) {
		this.phonenum = phonenum;
	}

	public String getRegid() {
		return regid;
	}

	public void setRegid(String regid) {
		this.regid = regid;
	}

	public int getAll_order_price() {
		return all_order_price;
	}

	public void setAll_order_price(int all_order_price) {
		this.all_order_price = all_order_price;
	}

	public int getMileage() {
		return mileage;
	}

	public void setMileage(int mileage) {
		this.mileage = mileage;
	}

	@Override
	public String toString() {
		return "PushVO [phonenum=" + phonenum + ", regid=" + regid
				+ ", all_order_price=" + all_order_price + ", mileage="
				+ mileage + "]";
	}


}
