package fr.jburet.nav.map.component;

import java.util.Collection;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.jburet.nav.database.airspace.Airspace;
import fr.jburet.nav.gps.PositionData;
import fr.jburet.nav.model.Coordinate;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;

public class AirspaceDrawer {
	// Pattern matcher
	private final Pattern directionPattern = Pattern.compile("V D=([+-])");
	private final Pattern centerPattern = Pattern.compile("V X=([0-9:.]*) ([NS]) ([0-9:.]*) ([EW])");
	private final Pattern drawPolygonPointPattern = Pattern.compile("DP ([0-9:.]*) ([NS]) ([0-9:.]*) ([EW])");
	private final Pattern drawArcWithAnglePattern = Pattern.compile("DA ([0-9]*),([0-9]*),([0-9]*)");
	private final Pattern drawArcWithCoordinatePattern = Pattern
			.compile("DB ([0-9:.]*) ([NS]) ([0-9:.]*) ([EW]),([0-9:.]*) ([NS]) ([0-9:.]*) ([EW])");
	private final Pattern drawCirclePattern = Pattern.compile("DC ([0-9.]*)");
	private final Pattern drawAirway = Pattern.compile("DY ([0-9.]*)");

	private Context context;

	private MapView mapView;

	// Openair variable
	private boolean direction = true;
	private Coordinate center = null;
	private Coordinate firstPathCoordinate = null;
	private Coordinate LastCoordinate = null;
	private Coordinate currentCoordinate = null;
	private Path currentPath = null;
	private boolean pathUsed = false;
	private float airwayWidth = 0f;

	public AirspaceDrawer(Context context, MapView mapView) {
		this.context = context;
		this.mapView = mapView;
	}

	public void drawAirspace(Canvas canvas, Collection<Airspace> airspaces) {
		for (Airspace airspace : airspaces) {
			// The class determine the paint to use and if airspace must be
			// drawed
			if (mustBeDrawed(airspace)) {
				resetVariable();
				currentPath = null;
				Paint fillPaint = getFillPaint(airspace);
				Paint strokePaint = getStrokePaint(airspace);
				doDraw(canvas, airspace, fillPaint, strokePaint);
			}
		}
	}

	private void doDraw(Canvas canvas, Airspace airspace, Paint fillPaint, Paint strokePaint) {
		canvas.save();
		Matcher currentMatch;
		// Parse and draw airspace shape
		StringTokenizer st = new StringTokenizer(airspace.getShape(), ";", false);
		currentPath = new Path();
		pathUsed = false;
		firstPathCoordinate = null;
		LastCoordinate = null;
		while (st.hasMoreElements()) {
			String currentElement = st.nextToken().trim();
			if (currentElement.startsWith("V")) {
				// Variable assignement
				currentMatch = directionPattern.matcher(currentElement);
				if (currentMatch.matches()) {
					String directionString = currentMatch.group(1);
					if (directionString.equals("+")) {
						this.direction = true;
					} else {
						this.direction = false;
					}
				}
				currentMatch = centerPattern.matcher(currentElement);
				if (currentMatch.matches()) {
					center = parseCoordinate(currentMatch.group(1), currentMatch.group(2), currentMatch.group(3),
							currentMatch.group(4));
				}
			} else if (currentElement.startsWith("DB")) {
				// drawing
				currentMatch = drawArcWithCoordinatePattern.matcher(currentElement);
				if (currentMatch.matches()) {
					// FIXME change to true when implemented
					pathUsed = false;
					Coordinate begin = parseCoordinate(currentMatch.group(1), currentMatch.group(2),
							currentMatch.group(3), currentMatch.group(4));
					Coordinate end = parseCoordinate(currentMatch.group(5), currentMatch.group(6),
							currentMatch.group(7), currentMatch.group(8));
					// FIXME TO finish

				}
			} else if (currentElement.startsWith("DC")) {
				// drawing
				currentMatch = drawCirclePattern.matcher(currentElement);
				if (currentMatch.matches()) {
					canvas.drawCircle(mapView.convertLongitudeToPixel(center.getLongitude()),
							mapView.convertLatitudeToPixel(center.getLatitude()),
							mapView.convertNmToPixel(Float.parseFloat(currentMatch.group(1))), fillPaint);
					canvas.drawCircle(mapView.convertLongitudeToPixel(center.getLongitude()),
							mapView.convertLatitudeToPixel(center.getLatitude()),
							mapView.convertNmToPixel(Float.parseFloat(currentMatch.group(1))), strokePaint);
				}
			} else if (currentElement.startsWith("DP")) {
				// drawing
				currentMatch = drawPolygonPointPattern.matcher(currentElement);
				if (currentMatch.matches()) {
					pathUsed = true;
					currentCoordinate = parseCoordinate(currentMatch.group(1), currentMatch.group(2),
							currentMatch.group(3), currentMatch.group(4));
					if (LastCoordinate != null) {
						currentPath.lineTo(mapView.convertLongitudeToPixel(currentCoordinate.getLongitude()),
								mapView.convertLatitudeToPixel(currentCoordinate.getLatitude()));
					} else {
						currentPath.moveTo(mapView.convertLongitudeToPixel(currentCoordinate.getLongitude()),
								mapView.convertLatitudeToPixel(currentCoordinate.getLatitude()));
						firstPathCoordinate = currentCoordinate;
					}
					LastCoordinate = currentCoordinate;
				}
			}
		}
		if (pathUsed) {
			currentPath.lineTo(mapView.convertLongitudeToPixel(firstPathCoordinate.getLongitude()),
					mapView.convertLatitudeToPixel(firstPathCoordinate.getLatitude()));
			canvas.drawPath(currentPath, fillPaint);
			canvas.drawPath(currentPath, strokePaint);
		}
		canvas.restore();
	}

	private Coordinate parseCoordinate(String firstCoordinate, String firstCoordinateSemi, String secondCoordinate,
			String secondCoordinateSemi) {
		float latitude = 0;
		float longitude = 0;
		if (firstCoordinateSemi.equals("N") || firstCoordinateSemi.equals("S")) {
			latitude = parseLatitude(firstCoordinateSemi, firstCoordinate);
			longitude = parseLongitude(secondCoordinateSemi, secondCoordinate);
		} else {
			latitude = parseLatitude(secondCoordinateSemi, secondCoordinate);
			longitude = parseLongitude(firstCoordinateSemi, firstCoordinate);
		}
		return new Coordinate(longitude, latitude);
	}

	private float parseLongitude(String coordinateSemi, String coordinate) {
		if (coordinateSemi.equals("E")) {
			return parseAngulaireValue(coordinate);
		} else {
			return -parseAngulaireValue(coordinate);
		}
	}

	private float parseLatitude(String coordinateSemi, String coordinate) {
		if (coordinateSemi.equals("N")) {
			return parseAngulaireValue(coordinate);
		} else {
			return -parseAngulaireValue(coordinate);
		}
	}

	private float parseAngulaireValue(String coordinate) {
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

	private void resetVariable() {
		direction = true;
		center = null;
		airwayWidth = 0f;
	}

	private Paint getFillPaint(Airspace airspace) {
		Paint paint = new Paint();
		paint.setColor(Color.RED);
		paint.setStyle(Style.FILL);
		paint.setAlpha(80);
		return paint;
	}
	
	private Paint getStrokePaint(Airspace airspace) {
		Paint paint = new Paint();
		paint.setColor(Color.RED);
		paint.setStyle(Style.STROKE);
		return paint;
	}

	private boolean mustBeDrawed(Airspace airspace) {
		return true;
	}

}
