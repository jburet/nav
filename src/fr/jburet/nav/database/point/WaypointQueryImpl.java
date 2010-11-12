package fr.jburet.nav.database.point;

import java.io.Serializable;
import java.util.List;

import android.content.ContentValues;
import android.util.Log;

import fr.jburet.nav.database.DatabaseHelper;
import fr.jburet.nav.database.Mapper;

public class WaypointQueryImpl implements WaypointQuery {

	private static final String LIST_ALL_QUERY = "select * from waypoint";

	private static final String FIND_BY_CODE_QUERY = "select * from waypoint where waypoint.code = ? ";

	private static final String FIND_BY_COORD = "select * from waypoint where waypoint.LONGITUDE > ? and waypoint.LONGITUDE < ? and waypoint.LATITUDE > ? and waypoint.LATITUDE < ?";

	private DatabaseHelper databaseHelper;
	private Mapper<Waypoint> mapper;

	public WaypointQueryImpl(DatabaseHelper databaseHelper) {
		this.databaseHelper = databaseHelper;
		this.mapper = new WaypointMapper();
	}

	public void delete(Waypoint entity) {

	}

	public void deleteByPk(Serializable pk) {
		// TODO Auto-generated method stub

	}

	public Waypoint selectByPk(Serializable pk) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Waypoint> listAll() {
		return databaseHelper.executeQuery(LIST_ALL_QUERY, new String[] {}, mapper);
	}

	public void deleteAll() {
		// TODO Auto-generated method stub

	}

	public List<Waypoint> listByCoord(float latitudeMin, float longitudeMin, float latitudeMax, float longitudeMax) {
		return databaseHelper.executeQuery(FIND_BY_COORD,
				new String[] { Float.toString(longitudeMin), Float.toString(longitudeMax), Float.toString(latitudeMin),
						Float.toString(latitudeMax) }, mapper);
	}

	public Waypoint findByCode(String code) {
		List<Waypoint> res = databaseHelper.executeQuery(FIND_BY_CODE_QUERY, new String[] { code }, mapper);
		if (res != null && res.size() == 1) {
			return res.get(0);
		}
		return null;
	}

	public Waypoint save(Waypoint newEntity) {
		ContentValues values = new ContentValues();
		values.put(WaypointColumn.CODE, newEntity.getCode());
		values.put(WaypointColumn.NAME, newEntity.getName());
		values.put(WaypointColumn.LATITUDE, newEntity.getLatitude());
		values.put(WaypointColumn.LONGITUDE, newEntity.getLongitude());
		values.put(WaypointColumn.ALTITUDE, newEntity.getAltitude());
		databaseHelper.insertNewRow("WAYPOINT", values);
		// TODO add pk
		return newEntity;
	}

	public void testImportFile() {
		try {
			new WaypointImporterFromCup(this).parseFile("/sdcard/france.cup");
		} catch (CannotImportException e) {
			Log.e("DATABASE_HELPER", e.getMessage(), e);
		}
	}

}
