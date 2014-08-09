package com.example.adamcrawford.smsbuddy;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Telephony;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;

import com.example.adamcrawford.smsbuddy.restricted.RestrictedActivity;


public class MyActivity extends Activity {

    private String TAG = "Main Activity";
    public static Context sContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        sContext = this;
        final Button composeButton = (Button) findViewById(R.id.composeButton);
        Button restrictedButton = (Button) findViewById(R.id.restrictedButton);

        composeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent composeIntent = new Intent(sContext, SMSActivity.class);
                startActivity(composeIntent);
            }
        });

        restrictedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchDialog();
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

    private void launchDialog(){
        Dialogs dialog = Dialogs.newInstance();
        dialog.show(getFragmentManager(), "test");
    }


    @Override
    protected void onResume() {

        Log.i(TAG, "On Resume");
        if (android.os.Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            setDefault();
        }
        super.onResume();
    }

    public static class Dialogs extends DialogFragment {
        private String TAG = "Dialogs";
        private Dialog builder;
        private static SharedPreferences preferences;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            builder = new Dialog(getActivity());
            final LayoutInflater inflater = getActivity().getLayoutInflater();
            preferences = PreferenceManager.getDefaultSharedPreferences(sContext);

            Log.i(TAG, "In Restricted");
            View view = inflater.inflate(R.layout.restric_dialog, null);

            ListView restrictedList = (ListView) view.findViewById(R.id.restricted);

            if (!preferences.getAll().toString().equals("{}")) {
                RestrictedActivity.writeList(sContext, restrictedList);
            }

            Button cancelButton = (Button) view.findViewById(R.id.cancel);
            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    builder.dismiss();
                }
            });

            Button addButton = (Button) view.findViewById(R.id.add);
            addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final Dialog addBuilder = new Dialog(getActivity());
                    final LayoutInflater addInflater = getActivity().getLayoutInflater();
                    View addView = addInflater.inflate(R.layout.restric_add, null);
                    final EditText addEdit = (EditText) addView.findViewById(R.id.addName);

                    Button cancel = (Button) addView.findViewById(R.id.addCancel);
                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            addBuilder.dismiss();
                        }
                    });

                    Button add = (Button) addView.findViewById(R.id.addButton);
                    add.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            SharedPreferences.Editor edit = preferences.edit();

                            if (!addEdit.getText().toString().equals("")){
                                edit.putString(addEdit.getText().toString(), addEdit.getText().toString());
                            }
                            Log.i(TAG,"Adding");
                            edit.apply();
                            addBuilder.dismiss();
                            builder.dismiss();
                        }
                    });

                    addBuilder.setContentView(addView);
                    addBuilder.setTitle(R.string.add);
                    addBuilder.show();
                }
            });


            builder.setContentView(view);
            builder.setTitle(R.string.restrict);

        return builder;
        }

        public static Dialogs newInstance(){
            return new Dialogs();
        }

    }
}
