package de.leander.projekt.structure;

import java.io.File;
import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class FileListAdapter extends BaseAdapter {

	private ArrayList<File> filelist;
	private Context context;

	public FileListAdapter(Context context, ArrayList<File> filelist) {
		this.context = context;
		this.filelist = filelist;
	}

	@Override
	public int getCount() {
		return filelist.size();
	}

	@Override
	public Object getItem(int position) {
		return filelist.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		MyView view;
		if (convertView == null) {
			view = new MyView(context);
		} else {
			view = (MyView) convertView;
		}

		view.configure(filelist.get(position));
		return view;
	}
}
