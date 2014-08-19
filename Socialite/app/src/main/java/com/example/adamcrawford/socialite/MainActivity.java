package com.example.adamcrawford.socialite;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.widget.Toast;

import com.example.adamcrawford.socialite.data.DataStorage;
import com.example.adamcrawford.socialite.data.SyncService;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;


public class MainActivity extends Activity {

    static String TAG = "Main Activity";
    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getData("33811", "10");
    }

    //method to display error to user
    private void printToast(String message) {
        //get active context
        Context c = getApplicationContext();
        //set length for message to be displayed
        int duration = Toast.LENGTH_LONG;
        //create message based on input parameter then display it
        Toast error = Toast.makeText(c, message, duration);
        error.show();
    }

    private void getData(String zipCode, String distance) {
        Intent getJSON = new Intent(this, SyncService.class);
        getJSON.putExtra("zip", zipCode);
        getJSON.putExtra("distance", distance);
        final DataHandler handler = new DataHandler(this);

        Messenger msgr = new Messenger(handler);
        getJSON.putExtra("msgr", msgr);
        Log.i(TAG, "Starting Service");
        startService(getJSON);
    }

    private static class DataHandler extends Handler {
        private final WeakReference<MainActivity> mainActivityWeakReference;
        public DataHandler(MainActivity activity) {
            mainActivityWeakReference = new WeakReference<MainActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity activity = mainActivityWeakReference.get();
            if (activity != null) {
                JSONObject returned = (JSONObject) msg.obj;
                if (msg.arg1 == RESULT_OK && returned != null) {
                    Log.i(TAG, "data returned");
                    //activity.writeList(returned);
                    try {
                       //TODO write data to DataStore
                        DataStorage.getInstance().writeFile("events", returned.toString(), activity.context);
                        JSONObject json = new JSONObject(DataStorage.getInstance().readFile("events", activity.context));
                        //activity.writeList(json);
                        Log.i(TAG, json.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    Log.i(TAG, "No data");

                    //activity.printToast(activity.getString(R.string.notFound));
                }
            }
        }

    }
}