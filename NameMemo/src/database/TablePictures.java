package database;

import android.database.sqlite.SQLiteDatabase;

/**
 * Currently the only table
 */
public class TablePictures{

	public static String NAME = "bilder";

	private static String CREATESTATEMENT = "CREATE TABLE " + NAME
			+ "(id INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ "source TEXT NOT NULL, "
			+ "name TEXT NOT NULL, "
			+ "called INT NOT NULL, "
			+ "gotright INT DEFAULT 0, "
			+ "inarow INT DEFAULT 0, "
			+ "imagingmode INT NOT NULL);";

	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATESTATEMENT);
	}

	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
	}

}
