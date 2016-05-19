package de.leander.projekt.structure;

import java.io.File;

public class MyFile {
	private File file;
	private boolean isChecked = false;

	public MyFile(File file, boolean isChecked) {
		super();
		this.file = file;
		this.isChecked = isChecked;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public boolean isChecked() {
		return isChecked;
	}

	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}
}
