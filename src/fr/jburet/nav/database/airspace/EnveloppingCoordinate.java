package fr.jburet.nav.database.airspace;

public class EnveloppingCoordinate {
	private float minLatitude;
	private float maxLatitude;
	private float minLongitude;
	private float maxLongitude;

	public EnveloppingCoordinate(float minLatitude, float maxLatitude, float minLongitude, float maxLongitude) {
		super();
		this.minLatitude = minLatitude;
		this.maxLatitude = maxLatitude;
		this.minLongitude = minLongitude;
		this.maxLongitude = maxLongitude;
	}

	public float getMinLatitude() {
		return minLatitude;
	}

	public float getMaxLatitude() {
		return maxLatitude;
	}

	public float getMinLongitude() {
		return minLongitude;
	}

	public float getMaxLongitude() {
		return maxLongitude;
	}

}
