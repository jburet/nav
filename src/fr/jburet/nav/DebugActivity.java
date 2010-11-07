package fr.jburet.nav;

import fr.jburet.nav.gps.GpsBackgroundService;
import fr.jburet.nav.gps.GpsService;
import fr.jburet.nav.gps.GpsStatusBackgroundService;
import fr.jburet.nav.gps.PositionData;
import fr.jburet.nav.gps.binder.GpsServiceBinder;
import fr.jburet.nav.gps.listener.GpsServiceListener;
import fr.jburet.nav.gps.listener.GpsServiceStatusListener;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.TextView;

public class DebugActivity extends MainActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.debug);
		bindGpsPositionListener();
		bindGpsStatusListener();
	}

	private void bindGpsPositionListener() {
		final GpsServiceListener listener = new GpsServiceListener() {
			public void positionChanged(final PositionData data) {
				DebugActivity.this.runOnUiThread(new Runnable() {
					public void run() {
						((TextView) findViewById(R.id.debug_latitude_value)).setText(Double.toString(data.getLatitute()));
						((TextView) findViewById(R.id.debug_longitude_value)).setText(Double.toString(data
								.getLongitude()));
						((TextView) findViewById(R.id.debug_speed_value)).setText(Double.toString(data.getSpeed()));
						((TextView) findViewById(R.id.debug_bearing_value)).setText(Double.toString(data.getBearing()));
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

	private void bindGpsStatusListener() {
		final GpsServiceStatusListener listener = new GpsServiceStatusListener() {

			public void gpsStatusChanged(GpsStatus status) {
				int nbSatelliteUsed = 0;
				int nbSatellite = 0;
				((TextView) findViewById(R.id.debug_gps_time_before_fix_value)).setText(Integer.toString(status
						.getTimeToFirstFix()));
				for (GpsSatellite gpsSatellite : status.getSatellites()) {
					nbSatellite++;
					if (gpsSatellite.usedInFix()) {
						nbSatelliteUsed++;
					}
					
				}
				((TextView) findViewById(R.id.debug_gps_up_value)).setText(Integer.toString(nbSatellite)+"/"+Integer.toString(nbSatellite));
			}

			public void gpsStarted() {
				((TextView) findViewById(R.id.debug_gps_state_value)).setText(R.string.debug_gps_state_started);
			}

			public void gpsStoped() {
				((TextView) findViewById(R.id.debug_gps_state_value)).setText(R.string.debug_gps_state_stopped);
			}

		};

		Intent intent = new Intent(this, GpsService.class);

		ServiceConnection connection = new ServiceConnection() {
			public void onServiceConnected(ComponentName name, IBinder service) {
				Log.i("DebugActivity", "Connected to GPS status service!");
				GpsStatusBackgroundService gpsService = ((GpsServiceBinder) service).getStatusService();
				gpsService.addStatusListener(listener);
			}

			public void onServiceDisconnected(ComponentName name) {
				Log.i("DebugActivity", "Disconnected from GPS status service!");
			}

		};

		bindService(intent, connection, Context.BIND_AUTO_CREATE);
	}
}
