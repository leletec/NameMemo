package net.bluetooth;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import net.Net;
import de.leander.projekt.R;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/*
 * https://developer.android.com/guide/topics/connectivity/net.bluetooth.html
 * https://github.com/googlesamples/android-BluetoothChat
 * http://stackoverflow.com/questions/24573755/android-net.bluetooth-socket-connect-fails
 * http://stackoverflow.com/questions/858980/file-to-byte-in-java
 * http://stackoverflow.com/questions/4350084/byte-to-file-in-java
 */
public class BluetoothActivity extends Net {
	private static final int REQUEST_ENABLE_BT = 200;
	private static final String name = "NameMemo";
	private static final UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	
	private final Context context = this;
	private final BluetoothActivity activity = this;
	private BluetoothAdapter adapter;
	private ListView devLv;
	private TextView devTv;
	private ArrayList<BluetoothDevice> devList;
	private AcceptThread srv;
	private ConnectThread client;
	private Button bSend;
	private Button bRecieve;
	private HandleThread handler;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bluetooth);
		
		adapter = BluetoothAdapter.getDefaultAdapter();
		devLv = (ListView) findViewById(R.id.lvDevices);
		devTv = (TextView) findViewById(R.id.tvDevices);
		bSend = (Button) findViewById(R.id.bSend);
		bRecieve = (Button) findViewById(R.id.bRevieve);
		
		registerReceiver(receiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
		
		if (adapter == null) {
		    Toast.makeText(context, "Device does not support Bluetooth", Toast.LENGTH_LONG).show();
		    finish();
		}
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		// Enable BT if it is not already enabled
		if (adapter.isEnabled()) {
			setup();
		} else {
			Intent enableBt = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBt, REQUEST_ENABLE_BT);
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
		}
		return false;
	}

	public void setup() {
		// Start a server
		srv = new AcceptThread(adapter, name, uuid, activity);
		srv.start(); //XXX
		
		// Show paired devices
		devTv.setText("Gekoppelte Ger�te:");
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
				Toast.makeText(context, "Versuche Verbindung mit dem Ger�t aufzubauen", Toast.LENGTH_SHORT).show();
				BluetoothDevice device = (BluetoothDevice) parent.getItemAtPosition(position);
				client = new ConnectThread(adapter, device, uuid, activity);
				client.start(); //XXX
			}
		});
	}
	
	private void discover() {
		devTv.setText("Gefundene Ger�te:");
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
	
	public void send() {
		try {	
			File f = getDatabasePath(dbName);
			FileInputStream fis = new FileInputStream(f);
			byte[] bytes = new byte[(int) f.length()];
			fis.read(bytes);
			fis.close();
			handler.sendDb(bytes);
		} catch (IOException e) {
			e.printStackTrace();
		}
	} 
	
	public void connected() {
		bSend.setVisibility(View.VISIBLE);
		bRecieve.setVisibility(View.VISIBLE);
		devLv.setVisibility(View.GONE);
		devTv.setText("Verbunden\nW�hlen Sie eine der beiden Optionen aus"); //XXX
		
		bSend.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				send();
			}
		});
	}
	
	public void setHandler(HandleThread handler) {
		this.handler = handler;
	}
	
	public void receiveDb() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				receive();
			}
		});
		
	}
}