package fr.jburet.nav.database.airspace;

import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.jburet.nav.model.Coordinate;

public class OpenAirParser {
	
	private final Pattern directionPattern = Pattern.compile("V D=([+-])");
	private final Pattern centerPattern = Pattern.compile("V X=(-?[0-9.]*) (-?[0-9.]*)");
	private final Pattern drawPolygonPointPattern = Pattern.compile("DP (-?[0-9.]*) (-?[0-9.]*)");
	private final Pattern drawArcWithAnglePattern = Pattern.compile("DA ([0-9.]*),([0-9.]*),([0-9.]*)");
	private final Pattern drawArcWithCoordinatePattern = Pattern
			.compile("DB (-?[0-9.]*) (-?[0-9.]*),(-?[0-9.]*) (-?[0-9.]*)");
	private final Pattern drawCirclePattern = Pattern.compile("DC ([0-9.]*)");
	private final Pattern drawAirway = Pattern.compile("DY ([0-9.]*)");

	public EnveloppingCoordinate getEnveloppingCoordinate(Airspace airspace) {
		Float minLatitude = null;
		Float maxLatitude = null;
		Float minLongitude = null;
		Float maxLongitude = null;
		// Parse shape
		Matcher currentMatch;
		boolean direction = true;
		Coordinate center = null;
		Coordinate currentCoordinate = null;
		StringTokenizer st = new StringTokenizer(airspace.getShape(), ";", false);
		while (st.hasMoreElements()) {
			String currentElement = st.nextToken().trim();
			if (currentElement.startsWith("V")) {
				// Variable assignement
				currentMatch = centerPattern.matcher(currentElement);
				if (currentMatch.matches()) {
					center = new Coordinate(Float.parseFloat(currentMatch.group(1)), Float.parseFloat(currentMatch.group(2)));
				}
			} else if (currentElement.startsWith("DB")) {
				// drawing
				currentMatch = drawArcWithCoordinatePattern.matcher(currentElement);
				if (currentMatch.matches()) {
					Coordinate begin = new Coordinate(Float.parseFloat(currentMatch.group(1)), Float.parseFloat(currentMatch.group(2)));
					currentCoordinate = begin;
					if (maxLongitude == null || currentCoordinate.getLongitude() > maxLongitude) {
						maxLongitude = currentCoordinate.getLongitude();
					}
					if (minLongitude == null || currentCoordinate.getLongitude() < minLongitude) {
						minLongitude = currentCoordinate.getLongitude();
					}
					if (maxLatitude == null || currentCoordinate.getLatitude() > maxLatitude) {
						maxLatitude = currentCoordinate.getLatitude();
					}
					if (minLatitude == null || currentCoordinate.getLatitude() < minLatitude) {
						minLatitude = currentCoordinate.getLatitude();
					}
				}
			} else if (currentElement.startsWith("DC")) {
				// drawing
				currentMatch = drawCirclePattern.matcher(currentElement);
				if (currentMatch.matches()) {
					currentCoordinate = center;
					if (maxLongitude == null || currentCoordinate.getLongitude() > maxLongitude) {
						maxLongitude = currentCoordinate.getLongitude();
					}
					if (minLongitude == null || currentCoordinate.getLongitude() < minLongitude) {
						minLongitude = currentCoordinate.getLongitude();
					}
					if (maxLatitude == null || currentCoordinate.getLatitude() > maxLatitude) {
						maxLatitude = currentCoordinate.getLatitude();
					}
					if (minLatitude == null || currentCoordinate.getLatitude() < minLatitude) {
						minLatitude = currentCoordinate.getLatitude();
					}
				}
			} else if (currentElement.startsWith("DP")) {
				// drawing
				currentMatch = drawPolygonPointPattern.matcher(currentElement);
				if (currentMatch.matches()) {
					currentCoordinate = new Coordinate(Float.parseFloat(currentMatch.group(1)), Float.parseFloat(currentMatch.group(2)));
					// update MBR var
					if (maxLongitude == null || currentCoordinate.getLongitude() > maxLongitude) {
						maxLongitude = currentCoordinate.getLongitude();
					}
					if (minLongitude == null || currentCoordinate.getLongitude() < minLongitude) {
						minLongitude = currentCoordinate.getLongitude();
					}
					if (maxLatitude == null || currentCoordinate.getLatitude() > maxLatitude) {
						maxLatitude = currentCoordinate.getLatitude();
					}
					if (minLatitude == null || currentCoordinate.getLatitude() < minLatitude) {
						minLatitude = currentCoordinate.getLatitude();
					}
				}
			}
		}
		return new EnveloppingCoordinate(minLatitude, maxLatitude, minLongitude, maxLongitude);
	}
}
