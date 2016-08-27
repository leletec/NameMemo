package database;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class DbListAdapter extends BaseAdapter {

	private ArrayList<Picture> filelist;
	private Context context;

	public DbListAdapter(Context context, ArrayList<Picture> filelist) {
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

	/**
	 * Configures the view with the help of DbView.
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		DbView view;
		if (convertView == null) {
			view = new DbView(context);
		} else {
			view = (DbView) convertView;
		}

		view.configure(filelist.get(position));
		return view;
	}
}
