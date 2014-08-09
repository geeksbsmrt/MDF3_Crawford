package com.example.adamcrawford.smsbuddy.restricted;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ListView;

import com.example.adamcrawford.smsbuddy.MyActivity;
import com.example.adamcrawford.smsbuddy.R;

import java.util.ArrayList;
import java.util.Map;

/**
 * Author:  Adam Crawford
 * Project: SMSBuddy
 * Package: com.example.adamcrawford.smsbuddy.sender
 * File:    RestrictedActivity
 * Purpose: TODO Minimum 2 sentence description
 */
public class RestrictedActivity extends Activity {

    static String TAG = "RestrictedAct";
    static ArrayList<RestrictedConstructor> restrictedNames;
    private static SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public static void writeList(Context context, ListView restrictedList) {

        Log.i(TAG, "Writing List");
        //create string array
        restrictedNames = new ArrayList();
        preferences = PreferenceManager.getDefaultSharedPreferences(MyActivity.sContext);

        if (preferences != null) {

            Map<String, ?> names = preferences.getAll();
            Log.i(TAG, names.toString());

            //loop through array putting member names into string array
            for (Map.Entry<String, ?> entry : names.entrySet()) {
                Log.i(TAG, (String) entry.getValue());
                RestrictedConstructor rc = new RestrictedConstructor((String) entry.getValue());
                restrictedNames.add(rc);
            }

            //build listAdapter
            RestrictedAdapter listAdapter = new RestrictedAdapter(context, R.id.restricted, restrictedNames);

            //refresh the data
            listAdapter.notifyDataSetChanged();
            restrictedList.setAdapter(listAdapter);
        }
    }
}
