package fr.jburet.nav;

import fr.jburet.nav.gps.GpsService;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;

public class NavActivity extends MainActivity {
    
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        //Start GPS Service
        startService(new Intent(this, GpsService.class));
    }
	

}