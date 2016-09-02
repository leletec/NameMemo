package database;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.Net;
import de.leander.projekt.R;
import design.DbListAdapter;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class ImportNewDb {
	private Context context;
	private Net activity;
	private ImportDAO dao;
	private SQLiteDatabase oldDb; // Already existing db
	private List<Picture> oldList; // Entrylist from the "old" db
	private SQLiteDatabase impDb; // Imported db
	private List<Picture> impList; // Entrylist from the imported db
	private File impFile;
	private ArrayList<Picture> newEntries;
	private String oldPath;
	private String impPath;
	

	public ImportNewDb(Net activity, Context context, String oldPath, String impPath) {
		this.oldPath = oldPath;
		this.impPath = impPath;
		impFile = new File(impPath);
		this.activity = activity;
		this.context = context;
	}
	
	private void openDb() {
		if ((oldDb = SQLiteDatabase.openDatabase(oldPath, null, SQLiteDatabase.OPEN_READWRITE)) == null) return;
		if ((impDb = SQLiteDatabase.openDatabase(impPath, null, SQLiteDatabase.OPEN_READONLY)) == null) return;
		dao = new ImportDAO(context);
		oldList = dao.getAllBilder(oldDb);
		impList = dao.getAllBilder(impDb);
	}
	
	public void lookForNew() {
		openDb();
		newEntries = new ArrayList<Picture>();
		for (Picture pic : impList)
			if (search(oldList, pic) == null) {
				newEntries.add(pic);
				Log.d("Imp", "Added " + pic.getName() + " to the list");
			}
		dialog("new");
	}
	
	public void lookForUpdate() {
		openDb();
		newEntries = new ArrayList<Picture>();
		Picture oldPic;
		for (Picture newPic : impList)
			if ((oldPic = search(oldList, newPic)) == null) {
				newPic.setShowAs("New: " + newPic.getName());
				newEntries.add(newPic);
				Log.d("Imp", "Added " + newPic.getName() + " to the list");
			} else if (!oldPic.equals(newPic) && oldPic.getCalled() < newPic.getCalled()) {
				newPic.setShowAs("Update: " + newPic.getName());
				newEntries.add(newPic);
			}
		dialog("update");
	}
	
	private void dialog(final String mode) {
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
				impFile.delete();
				activity.finish();
			}
		});
		
		final AlertDialog dialog = builder.create();
		dialog.show();
		
		listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Picture pic = (Picture) parent.getItemAtPosition(position);
				if (mode.equals("new")) {
					dao.add(oldDb, pic);
					dialog.dismiss();
					lookForNew();
				}
				else if (mode.equals("update")) {
					if (pic.getShowAs().startsWith("New:"))
						dao.add(oldDb, pic);
					else if (pic.getShowAs().startsWith("Update:"))
						dao.update(oldDb, pic);
					dialog.dismiss();
					lookForUpdate();
				} else dialog.dismiss();
			}
		});
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
			return super.getAllPics();
		}
	}

}
