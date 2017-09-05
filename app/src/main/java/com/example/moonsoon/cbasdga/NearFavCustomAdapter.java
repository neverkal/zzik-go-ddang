package com.example.moonsoon.cbasdga;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import vo.FavoriteVO;

/**
 * 내 지역 주변 즐겨찾기 리스트를 뿌려주기 위한 어답터 부분입니다
 * **/
public class NearFavCustomAdapter extends BaseAdapter {

	Context ctx;
	ArrayList<FavoriteVO> list;
	int layout;
	LayoutInflater inf;
	static String del_store_name;
	//public NearFavCustomAdapter(){}
	public NearFavCustomAdapter(Context ctx, ArrayList<FavoriteVO> list, int layout) {
		super();
		this.ctx = ctx;
		this.list = list;
		this.layout = layout;
		inf = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub

		NearFavViewHolder viewHolder;

		//final NearFavCustomAdapter nfa = new NearFavCustomAdapter();
		if(convertView == null) { //convertView = inf.inflate(layout, null);
			convertView = inf.inflate(layout,parent,false);
			viewHolder = new NearFavViewHolder();
			viewHolder.fav_near_store_name = (TextView)convertView.findViewById(R.id.fav_near_store_name);
			viewHolder.fav_near_store_addr = (TextView)convertView.findViewById(R.id.fav_near_store_addr);
			viewHolder.fav_near_count = (TextView)convertView.findViewById(R.id.fav_near_count);
			viewHolder.delnearfavbutton = (Button)convertView.findViewById(R.id.delnearfavbutton);
			viewHolder.delnearfavbutton.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					try {
						new DeleteNearFavinfoTask().execute();
						list.remove(position);
						notifyDataSetChanged();
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			});

			convertView.setTag(viewHolder);
		}else
		{
			viewHolder = (NearFavViewHolder) convertView.getTag();
		}

		viewHolder.fav_near_store_name.setText(list.get(position).getStore_name());
		viewHolder.fav_near_store_addr.setText(list.get(position).getStore_addr());
		del_store_name = (String) viewHolder.fav_near_store_name.getText();
		viewHolder.fav_near_count.setText("" + list.get(position).getStore_count());

		return convertView;
	}


	private class DeleteNearFavinfoTask extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				String requestURL = "http://172.20.10.2/proj_1/deleteNearFavList.jsp";
				InputStream is = requestdeleteNearFav(requestURL);
				if (is == null) {
					Log.i("xxx", "is is null");
				}

				return true;
			}catch(Exception ex){
				Log.i("err", ex.getMessage());
				return false;
			}
		}
	}

	public InputStream requestdeleteNearFav(String requestURL) {

		Log.i("del_store_name11", del_store_name);
		Log.i("xxx", "requestGet start");
		try {

			//1.
			HttpClient client = new DefaultHttpClient();
			Log.i("xxx", "111111111111111111111111111");
			Log.i("requestURL", requestURL);

			List dataList = new ArrayList();
			dataList.add(new BasicNameValuePair("del_store_name", del_store_name));
			Log.i("del_store_name22",del_store_name);
			requestURL = requestURL + "?" + URLEncodedUtils.format(dataList, "UTF-8");
			Log.i("requestURL222", requestURL);
			HttpGet get = new HttpGet(requestURL);
			Log.i("xxx", "222222222222222222222222222222");
			//2. 요청
			HttpResponse response = client.execute(get);
			Log.i("xxx", "333333333333333333333333333333333");
			//3. 결과 받기
			HttpEntity entity = response.getEntity();
			InputStream is = entity.getContent();
			Log.i("xxx", "4444444444444444444444444");
			return is;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}//end requestGet()
}
