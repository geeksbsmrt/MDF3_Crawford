package com.example.adamcrawford.smsbuddy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class SMSActivity extends Activity {

    private String TAG = "SMSA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);
        this.setTitle(R.string.newMessage);

        EditText to = (EditText) findViewById(R.id.toEdit);
        final EditText msg = (EditText) findViewById(R.id.messageEdit);
        Button send = (Button) findViewById(R.id.sendButton);
        ListView msgList = (ListView) findViewById(R.id.msgList);

        Bundle intent = getIntent().getExtras();

        msg.setText(intent.getString(Intent.EXTRA_TEXT));

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!msg.getText().toString().equals("")) {
                    Log.i(TAG, msg.getText().toString());

                } else {
                    Log.e(TAG, "Message blank");
                }
            }
        });

    }
}
