package fr.jburet.nav.gps.binder;

import fr.jburet.nav.gps.GpsBackgroundService;
import fr.jburet.nav.gps.GpsStatusBackgroundService;
import android.os.Binder;

public class GpsServiceBinder extends Binder {
	private GpsBackgroundService service = null;
	private GpsStatusBackgroundService statusService = null;

	public GpsServiceBinder(GpsBackgroundService service, GpsStatusBackgroundService statusService) {
		super();
		this.service = service;
		this.statusService = statusService;
	}

	public GpsBackgroundService getService() {
		return service;
	}

	public GpsStatusBackgroundService getStatusService() {
		return statusService;
	}
}
