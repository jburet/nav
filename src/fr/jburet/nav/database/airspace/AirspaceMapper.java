package fr.jburet.nav.database.airspace;

import android.database.Cursor;
import fr.jburet.nav.database.Mapper;
import fr.jburet.nav.database.point.WaypointColumn;

public class AirspaceMapper implements Mapper<Airspace> {

	public Airspace mapRow(Cursor cursor, int rowNumber) {
		Airspace airspace = new Airspace();
		airspace.setCode(cursor.getString(cursor.getColumnIndex(AirspaceColumn.CODE)));
		airspace.setLongName(cursor.getString(cursor.getColumnIndex(AirspaceColumn.NAME)));
		airspace.setClasse(cursor.getString(cursor.getColumnIndex(AirspaceColumn.CLASS)));
		airspace.setAltitudeBottom(cursor.getString(cursor.getColumnIndex(AirspaceColumn.ALT_BOTTOM)));
		airspace.setAltitudeTop(cursor.getString(cursor.getColumnIndex(AirspaceColumn.ALT_TOP)));
		airspace.setShape(cursor.getString(cursor.getColumnIndex(AirspaceColumn.SHAPE)));
		airspace.setMinLatitude(cursor.getFloat(cursor.getColumnIndex(AirspaceColumn.MIN_LAT)));
		airspace.setMaxLatitude(cursor.getFloat(cursor.getColumnIndex(AirspaceColumn.MAX_LAT)));
		airspace.setMinLongitude(cursor.getFloat(cursor.getColumnIndex(AirspaceColumn.MIN_LONG)));
		airspace.setMaxLongitude(cursor.getFloat(cursor.getColumnIndex(AirspaceColumn.MAX_LONG)));
		return airspace;
	}

}
