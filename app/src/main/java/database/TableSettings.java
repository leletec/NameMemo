package database;

import android.database.sqlite.SQLiteDatabase;

/**
 * A table to save the app's settings.
 */
public class TableSettings {

	static final String NAME = "settings";

	void onCreate(SQLiteDatabase db) {
		String CREATESTATEMENT = "CREATE TABLE " + NAME
				+ "(identifier TEXT PRIMARY KEY, "
				+ "value INT NOT NULL);";
		db.execSQL(CREATESTATEMENT);
	}
}
