package database;

import android.database.sqlite.SQLiteDatabase;

/**
 * A table to save the relationship between pictures, names and other important values.
 */
public class TablePictures{

	public static final String NAME = "pictures";

	private static String CREATESTATEMENT = "CREATE TABLE " + NAME
			+ "(id INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ "source TEXT NOT NULL, "
			+ "name TEXT NOT NULL, "
			+ "called INT NOT NULL, "
			+ "inarow INT NOT NULL);";

	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATESTATEMENT);
	}
}
