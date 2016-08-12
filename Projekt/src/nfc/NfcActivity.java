package nfc;

import java.io.File;

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
import android.widget.Toast;

/*
 * https://developer.android.com/training/beam-files/send-files.html
 */
@SuppressLint("NewApi") //TODO?
public class NfcActivity extends Activity {
	private NfcAdapter adapter;
	private final Context context = this;
	private Uri[] fileUris;
	private FileUriCallback callback;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Check if NFC is available
		if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_NFC)) {
			Toast.makeText(context, "This device does not support NFC", Toast.LENGTH_LONG).show();
			finish();
		}
		
		//Check if Android Beam file transfer is supported
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
			Toast.makeText(context, "This device does not support Android Beam", Toast.LENGTH_LONG).show();
			finish();
		}
		
		adapter = NfcAdapter.getDefaultAdapter(context);
		fileUris = new Uri[1];
		callback = new FileUriCallback();
		
		File requestFile = new File (Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "links.md");
		requestFile.setReadable(true, false);
		// Get a URI for the File and add it to the list of URIs
		Uri fileUri = Uri.fromFile(requestFile);
		if (fileUri != null) {
			fileUris[0] = fileUri;
		} else {
			Log.e("NFC", "No File URI available for file.");
		}
		adapter.setBeamPushUris(fileUris, this);
		
		adapter.setBeamPushUrisCallback(callback, this);
	}
	
	private class FileUriCallback implements NfcAdapter.CreateBeamUrisCallback {
		@Override
		public Uri[] createBeamUris(NfcEvent event) {
			return fileUris;
		}
		
	}
}
