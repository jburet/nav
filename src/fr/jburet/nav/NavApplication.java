package fr.jburet.nav;

import fr.jburet.nav.database.DatabaseHelper;
import fr.jburet.nav.database.point.WaypointQuery;
import fr.jburet.nav.database.point.WaypointQueryImpl;
import android.app.Application;

public class NavApplication extends Application {

	private WaypointQuery waypointQuery;

	private DatabaseHelper dbHelper;

	public WaypointQuery getWaypointQuery() {
		return waypointQuery;
	}

	@Override
	public void onCreate() {
		dbHelper = new DatabaseHelper(this);
		waypointQuery = new WaypointQueryImpl(dbHelper);
	}

	@Override
	public void onTerminate() {
		dbHelper.closeDb();
	}

}
