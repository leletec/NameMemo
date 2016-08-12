package bluetooth;

import java.io.IOException;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;

/* For client */
public class ConnectThread extends Thread {
	private final BluetoothSocket sock;
	private final BluetoothAdapter adapter;
	private final Context context;
	private HandleThread conn;
	private boolean connected;
	
	public ConnectThread (BluetoothAdapter adapter, BluetoothDevice device, UUID uuid, Context context) {
		this.context = context;
		// Use a temporary object that is later assigned to sock, because sock is final
		BluetoothSocket tmp = null;
		this.adapter = adapter;
		
		// Get a BluetoothSocket to connect with the given BluetoothDevice
		try {
			Log.d("BT", "Device: " + device.getName() + " | " + device.getAddress());
			tmp = device.createInsecureRfcommSocketToServiceRecord(uuid); // Same UUID in server and client
		} catch (IOException e) {}
		sock = tmp;
	}
	
	public void run() {
		// Cancel discovery because it will slow down the connection. You don't need to check 'isDiscovering()'
		adapter.cancelDiscovery();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {}

		try {
			// Connect the device through the socket. This will block until it succeeds or throws an exception
			sock.connect();
		} catch (IOException connectException) {
			Log.d("BT", "Connecting sock failed");
			// Unable to connect; close the socket and get out
			try {
				sock.close();
			} catch (IOException closeException) { }
			return;
		}

		Log.d("BT", "Connect sock " + sock);
		// Do work to manage the connection (in a separate thread)
		conn = new HandleThread(sock, context);
		conn.start();
		connected = true;
	}

	/** Will cancel an in-progress connection, and close the socket */
	public void cancel() {
		try {
			sock.close();
		} catch (IOException e) { }
	}
	
	public boolean isConnected() {
		return connected;
	}
	
	public void write(byte[] bytes) {
		conn.write(bytes);
	}
}
