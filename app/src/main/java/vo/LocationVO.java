package vo;

/**
 * Created by USER on 2015-09-22.
 */
public class LocationVO {
    double latitude,longitude;
    String store_name,store_tel;
    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    @Override
    public String toString() {
        return "LocationVO [latitude=" + latitude + ", longitude=" + longitude
                + ", store_name=" + store_name + ", store_tel=" + store_tel
                + "]";
    }

    public String getStore_tel() {
        return store_tel;
    }

    public void setStore_tel(String store_tel) {
        this.store_tel = store_tel;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getStore_name() {
        return store_name;
    }

    public void setStore_name(String store_name) {
        this.store_name = store_name;
    }




}
