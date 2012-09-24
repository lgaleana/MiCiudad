package com.apporta.javascript;

import com.apporta.location.LocationHandler;
import com.apporta.miciudad.MapActivity;

public class JavascriptInterface {
	
	MapActivity mapActivity;
	LocationHandler locationHandler;
	
	public JavascriptInterface(MapActivity mActivity) {
		mapActivity = mActivity;
		locationHandler = mapActivity.getLocationHandler();
	}
	
	public double getUserLat() {
		return locationHandler.getLat();
	}
	
	public double getUserLng() {
		return locationHandler.getLng();
	}
	
	public void showDialog(int dialog) {
		mapActivity.showDialog(dialog);
	}
}
