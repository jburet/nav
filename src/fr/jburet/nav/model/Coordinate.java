package fr.jburet.nav.model;

public class Coordinate {
	private final float longitude;
	private final float latitude;

	public Coordinate(float longitude, float latitude) {
		this.longitude = longitude;
		this.latitude = latitude;
	}

	public float getLongitude() {
		return longitude;
	}

	public float getLatitude() {
		return latitude;
	}

}
