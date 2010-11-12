package fr.jburet.nav.database;

import java.util.ArrayList;
import java.util.List;

import fr.jburet.nav.database.point.CannotImportException;
import fr.jburet.nav.database.point.WaypointImporterFromCup;
import fr.jburet.nav.database.point.WaypointQueryImpl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.IBinder;
import android.util.Log;

public class DatabaseHelper {
	private static final String DATABASE_NAME = "navigation.db";
	private static final int DATABASE_VERSION = 1;
	private static final String WAYPOINT_TABLE_NAME = "waypoint";
	private static final String AIRSPACE_TABLE_NAME = "airspace";

	/*
	 * SQLite does not enforce the length of a VARCHAR. You can declare a
	 * VARCHAR(10) and SQLite will be happy to let you put 500 characters in it.
	 * And it will keep all 500 characters intact - it never truncates.
	 */
	private static final String CREATE_WAYPOINT_TABLE = "CREATE TABLE "
			+ WAYPOINT_TABLE_NAME
			+ " (ID INTEGER PRIMARY KEY, CODE VARCHAR(8), NAME VARCHAR(20), ALTITUDE REAL, LONGITUDE REAL, LATITUDE REAL)";

	// FIXME Add the two converted altitude (use 1013 QNH)...
	// FIXME Add min, max longitude and latitude for search
	private static final String CREATE_AIRSPACE_TABLE = "CREATE TABLE "
			+ AIRSPACE_TABLE_NAME
			+ " (ID INTEGER PRIMARY KEY, CODE VARCHAR(8), NAME VARCHAR(20), CLASSE VARCHAR(3), ALTITUDE_BOTTOM VARCHAR(10), ALTITUDE_TOP VARCHAR(10), SHAPE VARCHAR(500), MIN_LAT REAL, MAX_LAT REAL, MIN_LONG REAL, MAX_LONG REAL)";

	private Context context;
	private NavHelper openHelper;
	SQLiteDatabase dbWritable;
	SQLiteDatabase dbReadable;

	public DatabaseHelper(Context context) {
		this.context = context;
		openHelper = new NavHelper(this.context);
		dbWritable = openHelper.getWritableDatabase();
		dbReadable = openHelper.getReadableDatabase();
	}

	public long executeUpdateQuery(String query) {

		dbWritable.beginTransaction();
		long nbLineUpdated = 0;
		Cursor cursor = null;
		try {
			cursor = dbWritable.rawQuery(query, new String[] {});
			cursor.moveToFirst();
			nbLineUpdated = cursor.getLong(0);
			dbWritable.setTransactionSuccessful();
		} catch (Exception e) {
			Log.e("NAV", "Cannot update database", e);
		} finally {
			if (cursor != null) {
				cursor.close();
			}
			dbWritable.endTransaction();
		}
		return nbLineUpdated;
	}

	public List executeQuery(String query, String[] args, Mapper mapper) {

		List res = new ArrayList();
		int position = 0;
		Cursor cursor = null;
		try {
			cursor = dbReadable.rawQuery(query, args);
			while (cursor.moveToNext()) {
				res.add(mapper.mapRow(cursor, position++));
			}
		} catch (Exception e) {
			Log.e("NAV", "Cannot update database", e);
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return res;
	}

	public long insertNewRow(String tableName, ContentValues values) {
		return dbWritable.insert(tableName, null, values);
	}

	private static class NavHelper extends SQLiteOpenHelper {

		public NavHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(CREATE_WAYPOINT_TABLE);
			db.execSQL("CREATE INDEX geocoord_index on waypoint(LONGITUDE, LATITUDE);");
			db.execSQL(CREATE_AIRSPACE_TABLE);
			db.execSQL("CREATE INDEX geocoord_airspace_index on airspace(MIN_LAT, MAX_LAT, MIN_LONG, MAX_LONG);");
			// db.execSQL("Insert into waypoint values(1, 'ECURY', 'Chalons ecury', 98, 4.33555555556, 48.8958333333)");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w("Nav", "Upgrading database from " + oldVersion + " to " + newVersion);
			db.execSQL("drop table " + WAYPOINT_TABLE_NAME);
			db.execSQL("drop table " + AIRSPACE_TABLE_NAME);
			onCreate(db);
		}
	}
}
