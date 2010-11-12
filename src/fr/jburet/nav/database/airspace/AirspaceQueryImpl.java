package fr.jburet.nav.database.airspace;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;

import fr.jburet.nav.database.DatabaseHelper;
import fr.jburet.nav.database.point.WaypointColumn;

public class AirspaceQueryImpl implements AirspaceQuery {

	private static final String LIST_ALL_QUERY = "select * from airspace";

	private static final String LIST_BY_COORD = "select * from airspace where airspace.MAX_LAT > ? AND airspace.MIN_LAT < ? AND airspace.MIN_LONG < ? AND airspace.MAX_LONG > ?";

	private DatabaseHelper databaseHelper;

	private AirspaceMapper mapper;

	public AirspaceQueryImpl(DatabaseHelper databaseHelper) {
		this.databaseHelper = databaseHelper;
		this.mapper = new AirspaceMapper();
	}

	public void delete(Airspace entity) {
		// TODO Auto-generated method stub

	}

	public void deleteByPk(Serializable pk) {
		// TODO Auto-generated method stub

	}

	public Airspace selectByPk(Serializable pk) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Airspace> listAll() {
		return databaseHelper.executeQuery(LIST_ALL_QUERY, new String[] {}, mapper);
	}

	public void deleteAll() {
		// TODO Auto-generated method stub

	}

	public List<Airspace> listAirspaceInPolygon(float topLatitude, float leftLongitude, float bottomLatitude,
			float rigthLongitude) {
		return databaseHelper.executeQuery(
				LIST_BY_COORD,
				new String[] { Float.toString(bottomLatitude), Float.toString(topLatitude),
						Float.toString(rigthLongitude), Float.toString(leftLongitude) }, mapper);
	}

	public Airspace save(Airspace newEntity) {
		ContentValues values = new ContentValues();
		values.put(AirspaceColumn.CODE, newEntity.getCode());
		values.put(AirspaceColumn.NAME, newEntity.getLongName());
		values.put(AirspaceColumn.CLASS, newEntity.getClasse());
		values.put(AirspaceColumn.ALT_BOTTOM, newEntity.getAltitudeBottom());
		values.put(AirspaceColumn.ALT_TOP, newEntity.getAltitudeTop());
		values.put(AirspaceColumn.SHAPE, newEntity.getShape());
		values.put(AirspaceColumn.MIN_LAT, newEntity.getMinLatitude());
		values.put(AirspaceColumn.MIN_LONG, newEntity.getMinLongitude());
		values.put(AirspaceColumn.MAX_LAT, newEntity.getMaxLatitude());
		values.put(AirspaceColumn.MAX_LONG, newEntity.getMaxLongitude());
		databaseHelper.insertNewRow("AIRSPACE", values);
		return newEntity;
	}

}
