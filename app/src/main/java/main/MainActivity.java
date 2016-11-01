package main;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide; /* https://inthecheesefactory.com/blog/get-to-know-glide-recommended-by-google/en */

import net.bluetooth.BluetoothActivity;
import net.nfc.NfcActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import database.Picture;
import database.PicturesDAO;
import database.SettingsDAO;
import de.leletec.namememo.R;
import design.FileListAdapter;

import static de.leletec.namememo.R.menu.menubar;

@SuppressWarnings("ResultOfMethodCallIgnored")
@SuppressLint("InflateParams")
/** http://stackoverflow.com/questions/26432544/missing-actionbar-in-material-design */
public class MainActivity extends AppCompatActivity implements OnClickListener {
	//Tables
	private PicturesDAO pictureDb;
	private SettingsDAO settingsDb;

	// Views
	private Button text;
	private Button yes;
	private Button no;
	private ImageView image;

	// Settings
	private int inarowReq;
	private boolean rdmSeq;
	private boolean colPics;
	private boolean addPicGallery;

	// misc
	private final Context context = this;
	private int currentPicture;
	private Picture[] pictures;
	private String app_name;
	private File cFile; // File used for camera operations
	private int ivHeight; // height of the ImageView
	private int ivWidth; // width of the ImageView

	private static final int CAPTURE_IMAGE_REQUEST = 100;
	private static final int NEW_IMAGE_REQUEST = 200;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		pictureDb = new PicturesDAO(this);
		pictureDb.open();
		settingsDb = new SettingsDAO(this);
		settingsDb.open();
		settingsDb.init();

		image = (ImageView) findViewById(R.id.image);
		text = (Button) findViewById(R.id.bText);
		text.setBackgroundColor(Color.LTGRAY);
		yes = (Button) findViewById(R.id.bJa);
		yes.setBackgroundColor(Color.GREEN);
		no = (Button) findViewById(R.id.bNein);
		no.setBackgroundColor(Color.RED);
		text.setOnClickListener(this);
		yes.setOnClickListener(this);
		no.setOnClickListener(this);
		app_name = getString(R.string.app_name);

		loadPictures();
		currentPicture = 0;

