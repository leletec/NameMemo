package bluetooth;

import java.io.IOException;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;


/* For server */
public class AcceptThread extends Thread {
	private final BluetoothServerSocket srvSock;
	private final Context context;
	
	public AcceptThread(BluetoothAdapter adapter, String name, UUID uuid, Context context) {
		this.context = context;
		// Use a temporary object that is later assigned to srvSock, because srvSock is final
		BluetoothServerSocket tmp = null;
		try {
			tmp = adapter.listenUsingRfcommWithServiceRecord(name, uuid); // Same UUID in server and client
		} catch (IOException e) {}
		srvSock = tmp;
	}
	
	public void run() {
		BluetoothSocket sock = null;
		for(;;) { // Keep listening until exception occurs or a socket is returned
			try {
				sock = srvSock.accept();
				if (sock != null) {
					// Do work to manage the connection (in a separate thread)
	                new HandleThread(sock, context).start();
	                srvSock.close();
	                break;
				}
			} catch (IOException e) {
				break;
			}
		}
	}
	/** Will cancel the listening socket, and cause the thread to finish */
	public void cancel() {
		try {
			srvSock.close();
		} catch (IOException e) {}
	}
}
