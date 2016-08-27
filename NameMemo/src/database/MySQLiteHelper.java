package database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * TODO
 */
public class MySQLiteHelper extends SQLiteOpenHelper{

	public static final int VERSION = 1;
	public static String DATABASENAME = "ProjektDB";
	public TablePictures tabel = new TablePictures();

	public MySQLiteHelper(Context context) {
		super(context, DATABASENAME, null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		tabel.onCreate(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		tabel.onUpgrade(db, oldVersion, newVersion);
	}
}
