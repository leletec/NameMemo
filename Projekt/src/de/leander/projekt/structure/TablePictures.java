package de.leander.projekt.structure;

import android.database.sqlite.SQLiteDatabase;

public class TablePictures{

	public static String NAME = "bilder";

	private static String CREATESTATEMENT = "CREATE TABLE " + NAME
			+ "(id INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ "source TEXT NOT NULL, "
			+ "name TEXT NOT NULL, "
			+ "called INT NOT NULL, "
			+ "gotright INT DEFAULT 0, "
			+ "inarow INT DEFAULT 0);";

	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATESTATEMENT);
	}

	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
	}

}
