package fr.jburet.nav.database;

import android.database.Cursor;

public interface Mapper<T> {
	public abstract T mapRow(Cursor cursor, int rowNumber);
}
