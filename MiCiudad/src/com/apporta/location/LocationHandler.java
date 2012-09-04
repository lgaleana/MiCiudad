package com.apporta.location;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class LocationHandler implements LocationListener {
	
	private LocationManager locationManager;
	private String locationProvider;
	private Location currentLocation;
	
	public LocationHandler(Context context) {
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
	}
	
	public boolean requestUpdates() {
		Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setCostAllowed(false);
        
		locationProvider = locationManager.getBestProvider(criteria, true);
		
		if(locationProvider == null)
			return false;
		else {
			locationManager.requestLocationUpdates(locationProvider, 5000, 50, this);
			return true;
		}
	}
	
	public void removeUpdates() {
		locationManager.removeUpdates(this);
	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		currentLocation = location;
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}
	
	public double getLat() {
		return currentLocation.getLatitude();
	}
	
	public double getLng() {
		return currentLocation.getLongitude();
	}
}
