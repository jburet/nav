package fr.jburet.nav.database.airspace;

import java.util.List;

import fr.jburet.nav.database.Query;

public interface AirspaceQuery extends Query<Airspace> {

	public List<Airspace> listAirspaceInPolygon(float topLatitude, float leftLongitude, float bottomLatitude,
			float rigthLongitude);
}
