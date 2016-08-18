package bluetooth;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import android.bluetooth.BluetoothSocket;


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
		InputStream tmpIn = null; // Once again, using tmp-Objects because of final attributes
		OutputStream tmpOut = null;
		bytes = new ArrayList<Byte>();
		
		try {
			tmpIn = sock.getInputStream();
			tmpOut = sock.getOutputStream();
		} catch (IOException e) {}
		
		in = tmpIn;
		out = tmpOut;
	}
	
	public void run() {
		activity.setHandler(this);

		buffer = new byte[1024];  // buffer store for the stream
		
		// Keep listening to the InputStream until an exception occurs
		for (;;) {
			try {
				// Read from the InputStream
				in.read(buffer);
				for (int i=0; i < buffer.length; ++i)
					bytes.add(buffer[i]);
				
			} catch (IOException e) {
//				try {
//					FileOutputStream fos = new FileOutputStream(new File (Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "test2.db"));
//					fos.write(buffer);
//					fos.close();
//					Log.d("BT", "File recieved");
//				} catch (Exception e1) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				break;
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
	
	public void sendDb(File file) {
//		try {
//			RandomAccessFile f = new RandomAccessFile(file, "r");
//			byte[] b = new byte[(int)f.length()];
//			f.readFully(b);
//			write(b);
//			f.close();
//			Log.d("BT", "File sent");
//			Log.d("BT", "f.length: " + b.length);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		String s = "Test test 123 !§";
//		write(s.getBytes());
		try {
			InputStream in = new FileInputStream(file);
			byte[] buffer = new byte[(int)file.length()];
			in.read(buffer);
			for (int i=0; i < buffer.length; i+=1024) {
				byte[] bytes = new byte[1024];
				for (int j=0; j < bytes.length; ++j)
					bytes[j] = buffer[i];
				write(bytes);
			}
			in.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void loadDb(File file) {
//		try {
//			FileOutputStream fos = new FileOutputStream(file);
//			fos.write(buffer);
//			fos.close();
//			Log.d("BT", "File recieved");
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		buffer = new byte[bytes.size()];
		for (int i=0 ; i < buffer.length; ++i)
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
