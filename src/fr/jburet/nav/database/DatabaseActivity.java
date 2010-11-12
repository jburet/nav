package fr.jburet.nav.database;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import fr.jburet.nav.MainActivity;
import fr.jburet.nav.NavApplication;
import fr.jburet.nav.R;
import fr.jburet.nav.database.airspace.AirspaceImporterFromOpenair;
import fr.jburet.nav.database.airspace.AirspaceQuery;
import fr.jburet.nav.database.point.CannotImportException;
import fr.jburet.nav.database.point.WaypointImporterFromCup;
import fr.jburet.nav.database.point.WaypointQuery;

public class DatabaseActivity extends MainActivity {

	private WaypointQuery waypointQuery;
	private AirspaceQuery airspaceQuery;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.database);
		waypointQuery = ((NavApplication) getApplication()).getWaypointQuery();
		airspaceQuery = ((NavApplication) getApplication()).getAirspaceQuery();
		findViewById(R.id.database_waypoint_import).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Thread importer = new Thread(new ImportWaypoint());
				importer.start();
			}
		});
		findViewById(R.id.database_airspace_import).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Thread importer = new Thread(new ImportAirspace());
				importer.start();
			}
		});
	}

	class ImportWaypoint implements Runnable {

		public void run() {
			try {
				new WaypointImporterFromCup(waypointQuery)
						.parseFile(((EditText) findViewById(R.id.database_waypoint_file)).getText().toString());
			} catch (CannotImportException e) {
				Log.e("DATABASE_ACTIVITY", "Cannot import file : "
						+ ((EditText) findViewById(R.id.database_waypoint_file)).getText().toString(), e);
			}
		}
	}

	class ImportAirspace implements Runnable {

		public void run() {
			try {
				new AirspaceImporterFromOpenair(airspaceQuery)
						.parseFile(((EditText) findViewById(R.id.database_airspace_file)).getText().toString());
			} catch (CannotImportException e) {
				Log.e("DATABASE_ACTIVITY", "Cannot import file : "
						+ ((EditText) findViewById(R.id.database_airspace_file)).getText().toString(), e);
			}
		}
	}
}
