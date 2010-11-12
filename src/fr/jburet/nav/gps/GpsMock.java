package fr.jburet.nav.gps;

import android.location.Location;

public class GpsMock implements Runnable {

	private GpsService gpsService;

	public GpsMock(GpsService gpsService) {
		this.gpsService = gpsService;
	}

	public void run() {
		while (true) {
			if (gpsService != null) {
				Location loc = new Location("SIMU");
				loc.setLatitude(48.89);
				loc.setLongitude(4.333);
				loc.setAltitude(1000);
				loc.setSpeed(100);
				loc.setBearing(0);
				gpsService.onLocationChanged(loc);
			}
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}