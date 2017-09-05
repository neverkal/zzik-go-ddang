package com.example.moonsoon.cbasdga.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;


/**
 * GCMIntentService
 * */
public class GCMIntentService extends IntentService {

    private static final String TAG = "GCMIntentService";

    /**
     * 생성자
     */
    public GCMIntentService() {
        super(TAG);

        Log.d(TAG, "GCMIntentService() called.");
    }

    /*
     * 전달받은 인텐트 처리
     */
    @Override
    protected void onHandleIntent(Intent intent) {

        Bundle extaras = intent.getExtras();
        //getExtras():Retrieves a map of extended data from the intent.
        String msg=extaras.getString("data");
        Log.i("msg",msg);
        String action = intent.getAction();
        //Retrieve the general action to be performed, such as ACTION_VIEW
        Log.d(TAG, "action : " + action);
    }


}