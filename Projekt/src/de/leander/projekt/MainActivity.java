package de.leander.projekt;

import java.io.File;
import java.util.ArrayList;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import de.leander.projekt.structure.Camera;
import de.leander.projekt.structure.DialogState;
import de.leander.projekt.structure.FileListAdapter;
import de.leander.projekt.structure.MainState;
import de.leander.projekt.structure.MyFile;
import de.leander.projekt.structure.Pictures;
import de.leander.projekt.structure.PicturesDAO;
import de.leander.projekt.structure.StateController;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
public class MainActivity extends Activity implements OnClickListener {
	// private String imageResource = "katze";
	// private int clicks = 3;
	private PicturesDAO datasource;
	private int currentPicture;
	private Pictures[] pictures;
	private Button text;
	private Button ja;
	private Button nein;
	private ImageView image;
	private StateController statecontroller;
	private Uri uri;
	private Camera camera;

	@Override
	// protected void onCreate(Bundle savedInstanceState) {
	// super.onCreate(savedInstanceState);
	// setContentView(R.layout.activity_main);
	//
	// text = (TextView) findViewById(R.id.text);
	// image = (ImageButton) findViewById(R.id.image);
	// gesturedetector = new GestureDetector(this, new GestureListener());
	//
	// image.setOnTouchListener(new OnTouchListener() {
	// @Override
	// public boolean onTouch(View v, MotionEvent event) {
	// image.performClick();
	// return gesturedetector.onTouchEvent(event);
	// }
	// });
	//
	// image.setOnClickListener(new OnClickListener() {
	// @Override
	// public void onClick(View v) {
	// showName(imageResource);
	// }
	// });
	// }
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		datasource = new PicturesDAO(this);
		datasource.open();

		updateArray();

		currentPicture = 0;