		/** http://stackoverflow.com/questions/33162152/storage-permission-error-in-marshmallow */
		if (Build.VERSION.SDK_INT >= 23) {
			if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
				Log.v("setup", "Permission is granted");
			} else {
				Log.v("setup", "Permission is revoked");
				ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
			}
		} else { //permission is automatically granted on sdk<23 upon installation
			Log.v("setup", "Permission is granted");
		}

		/** http://stackoverflow.com/questions/7655874/how-do-you-remove-the-title-text-from-the-android-actionbar */
		try {
			getSupportActionBar().setDisplayShowTitleEnabled(false);
		} catch (NullPointerException e) {
			e.printStackTrace();
		}

		/** http://stackoverflow.com/questions/1016896/get-screen-dimensions-in-pixels */
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		ivHeight = (int) Math.round(size.y * 0.75);
		ivWidth = size.x;
		image.setMaxWidth(ivWidth);
		image.setMinimumWidth(ivWidth);
		image.setMaxHeight(ivHeight);
		image.setMinimumHeight(ivHeight);
	}

	@Override
	protected void onStart() {
		super.onStart();
		File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), app_name);
		if (!(new File(dir, "katze.png").exists() && new File(dir, "hund.jpg").exists() && new File(dir, "hase.jpg").exists()))
			copyExamplePics(dir);
		else
			Log.d("setup", "everything is there");

		inarowReq = settingsDb.get("inarowReq");
		rdmSeq = settingsDb.get("seqType") == 1;
		colPics = settingsDb.get("colPics") == 1;
		addPicGallery = settingsDb.get("addPicType") == 1;
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	/**
	 * Handles the three buttons:
	 * Clicking the textbox while you only see the picture reveals the name and the Yes and No buttons.
	 * The database is updated and a new image is shown after you press Yes or No.
	 */
	public void onClick(View view) {
		if (pictures[currentPicture] == null)
			Log.e("error", "currentPicture(" + currentPicture + ") is null");
		else {
			switch (view.getId()) {
				case R.id.bText:
					text.setText(pictures[currentPicture].getName());
					yes.setVisibility(View.VISIBLE);
					no.setVisibility(View.VISIBLE);
					break;
				case R.id.bJa:
					pictureDb.update(pictures[currentPicture].getSource(),
							pictures[currentPicture].getName(),
							pictures[currentPicture].getCalled() + 1,
							pictures[currentPicture].getInarow() + 1);
					loadPictures();
					Log.d("Picture", "name:" + pictures[currentPicture].getName()
							+ " called:" + pictures[currentPicture].getCalled()
							+ " in a row:" + pictures[currentPicture].getInarow());
					if (pictures[currentPicture].getInarow() >= inarowReq)
						deleteDialog(pictures[currentPicture].getSource(),
								pictures[currentPicture].getName());
					else
						showNext();
					break;
				case R.id.bNein:
					pictureDb.update(pictures[currentPicture].getSource(),
							pictures[currentPicture].getName(),
							pictures[currentPicture].getCalled() + 1,
							0);
					loadPictures();
					Log.d("Picture", "name:" + pictures[currentPicture].getName()
							+ " called:" + pictures[currentPicture].getCalled()
							+ " in a row:" + pictures[currentPicture].getInarow());
					showNext();
					break;
			}
		}
	}

	/**
	 * Copies three example pictures to the app's pictures directory.
	 *
	 * @param dir the directory to copy to
	 */
	private void copyExamplePics(File dir) {
		int[] files = new int[]{R.raw.hund, R.raw.katze, R.raw.hase};
		String[] filenames = new String[]{"hund.jpg", "katze.png", "hase.jpg"};

		dir.mkdirs();

		for (int i = 0; i < files.length; i++) {
			int resId = files[i];
			String filename = filenames[i];
			InputStream in = getResources().openRawResource(resId);
			File dst = new File(dir, filename);
			Helper.copyFile(in, dst);
			Log.d("setup", "copied file " + filename);
		}
	}

	/**
	 * Resets the app to the factory settings.
	 */
	private void resetToFactory() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.cleanProject);
		builder.setMessage(R.string.cleanProjectMessage);
		builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialogInterface, int i) {
				pictureDb.clean();
				settingsDb.clean();
				Toast.makeText(context, "Datenbank wurde gelöscht", Toast.LENGTH_SHORT).show(); //XXX
				finish();
			}
		});
		builder.setNegativeButton(R.string.no, null);
		builder.create().show();
	}

	/**
	 * Loads the next or a "random" picture in the pictures array and displays it.
	 */
	private void showNext() {
		Picture newPic;
		if (rdmSeq) {
			Picture lastPic = pictures[currentPicture];
			ArrayList<Picture> tmp = new ArrayList<>();
			int i;
			for (Picture pic : pictures) {
				i = pic.getInarow();
				do {
					tmp.add(pic);
					++i;
				} while (i < inarowReq);
			}
			Random r = new Random();
			i = 0;
			do {
				newPic = tmp.get(r.nextInt(tmp.size()));
				++i;
			} while (newPic.equals(lastPic) && i < 100);
			for (i = 0; i < pictures.length; ++i)
				if (newPic.equals(pictures[i]))
					currentPicture = i;
		} else {
			currentPicture = (currentPicture + 1) % pictures.length;
			newPic = pictures[currentPicture];
		}

		File newFile = new File(newPic.getSource());
		if (!newFile.exists()) {
			missingFileDialog(pictures[currentPicture].getSource(), pictures[currentPicture].getName());
			return;
		}
		Glide.with(this).load(newFile).override(ivWidth, ivHeight).into(image);

		Log.d("showNext", newPic.getShowAs() + " loaded");
		yes.setVisibility(View.INVISIBLE);
		no.setVisibility(View.INVISIBLE);
		text.setText(R.string.showName);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(menubar, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			/**
			 * Loads two more example-pictures in the database.
			 */
			case R.id.addExamples:
				File f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + File.separator + app_name, "hund.jpg");
				boolean d = pictureDb.add(f.getAbsolutePath(), "Hund");
				f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + File.separator + app_name, "hase.jpg");
				boolean r = pictureDb.add(f.getAbsolutePath(), "Hase");

				if (r && d)
					Toast.makeText(this, "Beispielbilder wurden hinzugefügt", Toast.LENGTH_SHORT).show(); //XXX
				else if (r || d)
					Toast.makeText(this, "Beispielbild wurde hinzugefügt", Toast.LENGTH_SHORT).show(); //XXX
				else
					Toast.makeText(this, "Beispielbilder waren bereits vorhanden", Toast.LENGTH_SHORT).show(); //XXX

				loadPictures();
				return true;
			case R.id.captureImage:
				cameraIntent();
				return true;
			case R.id.infoDialog:
				infoDialog();
				return true;
			case R.id.addNewPic:
				addPicFromStorageDialog();
				return true;
			case R.id.connMenu:
				connDialog();
				return true;
			case R.id.settingsMenu:
				settingsDialog();
		}
		return false;
	}

	/**
	 * Loads the array from the database and adds a cat picture if the database would otherwise be empty.
	 */
	private void loadPictures() {
		String oldSource = null;
		if (pictures != null && pictures.length != 0)
			oldSource = pictures[currentPicture].getSource();
		List<Picture> allPics = pictureDb.getAllPics();
		pictures = allPics.toArray(new Picture[allPics.size()]);
		if (pictures.length == 0) {
			Toast.makeText(this, R.string.emptyDb, Toast.LENGTH_SHORT).show();
			File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), app_name);
			File f = new File(dir, "katze.png");
			pictureDb.add(f.getAbsolutePath(), "Katze");
			Log.d("loadPictures", "loaded 'Katze', source: " + f.getAbsolutePath());
			loadPictures();
			return;
		}
		for (int i = 0; i < pictures.length; i++)
			if (pictures[i].getSource().equals(oldSource))
				currentPicture = i;
		for (Picture pic : pictures)
			Log.d("loadPictures", pic + "loaded");
		Log.d("loadPictures", "Arraylength: " + pictures.length);
	}

	/**
	 * Shows a dialog where the user can delete a picture from the database after repeatedly remembering the name correctly.
	 *
	 * @param source The source of that picture.
	 * @param name   The name belonging to that picture.
	 */
	private void deleteDialog(final String source, String name) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.deleteDialogTitle);
		builder.setMessage("Soll der Eintrag '" + name + "' gelöscht werden?");
		builder.setPositiveButton(R.string.yes,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						pictureDb.delete(source);
					}
				});
		builder.setNegativeButton(R.string.no, null);
		AlertDialog dialog = builder.create();
		dialog.show();
		dialog.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				showNext();
			}
		});
	}

	/**
	 * Show an informative dialog.
	 */
	private void infoDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.infoDialogTitle);
		builder.setMessage(R.string.infoDialog);
		builder.create().show();
	}

	/**
	 * If the database retains a path to a file that doesn't exist anymore, this dialog is shown to the user, who can then delete the dangling path from the database.
	 *
	 * @param source The source of the missing file.
	 * @param name   The name belonging to the missing picture.
	 */
	private void missingFileDialog(final String source, String name) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.mfileDialogTitle);
		builder.setMessage("Die Datei " + name + " konnte nicht gefunden werden.\nVielleicht wurde sie gelöscht oder verschoben.\nWollen Sie den Verweis darauf löschen?");
		builder.setPositiveButton(R.string.yes,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						pictureDb.delete(source);
					}
				});
		builder.setNegativeButton(R.string.no, null);
		AlertDialog dialog = builder.create();
		dialog.show();
		dialog.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				showNext();
			}
		});
	}

	/**
	 * Prompts the user for the name to be associated with a newly taken picture.
	 *
	 * @param f The new picture. 'null' if it was taken by the camera.
	 */
	private void newShotDialog(final File f) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		if (f == null) {
			builder.setTitle(R.string.nPicDialogTitle);
			builder.setMessage(R.string.nPicDialogMessage);
		} else {
			builder.setTitle(R.string.nPicDialogTitle2);
			builder.setMessage("Geben Sie einen Namen ein, der der Datei '" + f.getName() + "' zugeordnet werden soll!");
		}
		builder.setView(getLayoutInflater().inflate(R.layout.npicdialog, null));
		builder.setPositiveButton(R.string.dialogOk,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						EditText name = (EditText) ((AlertDialog) dialog)
								.findViewById(R.id.etName);
						if (f == null) {
							if (!pictureDb.add(cFile.getPath(), name.getText().toString()))
								Toast.makeText(context, R.string.alreadyThere, Toast.LENGTH_SHORT).show();
						} else {
							File dst = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + File.separator + app_name, f.getName());
							String path = dst.getAbsolutePath();
							if (!f.getAbsolutePath().equals(path))
								Helper.copyFile(f, dst);
							if (!pictureDb.add(path, name.getText().toString()))
								Toast.makeText(context, R.string.alreadyThere, Toast.LENGTH_SHORT).show();
						}
						loadPictures();
					}
				});
		AlertDialog dialog = builder.create();
		dialog.show();
		dialog.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				showNext();
			}
		});
	}

	/**
	 * @return If external storage is available to at least read
	 */
	private boolean isExternalStorageReadable() {
		String state = Environment.getExternalStorageState();
		return (Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state));

	}

	/**
	 * Creates a intent to take a new photo (to add to the database).
	 */
	private void cameraIntent() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		if (!Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			Toast.makeText(this, R.string.writeErr, Toast.LENGTH_SHORT).show();
			return;
		}
		cFile = Helper.getOutputMediaFile(app_name); // create a file to save the image
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cFile)); // set the image file name
		startActivityForResult(intent, CAPTURE_IMAGE_REQUEST);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case CAPTURE_IMAGE_REQUEST:
			if (resultCode == RESULT_OK) {
				Glide.with(this).load(cFile).override(ivWidth, ivHeight).into(image);
				newShotDialog(null);
			} else {
				if (resultCode == RESULT_CANCELED)
					Toast.makeText(this, R.string.canceled, Toast.LENGTH_SHORT).show();
				else
					Toast.makeText(this, R.string.failed, Toast.LENGTH_SHORT).show();
			}
			break;
		case NEW_IMAGE_REQUEST:
			if (resultCode == RESULT_OK && data != null && data.getData() != null) {
				Uri uri = data.getData();
				Log.d("gallery", "Path: " + uri.getPath());
				File dst = Helper.getOutputMediaFile(app_name);
				if (dst == null) {
					Toast.makeText(this, R.string.writeErr, Toast.LENGTH_SHORT).show();
					return;
				}
				try {
					Bitmap bm = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
					Log.d("gallery", String.valueOf(bm));
					OutputStream out = new FileOutputStream(dst);
					bm.compress(Bitmap.CompressFormat.JPEG, 90, out);
				} catch (IOException e) {
					e.printStackTrace();
					Toast.makeText(this, R.string.failed, Toast.LENGTH_SHORT).show();
					return;
				}
				newShotDialog(dst);
			} else {
				Toast.makeText(this, R.string.failed, Toast.LENGTH_SHORT).show();
			}
		}
	}

	/**
	 * This dialog lets the user walk through the directory tree, previewing and adding images with the help of previewDialog().
	 */
	private void addPicFromStorageDialog() {
		if (!isExternalStorageReadable()) {
			Toast.makeText(this, R.string.readErr, Toast.LENGTH_SHORT).show();
			return;
		}
		if (addPicGallery) {
			/** http://codetheory.in/android-pick-select-image-from-gallery-with-intents/ */
			Intent intent = new Intent();
			intent.setType("image/*");
			intent.setAction(Intent.ACTION_GET_CONTENT);
			startActivityForResult(Intent.createChooser(intent, getString(R.string.addNewPic)), NEW_IMAGE_REQUEST);
		} else {
			AddPicFromStorageDialog(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), app_name));
		}
	}

	/**
	 * @param dir The directory you are currently in.
	 */
	private void AddPicFromStorageDialog(final File dir) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.addNewPic);
		File[] files = new File[0];
		if (dir.exists()) {
			files = dir.listFiles();
		}
		ArrayList<File> filelist = new ArrayList<>();
		filelist.add(new File(".."));
		for (File file : files) {
			String fname = file.getName();
			if (fname.endsWith(".jpg") || fname.endsWith(".png") || fname.endsWith(".JPG") || fname.endsWith(".PNG") || file.isDirectory())
				filelist.add(file);
		}
		FileListAdapter adapter = new FileListAdapter(this, filelist);
		ListView listview = new ListView(this);
		listview.setAdapter(adapter);
		builder.setView(listview);
		Log.d("path", dir.getAbsolutePath());
		builder.setNegativeButton(R.string.dialogCancel, null);
		final AlertDialog dialog = builder.create();

		listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {
				final File file = ((File) parent.getItemAtPosition(position));
				String fname = file.getName();
				if (fname.equals("..")) {
					if (new File(dir.getParent()).canRead()) {
						dialog.setOnDismissListener(new OnDismissListener() {
							@Override
							public void onDismiss(DialogInterface dialog) {
								AddPicFromStorageDialog(new File(dir.getParent()));
							}
						});
						dialog.dismiss();
					} else
						Toast.makeText(context, R.string.readErr, Toast.LENGTH_SHORT).show();
				} else if (file.isDirectory()) {
					if (file.canRead()) {
						dialog.setOnDismissListener(new OnDismissListener() {
							@Override
							public void onDismiss(DialogInterface dialog) {
								AddPicFromStorageDialog(file);
							}
						});
						dialog.dismiss();
					} else
						Toast.makeText(context, R.string.readErr, Toast.LENGTH_SHORT).show();
				} else {
					dialog.setOnDismissListener(new OnDismissListener() {
						@Override
						public void onDismiss(DialogInterface dialog) {
							previewDialog(file, dir);
						}
					});
				}
				dialog.dismiss();
			}
		});
		dialog.show();
	}

	/**
	 * The preview for the AddPicFromStorageDialog().
	 *
	 * @param file The picture you clicked on.
	 * @param dir  The directory you are in.
	 */
	private void previewDialog(final File file, final File dir) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		ImageView iv = new ImageView(this);
		Glide.with(this).load(file).override(Math.round(ivWidth / 2), Math.round(ivHeight / 2)).into(iv);
		builder.setView(iv);
		builder.setPositiveButton(R.string.dialogOk,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						newShotDialog(file);
					}
				});
		builder.setNegativeButton(R.string.dialogBack,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						AddPicFromStorageDialog(dir);
					}
				});
		builder.create().show();
	}

	/**
	 * A dialog where you can change the settings.
	 */
	private void settingsDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.settingsMenu);
		builder.setView(getLayoutInflater().inflate(R.layout.settings_menu, null));
		AlertDialog dialog = builder.create();
		dialog.show();

		EditText etInarowReq = (EditText) dialog.findViewById(R.id.etInarowReq);
		etInarowReq.setText(inarowReq + "");
		etInarowReq.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				try {
					StringBuilder sb = new StringBuilder(v.getText());
					int i = Integer.parseInt(sb.toString());
					if (i > 0)
						inarowReq = i;
					else
						Toast.makeText(context, R.string.invalidInput, Toast.LENGTH_SHORT).show();
					settingsDb.set("inarowReq", inarowReq);
					return true;
				} catch (Exception e) {
					return false;
				}
			}
		});

		Switch sRdmSeq = (Switch) dialog.findViewById(R.id.sRdmSeq);
		sRdmSeq.setChecked(rdmSeq);
		sRdmSeq.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				rdmSeq = isChecked;
				settingsDb.set("seqType", rdmSeq ? 1 : 0);
			}
		});

		Switch sColPics = (Switch) dialog.findViewById(R.id.sColPics);
		sColPics.setChecked(colPics);
		sColPics.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				colPics = isChecked;
				settingsDb.set("colPics", colPics ? 1 : 0);
			}
		});

		Switch sAddPicGallery = (Switch) dialog.findViewById(R.id.sAddPicGallery);
		sAddPicGallery.setChecked(addPicGallery);
		sAddPicGallery.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
				addPicGallery = b;
				settingsDb.set("addPicType", b ? 1 : 0);
			}
		});

		Button bClean = (Button) dialog.findViewById(R.id.bClean);
		bClean.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				resetToFactory();
			}
		});
	}

	/**
	 * A dialog where you can choose whether you want to connect via bluetooth or NFC
	 */
	private void connDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.connMenu);
		CharSequence[] foo = new CharSequence[] {"Bluetooth", "NFC"};
		builder.setItems(foo, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialogInterface, int i) {
				switch (i) {
				case 0:	startActivity(new Intent(context, BluetoothActivity.class)); break;
				case 1: startActivity(new Intent(context, NfcActivity.class));
				}
			}
		});
		builder.create().show();
	}
}
