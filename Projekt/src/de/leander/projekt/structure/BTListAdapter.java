package de.leander.projekt.structure;

import java.util.ArrayList;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class BTListAdapter extends BaseAdapter{
	private ArrayList<BluetoothDevice> filelist;
	private Context context;

	public BTListAdapter(Context context, ArrayList<BluetoothDevice> filelist) {
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
		BTView view;
		if (convertView == null) {
			view = new BTView(context);
		} else {
			view = (BTView) convertView;
		}

		view.configure(filelist.get(position));
		return view;
	}
}
