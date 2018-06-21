package learningmycity.util;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

/**
 * A helper class for retrieving GPS coordinates.
 */
public class GPSHandler implements LocationListener{

	// LocationManager for retrieving GPS coordinates.
	private LocationManager mLocationManager;
	
	// The location the user should find in the given task.
	private Location targetLocation; 
	
	// The radius around the target location. If the user is inside the radius, the task is complete.
	private double radius;
	
	// Tells if the user is inside the range of the location.
	private boolean inRadius;
	
	// True if onLocationChanged method has been called, false otherwise.
	private boolean onLocationChanged;
	
	/**
	 * Constructs a new GPSHandler.
	 */
	public GPSHandler(Context context){
		mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		targetLocation = new Location("");
		radius = 0;
		inRadius = false;
		onLocationChanged = false;
	}
	
	/**
	 * Is fired when a new GPS location has been retrieved. If the user is in range of the location, insideTaskRange is set to true.
	 */
	public void onLocationChanged(Location location) {
		float distance = location.distanceTo(targetLocation);
		radius = radius + location.getAccuracy();
		
		if(distance <= radius){
			inRadius = true;
		}else{
			inRadius = false;
		}
		onLocationChanged = true;
		stopUpdates();
	}
	
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}
	
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * Returns true if the user is in range of the location, false otherwise.
	 */
	public boolean getInsideTaskRange(){
		return inRadius;
	}
	
	/**
	 * Starts GPS updates.
	 */
	public void startUpdates(){
		mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
	}
	
	/**
	 * Stops GPS updates.
	 */
	public void stopUpdates(){
		mLocationManager.removeUpdates(this);
	}
	
	/**
	 * Sets the location that the user should find.
	 */
	public void setCoordinates(double latitude, double longitude, double radius) {
		targetLocation.setLatitude(latitude);
		targetLocation.setLongitude(longitude);
		this.radius = radius;
		
	}
	
	/**
	 * Sets onLocationChanged.
	 */
	public void setOnLocationChanged(boolean onLocationChanged) {
		this.onLocationChanged = onLocationChanged;
	}

	/**
	 * Returns true if onLocationChanged method has been called, false otherwise.
	 */
	public boolean getOnLocationChanged() {
		return onLocationChanged;
	}
	
	/**
	 * Returns true if GPS is enabled, false otherwise.
	 */
	public boolean ckeckGPS(){
		if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
	        return false;
	    }else{
	    	return true;
	    }
	}
}
