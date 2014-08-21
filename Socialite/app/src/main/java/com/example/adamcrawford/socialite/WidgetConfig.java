package com.example.adamcrawford.socialite;

import android.app.Activity;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.example.adamcrawford.socialite.dataHandler.DataStorage;
import com.example.adamcrawford.socialite.dataHandler.SyncService;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;


public class WidgetConfig extends Activity implements View.OnClickListener {

    public static SharedPreferences preferences;
    static String TAG = "Config";
    EditText editZip;
    EditText editDistance;
    Context context;
    static RemoteViews widgetView;
    SharedPreferences.Editor edit;
    Intent viewIntent;
    int widgetID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_widget_config);

        context = this;

        Log.i(TAG, "Started config");

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editZip = (EditText) findViewById(R.id.editZip);
        editDistance = (EditText) findViewById(R.id.editDistance);
        Button saveButton = (Button) findViewById(R.id.saveButton);
        saveButton.setOnClickListener(this);

        widgetView = new RemoteViews((this).getPackageName(), R.layout.layout_widget);
        viewIntent = null;


        if (preferences.contains("zip")){
            Log.i(TAG, "Contains zip");
            editZip.setText(preferences.getString("zip", ""));
            editDistance.setText(preferences.getString("distance",""));
        }
    }

    @Override
    public void onClick(View view) {

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            widgetID = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);

            if (widgetID != AppWidgetManager.INVALID_APPWIDGET_ID) {

                edit = preferences.edit();

                if (!editZip.getText().toString().equals("") && !editDistance.getText().toString().equals("")){
                    edit.putString("zip", editZip.getText().toString());
                    edit.putString("distance", editDistance.getText().toString());
                    edit.apply();
                    getData(editZip.getText().toString(), editDistance.getText().toString());
            }
            }
        }
    }

    private void writeWidget(){
        JSONObject events = null;

        try {
            events = new JSONObject(DataStorage.getInstance().readFile("events", this));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (events != null) {

            Log.i(TAG, events.toString());

            if (preferences.contains("currentEventNumber")){
                JSONObject currentEvent = null;
                int currentEventNumber = preferences.getInt("currentEvent", -1);

                if (currentEventNumber != -1) {
                    Log.i(TAG, "Getting current event");
                    try {
                        currentEvent = events.getJSONArray("events").getJSONObject(currentEventNumber);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                if (currentEvent != null){
                    try {
                        widgetView.setTextViewText(R.id.widgetEventName, currentEvent.getJSONObject("event").getString("title"));
                        widgetView.setTextViewText(R.id.widgetEventTime, currentEvent.getJSONObject("event").getString("start_date"));
                        widgetView.setTextViewText(R.id.widgetEventCity, currentEvent.getJSONObject("event").getString("city"));

                        viewIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(currentEvent.getJSONObject("event").getString("url")));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.i(TAG, "Null current event");
                    try {
                        widgetView.setTextViewText(R.id.widgetEventName, events.getJSONArray("events").getJSONObject(1).getJSONObject("event").getString("title"));
                        widgetView.setTextViewText(R.id.widgetEventTime, events.getJSONArray("events").getJSONObject(1).getJSONObject("event").getString("start_date"));
                        widgetView.setTextViewText(R.id.widgetEventCity, events.getJSONArray("events").getJSONObject(1).getJSONObject("event").getJSONObject("venue").getString("city"));
                        viewIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(events.getJSONArray("events").getJSONObject(1).getJSONObject("event").getString("url")));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    edit.putInt("currentEventNumber", 1);
                    edit.apply();
                }
            } else {
                Log.i(TAG,"No current event");
                try {
                    widgetView.setTextViewText(R.id.widgetEventName, events.getJSONArray("events").getJSONObject(1).getJSONObject("event").getString("title"));
                    widgetView.setTextViewText(R.id.widgetEventTime, events.getJSONArray("events").getJSONObject(1).getJSONObject("event").getString("start_date"));
                    widgetView.setTextViewText(R.id.widgetEventCity, events.getJSONArray("events").getJSONObject(1).getJSONObject("event").getJSONObject("venue").getString("city"));
                    viewIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(events.getJSONArray("events").getJSONObject(1).getJSONObject("event").getString("url")));
                    edit.putInt("currentEventNumber", 1);
                    edit.apply();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        if (viewIntent != null) {
            Log.i(TAG, "Setting Pending Intent");
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, viewIntent, 0);
            widgetView.setOnClickPendingIntent(R.id.imgButton, pendingIntent);
        }

        AppWidgetManager.getInstance(this).updateAppWidget(widgetID, widgetView);

        Intent result = new Intent();
        result.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetID);
        setResult(RESULT_OK, result);
        finish();
    }

    public void getData(String zipCode, String distance) {

        printToast(getString(R.string.wait));

        Intent getJSON = new Intent(this, SyncService.class);
        getJSON.putExtra("zip", zipCode);
        getJSON.putExtra("distance", distance);
        final DataHandler handler = new DataHandler(this);

        Messenger msgr = new Messenger(handler);
        getJSON.putExtra("msgr", msgr);
        Log.i(TAG, "Starting Service");
        startService(getJSON);
    }

    public static class DataHandler extends Handler {
        private final WeakReference<WidgetConfig> widgetConfigWeakReference;
        public DataHandler(WidgetConfig activity) {
            widgetConfigWeakReference = new WeakReference<WidgetConfig>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            WidgetConfig activity = widgetConfigWeakReference.get();
            if (activity != null) {
                JSONObject returned = (JSONObject) msg.obj;
                if (msg.arg1 == RESULT_OK && returned != null) {
                    Log.i(TAG, "data returned");
                    DataStorage.getInstance().writeFile("events", returned.toString(), activity.context);
                    activity.writeWidget();
                } else {
                    Log.i(TAG, "No data");

                    //activity.printToast(activity.getString(R.string.notFound));
                }
            }
        }
    }

    private void printToast(String message) {
        //get active context
        Context c = getApplicationContext();
        //set length for message to be displayed
        int duration = Toast.LENGTH_LONG;
        //create message based on input parameter then display it
        Toast error = Toast.makeText(c, message, duration);
        error.show();
    }
}
