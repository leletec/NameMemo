package database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MySQLiteHelper extends SQLiteOpenHelper{

	public static final String DATABASENAME = "ProjektDB";
	private TablePictures pictures = new TablePictures();
	private TableSettings settings = new TableSettings();

	public MySQLiteHelper(Context context) {
		super(context, DATABASENAME, null, 1);
	}

	/**
	 * Gets called when you use getReadableDatabase().
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		pictures.onCreate(db);
		settings.onCreate(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
}
