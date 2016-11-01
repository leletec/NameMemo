package net.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

/**
 * Thread for bluetooth server.
 */
public class AcceptThread extends Thread {
	private final BluetoothServerSocket srvSock;
	private final BluetoothActivity activity;
	
	AcceptThread(BluetoothAdapter adapter, String name, UUID uuid, BluetoothActivity activity) {
		this.activity = activity;
		// Use a temporary object that is later assigned to srvSock, because srvSock is final
		BluetoothServerSocket tmp = null;
		try {
			tmp = adapter.listenUsingInsecureRfcommWithServiceRecord(name, uuid); // Same UUID in server and client
		} catch (IOException e) {Log.d("BT", "Failed to get srvSock");}
		srvSock = tmp;
	}
	
	public void run() {
		BluetoothSocket sock = null;
		Log.d("BT", "Started srv");
		// Keep listening until exception occurs or a socket is returned
		for(;;) {
			try {
				sock = srvSock.accept();
				if (sock != null) {
					// Do work to manage the connection (in a separate thread)
					Log.d("BT", "Accept sock " + sock);
					HandleThread conn = new HandleThread(sock, activity);
					conn.start();
					activity.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							activity.connected();
						}
					});
					srvSock.close();
					break;
				}
			} catch (IOException e) {
				try {
					if (sock != null)
						sock.close();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				Log.d("BT", "Accepting sock failed");
				break;
			}
		}
	}
	/**
	 * Will cancel the listening socket, and cause the thread to finish.
	 */
	void cancel() {
		try {
			srvSock.close();
			Log.d("BT", "Closed srv");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
