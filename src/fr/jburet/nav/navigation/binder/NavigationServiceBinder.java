package fr.jburet.nav.navigation.binder;

import fr.jburet.nav.navigation.NavigationBackgroundService;
import android.os.Binder;

public class NavigationServiceBinder extends Binder {
	private NavigationBackgroundService service = null;

	public NavigationServiceBinder(NavigationBackgroundService service) {
		super();
		this.service = service;
	}

	public NavigationBackgroundService getService() {
		return service;
	}
}
