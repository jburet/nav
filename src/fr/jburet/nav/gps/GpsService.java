package fr.jburet.nav.gps;

import java.util.ArrayList;

import fr.jburet.nav.gps.binder.GpsServiceBinder;
import fr.jburet.nav.gps.listener.GpsServiceListener;
import fr.jburet.nav.gps.listener.GpsServiceStatusListener;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.GpsStatus;
import android.location.GpsStatus.Listener;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

public class GpsService extends Service implements GpsBackgroundService, GpsStatusBackgroundService, LocationListener,
		Listener {

	private static final long GPS_UPDATE_TIME = 1000l;

	private static final int NB_SAVED_LOCATION = 200;

	private GpsServiceBinder binder;

	private String gpsProvider;
	private LocationManager locationManager;

	private ArrayList<GpsServiceListener> servicesListener;
	private ArrayList<GpsServiceStatusListener> servicesStatusListener;

	// Location buffer management
	private Location[] locations = new Location[NB_SAVED_LOCATION];
	private int locationsIndex = 0;

	// Gps status
	private GpsStatus gpsStatus = null;

	// Distance, bearing and speed calcul result
	private float[] dbsResult = new float[5];

	private boolean simulationMode = true;

	@Override
	public void onCreate() {
		// Start mandatory services
		// GPS listner
		if (simulationMode) {
			GpsMock gpsMock = new GpsMock(this);
			Thread thread = new Thread(gpsMock);
			thread.start();
		} else {
			locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			// Register gps status listener
			locationManager.addGpsStatusListener(this);
			// Register for location
			Criteria gpsCriteria = new Criteria();
			gpsCriteria.setAccuracy(Criteria.ACCURACY_FINE);
			gpsCriteria.setAltitudeRequired(true);
			gpsProvider = locationManager.getBestProvider(gpsCriteria, true);
			
			// initial value
			onGpsStatusChanged(GpsStatus.GPS_EVENT_SATELLITE_STATUS);
		}
		binder = new GpsServiceBinder(this, this);

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (!simulationMode) {
			locationManager.requestLocationUpdates(gpsProvider, GPS_UPDATE_TIME, 0, this);
		}
		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}

	public void addListener(GpsServiceListener listener) {
		if (servicesListener == null) {
			servicesListener = new ArrayList<GpsServiceListener>();
		}
		servicesListener.add(listener);
	}

	public void removeListener(GpsServiceListener listener) {
		if (servicesListener != null) {
			servicesListener.remove(listener);
		}
	}

	public void addStatusListener(GpsServiceStatusListener listener) {
		if (servicesStatusListener == null) {
			servicesStatusListener = new ArrayList<GpsServiceStatusListener>();
		}
		servicesStatusListener.add(listener);
	}

	public void removeStatusListener(GpsServiceStatusListener listener) {
		if (servicesStatusListener != null) {
			servicesStatusListener.remove(listener);
		}
	}

	// LOCATION LISTENER IMPL //
	public void onLocationChanged(Location location) {
		PositionData positionData;
		positionData = calculPositionData(location);
		if (servicesListener != null) {
			for (GpsServiceListener gpsServiceListener : servicesListener) {
				gpsServiceListener.positionChanged(positionData);
			}
		}
	}

	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	// GPS Status listener //
	public void onGpsStatusChanged(int event) {
		if (servicesStatusListener != null) {
			switch (event) {
			case GpsStatus.GPS_EVENT_STARTED:
				for (GpsServiceStatusListener gpsServiceStatusListener : servicesStatusListener) {
					gpsServiceStatusListener.gpsStarted();
				}
				break;
			case GpsStatus.GPS_EVENT_STOPPED:
				for (GpsServiceStatusListener gpsServiceStatusListener : servicesStatusListener) {
					gpsServiceStatusListener.gpsStoped();
				}
				break;
			case GpsStatus.GPS_EVENT_FIRST_FIX:
				for (GpsServiceStatusListener gpsServiceStatusListener : servicesStatusListener) {
					// FIXME Do something
				}
				break;
			case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
				for (GpsServiceStatusListener gpsServiceStatusListener : servicesStatusListener) {
					gpsStatus = locationManager.getGpsStatus(gpsStatus);
					gpsServiceStatusListener.gpsStatusChanged(gpsStatus);
				}
				break;
			}
		}
	}

	// Locations store management //
	private void addLocation(Location location) {
		locations[locationsIndex] = location;
		locationsIndex = locationsIndex++ % NB_SAVED_LOCATION;
	}

	private Location getLastLocation() {
		return locations[locationsIndex];
	}

	// gps service impl //

	private PositionData calculPositionData(Location location) {
		Location lastLocation = getLastLocation();
		if (lastLocation != null) {
			Location.distanceBetween(lastLocation.getLatitude(), lastLocation.getLongitude(), location.getLatitude(),
					location.getLongitude(), dbsResult);
			if (location.hasSpeed()) {
				dbsResult[3] = location.getSpeed();
			} else {
				dbsResult[3] = calculSpeed(location, lastLocation);
			}
		} else {
			dbsResult[0] = 0f;
			dbsResult[1] = 0f;
			dbsResult[2] = 0f;
			dbsResult[3] = 0f;
		}
		PositionData positionData = new PositionData(location.getLatitude(), location.getLongitude(),
				location.getAltitude(), dbsResult[3], dbsResult[2]);
		addLocation(location);
		return positionData;
	}

	private float calculSpeed(Location location, Location lastLocation) {
		return Math.abs(lastLocation.distanceTo(location)) / ((location.getTime() - lastLocation.getTime()) / 1000f);
	}

}
