package com.example.adamcrawford.socialite.details;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

import com.example.adamcrawford.socialite.R;

public class DetailActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        WebView webView = (WebView) findViewById(R.id.webView);


    }
}
