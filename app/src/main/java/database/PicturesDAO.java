package database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

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

	private void close() {
		helper.close();
	}

	/**
	 * Add an entry to the database.
	 *
	 * @param source	the source of the Picture.
	 * @param name		the name related with the Picture.
	 * @return          'true' if it insertion was successful, 'false' if it failed (because the source already existed)
	 */
	public boolean add(String source, String name) {
		ContentValues values = new ContentValues();
		values.put("source", source);
		values.put("name", name);
		values.put("called", 0);
		values.put("gotRight", 0);
		values.put("inarow", 0);
		values.put("highscore", 0);
		return database.insert(TablePictures.NAME, null, values) != -1;
	}

	/**
	 * Add an entry to the database.
	 *
	 * @param pic       the Picture
	 * @return          'true' if it insertion was successful, 'false' if it failed (because the source already existed)
	 */
	public boolean add(Picture pic) {
		ContentValues values = new ContentValues();
		values.put("source", pic.getSource());
		values.put("name", pic.getName());
		values.put("called", pic.getCalled());
		values.put("gotRight", pic.getGotRight());
		values.put("inarow", pic.getInarow());
		values.put("highscore", pic.getHighscore());
		return database.insert(TablePictures.NAME, null, values) != -1;
	}

	/**
	 * Update an entry of the database.
	 *
	 * @param source		The source of the Picture.
	 * @param name			The name related with the Picture.
	 * @param called		How often the Picture got called.
	 * @param gotRight      How often the user got the right name to the picture.
	 * @param inarow		How often he got it right in a row.
	 * @param highscore     The highscore of inarow.
	 */
	public void update(String source, String name, int called, int gotRight, int inarow, int highscore) {
		ContentValues values = new ContentValues();
		values.put("source", source);
		values.put("name", name);
		values.put("called", called);
		values.put("gotRight", gotRight);
		values.put("inarow", inarow);
		values.put("highscore", highscore);
		database.update(TablePictures.NAME, values, "source = ?", new String[] { source });
	}

	/**
	 * Update an entry of the database.
	 *
	 * @param pic       the Picture
	 */
	void update(Picture pic) {
		ContentValues values = new ContentValues();
		values.put("source", pic.getSource());
		values.put("name", pic.getName());
		values.put("called", pic.getCalled());
		values.put("gotRight", pic.getGotRight());
		values.put("inarow", pic.getInarow());
		values.put("highscore", pic.getHighscore());
		database.update(TablePictures.NAME, values, "source = ?", new String[] { pic.getSource() });
	}

	/**
	 * @return A list of all entries in the database.
	 */
	public List<Picture> getAllPics() {
		List<Picture> pictures = new ArrayList<>();
		Cursor c = database.query(TablePictures.NAME, null, null, null, null, null, null);

		c.moveToFirst();
		while (!c.isAfterLast()) {
			pictures.add(cursorToPicture(c));
			c.moveToNext();
		}
		c.close();
		return pictures;
	}

	/**
	 * Helper method for getAllPics().
	 *
	 * @param cursor	    Current position in the database.
	 * @return			    An entry of the database.
	 */
	private Picture cursorToPicture(Cursor cursor) {
		Picture picture = new Picture();
		picture.setSource(cursor.getString(0));
		picture.setName(cursor.getString(1));
		picture.setCalled(cursor.getInt(2));
		picture.setGotRight(cursor.getInt(3));
		picture.setInarow(cursor.getInt(4));
		picture.setHighscore(cursor.getInt(5));
		Log.d("cursor", "source:"+picture.getSource() + " name:"+picture.getName() + " called:"+picture.getCalled() + " gotRight:"+picture.getGotRight()
				+ " inarow:"+picture.getInarow() + " highscore:"+picture.getHighscore());
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
		close();
		database.close();
	}
}
