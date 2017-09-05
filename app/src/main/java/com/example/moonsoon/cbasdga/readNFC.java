package com.example.moonsoon.cbasdga;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.NfcA;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.example.moonsoon.cbasdga.store.StoreDetailActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import nfc.NdefMessageParser;
import nfc.ParsedRecord;
import nfc.TextRecord;
import vo.StoreVO;

public class readNFC extends AppCompatActivity {

    private NfcAdapter nfcAdapter;
    private PendingIntent pendingIntent;
    private IntentFilter[] filters;
    private String[][] techLists;
    private Tag tag;
    private String recordStr;

    private Intent intent;
    private  int store_num;

    String store_addr;
    String store_tel;
    String store_time;
    String store_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_list);

        intent = new Intent(this, readNFC.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);

        try {
            ndef.addDataType("*/*");
        } catch (Exception e) {
            throw new RuntimeException("fail", e);
        }

        filters = new IntentFilter[]{ndef};
        techLists = new String[][]{new String[]{NfcA.class.getName()}};

//        Intent passedIntent = getIntent();
//        if (passedIntent != null) {
//            String action = passedIntent.getAction();
//            if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)) {
//                processTag(passedIntent);
//            }
//        }


        onNewIntent(getIntent());

    }


    @Override
    protected void onResume() {
        super.onResume();
        if (nfcAdapter != null) {
            nfcAdapter.enableForegroundDispatch(this, pendingIntent, filters, techLists);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {

        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        processTag(intent);

        Intent change = new Intent(getApplicationContext(), StoreDetailActivity.class);
        // 데이터를 change 인텐트에 넣어서 보냄

        store_num = Integer.parseInt(recordStr);
        change.putExtra("store_num", store_num);
        change.putExtra("choice", 2);

        Log.d("record", String.valueOf(store_num));
        startActivity(change);

    }

    private void processTag(Intent intent) {
        Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
        NdefMessage[] msgs;
        if ((rawMsgs != null)) {
            msgs = new NdefMessage[rawMsgs.length];
            for (int i = 0; i < rawMsgs.length; i++) {
                msgs[i] = (NdefMessage) rawMsgs[i];
                showTag(msgs[i]);
            }
        } else {
            return;
        }
    }

    private int showTag(NdefMessage ndefMessage) {
        List<ParsedRecord> records = NdefMessageParser.parse(ndefMessage);
        final int size = records.size();

        for (int i = 0; i < size; i++) {
            ParsedRecord record = records.get(i);
            int recordType = record.getType();
            if (recordType == ParsedRecord.RTD_TEXT) {
                recordStr = ((TextRecord) record).getText();
            }
        }

        return size;
    }
}



