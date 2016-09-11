package design;

import java.util.ArrayList;

import database.Picture;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

/**
 * ListAdapter for ImportNewDb.
 */
public class DbListAdapter extends AbstractListAdapter<Picture> {

	public DbListAdapter(Context context, ArrayList<Picture> list) {
		super(context, list);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		DbView view;
		if (convertView == null) {
			view = new DbView(context);
		} else {
			view = (DbView) convertView;
		}

		view.configure(list.get(position));
		return view;
	}

	private class DbView extends AbstractView {

		public DbView(Context context) {
			super(context);
		}

		@Override
		public void configure(Picture e) {
			tv.setText(e.getShowAs());
		}
	}
}
