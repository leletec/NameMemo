package database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class SettingsDAO {

	private SQLiteDatabase database;
	private MySQLiteHelper helper;

	public SettingsDAO(Context context) {
		helper = new MySQLiteHelper(context);
	}
	
	public void open() throws SQLException {
		database = helper.getWritableDatabase();
	}

	public void close() {
		helper.close();
	}
	
	public void init() {
		add("inarowReq", 3); // How high Picture.inarow has to go until the delete-dialog appears
		add("seqType", 1); // sequenceType:	0 = consecutive	|	1 = random
	}
	
	private void add(String identifier, int value) {
		ContentValues values = new ContentValues();
		values.put("identifier", identifier);
		values.put("value", value);
		database.insert(TableSettings.NAME, null, values);
	}
	
	public void set(String identifier, int value) {
		ContentValues values = new ContentValues();
		values.put("identifier", identifier);
		values.put("value", value);
		database.update(TableSettings.NAME, values, "identifier = ?", new String [] {identifier});
	}
	
	public int get(String identifier) {
		Cursor c = database.query(TableSettings.NAME, new String[] {"value"}, "identifier = '" + identifier + "'", null, null, null, null);
		c.moveToFirst();
		return c.getInt(0);
	}
}
