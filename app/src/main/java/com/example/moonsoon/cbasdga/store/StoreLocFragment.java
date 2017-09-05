package com.example.moonsoon.cbasdga.store;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;

import com.example.moonsoon.cbasdga.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
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

/**
 * Created by moonsoon on 2015-09-15.
 */
public class StoreLocFragment extends Fragment {
    private GoogleMap map;
    static double latitude;
    static double longitude;
    ArrayList<LocationVO> loc_list = new ArrayList<LocationVO>();
    LocationVO lvo = new LocationVO();
    String store_addr = "서울특별시 성북구 정릉동 16-169";
    static LatLng MyLoc;

    Context mCtx;

    public static StoreLocFragment newInstance(){
        StoreLocFragment fragment = new StoreLocFragment();
        return fragment;
    }
    public StoreLocFragment(){

    }

    public interface OnSelecttedListener{
        public void onDialogSelected();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCtx = getActivity();

        Bundle get = getArguments();
        store_addr = get.getString("store_addr");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.favorite_loc_fragment, container,false);
    }

    @Override
    public void onStart() {
        super.onStart();
        getLocByAddrTask getLocByAddrTask = new getLocByAddrTask();
        getLocByAddrTask.execute();
    }

    private class getLocByAddrTask extends AsyncTask<Void, Void, Boolean> {
        ArrayList<LocationVO> LocList;
        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if(result){
                loc_list.clear();
                loc_list.addAll(LocList);
                Log.i("222222222222222", "onPostExecute");

                map = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map))
                        .getMap();
                for(LocationVO vo:loc_list) {
                    MyLoc = new LatLng( vo.getLatitude(), vo.getLongitude());
                    Marker store = map.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.info))
                            .position(MyLoc).title(vo.getStore_name()).snippet(vo.getStore_tel()));
                }
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(MyLoc, 15)); map.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);}
        }
        @Override
        protected Boolean doInBackground(Void... params) {
            Log.i("111111111111", "1111111111111");
            String requestURL = "http://192.168.0.3/proj_1/android/showStoreLocToApp.jsp";
            InputStream is= requestLocByAddr(requestURL, store_addr);
            LocList = getLocParsingXML(is);
            return true;
        }

        public InputStream requestLocByAddr(String requestURL,String store_addr) {
            try {
                HttpClient client = new DefaultHttpClient();
                List dataList = new ArrayList();
                dataList.add(new BasicNameValuePair("store_addr",store_addr));
                requestURL = requestURL + "?" + URLEncodedUtils.format(dataList, "UTF-8");
                HttpGet get = new HttpGet(requestURL);
                HttpResponse response = client.execute(get);
                HttpEntity entity = response.getEntity();
                InputStream is = entity.getContent();
                return is;
            } catch (Exception e) {e.printStackTrace(); return null; }
        }

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
                    for(LocationVO vo:loc_list) {
                        Log.i("1111111111", ""+vo.getLatitude()+ ""+vo.getLongitude()+vo.getStore_name()+vo.getStore_tel());

                    }
                } } catch (XmlPullParserException e) { e.printStackTrace();
            } catch (IOException ie) {ie.printStackTrace(); }
            return list;
        }
    }
}
