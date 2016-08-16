package bluetooth;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import de.leander.projekt.R;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/*
 * https://developer.android.com/guide/topics/connectivity/bluetooth.html
 * https://github.com/googlesamples/android-BluetoothChat
 * http://stackoverflow.com/questions/24573755/android-bluetooth-socket-connect-fails
 * http://stackoverflow.com/questions/858980/file-to-byte-in-java
 */
public class BluetoothActivity extends Activity {
	private static final int REQUEST_ENABLE_BT = 200;
	private static final String name = "NameMemo";
	private static final UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	
	private final Context context = this;
	private BluetoothAdapter adapter;
	private ListView devLv;
	private TextView devTv;
	private ArrayList<BluetoothDevice> devList;
	private AcceptThread srv;
	private ConnectThread client;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bluetooth);
		
		adapter = BluetoothAdapter.getDefaultAdapter();
		devLv = (ListView) findViewById(R.id.lvDevices);
		devTv = (TextView) findViewById(R.id.tvDevices);
		
		// Register the BroadcastReceiver
		registerReceiver(receiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
		
		if (adapter == null) {
		    Toast.makeText(context, "Device does not support Bluetooth", Toast.LENGTH_LONG).show();
		    finish();
		}
		copyFiles();
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		// Enable BT if it is not already enabled
		if (adapter.isEnabled()) {
			setup();
		} else {
			Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(receiver);
	}
	
	/**
	 * Make this device discoverable.
	 */
	private void ensureDiscoverable() {
		if (adapter.getScanMode() !=
				BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
			Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
			discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
			startActivity(discoverableIntent);
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_ENABLE_BT:
			if (resultCode == RESULT_OK) {
				setup();
			} else {
				Toast.makeText(context, "You did not enable Bluetooth", Toast.LENGTH_LONG).show();
				finish();
			}
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.bluetoothmenu, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.ensureDiscoverable:
			ensureDiscoverable();
			return true;
		case R.id.discover:
			discover();
			return true;
		case R.id.sendFile:
			sendFile();
			return true;
		}
		return false;
	}

	public void setup() {
		// Start a server
		srv = new AcceptThread(adapter, name, uuid, context);
		srv.start();
		
		// Show paired devices
		devTv.setText("Gekoppelte Geräte:");
		Set<BluetoothDevice> pairedDevices = adapter.getBondedDevices();
		devList = new ArrayList<BluetoothDevice>();
		
		if (pairedDevices.size() > 0) {
			for (BluetoothDevice device : pairedDevices) {
				devList.add(device);
			}
			devLv.setAdapter(new BTListAdapter(context, devList));
		}
		
		// Connect to a paired device
		devLv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				srv.cancel();
				Toast.makeText(context, "Versuche Verbindung mit dem Gerät aufzubauen", Toast.LENGTH_SHORT).show();
				BluetoothDevice device = (BluetoothDevice) parent.getItemAtPosition(position);
				client = new ConnectThread(adapter, device, uuid, context);
				client.start();
			}
		});
	}
	
	private void discover() {
		devTv.setText("Gefundene Geräte:");
		adapter.startDiscovery();
		devLv.setAdapter(new BTListAdapter(context, new ArrayList<BluetoothDevice>()));
	}
	
	private final BroadcastReceiver receiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			// When discovery finds a device
			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				amendList(device);
			}
		}
	};

	private void amendList(BluetoothDevice device) {
		BTListAdapter adapter = (BTListAdapter) devLv.getAdapter();
		adapter.add(device);
		adapter.notifyDataSetChanged();
	}
	
	private void sendFile() {
		File file = new File("hase.jpg");
		if (!file.exists()) {
			Log.e("BT", "File not found");
			return;
		}
		byte[] buffer = null;
		try {
			buffer = read(file);
		} catch (IOException e) {}
		
		if (srv.isConnected())
			srv.write(buffer);
		else if (client.isConnected())
			client.write(buffer);
	}
	
	public byte[] read(File file) throws IOException {
	    byte[] buffer = new byte[(int) file.length()];
	    InputStream ios = null;
	    try {
	        ios = new FileInputStream(file);
	        if (ios.read(buffer) == -1) {
	            throw new IOException("EOF reached while trying to read the whole file");
	        }
	    } finally {
	        try {
	            if (ios != null)
	                ios.close();
	        } catch (IOException e) {}
	    }
	    return buffer;
	}
	
	private void copyFiles() {
		int[] files = new int[] { R.raw.hund, R.raw.katze, R.raw.hase };
		String[] filenames = new String[] { "hund.jpg", "katze.png", "hase.jpg"};

		if (files != null)
			for (int i = 0; i < files.length; i++) {
				int resId = files[i];
				String filename = filenames[i];
				InputStream in = null;
				OutputStream out = null;
				try {
					in = getResources().openRawResource(resId);
					out = openFileOutput(filename, Context.MODE_PRIVATE);
					copyFile(in, out);
				} catch (IOException e) {
				} finally {
					if (in != null) {
						try {
							in.close();
						} catch (IOException e) {}
					}
					if (out != null) {
						try {
							out.close();
						} catch (IOException e) {}
					}
				}
			}
		}
	private void copyFile(InputStream in, OutputStream out) throws IOException {
		byte[] buffer = new byte[1024];
		int read;
		while ((read = in.read(buffer)) != -1) {
			out.write(buffer, 0, read);
		}
	}
}
