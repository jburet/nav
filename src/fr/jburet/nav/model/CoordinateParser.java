package fr.jburet.nav.model;

public class CoordinateParser {
	public static Coordinate parseCoordinate(String firstCoordinate, String firstCoordinateSemi,
			String secondCoordinate, String secondCoordinateSemi) {
		float latitude = 0;
		float longitude = 0;
		if (firstCoordinateSemi.equals("N") || firstCoordinateSemi.equals("S")) {
			latitude = parseLatitude(firstCoordinateSemi, firstCoordinate);
			longitude = parseLongitude(secondCoordinateSemi, secondCoordinate);
		} else {
			latitude = parseLatitude(secondCoordinateSemi, secondCoordinate);
			longitude = parseLongitude(firstCoordinateSemi, firstCoordinate);
		}
		return new Coordinate(latitude, longitude);
	}

	public static float parseLongitude(String coordinateSemi, String coordinate) {
		if (coordinateSemi.equals("E")) {
			return parseAngulaireValue(coordinate);
		} else {
			return -parseAngulaireValue(coordinate);
		}
	}

	public static float parseLatitude(String coordinateSemi, String coordinate) {
		if (coordinateSemi.equals("N")) {
			return parseAngulaireValue(coordinate);
		} else {
			return -parseAngulaireValue(coordinate);
		}
	}

	public static float parseAngulaireValue(String coordinate) {
		// 3 format
		// DD 45.40234
		// DM 45:34.324324
		// DMS 45:34:28
		String[] splittedCoordinate = coordinate.split(":");
		if (splittedCoordinate.length == 1) {
			// DD
			return Float.parseFloat(coordinate);
		} else if (splittedCoordinate.length == 2) {
			// DM
			return Float.parseFloat(splittedCoordinate[0]) + Float.parseFloat(splittedCoordinate[1]) / 60f;
		} else {
			// DMS
			return Float.parseFloat(splittedCoordinate[0]) + Float.parseFloat(splittedCoordinate[1]) / 60f
					+ Float.parseFloat(splittedCoordinate[2]) / 3600f;
		}
	}
}
