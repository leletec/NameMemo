package de.leander.projekt.structure;

import java.io.File;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import de.leander.projekt.R;

public class FileListAdapter extends ArrayAdapter<MyFile> {

	private ArrayList<MyFile> filelist;
	private Context context;

	public FileListAdapter(Context context) {
		super(context, R.layout.listlayout);
	}

	public void addList(ArrayList filelist) {
		this.filelist = filelist;
	}

	public ArrayList<File> getCheckedFiles() {
		ArrayList<File> checkedFiles = new ArrayList<File>();
		for (MyFile file : filelist)
			if (file.isChecked())
				checkedFiles.add(file.getFile());
		return checkedFiles;
	}

	@SuppressLint("ViewHolder")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.listlayout, parent, false);
		MyFile file = getItem(position);

		final CheckBox check = (CheckBox) view.findViewById(R.id.fileListCB);
		check.setTag(file);
		check.setChecked(file.isChecked());
		check.setText(file.getFile().getName());
		check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				MyFile file = (MyFile) check.getTag();
				file.setChecked(check.isSelected());
			}
		});

		return view;
	}

}
