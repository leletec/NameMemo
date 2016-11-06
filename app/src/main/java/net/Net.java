package net;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;

import java.io.File;

import database.ImportNewDb;
import database.MySQLiteHelper;
import de.leletec.namememo.R;
import main.Helper;

/**
 * Abstract class for Bluetooth and NFCActivity
 */
public abstract class Net extends AppCompatActivity {
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

	/**
	 * Show a dialog where the user can decide, what to do with the newly received db-file.
	 */
	@SuppressLint("InflateParams")
	protected void receive() {
		final ImportNewDb helper = new ImportNewDb(this, this, dbFile.getAbsolutePath(), importFile.getAbsolutePath());
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(R.string.nfcImport);
		CharSequence[] items = new CharSequence[]{getString(R.string.impAll), getString(R.string.impNew), getString(R.string.impUpdate), getString(R.string.dialogCancel)};
		builder.setItems(items, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialogInterface, int i) {
				switch (i) {
				case 0: Helper.moveFile(importFile, dbFile); finish(); break;
				case 1: helper.lookForNew(); break;
				case 2: helper.lookForUpdate(); break;
				case 3: importFile.delete(); finish();
				}
			}
		});
		builder.create().show();
	}
}
