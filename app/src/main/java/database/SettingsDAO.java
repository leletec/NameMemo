package database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

/**
 * The DAO for the "Settings" table
 */
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

	/**
	 * Initialize the settings if they are not already there.
	 */
	public void init() {
		add("inarowReq", 3); // How high Picture.inarow has to go until the delete-dialog appears
		add("seqType", 1); // sequenceType:	0 = consecutive	|	1 = random
		add("colPics", 1); // If you want to collect all the pictures in one folder.
		add("addPicType", 1); // 0 = list | 1 = gallery intent
	}

	/**
	 * Helper method for init()
	 */
	private void add(String identifier, int value) {
		ContentValues values = new ContentValues();
		values.put("identifier", identifier);
		values.put("value", value);
		database.insert(TableSettings.NAME, null, values);
	}

	/**
	 * Set a specific setting.
	 *
	 * @param identifier    the setting
	 * @param value         it's value
	 */
	public void set(String identifier, int value) {
		ContentValues values = new ContentValues();
		values.put("identifier", identifier);
		values.put("value", value);
		database.update(TableSettings.NAME, values, "identifier = ?", new String [] {identifier});
	}

	/**
	 * Get a specific setting.
	 *
	 * @param identifier    the setting
	 * @return              it's value
	 */
	public int get(String identifier) {
		Cursor c = database.query(TableSettings.NAME, new String[] {"value"}, "identifier = '" + identifier + "'", null, null, null, null);
		c.moveToFirst();
		return c.getInt(0);
	}

	/**
	 * Delete the whole table.
	 */
	public void clean() {
		database.delete(TablePictures.NAME, null, null);
		database.close();
	}
}
