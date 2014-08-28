package com.adamcrawford.wowguildinfo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        WebView webView = (WebView) findViewById(R.id.webView);
        WebSettings webSettings = webView.getSettings();
        webView.loadUrl("file:///android_asset/www/index.html");

        webView.addJavascriptInterface(new JavaScriptInterface(this), "Android");
        webSettings.setJavaScriptEnabled(true);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
            webSettings.setAllowUniversalAccessFromFileURLs(true);
            webSettings.setAllowFileAccessFromFileURLs(true);
        }

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading ...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressDialog.hide();
            }
        });

    }

    public class JavaScriptInterface {
        Context mContext;

        // Instantiate the interface and set the context
        JavaScriptInterface(Context c) {
            mContext = c;
        }

        @JavascriptInterface
        public void logMessage(String string){
            Log.i("Android", string);
        }

        @JavascriptInterface
        public void charLookup(String rName, String cName) {
            Log.i("CharLookup", rName + " " +  cName);
            String url = "http://us.battle.net/wow/en/character/" + rName.toLowerCase() + "/" + cName.toLowerCase() + "/simple";
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        }

        @JavascriptInterface
        public void guildLookup(String rName, String gName) {
            Log.i("GuildLookup", rName + " " + gName);
            String url = "http://us.battle.net/wow/en/guild/" + rName.toLowerCase() + "/" + gName.toLowerCase().replace(" ", "_") + "/roster";
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        }
    }
}
