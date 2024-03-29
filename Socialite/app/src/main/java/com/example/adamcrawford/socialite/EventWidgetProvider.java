package com.example.adamcrawford.socialite;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.RemoteViews;

import com.example.adamcrawford.socialite.dataHandler.DataStorage;
import com.example.adamcrawford.socialite.events.EventConstructor;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

/**
 * Author:  Adam Crawford
 * Project: Socialite
 * Package: com.example.adamcrawford.socialite.events
 * File:    EventWidgetProvider
 * Purpose: TODO Minimum 2 sentence description
 */
public class EventWidgetProvider extends AppWidgetProvider {

    SharedPreferences preferences;
    String TAG = "EWP";

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);

        AppWidgetManager mgr = AppWidgetManager.getInstance(context);

        // Get the layout for the App Widget
        RemoteViews views = new RemoteViews(context.getPackageName(),R.layout.layout_widget);

        ComponentName comp = new ComponentName(context.getPackageName(), EventWidgetProvider.class.getName());

        mgr.updateAppWidget(comp, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        Log.i(TAG, "Updating Widget");

        RemoteViews widgetView = new RemoteViews(context.getPackageName(), R.layout.layout_widget);
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        JSONObject events = null;
        Intent viewIntent = null;
        SharedPreferences.Editor edit = preferences.edit();

        File eventsFile = new File("events");

        if (eventsFile.exists()) {

            try {
                events = new JSONObject(DataStorage.getInstance().readFile("events", context));
                Log.i(TAG, events.toString());
            } catch (JSONException e) {
                Log.e(TAG, e.getMessage());
            }
        }

        if (events != null) {

            Log.i(TAG, "events has data");
            JSONObject currentEvent = null;
            int currentEventNumber = preferences.getInt("currentEventNumber", -1);

            if (currentEventNumber != -1) {
                Log.i(TAG, "CEV ! -1");
                try {
                    currentEventNumber++;
                    Log.e(TAG, "CEV: " + currentEventNumber);
                    currentEvent = events.getJSONArray("events").getJSONObject(currentEventNumber);
                } catch (JSONException e) {
                    Log.e(TAG, e.getMessage());
                }
            }

            if (currentEvent != null) {
                try {
                    currentEventNumber++;
                    widgetView.setTextViewText(R.id.widgetEventName, currentEvent.getJSONObject("event").getString("title"));
                    widgetView.setTextViewText(R.id.widgetEventTime, currentEvent.getJSONObject("event").getString("start_date"));
                    widgetView.setTextViewText(R.id.widgetEventCity, currentEvent.getJSONObject("event").getString("city"));
                    viewIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(currentEvent.getJSONObject("event").getString("url")));
                    edit.putInt("currentEventNumber", currentEventNumber);
                    edit.apply();
                } catch (JSONException e) {
                    Log.e(TAG, e.getMessage());
                }
            } else {
                Log.i(TAG, "Null current event");
            }
        }

        if (viewIntent != null) {
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, viewIntent, 0);
            widgetView.setOnClickPendingIntent(R.id.imgButton, pendingIntent);
        }

        appWidgetManager.updateAppWidget(appWidgetIds, widgetView);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);

        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor edit = preferences.edit();

        edit.clear();
        edit.commit();
    }
}
