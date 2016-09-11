package net.nfc;

import net.Net;
import de.leletec.namememo.R;
import android.annotation.SuppressLint;
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

/**
 * https://developer.android.com/training/beam-files/send-files.html
 */
@SuppressLint("NewApi") //TODO?
public class NfcActivity extends Net {
	
	private NfcAdapter adapter;
	private final Context context = this;
	private Uri[] fileUris;
	private Button bImport;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_nfc);
		bImport = (Button) findViewById(R.id.bImport);
		adapter = NfcAdapter.getDefaultAdapter(context);
		
		// Check if NFC is supported
		if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_NFC) || adapter == null) {
			Toast.makeText(context, "This device does not support NFC", Toast.LENGTH_LONG).show();
			finish();
		}
		
		//Check if Android Beam file transfer is supported
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
			Toast.makeText(context, "This device does not support Android Beam", Toast.LENGTH_LONG).show();
			finish();
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
			Toast.makeText(this, "Please enable NFC and AndroidBeam", Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * Set the file to be sent by NFC.
	 */
	@Override
	public void send() {
		fileUris = new Uri[1];
		dbFile.setReadable(true, false);
		Uri fileUri = Uri.fromFile(dbFile);
		if (fileUri != null) {
			fileUris[0] = fileUri;
		} else {
			Log.e("NFC", "No File URI available for file.");
		}
		adapter.setBeamPushUris(fileUris, this);
	}
}
