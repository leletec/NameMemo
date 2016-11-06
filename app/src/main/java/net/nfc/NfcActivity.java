package net.nfc;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.nfc.NfcAdapter.OnNdefPushCompleteCallback;
import android.nfc.NfcEvent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import net.Net;

import de.leletec.namememo.R;

/* https://developer.android.com/training/beam-files/send-files.html */

/**
 * Activity to connect two devices via NFC and share the database.
 */
public class NfcActivity extends Net {

	private NfcAdapter adapter;
	private final Context context = this;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_nfc);
		Button bImport = (Button) findViewById(R.id.bImport);
		adapter = NfcAdapter.getDefaultAdapter(context);

		// Check if NFC is supported
		if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_NFC) || adapter == null) {
			Toast.makeText(context, R.string.noNFC, Toast.LENGTH_LONG).show();
			finish();
			return;
		}
		
		//Check if Android Beam file transfer is supported
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
			Toast.makeText(context, R.string.noAndroidBeam, Toast.LENGTH_LONG).show();
			finish();
			return;
		}
		
		bImport.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				receive();
			}
		});
		
		send();
		
		adapter.setOnNdefPushCompleteCallback(new OnNdefPushCompleteCallback() {
			@Override
			public void onNdefPushComplete(NfcEvent event) {
				finish();
			}
		}, this);
	}
	
	@Override
	protected void onStart() {
		super.onStart();

		//Force user to enable NFC and Android Beam
		if (!(adapter.isEnabled() && adapter.isNdefPushEnabled())) {
			startActivity(new Intent(android.provider.Settings.ACTION_NFC_SETTINGS));
			Toast.makeText(this, R.string.enableNFC, Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * Set the file to be sent by NFC.
	 */
	@Override
	public void send() {
		Uri[] fileUris = new Uri[1];
		dbFile.setReadable(true, false);
		Uri fileUri = Uri.fromFile(dbFile);
		if (fileUri == null) {
			Log.e("NFC", "No File URI available for file.");
			Toast.makeText(this, R.string.readErr, Toast.LENGTH_LONG).show();
			finish();
			return;
		}
		fileUris[0] = fileUri;
		adapter.setBeamPushUris(fileUris, this);
	}
}
