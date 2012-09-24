package com.apporta.location;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

public class LocationHandler implements LocationListener {
	
	private double lat;
	private double lng;
	
	private LocationManager locationManager;
	private Location currentBestLocation;
	
	private boolean gpsEnabled;
	private boolean networkEnabled;
	private boolean listenNetwork;
	
	private static final int TIME_THRESHOLD = 1000 * 60 * 2; // Two minutes.
	private static final int DISTANCE_THRESHOLD = 1000 * 5; // 5km.
	
	public LocationHandler(LocationManager locManager) {
        locationManager = locManager;
        
        gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        networkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        listenNetwork = networkEnabled;
	}
	
	public boolean isProviderEnabled() {
		return (gpsEnabled || networkEnabled);
	}
	
	public boolean updateLastKnownLocation() {
		Location gpsLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		
		if(gpsLocation != null) {
			if(gpsLocation.getAccuracy() > DISTANCE_THRESHOLD && networkEnabled) {
				Location networkLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
					
				if(isBetterLocation(gpsLocation, networkLocation)) {
					currentBestLocation = gpsLocation;
					listenNetwork = false;
				}
				else
					currentBestLocation = networkLocation;
			}
			else {
				currentBestLocation = gpsLocation;
				listenNetwork = false;
			}
		}
		else {
			Location networkLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			
			if(networkLocation != null && networkEnabled)
				currentBestLocation = networkLocation;
			else
				return false;
		}
		
		lat = currentBestLocation.getLatitude();
		lng = currentBestLocation.getLongitude();
		
		return true;
	}
	
	public void requestUpdates() {
		if(gpsEnabled)
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
		if(listenNetwork)
			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
	}
	
	public void removeUpdates() {
		locationManager.removeUpdates(this);
	}
	
	public double getLat() {
		return lat;
	}
	
	public double getLng() {
		return lng;
	}

	/** Modified from source: http://developer.android.com/guide/topics/location/strategies.html#BestEstimate.
	  * <p>
	  * Determines whether one Location reading is better than the current Location fix
	  * @param location  The new Location that you want to evaluate
	  * @param otherLocation  The current Location fix, to which you want to compare the new one
	  */
	private boolean isBetterLocation(Location location, Location otherLocation) {
		final int DISTANCE = 200;
		
	    // Check whether the new location fix is newer or older
	    long timeDelta = location.getTime() - otherLocation.getTime();
	    boolean isSignificantlyNewer = timeDelta > TIME_THRESHOLD;
	    boolean isSignificantlyOlder = timeDelta < -TIME_THRESHOLD;
	    boolean isNewer = timeDelta > 0;

	    // If it's been more than two minutes since the current location, use the new location
	    // because the user has likely moved
	    if (isSignificantlyNewer) {
	        return true;
	    // If the new location is more than two minutes older, it must be worse
	    } else if (isSignificantlyOlder) {
	        return false;
	    }

	    // Check whether the new location fix is more or less accurate
	    int accuracyDelta = (int) (location.getAccuracy() - otherLocation.getAccuracy());
	    boolean isLessAccurate = accuracyDelta > 0;
	    boolean isMoreAccurate = accuracyDelta < 0;
	    boolean isSignificantlyLessAccurate = DISTANCE > 200;

	    // Check if the old and new location are from the same provider
	    boolean isFromSameProvider = isSameProvider(location.getProvider(),
	            otherLocation.getProvider());

	    // Determine location quality using a combination of timeliness and accuracy
	    if (isMoreAccurate) {
	        return true;
	    } else if (isNewer && !isLessAccurate) {
	        return true;
	    } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
	        return true;
	    }
	    return false;
	}

	/** Checks whether two providers are the same */
	private boolean isSameProvider(String provider1, String provider2) {
	    if (provider1 == null) {
	      return provider2 == null;
	    }
	    return provider1.equals(provider2);
	}

	@Override
	public void onLocationChanged(Location location) {
		if(isBetterLocation(location, currentBestLocation)) {
			currentBestLocation = location;
			lat = currentBestLocation.getLatitude();
			lng = currentBestLocation.getLongitude();
		}
		
		if(location.getProvider().equals(LocationManager.GPS_PROVIDER)) {
			if(location.getAccuracy() > DISTANCE_THRESHOLD) {
				if(!listenNetwork) {
					listenNetwork = true;
					removeUpdates();
					requestUpdates();
				}
			}
			else {
				if(listenNetwork) {
					listenNetwork = false;
					removeUpdates();
					requestUpdates();
				}
			}
		}
		
		Log.d("New Location", location.getProvider());
		Log.d("Location", "" + currentBestLocation.getAccuracy());
	}

	@Override
	public void onProviderDisabled(String provider) {
		if(provider.equals(LocationManager.GPS_PROVIDER))
			gpsEnabled = false;
		else {
			networkEnabled = false;
			listenNetwork = false;
		}
	}

	@Override
	public void onProviderEnabled(String provider) {
		if(provider.equals(LocationManager.GPS_PROVIDER))
			gpsEnabled = true;
		else
			networkEnabled = true;
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}
}
