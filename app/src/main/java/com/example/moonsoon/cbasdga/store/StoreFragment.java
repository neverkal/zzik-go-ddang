package com.example.moonsoon.cbasdga.store;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.moonsoon.cbasdga.R;

/**
 * Created by moonsoon on 2015-09-14.
 */
public class StoreFragment extends Fragment {
    public StoreFragment newInstance(){
        StoreFragment fragment = new StoreFragment();
        return fragment;
    }

    public StoreFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.store_main, container, false);
    }
}
