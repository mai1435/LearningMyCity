package learningmycity.content;

import learningmycity.db.DbAdapter;
import learningmycity.util.GPSHandler;

/**
 * A GPS task in the game.
 */
public class GPS extends Task {

	// The GPS Handler for retrieving GPS coordinates.
	private GPSHandler gpsHandler;

	// The longitude for the location of this task.
	private Double longitude;

	// The latitude for the location of this task.
	private Double latitude;

	/**
	 * Constructs a new GPS task.
	 */
	public GPS(int taskId, String description, Question question, int penalty,
			int imageId, Hint[] hints, boolean completed, int wrongAnswers,
			int questId, DbAdapter dbAdapter, GPSHandler gpsHandler,
			double latitude, double longitude, double radius) {
		super(taskId, Task.GPS_TASK, description, question, penalty, imageId,
				hints, completed, wrongAnswers, questId, dbAdapter);
		this.gpsHandler = gpsHandler;
		this.longitude = longitude;
		this.latitude = latitude;

		gpsHandler.setCoordinates(latitude, longitude, radius);
	}

	/**
	 * Starts GPS updates.
	 */
	public void startGPS() {
		gpsHandler.startUpdates();
	}

	/**
	 * Stops GPS updates
	 */
	public void stopGPS() {
		gpsHandler.stopUpdates();
	}

	/**
	 * Returns true if the user is inside the range of the location, false
	 * otherwise.
	 */
	public boolean isInsideTaskRange() {
		return gpsHandler.getInsideTaskRange();
	}

	/**
	 * Returns true if GPSHandler.onLocationChanged method has been called,
	 * false otherwise.
	 */
	public boolean getOnLocationChanged() {
		return gpsHandler.getOnLocationChanged();
	}

	/**
	 * Returns true if GPS is enabled, false otherwise.
	 */
	public boolean checkGPS() {
		return gpsHandler.ckeckGPS();
	}

	/**
	 * Sets GPSHandler.onLocationChanged.
	 */
	public void setOnLocationChanged(boolean onLocationChanged) {
		gpsHandler.setOnLocationChanged(onLocationChanged);
	}

	/**
	 * Returns the longitude of the location.
	 */
	public Double getLongitude() {
		return longitude;
	}

	/**
	 * Returns the latitude of the location.
	 */
	public Double getLatitude() {
		return latitude;
	}

	/**
	 * Validates the location of the user against the task location.
	 */
	@Override
	public boolean validateTask(Object givenAnswer) {
		return isInsideTaskRange();
	}
}
