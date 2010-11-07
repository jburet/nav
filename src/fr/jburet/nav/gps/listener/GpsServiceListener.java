package fr.jburet.nav.gps.listener;

import fr.jburet.nav.gps.PositionData;

public interface GpsServiceListener {
	public void positionChanged(PositionData positionData);
}
