package com.apporta.location;

public class JavascriptInterface {
	
	double lat = 0.0;
	double lng = 0.0;
	
	LocationHandler locationHandler;
	
	public JavascriptInterface(LocationHandler locHandler) {
		locationHandler = locHandler;
	}
	
	public double getUserLat() {
		return locationHandler.getLat();
	}
	
	public double getUserLng() {
		return locationHandler.getLng();
	}
}
