package net;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Toast;
import database.ImportNewDb;
import database.MySQLiteHelper;
import de.leander.projekt.R;

public abstract class Net extends Activity{
	protected String dbName;
	protected File dbFile;
	public static File importFile;
	private Context context;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		dbName = MySQLiteHelper.DATABASENAME;
		dbFile = getDatabasePath(dbName);
		importFile = new File (Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), dbName);
		context = this;
	}

	abstract public void send();
	
	/*
	 * http://stackoverflow.com/questions/4452538/location-of-sqlite-database-on-the-device
	 */
//	protected void receive() {
//		AlertDialog.Builder builder = new AlertDialog.Builder(this);
//		builder.setTitle("Datenbank importieren"); //XXX
//		builder.setMessage("Wollen Sie die Datenbank kopieren?"); //XXX
//		builder.setPositiveButton(R.string.dialogPositive, new DialogInterface.OnClickListener() {
//			@Override
//			public void onClick(DialogInterface arg0, int arg1) {
//				FileInputStream fis = null;
//				FileOutputStream fos = null;
//
//				try {
//					fis = new FileInputStream(importFile);
//					fos = new FileOutputStream(dbFile);
//					for (;;) {
//						int i = fis.read();
//						if (i != -1)
//							fos.write(i);
//						else
//							break;
//					}
//					fos.flush();
//					Toast.makeText(context, "DB imported", Toast.LENGTH_LONG).show();
//					importFile.delete();
//				} catch(Exception e) {
//					e.printStackTrace();
//					Toast.makeText(context, "DB failed to import", Toast.LENGTH_LONG).show();
//				} finally {
//					try {
//						fos.close();
//						fis.close();
//					} catch (Exception e) {}
//					finally {finish();}
//				}
//			}
//		});
//		builder.setNegativeButton(R.string.dialogNegative, new DialogInterface.OnClickListener() {
//			@Override
//			public void onClick(DialogInterface arg0, int arg1) {
//				importFile.delete();
//			}
//		});
//		AlertDialog dialog = builder.create();
//		dialog.show();
//		dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
//			@Override
//			public void onDismiss(DialogInterface arg0) {
//				importFile.delete();
//			}
//		});
//	}
	
	protected void receive() {
		ImportNewDb ind = new ImportNewDb(context, dbFile.getAbsolutePath(), importFile.getAbsolutePath());
		ind.lookForNew();
	}
}
