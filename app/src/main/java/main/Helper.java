package main;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Helper class for several methods.
 */
public abstract class Helper {

	/**
	 * Copy file from 'src' to 'dst'.
	 */
	public static void copyFile(File src, File dst) {
		FileInputStream in = null;
		try {
			in = new FileInputStream(src);
			copyFile(in, dst);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (in != null)
					in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * @link http://stackoverflow.com/questions/9292954/how-to-make-a-copy-of-a-file-in-android#9293885
	 */
	public static void copyFile(InputStream in, File dst) {
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(dst);
			byte[] buf = new byte[1024];
		    int len;
		    while ((len = in.read(buf)) > 0)
		        out.write(buf, 0, len);
	    } catch (IOException e) {
	    	e.printStackTrace();
	    } finally {
			try {
				if (out != null)
					out.close();
				if (in != null)
					in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
	    }
	}

	/**
	 * Copy file from 'src' to 'dst', then delete 'src'.
	 */
	public static void moveFile(File src, File dst) {
		copyFile(src, dst);
		src.delete();
	}

	/**
	 * Create a time-coded File for saving an image.
	 */
	public static File getOutputMediaFile(String dir) {
		File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), dir);
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				Log.e("camera", "failed to create directory");
				return null;
			}
		}
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		String filename = "IMG_" + timeStamp + ".jpg";
		return new File(mediaStorageDir.getPath() + File.separator + filename);
	}
}

