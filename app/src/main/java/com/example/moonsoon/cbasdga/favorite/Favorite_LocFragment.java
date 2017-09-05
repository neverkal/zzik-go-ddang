package com.example.moonsoon.cbasdga.favorite;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.moonsoon.cbasdga.GPSTracker;
import com.example.moonsoon.cbasdga.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import vo.LocationVO;

public class Favorite_LocFragment extends Fragment {
    static View v;

    GoogleMap _map;
    GPSTracker gps;

    static double latitude;
    static double longitude;

    ArrayList<LocationVO> loc_list = new ArrayList<LocationVO>();
    LocationVO lvo = new LocationVO();

    Context mCtx;

    public static Favorite_LocFragment newInstance(){
        Favorite_LocFragment fragment = new Favorite_LocFragment();
        return fragment;
    }

    public Favorite_LocFragment(){

    }

    public interface OnSelectedLinstener{
        public void onDialogSelected();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCtx = getActivity();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(v!=null){
            ViewGroup parent = (ViewGroup)v.getParent();
            if(parent!=null){
                parent.removeView(v);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        try{
            v = inflater.inflate(R.layout.favorite_loc_fragment, container, false);
        }catch (InflateException e) {

            gps = new GPSTracker(v.getContext());
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
        }

        return  v;
    }

    @Override
    public void onStart() {
        super.onStart();
        new getLocByAddrTask().execute();
    }

    private class getLocByAddrTask extends AsyncTask<Void, Void, Boolean> {
        ArrayList<LocationVO> LocList;
        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
                Log.d("지도excute진입", "진입");
                loc_list.clear();
                loc_list.addAll(LocList);
                LatLng MyLoc = new LatLng( latitude, longitude);
                _map = ((SupportMapFragment)getChildFragmentManager().findFragmentById(R.id.map)).getMap();
                Marker seoul = _map.addMarker(new MarkerOptions().position(MyLoc)
                        .title("MyLoc").snippet("현재 위치입니다"));
                for(LocationVO vo:loc_list) {
                    _map.addMarker(new MarkerOptions(). icon(BitmapDescriptorFactory.fromResource(R.drawable.info))
                            .position(new LatLng(vo.getLatitude(), vo.getLongitude())) .title(vo.getStore_name()).snippet(vo.getStore_tel()));}
                _map.moveCamera(CameraUpdateFactory.newLatLngZoom(MyLoc, 15)); _map.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
        }
        @Override
        protected Boolean doInBackground(Void... params) {
            String requestURL = "http://192.168.0.3/proj_1/android/showLocToApp.jsp";
            InputStream is= requestLocByAddr(requestURL, latitude, longitude);
            LocList = getLocParsingXML(is);
            return true; }
    }

    public InputStream requestLocByAddr(String requestURL,double latitude,double longitude) {
        try {
            HttpClient client = new DefaultHttpClient();
            List dataList = new ArrayList();
            dataList.add(new BasicNameValuePair("latitude", Double.toString(latitude)));
            dataList.add(new BasicNameValuePair("longitude", Double.toString(longitude)));
            requestURL = requestURL + "?" + URLEncodedUtils.format(dataList, "UTF-8");
            HttpGet get = new HttpGet(requestURL);
            HttpResponse response = client.execute(get);
            HttpEntity entity = response.getEntity();
            InputStream is = entity.getContent();
            return is;
        } catch (Exception e) {e.printStackTrace(); return null; } }

    public ArrayList getLocParsingXML(InputStream is){
        ArrayList<LocationVO> list = new ArrayList<LocationVO>();
        try { XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true); XmlPullParser parser = factory.newPullParser();
            parser.setInput(is, "UTF-8"); int eventType = parser.getEventType(); LocationVO lvo = null;
            a:while( eventType != XmlPullParser.END_DOCUMENT) {
                String startTag = parser.getName();
                switch(eventType) {
                    case XmlPullParser.START_TAG:
                        if(startTag.equals("record")){ lvo = new LocationVO(); }
                        if(lvo !=null ) {
                            if(startTag.equals("latitude")) {lvo.setLatitude(Double.parseDouble(parser.nextText()));}
                            if(startTag.equals("longitude")){ lvo.setLongitude(Double.parseDouble(parser.nextText()));}
                            if(startTag.equals("store_name")) { lvo.setStore_name(parser.nextText());}
                            if(startTag.equals("store_tel")){ lvo.setStore_tel(parser.nextText());}
                        } else { Log.i("null", "fvo = null"); }
                        break;
                    case XmlPullParser.END_TAG:  String endTag = parser.getName();
                        if(endTag.equals("record")) {list.add(lvo);}else{break a;}
                        break;
                    default: Log.i("default", "default");break; }
                eventType = parser.next();
            } } catch (XmlPullParserException e) { e.printStackTrace();
        } catch (IOException ie) {ie.printStackTrace(); }
        return list;
    }

}
