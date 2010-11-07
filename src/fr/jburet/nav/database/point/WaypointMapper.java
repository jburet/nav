package fr.jburet.nav.database.point;

import android.database.Cursor;

import fr.jburet.nav.database.Mapper;

public class WaypointMapper implements Mapper<Waypoint> {

	public Waypoint mapRow(Cursor cursor, int rowNumber) {
		Waypoint waypoint = new Waypoint();
		waypoint.setCode(cursor.getString(cursor.getColumnIndex(WaypointColumn.CODE)));
		waypoint.setName(cursor.getString(cursor.getColumnIndex(WaypointColumn.NAME)));
		waypoint.setLatitude(cursor.getFloat(cursor.getColumnIndex(WaypointColumn.LATITUDE)));
		waypoint.setLongitude(cursor.getFloat(cursor.getColumnIndex(WaypointColumn.LONGITUDE)));
		waypoint.setAltitude(cursor.getFloat(cursor.getColumnIndex(WaypointColumn.ALTITUDE)));
		return waypoint;
	}

}
