package fr.jburet.nav;

import fr.jburet.nav.database.DatabaseHelper;
import fr.jburet.nav.database.airspace.AirspaceQuery;
import fr.jburet.nav.database.airspace.AirspaceQueryImpl;
import fr.jburet.nav.database.point.WaypointQuery;
import fr.jburet.nav.database.point.WaypointQueryImpl;
import android.app.Application;

public class NavApplication extends Application {

	private WaypointQuery waypointQuery;
	
	private AirspaceQuery airspaceQuery;

	private DatabaseHelper dbHelper;

	public WaypointQuery getWaypointQuery() {
		return waypointQuery;
	}
	
	public AirspaceQuery getAirspaceQuery() {
		return airspaceQuery;
	}

	@Override
	public void onCreate() {
		dbHelper = new DatabaseHelper(this);
		waypointQuery = new WaypointQueryImpl(dbHelper);
		airspaceQuery = new AirspaceQueryImpl(dbHelper);
	}

	@Override
	public void onTerminate() {
	}

}
