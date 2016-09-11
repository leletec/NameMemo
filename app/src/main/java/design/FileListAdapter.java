package design;

import java.io.File;
import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

/**
 * ListAdapter for AddPicFromStorageDialog().
 */
public class FileListAdapter extends AbstractListAdapter<File> {

	public FileListAdapter(Context context, ArrayList<File> list) {
		super(context, list);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		FileView view;
		if (convertView == null) {
			view = new FileView(context);
		} else {
			view = (FileView) convertView;
		}

		view.configure(list.get(position));
		return view;
	}

	private class FileView extends AbstractView  {

		public FileView(Context context) {
			super(context);
		}

		@Override
		public void configure(File e) {
			String filename = e.getName();
			tv.setText(filename);
		}
	}
}
