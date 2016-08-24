package net.bluetooth;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import net.Net;
import android.bluetooth.BluetoothSocket;

/* For client and server */
public class HandleThread extends Thread {
	private final InputStream in;
	private final OutputStream out;
	private final BluetoothActivity activity;
	private boolean receiving = false;

	public HandleThread(BluetoothSocket sock, BluetoothActivity activity) {
		this.activity = activity;
		InputStream tmpIn = null;
		OutputStream tmpOut = null;
		
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
			OutputStream fos = new FileOutputStream(Net.importFile);

			for (;;) {
				try {
					int length = in.read(buffer);
					fos.write(buffer, 0, length);
					receiving = true;
				} catch (IOException ex) {
					break;
				}
			}
			fos.close();
			if (receiving)
				activity.receiveDb();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendDb(byte[] bytes) {
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
}
