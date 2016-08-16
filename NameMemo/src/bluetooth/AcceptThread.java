package bluetooth;

import java.io.IOException;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;


/* For server */
public class AcceptThread extends Thread {
	private final BluetoothServerSocket srvSock;
	private final Context context;
	private HandleThread conn;
	private boolean connected;
	
	public AcceptThread(BluetoothAdapter adapter, String name, UUID uuid, Context context) {
		this.context = context;
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
		for(;;) { // Keep listening until exception occurs or a socket is returned
			try {
				sock = srvSock.accept();
				if (sock != null) {
					// Do work to manage the connection (in a separate thread)
	                Log.d("BT", "Accept sock " + sock);
					conn = new HandleThread(sock, context);
					conn.start();
					connected = true;
	                srvSock.close();
	                break;
				}
			} catch (IOException e) {
				try {
					sock.close();
				} catch (Exception e1) {}
				Log.d("BT", "Accepting sock failed");
				break;
			}
		}
	}
	/** Will cancel the listening socket, and cause the thread to finish */
	public void cancel() {
		try {
			srvSock.close();
			Log.d("BT", "Closed srv");
		} catch (IOException e) {}
	}
	
	public boolean isConnected() {
		return connected;
	}
	
	public void write(byte[] bytes) {
		conn.write(bytes);
	}
}
