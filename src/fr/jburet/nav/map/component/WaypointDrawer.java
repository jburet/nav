package fr.jburet.nav.map.component;

import java.util.Collection;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import fr.jburet.nav.database.point.Waypoint;

public class WaypointDrawer {

	private Context context;

	private MapView mapView;

	/** Paint */
	private Paint waypointPaint;
	private Paint textPaint;

	public WaypointDrawer(Context context, MapView mapView) {
		this.context = context;
		this.mapView = mapView;

		waypointPaint = new Paint();
		waypointPaint.setColor(Color.BLUE);
		waypointPaint.setStyle(Style.FILL_AND_STROKE);

		textPaint = new Paint();
		textPaint.setColor(Color.BLACK);
		textPaint.setStyle(Style.FILL_AND_STROKE);
	}

	public void drawWaypoint(Canvas canvas, Collection<Waypoint> waypoints) {
		// Draw waypoints
		canvas.save();
		for (Waypoint dest : waypoints) {
			// See the zoom for choosing :
			// - A circle for all waypoint
			// - A specific more large bitmap for waypoint
			if (mapView.scaleX > 150) {
				canvas.drawCircle(mapView.convertLongitudeToPixel(dest.getLongitude()),
						mapView.convertLatitudeToPixel(dest.getLatitude()), 3, waypointPaint);
			} else {
				canvas.drawCircle(mapView.convertLongitudeToPixel(dest.getLongitude()),
						mapView.convertLatitudeToPixel(dest.getLatitude()), 6, waypointPaint);
				canvas.drawText(dest.getName(), mapView.convertLongitudeToPixel(dest.getLongitude()) + 7,
						mapView.convertLatitudeToPixel(dest.getLatitude()) + 5, textPaint);
			}
		}
		canvas.restore();

	}
}
