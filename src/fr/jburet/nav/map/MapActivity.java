package fr.jburet.nav.map;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import fr.jburet.nav.MainActivity;
import fr.jburet.nav.NavActivity;
import fr.jburet.nav.NavApplication;
import fr.jburet.nav.NavConstant;
import fr.jburet.nav.R;
import fr.jburet.nav.database.DatabaseHelper;
import fr.jburet.nav.database.airspace.AirspaceQuery;
import fr.jburet.nav.database.point.Waypoint;
import fr.jburet.nav.database.point.WaypointQuery;
import fr.jburet.nav.database.point.WaypointQueryImpl;
import fr.jburet.nav.gps.GpsBackgroundService;
import fr.jburet.nav.gps.GpsService;
import fr.jburet.nav.gps.PositionData;
import fr.jburet.nav.gps.binder.GpsServiceBinder;
import fr.jburet.nav.gps.listener.GpsServiceListener;
import fr.jburet.nav.map.component.MapView;
import fr.jburet.nav.map.component.NavBox;
import fr.jburet.nav.navigation.ChooseDestinationActivity;
import fr.jburet.nav.navigation.NavigationBackgroundService;
import fr.jburet.nav.navigation.NavigationService;
import fr.jburet.nav.navigation.binder.NavigationServiceBinder;
import fr.jburet.nav.navigation.listener.NavigationServiceListener;
import fr.jburet.nav.utils.ResultCallback;

public class MapActivity extends MainActivity {

	private static final int CONTEXTUAL_MENU_GOTO = 1;
	private static final int CONTEXTUAL_MENU_NEARESTLANDPOINT = 2;

	/** holds the map of callbacks */
	private Map<Integer, ResultCallback> callbackMap = new HashMap<Integer, ResultCallback>();

	private Map<Integer, NavBox> navboxes = new HashMap<Integer, NavBox>();

	private RelativeLayout viewGroup;

	private NavigationBackgroundService navigationService;

	private MapView mapview;

	private WaypointQuery waypointQuery;
	
	private AirspaceQuery airspaceQuery;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		waypointQuery = ((NavApplication) getApplication()).getWaypointQuery();
		airspaceQuery = ((NavApplication) getApplication()).getAirspaceQuery();
		viewGroup = new RelativeLayout(this);
		viewGroup.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		setContentView(viewGroup);
		mapview = new MapView(this, null);
		viewGroup.addView(mapview, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		updateNavbox();
		bindGpsPositionListener();
		bindNavigationListener();
		// Context menu register
		registerForContextMenu(mapview);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		int idItem = item.getItemId();
		switch (idItem) {
		case CONTEXTUAL_MENU_GOTO:
			launchSubActivity(ChooseDestinationActivity.class, new ResultCallback() {

				public void resultOk(Intent data) {
					Log.i("Map Activity",
							"Destination choosed code : "
									+ data.getStringExtra(NavConstant.CHOOSE_WAYPOINT_WAYPOINT_CODE_RESULT));
					Waypoint dest = waypointQuery.findByCode(data
							.getStringExtra(NavConstant.CHOOSE_WAYPOINT_WAYPOINT_CODE_RESULT));
					// Update navbox destination
					if (navboxes.get(NavboxIndicator.DESTINATION) != null) {
						navboxes.get(NavboxIndicator.DESTINATION).updateValue(dest.getName());
					}
					// Assign value to navigation service
					navigationService.assignDestination(dest.getCode());
				}

				public void resultCancel(Intent data) {
					Log.i("Map Activity", "Cancel choose destination");
				}
			});
			return true;
		case CONTEXTUAL_MENU_NEARESTLANDPOINT:
			// Start choose a destination activity filter with 15 more nearest
			// landing point
			return true;
		default:
			return false;
		}

	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.setHeaderTitle(R.string.map_cm_title);
		menu.add(0, CONTEXTUAL_MENU_GOTO, 0, R.string.map_cm_goto);
	}

