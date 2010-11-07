package fr.jburet.nav.navigation;

import fr.jburet.nav.NavApplication;
import fr.jburet.nav.NavConstant;
import fr.jburet.nav.R;
import fr.jburet.nav.database.DatabaseHelper;
import fr.jburet.nav.database.point.Waypoint;
import fr.jburet.nav.database.point.WaypointQuery;
import fr.jburet.nav.database.point.WaypointQueryImpl;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

public class ChooseDestinationActivity extends ListActivity {

	private WaypointQuery waypointQuery;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.choose_destination);
		waypointQuery = ((NavApplication)getApplication()).getWaypointQuery();
		setListAdapter(new DestinationAdapter(this, waypointQuery.listAll()));
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		returnOkToCaller((Waypoint) l.getItemAtPosition(position));
	}

	public void returnOkToCaller(Waypoint destination) {
		Intent result = new Intent();
		result.putExtra(NavConstant.CHOOSE_WAYPOINT_WAYPOINT_CODE_RESULT, destination.getCode());
		// sets the result for the calling activity
		setResult(RESULT_OK, result);

		// equivalent of 'return'
		finish();
	}

}
