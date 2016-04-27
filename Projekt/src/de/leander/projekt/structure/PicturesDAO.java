package de.leander.projekt.structure;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class PicturesDAO {

	private SQLiteDatabase database;
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

	public void add(String source, String name) {
		ContentValues values = new ContentValues();
		values.put("source", source);
		values.put("name", name);
		values.put("called", 0);
		values.put("gotright", 0);
		values.put("inarow", 0);
		database.insert(TablePictures.NAME, null, values);
	}

	public void update(String source, String name, int called, int gotright,
			int inarow) {
		ContentValues values = new ContentValues();
		values.put("source", source);
		values.put("name", name);
		values.put("called", called);
		values.put("gotright", gotright);
		values.put("inarow", inarow);
		database.update(TablePictures.NAME, values, "source = ?",
				new String[] { source });
	}

	public List<Pictures> getAllBilder() {
		List<Pictures> pictures = new ArrayList<Pictures>();

		Cursor cursor = database.query(TablePictures.NAME, null, null, null,
				null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Pictures picture = cursorToPictures(cursor);
			pictures.add(picture);
			cursor.moveToNext();
		}
		cursor.close();
		return pictures;
	}

	private Pictures cursorToPictures(Cursor cursor) {
		Pictures picture = new Pictures();
		picture.setSource(cursor.getString(1));
		picture.setName(cursor.getString(2));
		picture.setCalled(cursor.getInt(3));
		picture.setGotright(cursor.getInt(4));
		picture.setInarow(cursor.getInt(5));
		Log.d("cursor", "source:"+picture.getSource()+" name:"+picture.getName()+" called:"+picture.getCalled()+" gotright:"+picture.getGotright()+" inarow:"+picture.getInarow());
		return picture;
	}

	public void delete(String source) {
		database.delete(TablePictures.NAME, "source = ?",
				new String[] { source });
	}

	public void clean() { //verbrannte Erde
		database.delete(TablePictures.NAME, null, null);
		database.close();
	}
}
