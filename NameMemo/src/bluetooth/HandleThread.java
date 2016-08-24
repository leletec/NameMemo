package bluetooth;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import states.DialogState;
import android.bluetooth.BluetoothSocket;
import android.os.Environment;
import android.provider.SyncStateContract.Constants;
import android.util.Log;

/* For client and server */
public class HandleThread extends Thread {
	private final BluetoothSocket sock;
	private final InputStream in;
	private final OutputStream out;
	private final BluetoothActivity activity;
	private byte[] buffer; // bytes returned from read()
	ArrayList<Byte> bytes;

	public HandleThread(BluetoothSocket sock, BluetoothActivity activity) {
		this.sock = sock;
		this.activity = activity;
		InputStream tmpIn = null; // Once again, using tmp-Objects because of
									// final attributes
		OutputStream tmpOut = null;
		bytes = new ArrayList<Byte>();

		try {
			tmpIn = sock.getInputStream();
			tmpOut = sock.getOutputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}

		in = tmpIn;
		out = tmpOut;
	}

	public void run() {
		activity.setHandler(this);
		byte[] buffer = new byte[16384];

		try {
			File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
			File f = new File(dir, "test.db");
			OutputStream fos = new FileOutputStream(f);

			for (;;) {
				try {
					int length = in.read(buffer);
					fos.write(buffer, 0, length);
				} catch (IOException ex) {
					break;
				}
			}
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/* Call this from the main activity to send data to the remote device */
	public void write(byte[] bytes) {
		try {
			out.write(bytes);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void write(byte b) {
		try {
			out.write(b);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void close() {
		try {
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/* Call this from the main activity to shutdown the connection */
	public void cancel() {
		try {
			sock.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendDb(byte[] bytes) throws IOException {
		try {
			int chunkLength = 8192;
			int pos = 0;
			
			while (pos < bytes.length) {
				int l = bytes.length - pos;
				if (l > chunkLength)
					l = chunkLength;
				out.write(bytes, pos, l);
				pos += l;
			}
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void loadDb(File file) {
		// try {
		// FileOutputStream fos = new FileOutputStream(file);
		// fos.write(buffer);
		// fos.close();
		// Log.d("BT", "File recieved");
		// } catch (Exception e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		buffer = new byte[bytes.size()];
		for (int i = 0; i < buffer.length; ++i)
			buffer[i] = bytes.get(i);
		OutputStream out;
		try {
			out = new FileOutputStream(file);
			out.write(buffer);
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
