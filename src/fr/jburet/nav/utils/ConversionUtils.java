package fr.jburet.nav.utils;

public class ConversionUtils {
	// Conversion degree to kilometer at equateur
	public final static float DEGREE_TO_DISTANCE = 111.325f;
	
	public static double distanceToLatitude(double distance){
		return distance/DEGREE_TO_DISTANCE;
	}
	
	public static double distanceToLongitude(double distance, double latitude){
		return distance / (Math.cos(latitude) * DEGREE_TO_DISTANCE);
	}
}