	private void updateNavbox() {
		// Add a navbox
		NavBox navboxSpeed = new NavBox(this, getString(R.string.navbox_grd_speed), "m/s");
		navboxSpeed.setId(R.id.navbox_ground_speed);
		NavBox navboxAltitude = new NavBox(this, getString(R.string.navbox_gps_alt), "m");
		navboxAltitude.setId(R.id.navbox_gps_alt);
		NavBox navboxDestination = new NavBox(this, getString(R.string.navbox_destination), "");
		navboxDestination.setId(R.id.navbox_destination);

		navboxes.put(NavboxIndicator.GRD_SPEED, navboxSpeed);
		navboxes.put(NavboxIndicator.GPS_ALT, navboxAltitude);
		navboxes.put(NavboxIndicator.DESTINATION, navboxDestination);

		LayoutParams navboxLayoutParam = new LayoutParams(NavBox.NAV_BOX_STANDARD_WIDTH, NavBox.NAV_BOX_2_LINE_HEIGHT);
		navboxLayoutParam.setMargins(5, 5, 5, 5);
		navboxLayoutParam.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		viewGroup.addView(navboxSpeed, navboxLayoutParam);

		navboxLayoutParam = new LayoutParams(NavBox.NAV_BOX_STANDARD_WIDTH, NavBox.NAV_BOX_2_LINE_HEIGHT);
		navboxLayoutParam.setMargins(0, 5, 5, 5);
		navboxLayoutParam.addRule(RelativeLayout.RIGHT_OF, navboxSpeed.getId());
		navboxLayoutParam.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		viewGroup.addView(navboxAltitude, navboxLayoutParam);

		navboxLayoutParam = new LayoutParams(NavBox.NAV_BOX_STANDARD_WIDTH, NavBox.NAV_BOX_2_LINE_HEIGHT);
		navboxLayoutParam.setMargins(0, 5, 5, 5);
		navboxLayoutParam.addRule(RelativeLayout.RIGHT_OF, navboxAltitude.getId());
		navboxLayoutParam.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		viewGroup.addView(navboxDestination, navboxLayoutParam);
	}

	// Subactivity managements
	public void launchSubActivity(Class subActivityClass, ResultCallback callback) {
		Intent i = new Intent(this, subActivityClass);
		Random rand = new Random();
		int correlationId = rand.nextInt();
		callbackMap.put(correlationId, callback);
		startActivityForResult(i, correlationId);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		try {
			ResultCallback callback = callbackMap.get(requestCode);

			switch (resultCode) {
			case Activity.RESULT_CANCELED:
				callback.resultCancel(data);
				callbackMap.remove(resultCode);
				break;
			case Activity.RESULT_OK:
				callback.resultOk(data);
				callbackMap.remove(resultCode);
				break;
			default:
				Log.e("Map Activity", "Couldn't find callback handler for correlationId");
			}
		} catch (Exception e) {
			Log.e("Map Activity", "Problem processing result from sub-activity", e);
		}
	}

	private void bindGpsPositionListener() {
		final GpsServiceListener listener = new GpsServiceListener() {
			public void positionChanged(final PositionData data) {
				MapActivity.this.runOnUiThread(new Runnable() {
					public void run() {
						for (int indic : NavboxIndicator.ALL_INDICATORS) {
							switch (indic) {
							case NavboxIndicator.GRD_SPEED:
								// Convert Speed TO km
								// TODO Unit should be configured
								navboxes.get(NavboxIndicator.GRD_SPEED).updateValue(
										Float.toString(data.getSpeed() * 3.6f));
								break;
							case NavboxIndicator.GPS_ALT:
								navboxes.get(NavboxIndicator.GPS_ALT).updateValue(Double.toString(data.getAltitude()));
								break;
							}
						}
						mapview.setPositionData(data);
					}
				});
			}
		};

		Intent intent = new Intent(this, GpsService.class);

		ServiceConnection connection = new ServiceConnection() {
			public void onServiceConnected(ComponentName name, IBinder service) {
				Log.i("DebugActivity", "Connected to GPS service!");
				GpsBackgroundService gpsService = ((GpsServiceBinder) service).getService();
				gpsService.addListener(listener);
			}

			public void onServiceDisconnected(ComponentName name) {
				Log.i("DebugActivity", "Disconnected from GPS service!");
			}

		};

		bindService(intent, connection, Context.BIND_AUTO_CREATE);
	}

	// Update navigationinfo
	private void bindNavigationListener() {
		final NavigationServiceListener listener = new NavigationServiceListener() {

			public void updateNavigationInfo(float distance, float altitude, float bearing, float finesse) {
				MapActivity.this.runOnUiThread(new Runnable() {
					public void run() {

					}
				});
			}
		};

		Intent intent = new Intent(this, NavigationService.class);

		ServiceConnection connection = new ServiceConnection() {
			public void onServiceConnected(ComponentName name, IBinder service) {
				Log.i("MapActivity", "Connected to Navigation service!");
				navigationService = ((NavigationServiceBinder) service).getService();
				navigationService.addListener(listener);
			}

			public void onServiceDisconnected(ComponentName name) {
				Log.i("MapActivity", "Disconnected from Navigation service!");
			}

		};

		bindService(intent, connection, Context.BIND_AUTO_CREATE);
	}

	public void onMapBoundChange(double mapLeftLongitude, double mapTopLatitude, double mapRightLongitude,
			double mapBottomLatitude) {
		mapview.setWaypointToDraw(waypointQuery.listAll());
		mapview.setAirspaceToDraw(airspaceQuery.listAll());
	}
}
