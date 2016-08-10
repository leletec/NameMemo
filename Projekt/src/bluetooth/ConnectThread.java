package bluetooth;

import java.io.IOException;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;

/* For client */
public class ConnectThread extends Thread {
	private final BluetoothSocket sock;
	private final BluetoothAdapter adapter;
	private final Context context;
	
	public ConnectThread (BluetoothAdapter adapter, BluetoothDevice device, UUID uuid, Context context) {
		this.context = context;
		// Use a temporary object that is later assigned to sock, because sock is final
		BluetoothSocket tmp = null;
		this.adapter = adapter;
		
		// Get a BluetoothSocket to connect with the given BluetoothDevice
		try {
			tmp = device.createRfcommSocketToServiceRecord(uuid); // Same UUID in server and client
		} catch (IOException e) {}
		sock = tmp;
	}
	
	public void run() {
		// Cancel discovery because it will slow down the connection. You don't need to check 'isDiscovering()'
		adapter.cancelDiscovery();

		try {
			// Connect the device through the socket. This will block until it succeeds or throws an exception
			sock.connect();
		} catch (IOException connectException) {
			// Unable to connect; close the socket and get out
			try {
				sock.close();
			} catch (IOException closeException) { }
			return;
		}

		// Do work to manage the connection (in a separate thread)
		new HandleThread(sock, context).start();
	}

	/** Will cancel an in-progress connection, and close the socket */
	public void cancel() {
		try {
			sock.close();
		} catch (IOException e) { }
	}
}
