package database;

import android.database.sqlite.SQLiteDatabase;

/**
 * A table to save the app's settings.
 */
public class TableSettings {

	public static final String NAME = "settings";
	
	private static String CREATESTATEMENT = "CREATE TABLE " + NAME
			+ "(identifier TEXT PRIMARY KEY, "
			+ "value INT NOT NULL);";

	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATESTATEMENT);
	}
}
