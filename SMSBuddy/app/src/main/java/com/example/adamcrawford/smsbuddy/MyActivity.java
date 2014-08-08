package com.example.adamcrawford.smsbuddy;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


public class MyActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        if (android.os.Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            setDefault();
        }
    }

    @TargetApi(19)
    private void setDefault(){
        final String myPackageName = getPackageName();
        if (!Telephony.Sms.getDefaultSmsPackage(this).equals(myPackageName) || Telephony.Sms.getDefaultSmsPackage(this).isEmpty()) {
            // App is not default.
            // Show the "not currently set as the default SMS app" interface
            AlertDialog alert = new AlertDialog.Builder(this).setTitle(R.string.SMSDefault).setMessage(R.string.notDefault).setPositiveButton(R.string.defaultButton, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent =
                            new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
                    intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME,
                            myPackageName);
                    startActivity(intent);
                }
            }).setNegativeButton(R.string.defaultCancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            }).show();
        }
    }
}
