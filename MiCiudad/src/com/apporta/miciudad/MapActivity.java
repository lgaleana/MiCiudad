package com.apporta.miciudad;

import com.apporta.location.JavascriptInterface;
import com.apporta.location.LocationHandler;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class MapActivity extends Activity {
	WebView mapView;
	
	LocationHandler locationHandler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        
        mapView = (WebView) findViewById(R.id.webview);
        WebSettings webSettings = mapView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        
        locationHandler = new LocationHandler(this);
        
        mapView.addJavascriptInterface(new JavascriptInterface(locationHandler), "Android");
        
        // Debug from web console.
        mapView.setWebChromeClient(new WebChromeClient() {
      	  public boolean onConsoleMessage(ConsoleMessage cm) {
      	    Log.d("MiCiudad", cm.message() + " -- From line "
      	                         + cm.lineNumber() + " of "
      	                         + cm.sourceId() );
      	    return true;
      	  }
      	});
        
        mapView.loadUrl("http://mytrip.x10.mx/");
    }
    
    @Override
    public void onResume() {
        super.onResume();
        
        mapView.resumeTimers();
        locationHandler.requestUpdates();
    }
    
    @Override
    public void onPause() {
        super.onPause();
        
        mapView.pauseTimers();
        locationHandler.removeUpdates();
    }
}
