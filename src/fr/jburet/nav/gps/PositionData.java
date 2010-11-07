package fr.jburet.nav.gps;

public class PositionData {

	private double latitute;
	private double longitude;
	private double altitude;
	private float speed;
	private float bearing;

	public PositionData(double latitute, double longitude, double altitude, float speed,
			float bearing) {
		this.latitute = latitute;
		this.longitude = longitude;
		this.altitude = altitude;
		this.speed = speed;
		this.bearing = bearing;
	}

	public double getLatitute() {
		return latitute;
	}
	
	public double getLongitude() {
		return longitude;
	}

	public double getAltitude() {
		return altitude;
	}

	public float getSpeed() {
		return speed;
	}

	public float getBearing() {
		return bearing;
	}

}
