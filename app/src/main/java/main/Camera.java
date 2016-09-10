package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

public class Camera {

	private Uri uri;
	private String filename;

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public Uri getUri() {
		return uri;
	}

	public void setUri(Uri uri) {
		this.uri = uri;
	}

	/**
	 * TODO source for that
	 * Create a File for saving an image.
	 */
	@SuppressLint("SimpleDateFormat")
	public Uri getOutputMediaFile(String dir) {
		// To be safe, you should check that the SDCard is mounted
		// using Environment.getExternalStorageState() before doing this.

		File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), dir);
		// This location works best if you want the created images to be shared
		// between applications and persist after your app has been uninstalled.

		// Create the storage directory if it does not exist
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				Log.d("MyCameraApp", "failed to create directory");
				return null;
			}
		}
		// Create a media file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		filename = "IMG_" + timeStamp + ".jpg";
		File mediaFile = new File(mediaStorageDir.getPath() + File.separator + filename);
		// mediaFile = new File(mediaStorageDir.getPath() + File.separator
		// + "test" + ".jpg");

		uri = Uri.fromFile(mediaFile);
		return uri;
	}

	/**
	 * Rotates an image to bring it into normal orientation.
	 * @return	The "repaired" file's Uri.
	 */
	public Uri fixFileOrientation() {
		String fname = new File(uri.getPath()).getAbsolutePath();
		Bitmap bm = BitmapFactory.decodeFile(fname);
		ExifInterface exif;
		try {
			exif = new ExifInterface(fname);
		}
		catch (IOException e) {
			e.printStackTrace();
			return uri;
		}
		int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
		Bitmap bmRot = Helper.rotateBitmap(bm, orientation);
		if (bmRot == null)
			return uri;

		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(fname);
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		bmRot.compress(Bitmap.CompressFormat.JPEG, 100, fos);
		try {
			if (fos != null)
				fos.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}

		uri = Uri.fromFile(new File(fname));
		return uri;
	}
}
