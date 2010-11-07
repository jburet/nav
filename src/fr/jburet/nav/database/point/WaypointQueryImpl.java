package fr.jburet.nav.database.point;

import java.io.Serializable;
import java.util.List;

import fr.jburet.nav.database.DatabaseHelper;
import fr.jburet.nav.database.Mapper;

public class WaypointQueryImpl implements WaypointQuery {

	private static final String LIST_ALL_QUERY = "select * from waypoint";

	private static final String FIND_BY_CODE_QUERY = "select * from waypoint where waypoint.code = ? ";

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

	public List<Waypoint> listByDistance(float latitude, float longitude, float distance) {
		// TODO Auto-generated method stub
		return null;
	}

	public Waypoint findByCode(String code) {
		List<Waypoint> res = databaseHelper.executeQuery(FIND_BY_CODE_QUERY, new String[] { code }, mapper);
		if (res != null && res.size() == 1) {
			return res.get(0);
		}
		return null;
	}

}
