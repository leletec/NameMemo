package bluetooth;

import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import de.leander.projekt.R;
import de.leander.projekt.structure.BTListAdapter;
import android.app.Activity;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class BluetoothActivity extends Activity {
	private static final int REQUEST_ENABLE_BT = 200;
	private static final String name = "NameMemo";
	private static final UUID uuid = UUID.randomUUID();
	
	private final Context context = this;
	private BluetoothAdapter btAdapter;
	private ListView devLv;
	private TextView devTv;
	private ArrayList<BluetoothDevice> devList;
	private AcceptThread srv;
	private ConnectThread client;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bluetooth);
		
		btAdapter = BluetoothAdapter.getDefaultAdapter();
		devLv = (ListView) findViewById(R.id.lvDevices);
		devTv = (TextView) findViewById(R.id.tvDevices);
		
		// Register the BroadcastReceiver
		registerReceiver(receiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
		
		if (btAdapter == null) {
		    Toast.makeText(context, "Device does not support Bluetooth", Toast.LENGTH_LONG).show();
		    finish();
		}
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		// Enable BT if it is not already enabled
		if (btAdapter.isEnabled()) {
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
		if (btAdapter.getScanMode() !=
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
		srv = new AcceptThread(btAdapter, name, uuid, context);
		srv.start();
		
		// Show paired devices
		devTv.setText("Gekoppelte Geräte:");
		Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();
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
				Toast.makeText(context, "Versuche Verbindung mit dem Gerät aufzubauen", Toast.LENGTH_SHORT).show();
				BluetoothDevice device = (BluetoothDevice) parent.getItemAtPosition(position);
				client = new ConnectThread(btAdapter, device, uuid, context);
				client.start();
			}
		});
	}
	
	private void discover() {
		devTv.setText("Gefundene Geräte:");
		btAdapter.startDiscovery();
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
}
