package fr.jburet.nav.navigation;

import java.util.ArrayList;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.IBinder;
import android.util.Log;

import fr.jburet.nav.NavApplication;
import fr.jburet.nav.database.DatabaseHelper;
import fr.jburet.nav.database.point.Waypoint;
import fr.jburet.nav.database.point.WaypointQuery;
import fr.jburet.nav.database.point.WaypointQueryImpl;
import fr.jburet.nav.gps.GpsBackgroundService;
import fr.jburet.nav.gps.GpsService;
import fr.jburet.nav.gps.PositionData;
import fr.jburet.nav.gps.binder.GpsServiceBinder;
import fr.jburet.nav.gps.listener.GpsServiceListener;
import fr.jburet.nav.navigation.binder.NavigationServiceBinder;
import fr.jburet.nav.navigation.listener.NavigationServiceListener;

public class NavigationService extends Service implements NavigationBackgroundService {

	private ArrayList<NavigationServiceListener> servicesListener;

	private Waypoint principalDestination;

	private PositionData lastPositionData;

	private float[] principaleDestRes = new float[5];

	private NavigationServiceBinder navigationServiceBinder;

	private WaypointQuery waypointQuery;

	public void addListener(NavigationServiceListener listener) {
		if (servicesListener == null) {
			servicesListener = new ArrayList<NavigationServiceListener>();
		}
		servicesListener.add(listener);
	}

	public void removeListener(NavigationServiceListener listener) {
		servicesListener.remove(listener);
	}

	public void assignDestination(String code) {
		principalDestination = waypointQuery.findByCode(code);
	}

	// Register to GPS service and update navigation //
	private void bindGpsPositionListener() {
		final GpsServiceListener listener = new GpsServiceListener() {
			public void positionChanged(final PositionData data) {
				if (servicesListener != null && servicesListener.size() > 0) {
					new Thread(new Runnable() {
						public void run() {
							lastPositionData = data;
							calculNavigationToDestination(principalDestination, lastPositionData);
						}
					}).start();
				}
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

	@Override
	public void onCreate() {
		waypointQuery = ((NavApplication)getApplication()).getWaypointQuery();
		navigationServiceBinder = new NavigationServiceBinder(this);
		bindGpsPositionListener();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return navigationServiceBinder;
	}

	private void calculNavigationToDestination(Waypoint destination, PositionData positionData) {
		if (destination != null && positionData != null) {
			Location.distanceBetween(positionData.getLatitute(), positionData.getLongitude(),
					destination.getLatitude(), destination.getLongitude(), principaleDestRes);
			principaleDestRes[3] = (float) positionData.getAltitude() - (float) destination.getAltitude();
			principaleDestRes[4] = principaleDestRes[0] / principaleDestRes[3];
			for (NavigationServiceListener nsl : servicesListener) {
				nsl.updateNavigationInfo(principaleDestRes[0], principaleDestRes[2], principaleDestRes[1],
						principaleDestRes[4]);
			}
		}
	}

}
