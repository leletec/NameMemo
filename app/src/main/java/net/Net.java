package net;

import java.io.File;

import main.Helper;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import database.ImportNewDb;
import database.MySQLiteHelper;
import de.leletec.namememo.R;

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
	
	@SuppressLint("InflateParams")
	protected void receive() {
		final ImportNewDb helper = new ImportNewDb(this, this, dbFile.getAbsolutePath(), importFile.getAbsolutePath());
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(R.string.nfcImport);
		builder.setView(getLayoutInflater().inflate(R.layout.importdialog, null));
		final AlertDialog dialog = builder.create();
		dialog.show();
		
		Button bImpAll = (Button) dialog.findViewById(R.id.bImpAll);
		Button bImpNew = (Button) dialog.findViewById(R.id.bImpNew);
		Button bImpUpdate = (Button) dialog.findViewById(R.id.bImpUpdate);
		Button bImpCancel = (Button) dialog.findViewById(R.id.bImpCancel);
		
		bImpAll.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				Helper.moveFile(importFile, dbFile);
			}
		});
		
		bImpNew.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				helper.lookForNew();
			}
		});
		
		bImpUpdate.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				helper.lookForUpdate();
			}
		});
		
		bImpCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				importFile.delete();
				dialog.dismiss();
			}
		});
	}
}
