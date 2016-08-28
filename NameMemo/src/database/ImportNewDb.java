package database;

import java.util.ArrayList;
import java.util.List;

import de.leander.projekt.R;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class ImportNewDb {
	private Context context;
	private ImportDAO dao;
	private SQLiteDatabase oldDb; // Already existing db
	private List<Picture> oldList; // Entrylist from the "old" db
	private SQLiteDatabase impDb; // Imported db
	private List<Picture> impList; // Entrylist from the imported db
	private ArrayList<Picture> newEntries;
	

	public ImportNewDb(Context context, String oldPath, String impPath) {
		if ((oldDb = SQLiteDatabase.openDatabase(oldPath, null, SQLiteDatabase.OPEN_READWRITE)) == null) return;
		if ((impDb = SQLiteDatabase.openDatabase(impPath, null, SQLiteDatabase.OPEN_READONLY)) == null) return;
		dao = new ImportDAO(context);
		oldList = dao.getAllBilder(oldDb);
		impList = dao.getAllBilder(impDb);
		this.context = context;
	}
	
	public void lookForNew() {
		newEntries = new ArrayList<Picture>();
		for (Picture pic : impList)
			if (search(oldList, pic) == null)
				newEntries.add(pic);
		dialog();
	}
	
	public void lookForUpdate() {
		newEntries = new ArrayList<Picture>();
		Picture oldPic;
		for (Picture newPic : impList)
			if ((oldPic = search(oldList, newPic)) == null) {
				newPic.setShowAs("New: " + newPic.getName());
				newEntries.add(newPic);
			} else if (!oldPic.equals(newPic) && oldPic.getCalled() < newPic.getCalled()) {
				newPic.setShowAs("Update: " + newPic.getName());
				newEntries.add(newPic);
			}
	}
	
	private void dialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(R.string.importDialogTitle);
		builder.setMessage(R.string.importDialogMessage);
		
		DbListAdapter adapter = new DbListAdapter(context, newEntries);
		ListView listview = new ListView(context);
		listview.setAdapter(adapter);
		builder.setView(listview);

		builder.setNeutralButton("Fertig", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//TODO
			}
		});
		
		final AlertDialog dialog = builder.create();
		
		listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Picture pic = (Picture) parent.getItemAtPosition(position);
				dao.add(oldDb, pic);
				dialog.dismiss();
				lookForNew();
			}
		});
		
		dialog.show();
	}
	
	private Picture search(List<Picture> list, Picture pic) {
		for (Picture picture : list)
			if (picture.getSource().equals(pic.getSource()))
				return picture;
		return null;
	}
	
	private class ImportDAO extends PicturesDAO {

		public ImportDAO(Context context) {
			super(context);
		}
		
		public void add(SQLiteDatabase db, Picture pic) {
			database = db;
			super.add(pic);
		}
		
		public void update(SQLiteDatabase db, Picture pic) {
			database = db;
			super.update(pic);
		}
		
		public List<Picture> getAllBilder(SQLiteDatabase db) {
			database = db;
			return super.getAllBilder();
		}
	}

}
