package fr.jburet.nav;

import fr.jburet.nav.component.FileChooser;
import fr.jburet.nav.database.DatabaseActivity;
import fr.jburet.nav.map.MapActivity;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

/**
 * @author jburet
 * 
 *         First level activity. (share the same menu management)
 */
public abstract class MainActivity extends Activity {

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.main_menu_debug:
			startActivity(new Intent(this, DebugActivity.class));
			return true;
		case R.id.main_menu_map:
			startActivity(new Intent(this, MapActivity.class));
			return true;
		case R.id.main_menu_database:
			startActivity(new Intent(this, DatabaseActivity.class));
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
