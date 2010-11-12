package fr.jburet.nav.map.component;

import java.util.Collection;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.jburet.nav.database.airspace.Airspace;
import fr.jburet.nav.model.Coordinate;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Typeface;
import android.util.Log;

public class AirspaceDrawer {
	// Pattern matcher
	private final Pattern directionPattern = Pattern.compile("V D=([+-])");
	private final Pattern centerPattern = Pattern.compile("V X=(-?[0-9.]*) (-?[0-9.]*)");
	private final Pattern drawPolygonPointPattern = Pattern.compile("DP (-?[0-9.]*) (-?[0-9.]*)");
	private final Pattern drawArcWithAnglePattern = Pattern.compile("DA ([0-9.]*),([0-9.]*),([0-9.]*)");
	private final Pattern drawArcWithCoordinatePattern = Pattern
			.compile("DB (-?[0-9.]*) (-?[0-9.]*),(-?[0-9.]*) (-?[0-9.]*)");
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

	// MBR polygon calcul
	private Float maxX = null;
	private Float minX = null;
	private Float maxY = null;
	private Float minY = null;

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
				Paint textPaint = getTextPaint(airspace);
				try {
					doDraw(canvas, airspace, fillPaint, strokePaint, textPaint);
				} catch (Exception e) {
					Log.w("AIRSPACE_DRAWER", "Cannot draw an airspace", e);
				}
			}
		}
	}

	private void doDraw(Canvas canvas, Airspace airspace, Paint fillPaint, Paint strokePaint, Paint textPaint) {
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
					center = new Coordinate(Float.parseFloat(currentMatch.group(1)), Float.parseFloat(currentMatch.group(2)));
				}
			} else if (currentElement.startsWith("DB")) {
				// drawing
				currentMatch = drawArcWithCoordinatePattern.matcher(currentElement);
				if (currentMatch.matches()) {
					// FIXME change to true when implemented
					pathUsed = false;
					Coordinate begin = new Coordinate(Float.parseFloat(currentMatch.group(1)), Float.parseFloat(currentMatch.group(2)));
					Coordinate end = new Coordinate(Float.parseFloat(currentMatch.group(3)), Float.parseFloat(currentMatch.group(4)));
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
					// Draw airspace code at center of circle
					if (showAirspaceCode()) {
						if (airspace.getCode() != null) {
							canvas.drawText(airspace.getCode(), mapView.convertLongitudeToPixel(center.getLongitude()),
									mapView.convertLatitudeToPixel(center.getLatitude()), textPaint);
						}
					}
				}
			} else if (currentElement.startsWith("DP")) {
				// drawing
				currentMatch = drawPolygonPointPattern.matcher(currentElement);
				if (currentMatch.matches()) {
					pathUsed = true;
					currentCoordinate = new Coordinate(Float.parseFloat(currentMatch.group(1)), Float.parseFloat(currentMatch.group(2)));
					if (LastCoordinate != null) {
						currentPath.lineTo(mapView.convertLongitudeToPixel(currentCoordinate.getLongitude()),
								mapView.convertLatitudeToPixel(currentCoordinate.getLatitude()));
					} else {
						currentPath.moveTo(mapView.convertLongitudeToPixel(currentCoordinate.getLongitude()),
								mapView.convertLatitudeToPixel(currentCoordinate.getLatitude()));
						firstPathCoordinate = currentCoordinate;
					}
					LastCoordinate = currentCoordinate;
					// update MBR var
					if (maxX == null || currentCoordinate.getLongitude() > maxX) {
						maxX = currentCoordinate.getLongitude();
					}
					if (minX == null || currentCoordinate.getLongitude() < minX) {
						minX = currentCoordinate.getLongitude();
					}
					if (maxY == null || currentCoordinate.getLatitude() > maxY) {
						maxY = currentCoordinate.getLatitude();
					}
					if (minY == null || currentCoordinate.getLatitude() < minY) {
						minY = currentCoordinate.getLatitude();
					}
				}
			}
		}
		if (pathUsed) {
			currentPath.lineTo(mapView.convertLongitudeToPixel(firstPathCoordinate.getLongitude()),
					mapView.convertLatitudeToPixel(firstPathCoordinate.getLatitude()));
			canvas.drawPath(currentPath, fillPaint);
			canvas.drawPath(currentPath, strokePaint);
			// Draw text at 'center' of polygon
			if (showAirspaceCode()) {
				canvas.drawText(airspace.getCode(), mapView.convertLongitudeToPixel((minX + maxX) / 2f),
						mapView.convertLatitudeToPixel((minY + maxY) / 2f), textPaint);
			}
		}
		canvas.restore();
	}



	private void resetVariable() {
		direction = true;
		center = null;
		airwayWidth = 0f;
		maxX = null;
		minX = null;
		maxY = null;
		minY = null;
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

	private Paint getTextPaint(Airspace airspace) {
		Paint paint = new Paint();
		paint.setColor(Color.BLACK);
		paint.setStyle(Style.FILL_AND_STROKE);
		paint.setTextAlign(Align.CENTER);
		paint.setTypeface(Typeface.DEFAULT_BOLD);
		return paint;
	}

	private boolean mustBeDrawed(Airspace airspace) {
		// TODO Auto-generated method stub
		return true;
	}

	private boolean showAirspaceCode() {
		// TODO Auto-generated method stub
		return true;
	}

}
