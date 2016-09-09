package database;

import android.database.sqlite.SQLiteDatabase;

/**
 * Currently the only table
 */
public class TablePictures{

	public static final String NAME = "bilder";

	private static String CREATESTATEMENT = "CREATE TABLE " + NAME
			+ "(id INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ "source TEXT NOT NULL, "
			+ "name TEXT NOT NULL, "
			+ "called INT NOT NULL, "
			+ "gotright INT DEFAULT 0, " //XXX
			+ "inarow INT DEFAULT 0);";

	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATESTATEMENT);
	}
}
