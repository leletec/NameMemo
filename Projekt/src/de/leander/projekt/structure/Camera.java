package de.leander.projekt.structure;

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

	public String getFilename(){
		return filename;
	}
	
	public void setFilename(String filename){
		this.filename = filename;
	}
	
	/** Create a File for saving an image */
	@SuppressLint("SimpleDateFormat")
	public Uri getOutputMediaFile() {
		// To be safe, you should check that the SDCard is mounted
		// using Environment.getExternalStorageState() before doing this.

		File mediaStorageDir = new File(
				Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
				"MyCameraApp");
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
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
				.format(new Date());
		filename = "IMG_" + timeStamp + ".jpg";
		File mediaFile = new File(mediaStorageDir.getPath() + File.separator
				+ filename);
		// mediaFile = new File(mediaStorageDir.getPath() + File.separator
		// + "test" + ".jpg");

		uri = Uri.fromFile(mediaFile);
		return uri;
	}

	public Uri fixFileOrientation() {
		BitmapFactory.Options bounds = new BitmapFactory.Options();
		BitmapFactory.Options opts = new BitmapFactory.Options();
		String fname = new File(uri.getPath()).getAbsolutePath();
		Bitmap bm;

		bounds.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(fname, bounds);
		bm = BitmapFactory.decodeFile(fname, opts);
		ExifInterface exif = null;
		try {
			exif = new ExifInterface(fname);
		} catch (IOException e) {
			Log.e("camera", e.toString());
			e.printStackTrace();
			return uri;
		}

		String oristr = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
		int ori = ExifInterface.ORIENTATION_NORMAL;
		if (oristr != null)
			ori = Integer.parseInt(oristr);

		int angle = 0;
		if (ori == ExifInterface.ORIENTATION_ROTATE_90)
			angle = 90;
		else if (ori == ExifInterface.ORIENTATION_ROTATE_180)
			angle = 180;
		else if (ori == ExifInterface.ORIENTATION_ROTATE_270)
			angle = 270;

		Matrix mat = new Matrix();
		mat.setRotate(angle, bm.getWidth() / 2.0f, bm.getHeight() / 2.0f);
		Bitmap rotbm = Bitmap.createBitmap(bm, 0, 0, bounds.outWidth,
				bounds.outHeight, mat, true);
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(fname);
		} catch (FileNotFoundException e) {
			Log.e("camera", e.toString());
			e.printStackTrace();
		}
		rotbm.compress(Bitmap.CompressFormat.JPEG, 100, fos);
		try {
			fos.close();
		} catch (IOException e) {
			Log.e("camera", e.toString());
			e.printStackTrace();
		}
		
		uri = Uri.fromFile(new File(fname));
		return uri;
	}
}
