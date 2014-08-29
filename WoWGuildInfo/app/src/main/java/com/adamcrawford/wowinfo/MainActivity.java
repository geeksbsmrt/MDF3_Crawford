package com.adamcrawford.wowinfo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;

import org.jetbrains.annotations.NotNull;


public class MainActivity extends Activity {

    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webView = (WebView) findViewById(R.id.webView);
        webView.addJavascriptInterface(new JavaScriptInterface(this), "Android");
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
            webSettings.setAllowUniversalAccessFromFileURLs(true);
            webSettings.setAllowFileAccessFromFileURLs(true);
        }

        if (savedInstanceState == null) {
            webView.loadUrl("file:///android_asset/www/index.html");
        }
    }
    @Override
    protected void onSaveInstanceState(@NotNull Bundle outState) {
        webView.saveState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Restore the state of the WebView
        webView.restoreState(savedInstanceState);
    }
    public class JavaScriptInterface {
        Context mContext;

        // Instantiate the interface and set the context
        JavaScriptInterface(Context c) {
            mContext = c;
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
