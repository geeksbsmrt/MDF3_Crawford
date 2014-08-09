package com.example.adamcrawford.smsbuddy;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;

public class SMSActivity extends Activity {

    private String TAG = "SMSA";
    EditText to;
    private static SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);
        this.setTitle(R.string.newMessage);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        to = (EditText) findViewById(R.id.toEdit);
        final EditText msg = (EditText) findViewById(R.id.messageEdit);
        Button send = (Button) findViewById(R.id.sendButton);
        ListView msgList = (ListView) findViewById(R.id.msgList);
        Button pickButton = (Button) findViewById(R.id.contactsButton);

        Intent intent = getIntent();

        Bundle extras = intent.getExtras();

        Uri data = intent.getData();

        if (!(data == null)) {
            Log.i(TAG, data.getScheme());
            if (data.getScheme().equals("smsto")) {
                String msgTo = null;
                try {
                    msgTo = URLDecoder.decode(String.valueOf(data).replace("smsto:", ""), "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                Log.i(TAG, msgTo);
                to.setText(msgTo);
            }
        }

        if (!(extras == null)) {
            msg.setText(extras.getString(Intent.EXTRA_TEXT));

        }

        pickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent, 0);
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Boolean match = false;
                if (!msg.getText().toString().equals("")) {
                    if (!to.getText().toString().equals("")) {
                        Log.i(TAG, msg.getText().toString());
                        if (preferences != null) {

                            Map<String, ?> names = preferences.getAll();
                            Log.i(TAG, names.toString());

                            //loop through array putting member names into string array
                            for (Map.Entry<String, ?> entry : names.entrySet()) {
                                Log.i(TAG, (String) entry.getValue());
                                if (to.getText().toString().toLowerCase().contains(String.valueOf(entry.getValue()).toLowerCase())) {
                                    //Prompt user
                                    Log.i(TAG, "match");
                                    match = true;
                                } else {
                                    //send
                                    Log.i(TAG, "no Match");
                                }
                            }
                        }
                        if (!match){
                            Log.i(TAG, "sending message");
                        }
                    } else {
                        Log.e(TAG, "To is blank");
                    }
                } else {
                    Log.e(TAG, "Message blank");
                }
            }
        });
    }

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data){
        super.onActivityResult(reqCode, resultCode, data);

        switch (reqCode){
            case 0: {
                if (resultCode == RESULT_OK){
                    String name;
                    String number = "0";
                    Uri contactData = data.getData();
                    Cursor c = getContentResolver().query(contactData, null, null, null, null);
                    if (c.moveToFirst()) {
                        name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                        String id = c.getString(c.getColumnIndex(ContactsContract.Contacts._ID));
                        if (Integer.parseInt(c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                            Cursor p = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id, null, null);
                            while (p.moveToNext()) {
                                int type = p.getInt(p.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
                                if (type == ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE) {
                                    number = p.getString(p.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DATA));
                                    number = number.replaceAll("\\s", "");
                                }
                            }
                        }
                        if (number.equals("0")) {
                            //TODO Make toast to alert user
                            Log.i(TAG, "No mobile number found.");
                        }
                        Log.i(TAG, String.format("Name: %s, Number: %s", name, number));
                        to.setText(String.format("%s <%s>", name, number));
                    }
                }
            }
            break;
        }
    }
}
