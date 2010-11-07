package fr.jburet.nav.gps.listener;

import android.location.GpsStatus;

public interface GpsServiceStatusListener {
	public void gpsStatusChanged(GpsStatus status);

	public void gpsStarted();

	public void gpsStoped();
}
