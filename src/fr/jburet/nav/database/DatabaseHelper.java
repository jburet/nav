package fr.jburet.nav.database;

import java.util.ArrayList;
import java.util.List;

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

	/*
	 * SQLite does not enforce the length of a VARCHAR. You can declare a
	 * VARCHAR(10) and SQLite will be happy to let you put 500 characters in it.
	 * And it will keep all 500 characters intact - it never truncates.
	 */
	private static final String CREATE_WAYPOINT_TABLE = "CREATE TABLE "
			+ WAYPOINT_TABLE_NAME
			+ " (ID INTEGER PRIMARY KEY, CODE VARCHAR(8), NAME VARCHAR(20), ALTITUDE REAL, LONGITUDE REAL, LATITUDE REAL)";

	private Context context;
	private SQLiteDatabase db;

	public DatabaseHelper(Context context) {
		this.context = context;
		NavHelper openHelper = new NavHelper(this.context);
		this.db = openHelper.getWritableDatabase();
	}

	public long executeUpdateQuery(String query) {
		db.beginTransaction();
		long nbLineUpdated = 0;
		try {
			Cursor cursor = db.rawQuery(query, new String[] {});
			cursor.moveToFirst();
			nbLineUpdated = cursor.getLong(0);
			db.setTransactionSuccessful();
		} catch (Exception e) {
			Log.e("NAV", "Cannot update database", e);
		}
		return nbLineUpdated;
	}

	public List executeQuery(String query, String[] args, Mapper mapper) {
		db.beginTransaction();
		List res = new ArrayList();
		int position = 0;
		try {
			Cursor cursor = db.rawQuery(query, args);
			while (cursor.moveToNext()) {
				res.add(mapper.mapRow(cursor, position++));
			}
		} catch (Exception e) {
			Log.e("NAV", "Cannot update database", e);
		}
		return res;
	}

	public void closeDb(){
		db.close();
	}
	
	private static class NavHelper extends SQLiteOpenHelper {

		public NavHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(CREATE_WAYPOINT_TABLE);
			db.execSQL("Insert into waypoint values(1, 'ECURY', 'Chalons ecury', 98, 4.33555555556, 48.8958333333)");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w("Nav", "Upgrading database from " + oldVersion + " to " + newVersion);
			db.execSQL("drop table " + WAYPOINT_TABLE_NAME);
			onCreate(db);
		}
	}
}
