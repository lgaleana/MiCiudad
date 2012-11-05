package com.apporta.javascript;

import com.apporta.location.LocationHandler;
import com.apporta.miciudad.MapActivity;

public class JavaScriptInterface {
	
	MapActivity mapActivity;
	LocationHandler locationHandler;
	
	public JavaScriptInterface(MapActivity mActivity) {
		mapActivity = mActivity;
		locationHandler = mapActivity.getLocationHandler();
	}
	
	public double getUserLat() {
		return locationHandler.getLat();
	}
	
	public double getUserLng() {
		return locationHandler.getLng();
	}
	
	public void showEventSelectorDialog(double lat, double lng) {
		mapActivity.setEventSelectorData(lat, lng);
		mapActivity.showDialog(MapActivity.EVENT_SELECTOR_DIALOG);
	}
	
	public void showEventInfoDialog(String eventType, String eventTime, String eventDescription) {
		mapActivity.setEventInfoData(eventType, eventTime, eventDescription);
		mapActivity.showDialog(MapActivity.EVENT_INFO_DIALOG);
	}
	
	public void showDirectionsInfoDialog(String message) {
		mapActivity.setDirectionsInfoData(message);
		mapActivity.showDialog(MapActivity.DIRECTIONS_INFO_DIALOG);
	}
}
