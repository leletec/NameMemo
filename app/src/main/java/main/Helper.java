package main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public abstract class Helper {

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
	
	public static void moveFile(File src, File dst) {
		copyFile(src, dst);
		src.delete();
	}
}

