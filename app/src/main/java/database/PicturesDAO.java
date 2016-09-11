package database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * The DAO for the "Pictures" table.
 */
public class PicturesDAO {

	protected SQLiteDatabase database;
	private MySQLiteHelper helper;

	public PicturesDAO(Context context) {
		helper = new MySQLiteHelper(context);
	}

	public void open() throws SQLException {
		database = helper.getWritableDatabase();
	}

	public void close() {
		helper.close();
	}

	/**
	 * Add an entry to the database.
	 *
	 * @param source	the source of the Picture.
	 * @param name		the name related with the Picture.
	 */
	public void add(String source, String name) {
		ContentValues values = new ContentValues();
		values.put("source", source);
		values.put("name", name);
		values.put("called", 0);
		values.put("gotright", 0);
		values.put("inarow", 0);
		database.insert(TablePictures.NAME, null, values);
	}

	/**
	 * Add an entry to the database.
	 *
	 * @param pic       the Picture
	 */
	public void add(Picture pic) {
		ContentValues values = new ContentValues();
		values.put("source", pic.getSource());
		values.put("name", pic.getName());
		values.put("called", pic.getCalled());
		values.put("gotright", pic.getGotright());
		values.put("inarow", pic.getInarow());
		database.insert(TablePictures.NAME, null, values);
	}

	/**
	 * Update an entry of the database.
	 *
	 * @param source		The source of the Picture.
	 * @param name			The name related with the Picture.
	 * @param called		How often the Picture got called.
	 * @param gotright		How often the user got the name to the Picture right.
	 * @param inarow		How often he got it right in a row.
	 */
	public void update(String source, String name, int called, int gotright, int inarow) {
		ContentValues values = new ContentValues();
		values.put("source", source);
		values.put("name", name);
		values.put("called", called);
		values.put("gotright", gotright);
		values.put("inarow", inarow);
		database.update(TablePictures.NAME, values, "source = ?", new String[] { source });
	}

	/**
	 * Update an entry of the database.
	 *
	 * @param pic       the Picture
	 */
	public void update(Picture pic) {
		ContentValues values = new ContentValues();
		values.put("source", pic.getSource());
		values.put("name", pic.getName());
		values.put("called", pic.getCalled());
		values.put("gotright", pic.getGotright());
		values.put("inarow", pic.getInarow());
		database.update(TablePictures.NAME, values, "source = ?", new String[] { pic.getSource() });
	}

	/**
	 * @return A list of all entries in the database.
	 */
	public List<Picture> getAllPics() {
		List<Picture> pictures = new ArrayList<Picture>();
		Cursor cursor = database.query(TablePictures.NAME, null, null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Picture picture = cursorToPictures(cursor);
			pictures.add(picture);
			cursor.moveToNext();
		}
		cursor.close();
		return pictures;
	}

	/**
	 * Helper method for getAllPics().
	 *
	 * @param cursor	    Current position in the database.
	 * @return			    An entry of the database.
	 */
	private Picture cursorToPictures(Cursor cursor) {
		Picture picture = new Picture();
		picture.setSource(cursor.getString(1));
		picture.setName(cursor.getString(2));
		picture.setCalled(cursor.getInt(3));
		picture.setGotright(cursor.getInt(4));
		picture.setInarow(cursor.getInt(5));
		Log.d("cursor", "source:"+picture.getSource()+" name:"+picture.getName()+" called:"+picture.getCalled()+" gotright:"+picture.getGotright()+" inarow:"+picture.getInarow());
		return picture;
	}

	/**
	 * Remove an entry from the table.
	 *
	 * @param source	    its source
	 */
	public void delete(String source) {
		database.delete(TablePictures.NAME, "source = ?",
				new String[] { source });
	}

	/**
	 * Delete the whole table.
	 */
	public void clean() {
		database.delete(TablePictures.NAME, null, null);
		database.close();
	}
}
