package fr.jburet.nav.map;

public interface NavboxIndicator {
	// 2D position
	public static final int LAT = 1;
	public static final int LONG = 2;

	// Dynamic
	public static final int GRD_SPEED = 10;
	public static final int BEARING = 30;

	// 3D position
	public static final int GPS_ALT = 20;

	// Navigation
	public static final int DESTINATION = 40;
	public static final int DESTINATION_BEARING = 41;
	public static final int DESTINATION_DISTANCE = 42;
	public static final int DESTINATION_ALT_DIFF = 43;
	public static final int DESTINATION_FINESSE = 44;

	public static final int[] ALL_INDICATORS = { 1, 2, 10, 20, 30 };

}
