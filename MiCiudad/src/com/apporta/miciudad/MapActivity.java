package com.apporta.miciudad;

import com.apporta.location.JavascriptInterface;

import android.os.Bundle;
import android.app.Activity;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class MapActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        
        WebView myWebView = (WebView) findViewById(R.id.webview);
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        
        myWebView.addJavascriptInterface(new JavascriptInterface(), "Android");
        myWebView.loadUrl("http://mytrip.x10.mx/");
    }
}
