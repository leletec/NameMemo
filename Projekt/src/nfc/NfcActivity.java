package nfc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import de.leander.projekt.R;
import de.leander.projekt.structure.MySQLiteHelper;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

/*
 * https://developer.android.com/training/beam-files/send-files.html
 */
@SuppressLint("NewApi") //TODO?
public class NfcActivity extends Activity {
	private NfcAdapter adapter;
	private final Context context = this;
	private Uri[] fileUris;
	private String dbName;
	private Button bImport;
	private File importFile;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_nfc);
		
		// Check if NFC is supported
		if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_NFC)) {
			Toast.makeText(context, "This device does not support NFC", Toast.LENGTH_LONG).show();
			finish();
		}
		
		//Check if Android Beam file transfer is supported
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
			Toast.makeText(context, "This device does not support Android Beam", Toast.LENGTH_LONG).show();
			finish();
		}
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		bImport = (Button) findViewById(R.id.bImport);
		adapter = NfcAdapter.getDefaultAdapter(context);
		fileUris = new Uri[1];
		dbName = MySQLiteHelper.DATABASENAME;
		importFile = new File (Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), dbName);
		
		bImport.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				importDB();
			}
		});
		
		File requestFile = getDatabasePath(dbName);
		requestFile.setReadable(true, false);
		// Get a URI for the File and add it to the list of URIs
		Uri fileUri = Uri.fromFile(requestFile);
		if (fileUri != null) {
			fileUris[0] = fileUri;
		} else {
			Log.e("NFC", "No File URI available for file.");
		}
		adapter.setBeamPushUris(fileUris, this);
	}
		
	/*
	 * http://stackoverflow.com/questions/4452538/location-of-sqlite-database-on-the-device
	 */
	/**
	 * Export database to the "Downloads" folder
	 */
	private void importDB() {
		FileInputStream fis=null;
		FileOutputStream fos=null;

		try {
			fis=new FileInputStream(importFile);
			fos=new FileOutputStream(getDatabasePath(dbName));
			for (;;) {
				int i=fis.read();
				if(i!=-1)
					fos.write(i);
				else
					break;
			}
			fos.flush();
		  Toast.makeText(this, "DB dump OK", Toast.LENGTH_LONG).show();
		  importFile.delete();
		  finish();
		} catch(Exception e) {
		  e.printStackTrace();
		  Toast.makeText(this, "DB dump ERROR", Toast.LENGTH_LONG).show();
		} finally {
			try {
				fos.close();
				fis.close();
			} catch(Exception e) {}
		}
	}
}
