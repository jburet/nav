package fr.jburet.nav.gps;

import fr.jburet.nav.gps.listener.GpsServiceListener;

public interface GpsBackgroundService {
	public void addListener(GpsServiceListener listener); 
    public void removeListener(GpsServiceListener listener); 
}
