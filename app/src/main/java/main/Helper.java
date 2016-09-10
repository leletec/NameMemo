package main;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;

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

	// http://stackoverflow.com/questions/20478765/how-to-get-the-correct-orientation-of-the-image-selected-from-the-default-image
	public static Bitmap rotateBitmap(Bitmap bitmap, int orientation) {
		Matrix matrix = new Matrix();
		switch (orientation) {
		case ExifInterface.ORIENTATION_NORMAL:
			return bitmap;
		case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
			matrix.setScale(-1, 1);
			break;
		case ExifInterface.ORIENTATION_ROTATE_180:
			matrix.setRotate(180);
			break;
		case ExifInterface.ORIENTATION_FLIP_VERTICAL:
			matrix.setRotate(180);
			matrix.postScale(-1, 1);
			break;
		case ExifInterface.ORIENTATION_TRANSPOSE:
			matrix.setRotate(90);
			matrix.postScale(-1, 1);
			break;
		case ExifInterface.ORIENTATION_ROTATE_90:
			matrix.setRotate(90);
			break;
		case ExifInterface.ORIENTATION_TRANSVERSE:
			matrix.setRotate(-90);
			matrix.postScale(-1, 1);
			break;
		case ExifInterface.ORIENTATION_ROTATE_270:
			matrix.setRotate(-90);
			break;
		default:
			return bitmap;
		}
		try {
			Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
			bitmap.recycle();
			return bmRotated;
		}
		catch (OutOfMemoryError e) {
			e.printStackTrace();
			return null;
		}
	}
}

