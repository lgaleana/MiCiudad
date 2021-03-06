package com.apporta.miciudad;

import com.apporta.javascript.JavaScriptInterface;
import com.apporta.location.LocationHandler;
import com.apporta.network.NetworkHandler;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.location.LocationManager;

public class MapActivity extends Activity {
	
	private final String URL = "http://mytrip.x10.mx/";
	
	private WebView mapView;
	
	private LocationHandler locationHandler;
	private NetworkHandler networkHandler = new NetworkHandler(URL);
	
	private Spinner eventsSpinner;
	private EditText descriptionText;
	
	private double lat;
	private double lng;
	
	private static String title;
	private static String description;
	
	public static final int EVENT_SELECTOR_DIALOG = 0;
	public static final int EVENT_INFO_DIALOG = 1;
	public static final int DIRECTIONS_INFO_DIALOG = 2;
	public static final int NO_NETWORK_DIALOG = 3;
	public static final int NO_LOCATION_DIALOG = 4;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        
        mapView = (WebView) findViewById(R.id.webview);
        WebSettings webSettings = mapView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        
        locationHandler = new LocationHandler((LocationManager) getSystemService(Context.LOCATION_SERVICE));
        if(locationHandler.isProviderEnabled())
        	locationHandler.updateLastKnownLocation();
        else
        	showDialog(NO_LOCATION_DIALOG);
        
        // Debug from web console.
       setUpWebDebug(true);
       
        if(networkHandler.isOnline(this)) {
        	mapView.addJavascriptInterface(new JavaScriptInterface(this), "Android");
            mapView.loadUrl(URL);
        }
        else
        	showDialog(NO_NETWORK_DIALOG);
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
    
    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog dialog = null;
        
        switch(id) {
        case EVENT_SELECTOR_DIALOG:
        	dialog = eventSelectorDialog();
        	break;
        case EVENT_INFO_DIALOG:
        	dialog = eventInfoDialog();
        	break;
        case DIRECTIONS_INFO_DIALOG:
        	dialog = directionsInfoDialog();
        	break;
        case NO_NETWORK_DIALOG:
        	dialog = exitDialog(R.string.no_network_message);
        	break;
    	case NO_LOCATION_DIALOG:
    		dialog = exitDialog(R.string.no_location_message);
    		break;
        }
        return dialog;
    }
    
    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
    	switch(id) {
    	case EVENT_INFO_DIALOG:
    		AlertDialog alertDialog = (AlertDialog) dialog;
    		alertDialog.setMessage(description);
    		alertDialog.setTitle(title);
    		break;
    	}
    }
    
    private Dialog eventSelectorDialog() {
    	Dialog dialog = new Dialog(MapActivity.this);
    	dialog.setContentView(R.layout.event_selector_dialog);
    	dialog.setTitle(R.string.title_event_selector_dialog);
    	
    	// Defines the spinner that will be in the dialog window.
    	eventsSpinner = (Spinner) dialog.findViewById(R.id.event_selector_spinner);
    	ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.event_selector_options,
    			android.R.layout.simple_spinner_item);
    	adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    	eventsSpinner.setAdapter(adapter);
    	eventsSpinner.setSelection(0);
    	
    	descriptionText = (EditText) dialog.findViewById(R.id.event_selector_text);
    	
    	Button acceptButton = (Button) dialog.findViewById(R.id.event_selector_accept_button);
    	acceptButton.setOnClickListener(new View.OnClickListener() {
    		@Override
    	    public void onClick(View v) {
    	        saveEvent();
    	        removeDialog(EVENT_SELECTOR_DIALOG);
    	    }
    	});
    	
    	Button cancelButton = (Button) dialog.findViewById(R.id.event_selector_cancel_button);
    	cancelButton.setOnClickListener(new View.OnClickListener() {
    		@Override
    	    public void onClick(View v) {
    	    	removeDialog(EVENT_SELECTOR_DIALOG);
    	    }
    	});
    	
    	return dialog;
    }
    
    public Dialog eventInfoDialog() {
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setMessage(description)
    		   .setTitle(title)
    	       .setCancelable(false)
    	       .setPositiveButton(R.string.accept_message, new DialogInterface.OnClickListener() {
    	           public void onClick(DialogInterface dialog, int id) {
    	                dialog.cancel();
    	           }
    	       });
    	return builder.create();
    }
    
    public Dialog directionsInfoDialog() {
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setMessage(description)
    	       .setCancelable(false)
    	       .setPositiveButton(R.string.accept_message, new DialogInterface.OnClickListener() {
    	           public void onClick(DialogInterface dialog, int id) {
    	                dialog.cancel();
    	           }
    	       });
    	return builder.create();
    }
    
    public Dialog exitDialog(int message) {
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setMessage(message)
    	       .setCancelable(false)
    	       .setPositiveButton(R.string.accept_message, new DialogInterface.OnClickListener() {
    	           public void onClick(DialogInterface dialog, int id) {
    	                MapActivity.this.finish();
    	           }
    	       });
    	return builder.create();
    }
    
    public LocationHandler getLocationHandler() {
    	return locationHandler;
    }
    
    public void setEventSelectorData(double latitude, double longitude) {
    	lat = latitude;
    	lng = longitude;
    }
    
    public void setEventInfoData(String eventType, String eventTime, String eventDescription) {
    	title = eventType;
    	description = "Tiempo: " + eventTime + "\n\n" + eventDescription;
    }
    
    public void setDirectionsInfoData(String message) {
    	description = message;
    }
    
    private void saveEvent() {
    	String description = descriptionText.getText().toString();
    	String type = eventsSpinner.getSelectedItem().toString();
    	String address = "events";
    	
    	String data = lat + "|" + lng + "|" + description + "|" + type;
    	
    	networkHandler.execute(NetworkHandler.POST, data, address);
    }
    
    private void setUpWebDebug(boolean flag) {
    	if(flag) {
    		mapView.setWebChromeClient(new WebChromeClient() {
    			public boolean onConsoleMessage(ConsoleMessage cm) {
    				Log.d("MiCiudad", cm.message() + " -- From line "
    	      	                    + cm.lineNumber() + " of "
    	      	                    + cm.sourceId() );
    				return true;
    			}
    		});
    	}
    }
}
