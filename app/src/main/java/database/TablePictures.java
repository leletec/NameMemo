package database;

import android.database.sqlite.SQLiteDatabase;

/**
 * A table to save the relationship between pictures, names and other important values.
 */
public class TablePictures{

	static final String NAME = "pictures";

	void onCreate(SQLiteDatabase db) {
		String CREATESTATEMENT = "CREATE TABLE " + NAME
				+ "(source TEXT PRIMARY KEY, "
				+ "name TEXT NOT NULL, "
				+ "called INT NOT NULL, "
				+ "gotRight INT NOT NULL, "
				+ "inarow INT NOT NULL, "
				+ "highscore INT NOT NULL);";
		db.execSQL(CREATESTATEMENT);
	}
}
