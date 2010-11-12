package fr.jburet.nav.database.point;

import java.util.List;

import fr.jburet.nav.database.Query;

public interface WaypointQuery extends Query<Waypoint> {

	public List<Waypoint> listByCoord(float latitudeMin, float longitudeMin, float latitudeMax, float longitudeMax);

	public Waypoint findByCode(String code);

}
