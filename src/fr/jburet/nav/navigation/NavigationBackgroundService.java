package fr.jburet.nav.navigation;

import fr.jburet.nav.navigation.listener.NavigationServiceListener;

public interface NavigationBackgroundService {
	public void addListener(NavigationServiceListener listener); 
    public void removeListener(NavigationServiceListener listener); 
    public void assignDestination(String code);
}
