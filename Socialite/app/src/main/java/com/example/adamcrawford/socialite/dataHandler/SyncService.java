package com.example.adamcrawford.socialite.dataHandler;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.example.adamcrawford.socialite.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Author:  Adam Crawford
 * Project: Socialite
 * Package: com.example.adamcrawford.socialite.data
 * File:    SyncService
 * Purpose: TODO Minimum 2 sentence description
 */
public class SyncService extends IntentService {
    private String TAG = "SyncService";

    public SyncService() {
        super("SyncService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Bundle extras = intent.getExtras();
        String zip = extras.getString("zip");
        String distance = extras.getString("distance");
        JSONObject events = getData(zip, distance);
        Messenger msgr = (Messenger) extras.get("msgr");
        Message msg = Message.obtain();

        //Log.i(TAG, events.toString());
        msg.arg1 = MainActivity.RESULT_OK;
        msg.obj = events;

        try {
            msgr.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }


    }

    private JSONObject getData (String zipCode, String distance) {
        Log.i(TAG, "Getting data");
        JSONObject events = null;
        URL url;
        StringBuffer contentBuffer = null;
        InputStream bin = null;

        try {
            //build URL from user inputs
            String loc = "https://www.eventbrite.com/json/event_search?postal_code="+ zipCode +"&within="+ distance +"&within_unit=M&date=Future&max=20&sort_by=date&app_key=MXJ6QL3VZTULJNGDQ5";
            url = new URL(loc);

            //create connection
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            if (conn.getResponseMessage().equals("Not Found")) {
                Log.e(TAG, "Not found");

            }
            //if valid response handle data
            if (conn.getResponseMessage().equals("OK")) {

                //create buffer stream
                bin = new BufferedInputStream(conn.getInputStream());
                //set byte size for buffer stream
                byte[] connBytes = new byte[1024];
                //bytes read by input stream
                int byteRead;
                //string to put data into
                String content;
                //stringbuffer
                contentBuffer = new StringBuffer();

                //while data is incoming from the HTTPRequest append data to the string buffer
                //BIS returns -1 when end of content reached
                while ((byteRead = bin.read(connBytes)) != - 1) {
                    content = new String(connBytes, 0, byteRead);
                    contentBuffer.append(content);
                }
            }
        } catch (IOException e) {
            Log.e(TAG, "Not Found");
            e.printStackTrace();
        } finally {
            if (bin != null) {
                try {
                    //close inputstream
                    bin.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        try {
            //create JSON from StringBuffer
            if (contentBuffer != null) {
                events = new JSONObject(String.valueOf(contentBuffer));
                //Log.i(TAG, events.toString());
            }
        } catch (JSONException e) {
            Log.e(TAG, "JSON Error");
            e.printStackTrace();
        }
        return events;
    }
}