		image = (ImageView) findViewById(R.id.image);
		text = (Button) findViewById(R.id.bText);
		text.setBackgroundColor(Color.LTGRAY);
		ja = (Button) findViewById(R.id.bJa);
		ja.setBackgroundColor(Color.GREEN);
		nein = (Button) findViewById(R.id.bNein);
		nein.setBackgroundColor(Color.RED);
		text.setOnClickListener(this);
		ja.setOnClickListener(this);
		nein.setOnClickListener(this);
		statecontroller = new StateController();
		camera = new Camera();
	}

	// private void copyFiles() {
	// int[] files = new int[] { R.raw.hund, R.raw.katze, R.raw.hase };
	// String[] filenames = new String[] { "hund.jpg", "katze.png", "hase.jpg"
	// };
	//
	// if (files != null)
	// for (int i = 0; i < files.length; i++) {
	//
	// int resId = files[i];
	// String filename = filenames[i];
	//
	// InputStream in = null;
	// OutputStream out = null;
	// try {
	// in = getResources().openRawResource(resId);
	// out = openFileOutput(filename, Context.MODE_PRIVATE);
	// copyFile(in, out);
	// } catch (IOException e) {
	// Log.e("tag", "Failed to copy asset file: " + filename, e);
	// } finally {
	// if (in != null) {
	// try {
	// in.close();
	// } catch (IOException e) {
	// // NOOP
	// }
	// }
	// if (out != null) {
	// try {
	// out.close();
	// } catch (IOException e) {
	// // NOOP
	// }
	// }
	// }
	// }
	// }
	//
	// private void copyFile(InputStream in, OutputStream out) throws
	// IOException {
	// byte[] buffer = new byte[1024];
	// int read;
	// while ((read = in.read(buffer)) != -1) {
	// out.write(buffer, 0, read);
	// }
	// }

	public void onClick(View view) {
		if (pictures[currentPicture] == null)
			Log.e("error", "currentBild(" + currentPicture + ") ist null");
		else {
			switch (view.getId()) {
			case R.id.bText:
				text.setText(pictures[currentPicture].getName());
				ja.setVisibility(View.VISIBLE);
				nein.setVisibility(View.VISIBLE);
				try {
					statecontroller.showName();
				} catch (Exception e) {
					Log.e("stateError", e.toString());
					e.printStackTrace();
				}
				break;
			case R.id.bJa:
				datasource.update(pictures[currentPicture].getSource(),
						pictures[currentPicture].getName(),
						pictures[currentPicture].getCalled() + 1,
						pictures[currentPicture].getGotright() + 1,
						pictures[currentPicture].getInarow() + 1);
				updateArray();
				Log.d("Picture", "name:" + pictures[currentPicture].getName()
						+ " called:" + pictures[currentPicture].getCalled()
						+ " gotright:" + pictures[currentPicture].getGotright()
						+ "in a row:" + pictures[currentPicture].getInarow());
				if (pictures[currentPicture].getInarow() >= 3)
					deleteDialog(pictures[currentPicture].getSource(),
							pictures[currentPicture].getName());
				else
					changeSource();
				break;
			case R.id.bNein:
				datasource.update(pictures[currentPicture].getSource(),
						pictures[currentPicture].getName(),
						pictures[currentPicture].getCalled() + 1,
						pictures[currentPicture].getGotright(), 0);
				updateArray();
				Log.d("Picture", "name:" + pictures[currentPicture].getName()
						+ " called:" + pictures[currentPicture].getCalled()
						+ " gotright:" + pictures[currentPicture].getGotright()
						+ "in a row:" + pictures[currentPicture].getInarow());
				changeSource();
				break;
			}
		}
	}

	private void cleanProject() { // verbrannte Erde
		datasource.clean();
		Activity activity = this;
		activity.finish();
	}

	private void changeSource() {
		String source;
		if (currentPicture + 1 < pictures.length
				&& pictures[currentPicture + 1] != null)
			currentPicture += 1;
		else
			currentPicture = 0;
		source = pictures[currentPicture].getSource();

		// String fname = new File(getFilesDir(), source).getAbsolutePath();
		String fname = new File(
				Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
						+ File.separator + "MyCameraApp", source)
				.getAbsolutePath();

		// File mediaStorageDir = new File(
		// Environment
		// .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
		// "MyCameraApp");

		Bitmap bmp = BitmapFactory.decodeFile(fname);
		if (bmp == null) {
			missingFileDialog(source, pictures[currentPicture].getName());
			return;
		} else
			Log.d("changeSource", "Bitmap=" + bmp.toString());
		image.setImageBitmap(bmp);
		Log.d("source", source + " loaded");
		ja.setVisibility(View.INVISIBLE);
		nein.setVisibility(View.INVISIBLE);
		text.setText(R.string.name_anzeigen);
		try {
			statecontroller.showPicture();
		} catch (Exception e) {
			Log.e("stateError", e.toString());
			e.printStackTrace();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menubar, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		// case R.id.importDrawable:
		// copyFiles();
		// break;
		case R.id.addDatabase:
			datasource.add("hund.jpg", "Hund");
			datasource.add("hase.jpg", "Hase");
			updateArray();
			break;
		case R.id.captureImage:
			cameraIntent();
			break;
		case R.id.infoDialog:
			infoDialog();
			break;
		case R.id.cleanProject:
			cleanProject();
			break;
		case R.id.addNewPic:
			addNewPicDialog();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	public void updateArray() {
		String oldSource = null;
		if (pictures != null && pictures.length != 0)
			oldSource = pictures[currentPicture].getSource();
		pictures = datasource.getAllBilder().toArray(new Pictures[0]);
		if (pictures.length == 0) {
			datasource.add("katze.png", "Katze");
			updateArray();
			return;
		}
		for (int i = 0; i < pictures.length; i++)
			if (pictures[i].getSource().equals(oldSource))
				currentPicture = i;
		for (Pictures pic : pictures)
			Log.d("Bilder", pic + "loaded");
		Log.d("Bilder", "Arraylength: " + pictures.length);
	}

	public void deleteDialog(final String source, String name) {
		try {
			statecontroller.showDeleteDialog();
		} catch (Exception e) {
			Log.e("stateError", e.toString());
			e.printStackTrace();
		}
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.deleteDialogTitle);
		builder.setMessage("Soll der Eintrag '" + name + "' gelöscht werden?");
		builder.setPositiveButton(R.string.dialogPositive,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						datasource.delete(source);
					}
				});
		builder.setNegativeButton(R.string.dialogNegative, null);
		AlertDialog dialog = builder.create();
		dialog.show();
		dialog.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				try {
					statecontroller.dismissDeleteDialog();
				} catch (Exception e) {
					Log.e("stateError", e.toString());
					e.printStackTrace();
				} finally {
					changeSource();
				}
			}
		});
	}

	public void infoDialog() {
		try {
			statecontroller.showInfoDialog();
		} catch (Exception e1) {
			Log.e("stateError", e1.toString());
			e1.printStackTrace();
		}
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.infoDialogTitle);
		builder.setMessage(R.string.infoDialog);
		AlertDialog dialog = builder.create();
		dialog.show();
		dialog.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				try {
					statecontroller.dismissInfoDialog();
				} catch (Exception e) {
					Log.e("stateError", e.toString());
					e.printStackTrace();
				}
			}
		});
	}

	public void missingFileDialog(final String source, String name) {
		try {
			statecontroller.showMFileDialog();
		} catch (Exception e) {
			Log.e("stateError", e.toString());
			e.printStackTrace();
		}
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.mfileDialogTitle);
		builder.setMessage("Die Datei "
				+ name
				+ " konnte nicht gefunden werden.\nVielleicht wurde sie gelöscht oder verschoben.\nWollen sie den Verweis darauf löschen?");
		builder.setPositiveButton(R.string.dialogPositive,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						datasource.delete(source);
					}
				});
		builder.setNegativeButton(R.string.dialogNegative, null);
		AlertDialog dialog = builder.create();
		dialog.show();
		dialog.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				try {
					statecontroller.dismissMFileDialog();
				} catch (Exception e) {
					Log.e("stateError", e.toString());
					e.printStackTrace();
				} finally {
					changeSource();
				}
			}
		});
	}

	@SuppressLint("InflateParams")
	public void newPictureDialog(File file) {
		try {
			statecontroller.showNPicDialog();
		} catch (Exception e1) {
			Log.e("stateError", e1.toString());
			e1.printStackTrace();
		}
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		final File f = file;
		if (f == null) {
			builder.setTitle(R.string.nPicDialogTitle);
			builder.setMessage(R.string.nPicDialogMessage);
		} else {
			builder.setTitle(R.string.nPicDialogTitle2);
			builder.setMessage("Geben Sie einen Namen ein, der der Datei '"
					+ f.getName() + "' zugeordnet werden soll!");
		}
		builder.setView(getLayoutInflater().inflate(R.layout.npicdialog, null));
		builder.setPositiveButton(R.string.dialogOk,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						EditText name = (EditText) ((AlertDialog) dialog)
								.findViewById(R.id.ETname);
						if (f == null)
							datasource.add(camera.getFilename(), name.getText()
									.toString());
						else
							datasource.add(f.getAbsolutePath(), name.getText().toString());
						updateArray();
					}
				});
		AlertDialog dialog = builder.create();
		dialog.show();
		dialog.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				try {
					statecontroller.dismissNPicDialog();
				} catch (Exception e) {
					Log.e("stateError", e.toString());
					e.printStackTrace();
				} finally {
					changeSource();
				}
			}
		});
	}

	/* Checks if external storage is available to at least read */
	// TODO Usage for this!
	public boolean isExternalStorageReadable() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)
				|| Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			return true;
		}
		return false;
	}

	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;

	public void cameraIntent() {
		// create Intent to take a picture and return control to the calling
		// application
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		Uri fileUri = camera.getOutputMediaFile(); // create a file to
		// save the image
		intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file
															// name

		try {
			statecontroller.showCameraIntent();
		} catch (Exception e) {
			Log.e("stateError", e.toString());
			e.printStackTrace();
		}
		// start the image capture Intent
		startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
			if (resultCode == RESULT_OK) {
				uri = camera.fixFileOrientation();
				Toast.makeText(this, "Image saved to:\n" + uri,
						Toast.LENGTH_LONG).show();
				image.setImageURI(uri);
				newPictureDialog(null);
			} else {
				if (resultCode == RESULT_CANCELED)
					Toast.makeText(this, "Capturing image canceled",
							Toast.LENGTH_SHORT).show();
				else
					Toast.makeText(this, "Capturing image failed",
							Toast.LENGTH_SHORT).show();
				try {
					statecontroller.dismissCameraIntent();
				} catch (Exception e) {
					Log.e("stateError", e.toString());
					e.printStackTrace();
				}
			}
		}
	}

	@SuppressLint("InflateParams")
	public void addNewPicDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.addNewPic);
		builder.setMessage(R.string.addNewPicMessage);

		File sdcard = Environment
				.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
		File dirs = new File(sdcard.getAbsolutePath());
		File[] files = new File[0];
		if (dirs.exists()) {
			files = dirs.listFiles();
		}
		ArrayList<MyFile> filelist = new ArrayList<MyFile>();
		for (File file : files)
			filelist.add(new MyFile(file, false));
		final FileListAdapter flAdapter = new FileListAdapter(this);
		flAdapter.addList(filelist);
		ListView listview = new ListView(this);
		listview.setAdapter(flAdapter);
		builder.setView(listview); //FIXME No list is displayed
		builder.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				ArrayList<File> checkedFiles = flAdapter.getCheckedFiles();
				for (File file : checkedFiles)
					newPictureDialog(file);
			}
		});
		builder.setNegativeButton(R.string.dialogCancel, null);
		AlertDialog dialog = builder.create();
		// TODO Way to navigate through folders
		dialog.show();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putString("mainstate", statecontroller.getMainstate()
				.toString());
		outState.putString("dialogstate", statecontroller.getDialogstate()
				.toString());
		outState.putString("last", statecontroller.getLast().toString());
		outState.putInt("currentPicture", currentPicture);
		if (camera.getFilename() != null)
			outState.putString("filename", camera.getFilename());
		if (camera.getUri() != null)
			outState.putString("uri", camera.getUri().toString());
		Log.d("onSave", "mainstate '" + statecontroller.getMainstate()
				+ "', dialogstate '" + statecontroller.getDialogstate()
				+ "' , last '" + statecontroller.getLast()
				+ "', currentPicture '" + currentPicture + "', filename '"
				+ camera.getFilename() + "', uri '" + camera.getUri()
				+ "' saved");
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		String mainstate = savedInstanceState.getString("mainstate");
		String dialogstate = savedInstanceState.getString("dialogstate");
		String last = savedInstanceState.getString("last");
		String filename = savedInstanceState.getString("filename");
		Uri uri = Uri.parse(savedInstanceState.getString("uri"));

		statecontroller = new StateController(MainState.valueOf(mainstate),
				DialogState.valueOf(dialogstate), MainState.valueOf(last));

		camera.setFilename(filename);
		camera.setUri(uri);

		Log.d("onRestore", "mainstate '" + mainstate + "', dialogstate '"
				+ dialogstate + "', last '" + last + "', filename" + filename
				+ "', uri '" + uri + "' loaded");

		currentPicture = savedInstanceState.getInt("currentPicture");
		Bitmap bmp = BitmapFactory
				.decodeFile(new File(
						Environment
								.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
								+ File.separator + "MyCameraApp",
						pictures[currentPicture].getSource()).getAbsolutePath());
		if (bmp == null) {
			missingFileDialog(pictures[currentPicture].getSource(),
					pictures[currentPicture].getName());
			return;
		} else
			Log.d("changeSource", "Bitmap=" + bmp.toString());
		image.setImageBitmap(bmp);
		Log.d("onRestore", "source " + pictures[currentPicture].getSource()
				+ " loaded");

		if (statecontroller.getMainstate() == MainState.SHOWSPICTURE
				|| statecontroller.getMainstate() == MainState.CAMERAINTENT) {
			ja.setVisibility(View.INVISIBLE);
			nein.setVisibility(View.INVISIBLE);
			text.setText(R.string.name_anzeigen);
		} else if (statecontroller.getMainstate() == MainState.SHOWSNAME) {
			ja.setVisibility(View.VISIBLE);
			nein.setVisibility(View.VISIBLE);
			text.setText(pictures[currentPicture].getName());
		}

		if (statecontroller.getDialogstate() == DialogState.INFO)
			infoDialog();
		else if (statecontroller.getDialogstate() == DialogState.DELETE)
			deleteDialog(pictures[currentPicture].getSource(),
					pictures[currentPicture].getName());
		else if (statecontroller.getDialogstate() == DialogState.NPIC)
			newPictureDialog(null);

		super.onRestoreInstanceState(savedInstanceState);
	}
}
