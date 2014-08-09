package com.example.adamcrawford.smsbuddy;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.util.Log;
import android.view.View;
import android.widget.Button;


public class MyActivity extends Activity {

    private String TAG = "Main Activity";
    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        final Button composeButton = (Button) findViewById(R.id.composeButton);

        composeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent composeIntent = new Intent(context, SMSActivity.class);
                startActivity(composeIntent);
            }
        });
    }

    //taken from the internet to allow the default use of this app for SMS/MMS with API 19
    @TargetApi(19)
    private void setDefault(){
        //creates string to compare the default app to
        final String myPackageName = getPackageName();
        //compare the default SMS app to my app name
        if (!Telephony.Sms.getDefaultSmsPackage(this).equals(myPackageName)) {
            // App is not default.
            //Build alert dialog informing user to set app as default
            new AlertDialog.Builder(this).setTitle(R.string.SMSDefault).setMessage(R.string.notDefault).setPositiveButton(R.string.defaultButton, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    //build intent to change the default
                    Intent intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
                    //set my name as an extra
                    intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, myPackageName);
                    //start the intent
                    startActivity(intent);
                }
            }).setNegativeButton(R.string.defaultCancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    //TODO TOAST message
                    dialogInterface.dismiss();
                }
            }).show();
        }
    }

    @Override
    protected void onResume() {

        Log.i(TAG, "On Resume");
        if (android.os.Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            setDefault();
        }
        super.onResume();
    }
}
