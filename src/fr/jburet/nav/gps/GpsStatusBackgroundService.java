package fr.jburet.nav.gps;

import fr.jburet.nav.gps.listener.GpsServiceStatusListener;

public interface GpsStatusBackgroundService {
	public void addStatusListener(GpsServiceStatusListener listener); 
    public void removeStatusListener(GpsServiceStatusListener listener);
}
