package database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class PicturesDAO {

	protected SQLiteDatabase database;
	private MySQLiteHelper dbHelper;

	public PicturesDAO(Context context) {
		dbHelper = new MySQLiteHelper(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	/**
	 * Adds an entry to the database.
	 * @param source		The source of the picture.
	 * @param name			The name related with the picture.
	 * @param imagingmode	Where the picture came from.
	 */
	public void add(String source, String name, int imagingmode) {
		ContentValues values = new ContentValues();
		values.put("source", source);
		values.put("name", name);
		values.put("called", 0);
		values.put("gotright", 0);
		values.put("inarow", 0);
		values.put("imagingmode", imagingmode);
		database.insert(TablePictures.NAME, null, values);
	}
	
	public void add(Picture pic) {
		ContentValues values = new ContentValues();
		values.put("source", pic.getSource());
		values.put("name", pic.getName());
		values.put("called", pic.getCalled());
		values.put("gotright", pic.getGotright());
		values.put("inarow", pic.getInarow());
		values.put("imagingmode", pic.getImagingmode());
		database.insert(TablePictures.NAME, null, values);
	}

	/**
	 * Updates an entry of the database.
	 * @param source		The source of the picture.
	 * @param name			The name related with the picture.
	 * @param called		How often the picture got called.
	 * @param gotright		How often the user got the name to the picture right.
	 * @param inarow		How often he got it right in a row.
	 * @param imagingmode	Where the picture came from.
	 */
	public void update(String source, String name, int called, int gotright,
			int inarow, int imagingmode) {
		ContentValues values = new ContentValues();
		values.put("source", source);
		values.put("name", name);
		values.put("called", called);
		values.put("gotright", gotright);
		values.put("inarow", inarow);
		values.put("imagingmode", imagingmode);
		database.update(TablePictures.NAME, values, "source = ?", new String[] { source });
	}
	
	public void update(Picture pic) {
		ContentValues values = new ContentValues();
		values.put("source", pic.getSource());
		values.put("name", pic.getName());
		values.put("called", pic.getCalled());
		values.put("gotright", pic.getGotright());
		values.put("inarow", pic.getInarow());
		values.put("imagingmode", pic.getImagingmode());
		database.update(TablePictures.NAME, values, "source = ?", new String[] { pic.getSource() });
	}

	/**
	 * @return A list of all entries in the database.
	 */
	public List<Picture> getAllBilder() {
		List<Picture> pictures = new ArrayList<Picture>();

		Cursor cursor = database.query(TablePictures.NAME, null, null, null,
				null, null, null);

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
	 * Helper method for getAllBilder().
	 * @param cursor	Current position in the database.
	 * @return			An entry of the database.
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
	 * Remove an entry from the database.
	 * @param source	Its source.
	 */
	public void delete(String source) {
		database.delete(TablePictures.NAME, "source = ?",
				new String[] { source });
	}

	/**
	 * Delete the whole database.
	 */
	public void clean() { //verbrannte Erde
		database.delete(TablePictures.NAME, null, null);
		database.close();
	}
}
