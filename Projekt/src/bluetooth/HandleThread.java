package bluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.widget.Toast;

/* For client and server */
public class HandleThread extends Thread {
	private final BluetoothSocket sock;
	private final InputStream in;
	private final OutputStream out;
	private final Context context;
	
	public HandleThread(BluetoothSocket sock, Context context) {
		this.sock = sock;
		this.context = context;
		InputStream tmpIn = null; // Once again, using tmp-Objects because of final attributes
		OutputStream tmpOut = null;
		
		try {
			tmpIn = sock.getInputStream();
			tmpOut = sock.getOutputStream();
		} catch (IOException e) {}
		
		in = tmpIn;
		out = tmpOut;
	}
	
	public void run() {
		Toast.makeText(context, "Connected to socket " + sock, Toast.LENGTH_LONG).show();
		
		byte[] buffer = new byte[1024];  // buffer store for the stream
		int bytes; // bytes returned from read()
		
		// Keep listening to the InputStream until an exception occurs
		for (;;) {
			try {
				// Read from the InputStream
				bytes = in.read(buffer);
				//TODO do stuff
			} catch (IOException e) {
				break;
			}
		}
	}
	
	/* Call this from the main activity to send data to the remote device */
	public void write(byte[] bytes) {
		try {
			out.write(bytes);
		} catch (IOException e) { }
	}

	/* Call this from the main activity to shutdown the connection */
	public void cancel() {
		try {
			sock.close();
		} catch (IOException e) { }
	}
}
