package fr.jburet.nav.database.point;

import java.util.List;

import fr.jburet.nav.database.Query;

public interface WaypointQuery extends Query<Waypoint> {

	public List<Waypoint> listByDistance(float latitude, float longitude, float distance);

	public Waypoint findByCode(String code);

}
